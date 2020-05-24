package com.ruoyi.project.kwswitch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="kw_switch_log")
public class KwSwitchLog {
    @Id
    private String id;
    private long deviceId;
    private boolean switchA;
    private boolean switchB;
    private byte temperature;
    private byte humidity;
    private Date createTime;

    public String getId(){return id;}
    public void setId(String id){this.id=id;}

    public Long getDeviceId(){return deviceId;}
    public void setDeviceId(Long deviceId){this.deviceId=deviceId;}

    public boolean getSwitchA(){return switchA;}
    public void setSwitchA(boolean switchA){this.switchA=switchA;}

    public boolean getSwitchB(){return switchB;}
    public void setSwitchB(boolean switchB){this.switchB=switchB;}

    public byte getTemperature(){return temperature;}
    public void setTemperature(byte temperature){this.temperature=temperature;}

    public byte getHumidity(){return humidity;}
    public void setHumidity(byte humidity){this.humidity=humidity;}

    public Date getCreateTime(){return createTime;}
    public void setCreateTime(Date createTime){this.createTime=createTime;}
}
