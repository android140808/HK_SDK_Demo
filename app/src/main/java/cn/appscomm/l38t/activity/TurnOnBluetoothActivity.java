package cn.appscomm.l38t.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;


public class TurnOnBluetoothActivity extends BaseActivity {
    private static final int REQUEST_ENABLE_BT = 11111;
    @BindView(R.id.btn_ok)
    Button btnOk;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_on_bluetooth);
        setTitle(getString(R.string.title_turn_on_bluetooth));
        init();
    }

    private void init() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBluetoothAdapter.isEnabled()) {
            // startActivity(EnsureDeviceNearbyActivity.class);
            startActivity(DeviceChooseActivity.class);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            showToast(getString(R.string.turn_on_bluetooth_tips));
            return;
        }
        // Tim 开启蓝牙设备就到下一步
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            // startActivity(EnsureDeviceNearbyActivity.class);
            startActivity(DeviceChooseActivity.class);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btn_ok)
    public void onClick() {
        backPress();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backPress();
    }

    private void backPress() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // startActivity(EnsureDeviceNearbyActivity.class);
            startActivity(DeviceChooseActivity.class);
            finish();
        }
    }
}
