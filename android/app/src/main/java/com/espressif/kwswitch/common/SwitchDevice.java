package com.espressif.kwswitch.common;

public class SwitchDevice {
            /** 设备ID */
        private Long deviceId;

        /** 设备名称 */
        private String deviceName;

        /** APIKEY */
        private String apiKey;

        /** 状态 */
        private String state;

        /** 传感器 */
        private String sensor;

        private String switchA;

        private String switchB;

        /** 设备分类 */
        private String category;

        /** 用户ID */
        private Long userId;

        /** 用户昵称 */
        private String userName;


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


}
