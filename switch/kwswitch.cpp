/********************************************************
* 作者：kerwincui
* 名称：kwswitch智能开关
* 项目地址：https://gitee.com/kerwincui/kwswitch
********************************************************/
#include "kwswitch.h"

Kwswitch::Kwswitch(byte _ledSwitchAPin,
                   byte _ledSwitchBPin,
                   byte _ledSmartConfigPin,
                   byte _switchAButtonPin,
                   byte _switchBButtonPin,
                   byte _smartConfigButtonPin,
                   byte _infraredPin,
                   byte _dht11Pin,
                   byte _touchPin)
{
    ledSwitchAPin = _ledSwitchAPin;               //开关A指示灯
    ledSwitchBPin = _ledSwitchBPin;               //开关B指示灯
    ledSmartConfigPin = _ledSmartConfigPin;       //智能配网指示灯
    switchAButtonPin = _switchAButtonPin;         //开关按钮
    switchBButtonPin = _switchBButtonPin;         //开关按钮
    smartConfigButtonPin = _smartConfigButtonPin; //智能配网按钮
    dht11Pin = _dht11Pin;                         //温湿度传感器引脚
    touchPin = _touchPin;                         //触摸开关引脚
    infraredPin = _infraredPin;                   //红外线引脚

    ssid = "kwswitch";          //wifi名称，一般32字节内
    password = "kwswitch";      //wifi密码，一般64字节内

    switchAState = 0;           //开关A的状态
    switchBState = 0;           //开关B的状态
    disconnectSwitchAState=0;   //掉线前开关A的状态
    disconnectSwitchBState=0;   //掉线前开关B的状态
    isDisconnect=false;         //是否掉线
    isNetwork = 0;              //是否联网

    switchAStateAddr = 0;        //开关A状态存储的地址
    switchBStateAddr = 1;        //开关B状态存储的地址
    isNetworkAddr = 2;           //是否联网存储的地址
    ssidLengthAddr = 3;          //wifi名称长度存储地址
    passwordLengthAddr = 36;     //wifi密码长度存储地址

    lastMsg = 0;                 //上次发布消息时间
    lastWifiConn = 0;            //上次wifi连接时间
}

//初始化
void Kwswitch::initConfig()
{
    pinMode(ledSwitchAPin, OUTPUT);
    pinMode(ledSwitchBPin, OUTPUT);
    pinMode(ledSmartConfigPin, OUTPUT);
    pinMode(touchPin, INPUT);

    //掉电记忆
    EEPROM.begin(100);
    switchAState = EEPROM.read(switchAStateAddr);
    switchBState = EEPROM.read(switchBStateAddr);
    digitalWrite(ledSwitchAPin, switchAState ? HIGH : LOW);
    digitalWrite(ledSwitchBPin, switchBState ? HIGH : LOW);

    //是否联网
    bool isOnline = EEPROM.read(isNetworkAddr);
    if (isOnline)
    {
        setNetwork();
    }
    else
    {
        setNotNetwork();
    }

    //获取Wifi密码账号
    byte ssidLength = EEPROM.read(ssidLengthAddr);
    byte passwordLength = EEPROM.read(passwordLengthAddr);
    ssid = eepromRead(ssidLengthAddr + 1, ssidLength);
    password = eepromRead(passwordLengthAddr + 1, passwordLength);
}

//读取EEPROM数据
String Kwswitch::eepromRead(int addr, int length)
{
    String temp = "";
    for (int i = 0; i < length; i++)
    {
        temp += char(EEPROM.read(addr + i));
    }
    return temp;
}

//写入数据到EEPROM
void Kwswitch::eepromWrite(int addr, String buf)
{
    char _buf[buf.length() + 1];
    buf.toCharArray(_buf, buf.length() + 1);
    for (int i = 0; i < buf.length(); i++)
    {
        EEPROM.write(addr + i, buf[i]);
    }
    EEPROM.commit();
}

