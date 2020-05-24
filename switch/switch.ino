/********************************************************
* 作者：kerwincui
* 名称：kwswitch智能开关
* 项目地址：https://gitee.com/kerwincui/kwswitch
********************************************************/
#include "kwswitch.h"
#include <IRremoteESP8266.h>
#include <IRrecv.h>
#include <IRutils.h>
#include "OneButton.h"

char *deviceId = "1";                               //设备ID
char *apiKey = "A432594C7B234966B0CC4D24E57C22FE";  //设备apiKey
char *mqtt_server = "192.168.0.1";                  //Mqtt服务器地址
int mqtt_port = 1883;                               //Mqtt服务器端口
char *mqtt_user_name = "admin";                     //Mqtt服务器账号
char *mqtt_pwd = "admin";                           //Mqtt服务器密码

//单路继电器使用
Kwswitch kwswitch(12, 100, 15, 13, 100, 14, 5, 2,4);
//双路继电器使用
//Kwswitch kwswitch(12, 14, 15, 4, 13, 5, 2, 100, 100);

Dht11 dht11;
IRrecv irrecv(kwswitch.infraredPin);
decode_results results;
WiFiClient espClient;
PubSubClient client(espClient);
OneButton smartconfigButton(kwswitch.smartConfigButtonPin, true);
OneButton switchAButton(kwswitch.switchAButtonPin, true);
OneButton switchBButton(kwswitch.switchBButtonPin, true);

//初始配置
void setup()
{
  //打开串行端口：
  Serial.begin(115200);
  //初始化配置
  kwswitch.initConfig();
  //按钮添加事件
  buttonAttachEvent();
  //启动红外线接收
  irrecv.enableIRIn();
  //网络
  if (kwswitch.isNetwork)
  {
    //连接WIFI
    if (kwswitch.connectWifi())
    {
      //启动MQTT
      client.setServer(mqtt_server, mqtt_port);
      client.setCallback(callback);
      kwswitch.reconnectMqtt(client, deviceId, apiKey, mqtt_user_name, mqtt_pwd);
    }
  }
  kwswitch.printTips("kwswitch start...");
}

//运行
void loop()
{
  //红外遥控
  if (irrecv.decode(&results))
  {
    // 打印uint64_t使用serialPrintUint64
    serialPrintUint64(results.value, HEX);
    Serial.println();
    switch (results.value)
    {
    case 0xFF30CF:
    case 0x44BB01FE:
    case 0xAFFABE0:
      kwswitch.switchAOn();
      kwswitch.switchBOn();
      break;
    case 0xFF18E7:
    case 0x44BB817E:
    case 0x11E7469A:
      kwswitch.switchAOff();
      kwswitch.switchBOff();
      break;
    case 0xFF10EF:
    case 0x44BBA15E:
    case 0xB20F281E:
      kwswitch.switchAOn();
      kwswitch.switchBOff();
      break;
    case 0xFF7A85:
    case 0x44BB619E:
    case 0x1A56640:
      kwswitch.switchAOff();
      kwswitch.switchBOn();
      break;
    }
    //发布开关状态
    if (kwswitch.isNetwork)
    {
      kwswitch.publishState(client, deviceId, apiKey);
    }
    delay(500);
    irrecv.resume(); // 接受下一个值
  }

  //配置按钮
  smartconfigButton.tick();
  //开关A按钮
  switchAButton.tick();
  //开关B按钮
  switchBButton.tick();

  //触摸开关,特定环境下会导致灯不断开关，暂时注释掉
  // if (digitalRead(kwswitch.touchPin)==HIGH)
  // {
  //   if (kwswitch.switchAState)
  //   {
  //     kwswitch.switchAOff();
  //     kwswitch.switchBOff();
  //   }
  //   else
  //   {
  //     kwswitch.switchAOn();
  //     kwswitch.switchBOn();
  //   }
  // //发布开关状态
  //   if(kwswitch.isNetwork){
  //     publishState(client,deviceId, apiKey);
  //   }
  //   delay(500);
  // }

  //Wifi连接，间隔60秒
  if (kwswitch.isNetwork && WiFi.status() != WL_CONNECTED)
  {
    long now = millis();
    if (now - kwswitch.lastWifiConn > 60000)
    {
      kwswitch.lastWifiConn = now;
      kwswitch.connectWifi();
    }
  }

  //Mqtt连接和订阅、发布,间隔30秒
  if (kwswitch.isNetwork && WiFi.status() == WL_CONNECTED)
  {
    long now = millis();
    //连接Mqtt
    if (!client.connected())
    {
      if (now - kwswitch.lastMsg > 30000)
      {
        kwswitch.lastMsg = now;
        //连接失败关闭继电器
        if(kwswitch.isDisconnect==false) {kwswitch.disconnectSwitchClose();}

        //启动MQTT
        client.setServer(mqtt_server, mqtt_port);
        client.setCallback(callback);
        if(kwswitch.reconnectMqtt(client, deviceId, apiKey, mqtt_user_name, mqtt_pwd))
        {
          kwswitch.printTips("mqtt处还原");
          //连接成功后还原状态
          if(kwswitch.isDisconnect) {kwswitch.connectSwitchRestore();}
        }
      }
    }
    //订阅和发布
    else
    {
      client.loop();
      if (now - kwswitch.lastMsg > 30000)
      {
        kwswitch.lastMsg = now;
        kwswitch.publishMonitor(client, dht11, deviceId, apiKey);
      }
    }
  }
}

