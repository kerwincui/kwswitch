package com.espressif.kwswitch.common;

import android.graphics.Bitmap;

import java.util.List;

public class ReceiverData {
    private int code;
    private String img;
    private String msg;
    private String uuid;
    private String token;
    private Bitmap bitmap;
    private List<SwitchDevice> rows;

    public int getCode(){return this.code;}
    public void setCode(int code){this.code=code;}

    public String getImg(){return this.img;}
    public void setImg(String img){this.img=img;}

    public String getMsg(){return this.msg;}
    public void setMsg(String msg){this.msg=msg;}

    public String getUuid(){return this.uuid;}
    public void setUuid(String uuid){this.uuid=uuid;}

    public String getToken(){return this.token;}
    public void setToken(String token){this.token=token;}

    public Bitmap getBitmap(){return this.bitmap;}
    public void setBitmap(Bitmap bitmap){this.bitmap=bitmap;}

    public List<SwitchDevice> getRows(){return this.rows;}
    public void setRows(List<SwitchDevice> token){this.rows=rows;}
}
