package bluetooth.arduino.domacoski.tl.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Integer ENABLE_BLUETOOTH = 212;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoohReceiver mBluetoohReceiver;
    private BluetoothDevice mDevice = null;
    private DeviceSelect mDeviceSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoohReceiver = new BluetoohReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoohReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mBluetoohReceiver){
            unregisterReceiver(mBluetoohReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mBluetoothAdapter.isEnabled()){
            enableBluetooh();
        }else{
            if(null == mDevice){
                openDeviceSelect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ENABLE_BLUETOOTH){
            if(resultCode == RESULT_OK){
                openDeviceSelect();
            }else{
                Toast.makeText(this, "O aplicativo só funciona com o Bluetooth Ligado!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void openDeviceSelect(){
        if(null != mDeviceSelect){
            if(mDeviceSelect.isShowing()){
                return;
            }
            mDeviceSelect = null;
        }

        mDeviceSelect = new DeviceSelect(this);
        mDeviceSelect.show();
    }
    public void closeDeviceSelect(){
        if(null != mDeviceSelect){
            if(mDeviceSelect.isShowing()){
                mDeviceSelect.dismiss();
            }
        }
    }

    public void setDevice(final BluetoothDevice mDevice){
        this.mDevice = mDevice;
        updateUIFromDevice();
    }

    private void updateUIFromDevice(){
        if(null == mDevice){
            setTitle("Nenhum dispositivo");
        }else{
            setTitle(mDevice.getName());
        }

    }

    public void enableBluetooh(){
        final Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(turnOn, ENABLE_BLUETOOTH);
    }

    public class BluetoohReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        enableBluetooh();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        setDevice(null);
                        Toast.makeText(context, "Desligando Bluetooth...", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "Bluetooth online!", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(context, "Aguarde...", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    class DeviceSelect extends Dialog{
        public DeviceSelect(@NonNull Context context) {
            super(context);
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            setContentView(R.layout.dialog_device);
            final List<BluetoothDevice> devices = new ArrayList<>();
            for(final BluetoothDevice d : mBluetoothAdapter.getBondedDevices()){
                devices.add(d);
            }
            final AdapterDevice adapterDevice = new AdapterDevice(getBaseContext(), devices);
            final ListView deviceList = ListView.class.cast(findViewById(R.id.deviceList));
            deviceList.setAdapter(adapterDevice);
        }
    }
    class AdapterDevice extends ArrayAdapter<BluetoothDevice>{
        private final List<BluetoothDevice> devices;
        private final LayoutInflater inflater;
        public AdapterDevice(final Context context, final List<BluetoothDevice> devices){
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
            if(device.getUuids() != null){
                if(null != device.getUuids()[0]){
                    uuid = device.getUuids()[0].getUuid().toString();
                }
            }
            TextView.class.cast(view.findViewById(R.id.description)).setText(uuid);
            view.setOnClickListener(new View.OnClickListener(){
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


}
