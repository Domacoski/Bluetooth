package bluetooth.arduino.domacoski.tl.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BluetoothConnectService.BluetoohReceiverUpdate{

    private Integer ENABLE_BLUETOOTH = 212;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice mDevice = null;
    private DeviceSelect mDeviceSelect;

    private BluetoothConnectService.BluetoohReceiver mBluetoohReceiver;
    private BluetoothConnectService mConnect;

    private int[] states = {0, 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoohReceiver = new BluetoothConnectService.BluetoohReceiver(this);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoohReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBluetoohReceiver) {
            unregisterReceiver(mBluetoohReceiver);
        }
        if(mConnect != null){
            mConnect.finalize();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            enableBluetooh();
        } else {
            if (null == mDevice) {
                openDeviceSelect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                openDeviceSelect();
            } else {
                Toast.makeText(this, "O aplicativo só funciona com o Bluetooth Ligado!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void onOff(final View led) {

        switch (led.getId()) {
            case R.id.led1: {
                if (states[0] == 0) {
                    states[0] = 1;
                    led.setBackgroundResource(R.drawable.ic_led_on);
                } else {
                    states[0] = 0;
                    led.setBackgroundResource(R.drawable.ic_led_off);
                }
                break;
            }
            case R.id.led2: {
                if (states[1] == 0) {
                    states[1] = 1;
                    led.setBackgroundResource(R.drawable.ic_led_on);
                } else {
                    states[1] = 0;
                    led.setBackgroundResource(R.drawable.ic_led_off);
                }
                break;
            }
            case R.id.led3: {
                if (states[2] == 0) {
                    states[2] = 1;
                    led.setBackgroundResource(R.drawable.ic_led_on);
                } else {
                    states[2] = 0;
                    led.setBackgroundResource(R.drawable.ic_led_off);
                }
                break;
            }
            case R.id.led4: {
                if (states[3] == 0) {
                    states[3] = 1;
                    led.setBackgroundResource(R.drawable.ic_led_on);
                } else {
                    states[3] = 0;
                    led.setBackgroundResource(R.drawable.ic_led_off);
                }
                break;
            }
            case R.id.led5: {
                if (states[4] == 0) {
                    states[4] = 1;
                    led.setBackgroundResource(R.drawable.ic_led_on);
                } else {
                    states[4] = 0;
                    led.setBackgroundResource(R.drawable.ic_led_off);
                }
                break;
            }
            case R.id.led6: {
                if (states[5] == 0) {
                    states[5] = 1;
                    led.setBackgroundResource(R.drawable.ic_led_on);
                } else {
                    states[5] = 0;
                    led.setBackgroundResource(R.drawable.ic_led_off);
                }
                break;
            }
        }
        new SendAction(mDevice, states).start();
    }

    public void openDeviceSelect() {
        if (null != mDeviceSelect) {
            if (mDeviceSelect.isShowing()) {
                return;
            }
            mDeviceSelect = null;
        }
        mDeviceSelect = new DeviceSelect(this);
        mDeviceSelect.show();
    }

    public void closeDeviceSelect() {
        if (null != mDeviceSelect) {
            if (mDeviceSelect.isShowing()) {
                mDeviceSelect.dismiss();
            }
        }
    }

    public void setDevice(final BluetoothDevice mDevice) {
        this.mDevice = mDevice;
        updateUIFromDevice();
    }

    private void updateUIFromDevice() {
        if (null == mDevice) {
            setTitle("Nenhum dispositivo");
        } else {
            setTitle(mDevice.getName());
            initConnect();
        }
    }

    private void initConnect(){
        if(null == mDevice){
            return;
        }
        mConnect = new BluetoothConnectService(mDevice, this);
        mConnect.initialize();
    }
    public void enableBluetooh() {
        final Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(turnOn, ENABLE_BLUETOOTH);
    }

    @Override
    public void stateOff() {

    }

    @Override
    public void stateTurningOff() {

    }

    @Override
    public void stateOn() {

    }

    @Override
    public void stateTurningOn() {

    }


    class DeviceSelect extends Dialog {
        public DeviceSelect(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            setContentView(R.layout.dialog_device);
            final List<BluetoothDevice> devices = new ArrayList<>();
            for (final BluetoothDevice d : mBluetoothAdapter.getBondedDevices()) {
                devices.add(d);
            }
            final AdapterDevice adapterDevice = new AdapterDevice(getBaseContext(), devices);
            final ListView deviceList = ListView.class.cast(findViewById(R.id.deviceList));
            deviceList.setAdapter(adapterDevice);
        }
    }

    class AdapterDevice extends ArrayAdapter<BluetoothDevice> {
        private final List<BluetoothDevice> devices;
        private final LayoutInflater inflater;

        public AdapterDevice(final Context context, final List<BluetoothDevice> devices) {
            super(context, 0);
            this.devices = devices;
            this.inflater = LayoutInflater.class.cast(context.getSystemService(LAYOUT_INFLATER_SERVICE));
        }

        @Nullable
        @Override
        public BluetoothDevice getItem(int position) {
            return devices.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final View view = inflater.inflate(R.layout.adapter_device, parent, false);
            final BluetoothDevice device = getItem(position);
            TextView.class.cast(view.findViewById(R.id.name)).setText(device.getName());
            String uuid = ". . .";
            if (device.getUuids() != null) {
                if (null != device.getUuids()[0]) {
                    uuid = device.getUuids()[0].getUuid().toString();
                }
            }
            TextView.class.cast(view.findViewById(R.id.description)).setText(uuid);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDevice(device);
                    closeDeviceSelect();
                }
            });
            return view;
        }

        @Override
        public int getCount() {
            return devices.size();
        }
    }



    class SendAction extends Thread {

        final BluetoothDevice _device;
        private DataOutputStream os;
        int[] mValues;

        public SendAction(final BluetoothDevice mDevice, int[] values) {
            this._device = mDevice;
            this.mValues = values;
        }

        @Override
        public void run() {
            final StringBuffer buffer = new StringBuffer();
            for (int i : mValues) {
                buffer.append(i);
            }

            try {
                UUID _id = null;
                if (_device.getUuids() != null) {
                    if (null != _device.getUuids()[0]) {
                        _id = _device.getUuids()[0].getUuid();
                    }
                }
                final BluetoothSocket mSocket = _device.createInsecureRfcommSocketToServiceRecord(_id);
                mSocket.connect();
                os = new DataOutputStream(mSocket.getOutputStream());
                os.writeBytes(buffer.toString());
                os.flush();

            } catch (final Exception e1) {
                e1.printStackTrace();
            } finally {
                if (null != os) {
                    try {
                        os.close();
                    } catch (final Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

}
