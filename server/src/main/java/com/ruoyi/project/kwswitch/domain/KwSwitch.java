package com.ruoyi.project.kwswitch.domain;

import com.alibaba.druid.sql.visitor.functions.Char;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 智能开关对象 kw_switch
 * 
 * @author kerwincui
 * @date 2020-04-23
 */
public class KwSwitch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 设备ID */
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** APIKEY */
    private String apiKey;

    /** 状态 */
    @Excel(name = "状态")
    private String state;

    /** 传感器 */
    @Excel(name = "传感器")
    private String sensor;

    private String switchA;

    private String switchB;

    /** 设备分类 */
    @Excel(name = "设备分类")
    private String category;

    /** 用户ID */
    private Long userId;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String userName;

    /** 删除标识 */
    private String delFlg;

    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }
    public void setDeviceName(String deviceName) 
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName() 
    {
        return deviceName;
    }

    public void setSwitchA(String switchA)
    {
        this.switchA = switchA;
    }
    public String getSwitchA()
    {
        return switchA;
    }

    public void setSwitchB(String switchB)
    {
        this.switchB = switchB;
    }
    public String getSwitchB()
    {
        return switchB;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getApiKey() 
    {
        return apiKey;
    }
    public void setState(String state) 
    {
        this.state = state;
    }

    public String getState() 
    {
        return state;
    }
    public void setSensor(String sensor)
    {
        this.sensor = sensor;
    }

    public String getSensor()
    {
        return sensor;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setDelFlg(String delFlg) 
    {
        this.delFlg = delFlg;
    }

    public String getDelFlg() 
    {
        return delFlg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("switchState", getSwitchA())
            .append("switchState", getSwitchB())
            .append("apiKey", getApiKey())
            .append("state", getState())
            .append("sensor", getSensor())
            .append("category", getCategory())
            .append("createTime", getCreateTime())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("remark", getRemark())
            .append("delFlg", getDelFlg())
            .toString();
    }
}