//Mqtt回调
void callback(char *topic, byte *payload, unsigned int length)
{
  String switchTopic = (String) "set/switch/" + (String)deviceId;                  //设置开关
  String monitorTopic = (String) "get/monitor/" + (String)deviceId;                //获取监测数据
  String resetTopic = (String) "set/reset/" + (String)deviceId;                    //设备重启

  kwswitch.printTips("收到消息 [");
  Serial.print(topic);
  Serial.print("] ");
  String deviceKey=apiKey;  //设备的key
  String newKey="";         //验证的key
  for (int i = 0; i < length; i++)
  {
    Serial.print((char)payload[i]);
    if(i>1){
      newKey += (char)payload[i];
    }
  }
  Serial.println();
  //验证APIKEY是否匹配
  if(!deviceKey.equals(newKey)){
    kwswitch.printTips("设备的APIKEY验证失败。");
    return;
  }

  if (strcmp(topic, switchTopic.c_str()) == 0) //控制开关
  {
    //开关A
    if((char)payload[0]=='1')
    {
        kwswitch.switchAOn();
    }else if((char)payload[0]=='0')
    {
      kwswitch.switchAOff();
    }
    //开关B
    if((char)payload[1]=='1')
    {
        kwswitch.switchBOn();
    }else if((char)payload[1]=='0')
    {
      kwswitch.switchBOff();
    }
    //发布设备状态
    kwswitch.publishState(client,deviceId,apiKey);
    
  }
  else if (strcmp(topic, monitorTopic.c_str()) == 0) //获取监测数据
  {
    //发布监测数据
    kwswitch.publishMonitor(client, dht11, deviceId, apiKey);
  }
  else if(strcmp(topic, resetTopic.c_str()) == 0)
  {
    kwswitch.printTips("设备重启...");
    ESP.restart();
  }
}

//按钮添加事件
void buttonAttachEvent()
{
  switchAButton.attachClick(clickSwitchA);
  switchAButton.attachDoubleClick(doubleClickRSTConfig);
  switchBButton.attachClick(clickSwitchB);
  smartconfigButton.attachDoubleClick(doubleclickConfig);
  smartconfigButton.attachLongPressStart(longPressStartConfig);
}

//单击开关按钮A
void clickSwitchA()
{
  if (kwswitch.switchAState)
  {
    kwswitch.switchAOff();
  }
  else
  {
    kwswitch.switchAOn();
  }
  //发布开关状态
  if (kwswitch.isNetwork)
  {
    kwswitch.publishState(client, deviceId, apiKey);
  }
}

//单击开关按钮B
void clickSwitchB()
{
  if (kwswitch.switchBState)
  {
    kwswitch.switchBOff();
  }
  else
  {
    kwswitch.switchBOn();
  }
  //发布开关状态
  if (kwswitch.isNetwork)
  {
    kwswitch.publishState(client, deviceId, apiKey);
  }
}

//双击配置按钮，设置是否联网
void doubleclickConfig()
{
  if (kwswitch.isNetwork)
  {
    kwswitch.setNotNetwork();
    kwswitch.publishState(client, deviceId, apiKey);
  }
  else
  {
    kwswitch.setNetwork();
    //联网
    if (kwswitch.connectWifi())
    {
      //启动MQTT
      client.setServer(mqtt_server, mqtt_port);
      client.setCallback(callback);
      kwswitch.reconnectMqtt(client, deviceId, apiKey, mqtt_user_name, mqtt_pwd);
    }
  }
}

//双击开关按钮，设备重启
void doubleClickRSTConfig()
{
  kwswitch.printTips("设备重启...");
  ESP.restart();
}

//长按配置按钮，进入智能配网
void longPressStartConfig()
{
  if (kwswitch.smartConfig())
  {
    //启动MQTT
    client.setServer(mqtt_server, mqtt_port);
    client.setCallback(callback);
    kwswitch.reconnectMqtt(client, deviceId, apiKey, mqtt_user_name, mqtt_pwd);
  }
}
