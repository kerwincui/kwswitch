package com.espressif.kwswitch.common;


import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiHelper {
    private Gson gson;
    private String target="http://192.168.0.100/prod-api/";

    public ApiHelper(){
        gson = new Gson();
    }

    //获取验证码
    public void getCodeImg(Handler handler){
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url(target+"captchaImage").build();
                try{
                    Response response=client.newCall(request).execute();
                    String json=response.body().string();
                    ReceiverData data=gson.fromJson(json,ReceiverData.class);
                    byte[] bytes = Base64.decode(data.getImg(), Base64.DEFAULT);
                    data.setBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    //传递数据
                    Looper.prepare();
                    Message message=new Message();
                    message.what=0;
                    message.obj=data;
                    handler.sendMessage(message);
                    Looper.loop();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //登录
    public void loginIn(Handler handler,String username,String password,String code,String uuid){
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                String requestJson="{\"username\":\""+username+"\",\"password\":\""+password+"\",\"code\":\""+code+"\",\"uuid\":\""+uuid+"\"}";
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJson);
                Request request=new Request.Builder()
                        .addHeader("Content-Type","application/json")
                        .url(target+"login")
                        .post(body)
                        .build();
                try{
                    Response response=client.newCall(request).execute();
                    String json=response.body().string();
                    ReceiverData data=gson.fromJson(json,ReceiverData.class);
                    Looper.prepare();
                    Message message=new Message();
                    message.what=1;
                    message.obj=data;
                    handler.sendMessage(message);
                    Looper.loop();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //获取设备列表
    public void getSwitchList(Handler handler,String token){
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder()
                        .url(target+"kwswitch/switch/list?pageNum=1&pageSize=100")
                        .addHeader("Authorization","Bearer "+token)
                        .cacheControl(new CacheControl.Builder().noCache().noStore().build())
                        .build();
                try{
                    Response response=client.newCall(request).execute();
                    String json=response.body().string();
                    ReceiverData data=gson.fromJson(json,ReceiverData.class);
                    Looper.prepare();
                    Message message=new Message();
                    message.what=2;
                    message.obj=data;
                    handler.sendMessage(message);
                    Looper.loop();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //控制开关
    public void setSwitch(Handler handler,String token,long deviceId,int switchA,int switchB){
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                FormBody formBody=new FormBody.Builder()
                        .add("deviceId",String.valueOf(deviceId))
                        .add("switchA",String.valueOf(switchA))
                        .add("switchB",String.valueOf(switchB))
                        .build();
                Request request=new Request.Builder()
                        .url(target+"kwswitch/switch/set")
                        .addHeader("Authorization","Bearer "+token)
                        .post(formBody)
                        .build();
                try{
                    Response response=client.newCall(request).execute();
                    String json=response.body().string();
                    ReceiverData data=gson.fromJson(json,ReceiverData.class);
                    Looper.prepare();
                    Message message=new Message();
                    message.what=3;
                    message.obj=data;
                    handler.sendMessage(message);
                    Looper.loop();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
