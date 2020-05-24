package com.espressif.kwswitch.common;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.solver.widgets.Helper;

import com.espressif.kwswitch.R;

import java.util.List;

public class DeviceListItemAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SwitchDevice> listItems;
    private Handler handler;
    private String token;
    private ApiHelper helper;

    public DeviceListItemAdapter(Context context,List<SwitchDevice> listItems,Handler handler,String token) {
        this.mInflater = LayoutInflater.from(context);
        this.listItems = listItems;
        this.handler=handler;
        this.token=token;
        this.helper=new ApiHelper();
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_view_item_device, null);
            holder = new ViewHolder();
            holder.deviceName=convertView.findViewById(R.id.deviceNameTxt);
            holder.category=convertView.findViewById(R.id.categoryTxt);
            holder.sensor=convertView.findViewById(R.id.sensorTxt);
            holder.state=convertView.findViewById(R.id.stateTxt);
            holder.switchA=convertView.findViewById(R.id.switchA);
            holder.switchB=convertView.findViewById(R.id.switchB);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.deviceName.setText(listItems.get(position).getDeviceName());
        boolean switchAState=listItems.get(position).getSwitchA().equals("0")?false:true;
        boolean switchBState=listItems.get(position).getSwitchB().equals("0")?false:true;
        holder.switchA.setChecked(switchAState);
        holder.switchB.setChecked(switchBState);
        String categoryDic=listItems.get(position).getCategory();
        if(categoryDic.equals("single_switch")){
            holder.category.setText("单路");
            //设置开关B不能用
            holder.switchB.setEnabled(false);
        }else if(categoryDic.equals("double_switch")){
            holder.category.setText("双路");
        }
        String sensorDic=listItems.get(position).getSensor();
        if(sensorDic.equals("temperature")){
            holder.sensor.setText("温湿度传感器");
        }else{
            holder.sensor.setText("未知传感器");
        }
        String stateDic=listItems.get(position).getState();
        if(stateDic.equals("1")){
            holder.state.setText("● 在线");
        }else if(stateDic.equals("0")){
            holder.state.setText("● 离线");
            //设置开关不能用
            holder.switchA.setEnabled(false);
            holder.switchB.setEnabled(false);
            holder.deviceName.setTextColor(Color.parseColor("#999999"));
            holder.state.setTextColor(Color.parseColor("#999999"));
        }

        holder.switchA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //调用开关A接口
                int switchA=holder.switchA.isChecked()?1:0;
                int switchB=holder.switchB.isChecked()?1:0;
                helper.setSwitch(handler,token,listItems.get(position).getDeviceId(),switchA,switchB);
            }
        });

        holder.switchB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //调用开关A接口
                int switchA=holder.switchA.isChecked()?1:0;
                int switchB=holder.switchB.isChecked()?1:0;
                helper.setSwitch(handler,token,listItems.get(position).getDeviceId(),switchA,switchB);
            }
        });

        return convertView;
    }

    public class ViewHolder {
        TextView deviceName;
        TextView category;
        TextView sensor;
        TextView state;
        Switch switchA;
        Switch switchB;
    }

}