//打印提示信息
void Kwswitch::printTips(String tips)
{
    Serial.print("\r\n[");
    Serial.print(millis());
    Serial.print("ms]");
    Serial.print(tips);
}

//打开继电器A
void Kwswitch::switchAOn()
{
    digitalWrite(ledSwitchAPin, HIGH);
    printTips("设置开关A灯亮");
    switchAState = 1;
    EEPROM.write(switchAStateAddr, 1);
    EEPROM.commit();
}

//关闭继电器A
void Kwswitch::switchAOff()
{
    digitalWrite(ledSwitchAPin, LOW);
    printTips("设置开关A灯灭");
    switchAState = 0;
    EEPROM.write(switchAStateAddr, 0);
    EEPROM.commit();
}

//打开继电器B
void Kwswitch::switchBOn()
{
    digitalWrite(ledSwitchBPin, HIGH);
    printTips("设置开关B灯亮");
    switchBState = 1;
    EEPROM.write(switchBStateAddr, 1);
    EEPROM.commit();
}

//关闭继电器B
void Kwswitch::switchBOff()
{
    digitalWrite(ledSwitchBPin, LOW);
    printTips("设置开关B灯灭");
    switchBState = 0;
    EEPROM.write(switchBStateAddr, 0);
    EEPROM.commit();
}

//掉线关闭继电器
void Kwswitch::disconnectSwitchClose()
{
    printTips("掉线关闭继电器");
    disconnectSwitchAState=switchAState;
    disconnectSwitchBState=switchBState;
    switchAOff();
    switchBOff();
    isDisconnect=true;
}

//上线后恢复继电器
void Kwswitch::connectSwitchRestore()
{
    printTips("上线后恢复继电器");
    if(disconnectSwitchAState){
        switchAOn();
    }
    if(disconnectSwitchBState){
        switchBOn();
    }
    isDisconnect=false;
}

//设置联网模式,关闭配置灯
void Kwswitch::setNetwork()
{
    printTips("设置为联网");
    digitalWrite(ledSmartConfigPin, LOW);
    isNetwork = 1;
    EEPROM.write(isNetworkAddr, 1);
    EEPROM.commit();
}

//设置不联网模式,配置灯常亮
void Kwswitch::setNotNetwork()
{
    printTips("设置为不联网");
    digitalWrite(ledSmartConfigPin, HIGH);
    isNetwork = 0;
    EEPROM.write(isNetworkAddr, 0);
    EEPROM.commit();
}

//智能配网
bool Kwswitch::smartConfig()
{
    WiFi.mode(WIFI_STA);
    printTips("等待配网");
    // 等待配网
    WiFi.beginSmartConfig();
    unsigned long beginTime = millis();
    while (1)
    {
        Serial.print(".");
        //智能配网提示灯闪烁
        digitalWrite(ledSmartConfigPin, HIGH);
        delay(500);
        digitalWrite(ledSmartConfigPin, LOW);
        delay(500);
        if (WiFi.smartConfigDone())
        {
            printTips("设置wifi成功");
            //存储ssid和密码
            ssid = WiFi.SSID();
            password = WiFi.psk();
            String ssidBuf = ssid.length() + ssid;             //存储格式为 ssid长度+ssid
            String passwordBuf = password.length() + password; //存储格式为 ssid长度+ssid
            eepromWrite(ssidLengthAddr, ssidBuf);
            eepromWrite(passwordLengthAddr, passwordBuf);
            //设置为联网模式
            setNetwork();
            WiFi.setAutoConnect(true); // 设置自动连接
            if (connectWifi())
            {
                printTips("智能配网成功");
                return true;
            }
            break;
        }
        //配置超时，自动退出
        if (millis() - beginTime > 60000)
        {
            beginTime = millis();
            setNotNetwork();
            printTips("智能配网超时");
            break;
        }
    }
    setNotNetwork();
    printTips("智能配网失败");
    WiFi.stopSmartConfig();
    return false;
}

