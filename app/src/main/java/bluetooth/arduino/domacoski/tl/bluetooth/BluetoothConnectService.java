package bluetooth.arduino.domacoski.tl.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by thiago.domacoski on 29/08/17.
 */

public class BluetoothConnectService {

    private final BluetoothDevice mDevice;
    private UUID mUUID = null;
    final Context mContext;
    volatile SocketBluetooth mSocketBluetooth = null;

    public BluetoothConnectService(final BluetoothDevice device, final Context context){
        this.mContext = context;
        mDevice = device;
        if(null != mDevice){
            if(null != mDevice.getUuids()){
                if(null !=mDevice.getUuids()[0]){
                    mUUID = mDevice.getUuids()[0].getUuid();
                }
            }
        }
        if(null == mUUID){
            mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        }
    }

    public void initialize(){
        synchronized (this){
            mSocketBluetooth = new SocketBluetooth();
            mSocketBluetooth.start();
        }
    }

    public void finalize(){
        if(null == mSocketBluetooth){
            return;
        }
        mSocketBluetooth.setAlive(false);
        mSocketBluetooth.interrupt();
    }


    class SocketBluetooth extends Thread {
        volatile boolean mAlive = true;
        private DataOutputStream mOutput;
        private InputStream mInput;

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted() & mAlive){
            }
        }

        public void setAlive(boolean alive) {
            synchronized (this){
                this.mAlive = alive;
            }


        }
    }

    static class BluetoohReceiver extends BroadcastReceiver{
        private final BluetoohReceiverUpdate update;
        public BluetoohReceiver(final BluetoohReceiverUpdate update){
            super();
            this.update= update;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if(null == update){
                return;
            }
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        update.stateOff();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        update.stateTurningOff();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        update.stateOn();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        update.stateTurningOn();
                        break;
                }
            }
        }

    }

    static interface BluetoohReceiverUpdate{
        public void stateOff();
        public void stateTurningOff();
        public void stateOn();
        public void stateTurningOn();
    }


}
