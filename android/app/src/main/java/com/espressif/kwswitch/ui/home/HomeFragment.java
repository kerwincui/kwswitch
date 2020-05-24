package com.espressif.kwswitch.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.espressif.kwswitch.LoginActivity;
import com.espressif.kwswitch.R;
import com.espressif.kwswitch.common.ApiHelper;
import com.espressif.kwswitch.common.DeviceListItemAdapter;
import com.espressif.kwswitch.common.ReceiverData;
import com.espressif.kwswitch.common.SwitchDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private DeviceListItemAdapter deviceAdapter;
    private ListView listView = null;
    private List<HashMap<String, Object>> data = null;
    private HashMap<String, Object> dataItem = null;
    private Switch switchA;
    private Switch switchB;
    private String token;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        token=getActivity().getIntent().getStringExtra("token");

        //适配器
        ListView listView = (ListView)root.findViewById(R.id.device_listview);

        ApiHelper apiHelper=new ApiHelper(); //网络请求类
        //Handler处理线程间消息传递
        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==2) {
                    ReceiverData data=(ReceiverData) msg.obj;
                    if(data.getCode()!=200){
                        Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
                    }else{
                        List<SwitchDevice> list=data.getRows();
                        deviceAdapter = new DeviceListItemAdapter(getActivity(),list,this,token);
                        listView.setAdapter(deviceAdapter);
                    }
                }else if(msg.what==3){
                    ReceiverData data=(ReceiverData) msg.obj;
                    if(data.getCode()!=200) {
                        Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "开关指定已发送", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        //调用设备列表接口
        apiHelper.getSwitchList(handler,token);
        return root;
    }






    //下拉刷新功能



}