//连接WIFI
bool Kwswitch::connectWifi()
{
    printTips("连接 ");
    Serial.println(ssid);
    WiFi.begin(ssid, password);
    byte i = 0;
    while (WiFi.status() != WL_CONNECTED)
    {
        delay(500);
        Serial.print(".");
        if (WiFi.status() == WL_CONNECTED)
        {
            printTips("WiFi连接成功");
            printTips("IP地址: ");
            Serial.println(WiFi.localIP());
            //配置灯快闪
            for (byte i = 0; i < 3; i++)
            {
                digitalWrite(ledSmartConfigPin, HIGH);
                delay(200);
                digitalWrite(ledSmartConfigPin, LOW);
                delay(200);
            }
            return true;
        }

        i++;
        if (i > 30)
        {
            printTips("WiFi连接超时");
            return false;
        }
    }
    return true;
}

//连接Mqtt服务器
bool Kwswitch::reconnectMqtt(PubSubClient client, char *deviceId, char *apiKey, char *mqtt_user_name, char *mqtt_pwd)
{
    printTips("尝试连接Mqtt服务器...");
    String clientId = "kwswitch-";
    clientId += String(random(0xffff), HEX);
    String willMsg = "{\"deviceId\":\"" + (String)deviceId + "\",\"state\":\"0\",\"apiKey\":\"" + (String)apiKey + "\"}"; //0-离线
    //连接并设置离线遗嘱
    bool connectResult = client.connect(clientId.c_str(), mqtt_user_name, mqtt_pwd, "state", 1, 1, willMsg.c_str());
    if (connectResult)
    {
        printTips("连接成功");
        //发布设备状态
        publishState(client, deviceId, apiKey);
        //订阅
        printTips("订阅云端数据");
        String switchTopic = "set/switch/" + (String)deviceId;
        String resetTopic = "set/reset/" + (String)deviceId;
        String monitorTopic = "get/monitor/" + (String)deviceId;
        client.subscribe(switchTopic.c_str());
        client.subscribe(resetTopic.c_str());
        client.subscribe(monitorTopic.c_str());
        return true;
    }
    else
    {
        printTips("连接失败, rc=");
        Serial.print(client.state());
        return false;
    }
}

void Kwswitch::publishState(PubSubClient client, char *deviceId, char *apiKey)
{
    printTips("发布开关状态");
    String message = "{\"deviceId\":" + (String)deviceId + ",\"state\":\"1\",\"apiKey\":\"" + (String)apiKey + "\",\"switchA\":\"" + (String)switchAState + "\",\"switchB\":\"" + (String)switchBState+"\"}"; //state=1-在线
    client.publish("state", message.c_str());
}

//发布监测数据
void Kwswitch::publishMonitor(PubSubClient client, Dht11 dht11, char *deviceId, char *apiKey)
{
    printTips("发布监测数据: ");
    //获取温湿度
    int chk = dht11.read(dht11Pin);
    Serial.print("\r\n读取传感器: ");
    switch (chk)
    {
    case DHTLIB_OK:
        Serial.println("OK");
        break;
    case DHTLIB_ERROR_CHECKSUM:
        Serial.println("Checksum error");
        break;
    case DHTLIB_ERROR_TIMEOUT:
        Serial.println("Time out error");
        break;
    default:
        Serial.println("Unknown error");
        break;
    }
    Serial.print("湿度 (%): ");
    Serial.println(dht11.humidity, 2);
    Serial.print("温度 (oC): ");
    Serial.println(dht11.temperature, 2);
    //拼接json并发布
    String message = "{\"deviceId\":\"" + (String)deviceId + "\",\"apiKey\":\"" + (String)apiKey + "\",\"switchA\":" + switchAState;
    message += ",\"switchB\": " + (String)switchBState + ",\"humidity\":" + (String)dht11.humidity + ",\"Temperature\":" + (String)dht11.temperature + "}";
    client.publish_P("monitor", message.c_str(),false);
    
}
