/********************************************************* 
* 作者：kerwincui
* 名称：kwswitch智能开关
* 项目地址：https://gitee.com/kerwincui/kwswitch
********************************************************/
#ifndef Kwswitch_h
#define Kwswitch_h

#include <ESP8266WiFi.h>
#include <EEPROM.h>
#include <PubSubClient.h>
#include "dht11.h"

class Kwswitch
{
private:
    String ssid;             //wifi名称
    String password;         //wifi密码
    byte isNetworkAddr;      //开关A状态存储的地址
    byte switchAStateAddr;   //开关A状态存储的地址
    byte switchBStateAddr;   //是否联网存储的地址
    byte ssidLengthAddr;     //wifi名称长度存储地址
    byte passwordLengthAddr; //wifi密码长度存储地址

    String eepromRead(int addr, int length);
    void eepromWrite(int addr, String buf);

public:
    byte ledSwitchAPin;        //开关A指示灯
    byte ledSwitchBPin;        //开关B指示灯
    byte ledSmartConfigPin;    //智能配网指示灯
    byte switchAButtonPin;     //开关A按钮
    byte switchBButtonPin;     //开关B按钮
    byte smartConfigButtonPin; //智能配网按钮
    byte dht11Pin;             //温湿度传感器引脚
    byte touchPin;             //触摸开关引脚
    byte infraredPin;          //红外线引脚

    bool switchAState; //开关A的状态
    bool switchBState; //开关B的状态
    bool isNetwork;    //是否联网
    long lastMsg;      //上次发布消息时间
    long lastWifiConn; //上次wifi连接时间

    bool isDisconnect;
    bool disconnectSwitchAState; //断网前开关A的状态
    bool disconnectSwitchBState; //断网前开关B的状态

    Kwswitch(byte ledSwitchAPin,
             byte ledSwitchBPin,
             byte ledSmartConfigPin,
             byte switchAButtonPin,
             byte switchBButtonPin,
             byte smartConfigButtonPin,
             byte infraredPin,
             byte dht11Pin,
             byte touchPin);
    void printTips(String tips);
    void initConfig();
    void switchAOn();
    void switchBOn();
    void switchAOff();
    void switchBOff();
    void disconnectSwitchClose();
    void connectSwitchRestore();
    void setNetwork();
    void setNotNetwork();
    bool smartConfig();
    bool connectWifi();
    bool reconnectMqtt(PubSubClient client, char *deviceId, char *apiKey, char *mqtt_user_name, char *mqtt_pwd);
    void publishMonitor(PubSubClient client, Dht11 dht11, char *deviceId, char *apiKey);
    void publishState(PubSubClient client, char *deviceId, char *apiKey);
};
#endif