package cn.appscomm.l38t.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.constant.AppUtil;


/**
 * Created by Administrator on 2016/2/18.
 */
public class DeviceListAdapter extends BaseAdapter {
    int minCount = 0;
    private List<DevData> deviceList;
    Context context;

    public DeviceListAdapter(Context context) {
        this.context = context;
        deviceList = new ArrayList<DevData>();
    }

    public void clearList() {
        deviceList.clear();
        this.notifyDataSetChanged();
    }

    public void addDevice(DevData dev) {
        Log.d(getClass().getSimpleName(), "addDevice rssi : " + dev.rssi);
        //如果这个设备已经存在，则不需要再处理了。
        if (hasDevice(dev.bluetoothDevice) || dev.bluetoothDevice == null || TextUtils.isEmpty(dev.bluetoothDevice.getName())) {
            return;
        }
        deviceList.add(dev);
        Collections.sort(deviceList, comparator);
        this.notifyDataSetChanged();
    }

    Comparator comparator = new Comparator<DevData>() {
        @Override
        public int compare(DevData lhs, DevData rhs) {
            if (lhs.rssi > rhs.rssi) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    public boolean hasDevice(BluetoothDevice bluetoothDevice) {
        for (DevData dd : deviceList) {
            if (dd.bluetoothDevice.equals(bluetoothDevice)) {
                return true;
            }
        }
        return false;
    }

    public void setChoosed(int position) {
        if (position < 0 || position >= deviceList.size()) {
            return;
        }
        for (int i = 0; i < deviceList.size(); i++) {
            deviceList.get(i).choose = false;
        }
        deviceList.get(position).choose = true;
        this.notifyDataSetChanged();
    }

    public BluetoothDevice getChoosedDev(int position) {
        if (position < 0 || position >= deviceList.size()) {
            return null;
        }
        return deviceList.get(position).bluetoothDevice;
    }


    @Override
    public int getCount() {
        if (deviceList.size() <= minCount) {
            return minCount;
        }
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_device_list_item, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.iv_choosed = (ImageView) convertView.findViewById(R.id.iv_choosed);
            viewHolder.iv_signal2 = (ImageView) convertView.findViewById(R.id.signal2);
            viewHolder.iv_signal3 = (ImageView) convertView.findViewById(R.id.signal3);
            viewHolder.iv_signal4 = (ImageView) convertView.findViewById(R.id.signal4);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position < deviceList.size()) {
            viewHolder.tv_name.setText(AppUtil.getShowNameWithWatchId(deviceList.get(position).bluetoothDevice.getName()));
            if (deviceList.get(position).choose) {
                viewHolder.iv_choosed.setVisibility(View.VISIBLE);
            } else {
                viewHolder.iv_choosed.setVisibility(View.INVISIBLE);
            }
            if (-85 < deviceList.get(position).rssi) {
                viewHolder.iv_signal2.setVisibility(View.VISIBLE);
                viewHolder.iv_signal3.setVisibility(View.VISIBLE);
                viewHolder.iv_signal4.setVisibility(View.VISIBLE);
            } else if (-100 < deviceList.get(position).rssi) {
                viewHolder.iv_signal2.setVisibility(View.VISIBLE);
                viewHolder.iv_signal3.setVisibility(View.VISIBLE);
                viewHolder.iv_signal4.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.iv_signal2.setVisibility(View.VISIBLE);
                viewHolder.iv_signal3.setVisibility(View.INVISIBLE);
                viewHolder.iv_signal4.setVisibility(View.INVISIBLE);
            }
        } else {
            viewHolder.tv_name.setText("");
            viewHolder.iv_choosed.setVisibility(View.INVISIBLE);
            viewHolder.iv_signal2.setVisibility(View.INVISIBLE);
            viewHolder.iv_signal3.setVisibility(View.INVISIBLE);
            viewHolder.iv_signal4.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_name;
        ImageView iv_choosed;
        ImageView iv_signal2, iv_signal3, iv_signal4;
    }

    public class DevData {
        BluetoothDevice bluetoothDevice;
        int rssi;
        boolean choose;

        public DevData(BluetoothDevice bluetoothDevice, int rssi) {
            this.bluetoothDevice = bluetoothDevice;
            this.rssi = rssi;
            choose = false;
        }
    }
}
