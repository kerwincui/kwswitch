package com.espressif.kwswitch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.espressif.kwswitch.R;
import com.espressif.kwswitch.common.ApiHelper;
import com.espressif.kwswitch.common.ReceiverData;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private ImageButton buttonCapture;
    private ImageButton buttonClose;
    private EditText accountTxt;
    private EditText passwordTxt;
    private EditText captchaTxt;
    private CheckBox rememberChk;
    private String username;
    private String password;
    //声明一个SharedPreferences对象和一个Editor对象
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String uuid; //验证码的标识符


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取Indent的值
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        ApiHelper apiHelper = new ApiHelper(); //网络请求类
        //Handler处理线程间消息传递
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    ReceiverData data = (ReceiverData) msg.obj;
                    buttonCapture.setImageBitmap(data.getBitmap());
                    uuid = data.getUuid();
                } else if (msg.what == 1) {
                    ReceiverData data = (ReceiverData) msg.obj;
                    loginProcess(data);
                }
            }
        };


        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        buttonCapture = (ImageButton) findViewById(R.id.captureBtn);
        buttonLogin = (Button) findViewById(R.id.loginBtn);
        buttonCapture = (ImageButton) findViewById(R.id.captureBtn);
        buttonClose = (ImageButton) findViewById(R.id.closeBtn);
        accountTxt = (EditText) findViewById(R.id.accountTxt);
        passwordTxt = (EditText) findViewById(R.id.passwordTxt);
        captchaTxt = (EditText) findViewById(R.id.captchaTxt);
        rememberChk = (CheckBox) findViewById(R.id.rememberChk);
        //获取preferences和editor对象
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();

        //判断是否为退出
        if (message != null && message.equals("exit")) {
            passwordTxt.setText("");
            editor.remove("password");
            editor.commit();
        }

        //获取本地存储的账号和密码
        String localName = preferences.getString("username", null);
        if (localName != null) {
            accountTxt.setText(localName);
        }
        String localPassword = preferences.getString("password", null);
        if (localPassword == null) {
            rememberChk.setChecked(false);
        } else {
            passwordTxt.setText(localPassword);
            rememberChk.setChecked(true);
        }

        //获取验证码
        apiHelper.getCodeImg(handler);
        //刷新验证码
        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取新的验证码
                apiHelper.getCodeImg(handler);
            }
        });

        //关闭
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出程序
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        //登录功能
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断密码、账号和验证码不为空
                username = accountTxt.getText().toString().trim();
                password = passwordTxt.getText().toString().trim();
                String code = captchaTxt.getText().toString().trim();
                if (username.length() != 0 && password.length() != 0 && code.length() != 0) {
                    //调用登录接口
                    apiHelper.loginIn(handler, username, password, code, uuid);
                } else {
                    Toast.makeText(LoginActivity.this, "用户名、密码、验证码都不能为空。", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //登录处理
    private void loginProcess(ReceiverData data) {
        if (data.getCode() != 200) {
            //若登陆不成功，则将错误的用户名和密码清除，并提示登陆失败
            editor.remove("userName");
            editor.remove("password");
            editor.commit();
            Toast.makeText(LoginActivity.this, data.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            //保存用户名和密码
            if (rememberChk.isChecked()) {
                editor.putString("username", username);
                editor.putString("password", password);
                editor.commit();
                //进入设备列表界面
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, DeviceActivity.class);
                intent.putExtra("token", data.getToken());
                startActivity(intent);
            } else {
                //否则将用户名清除
                editor.remove("username");
                editor.remove("password");
                editor.commit();
            }
        }
    }
}
