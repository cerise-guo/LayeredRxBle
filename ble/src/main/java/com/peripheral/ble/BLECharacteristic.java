package com.peripheral.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

public class BLECharacteristic {

    final String TAG_NAME = "LEDCharactertistic";

    final private DeviceManager deviceManager;
    final private UUID serviceUUID;
    final private UUID charUUID;
    private ReadWriteListener listener = null;


    public BLECharacteristic(
            DeviceManager deviceManager,
            UUID serviceUUID,
            UUID characteristicUUID){
        this.deviceManager = deviceManager;
        this.serviceUUID = serviceUUID;
        this.charUUID = characteristicUUID;

        this.deviceManager.RegisterGattListener( new BleGattListenerImpl());
    }

    private BLECharacteristic(){
        //should not be called.
        deviceManager = null;
        serviceUUID = null;
        listener = null;
        charUUID = null;
        throw new RuntimeException("this ctor doesn't suppose to be called");
    }

    public void setListener( ReadWriteListener listener ){
        this.listener = listener;
    }

    public boolean write( byte[] value ){
        Log.d(TAG_NAME, "Turn off LED");

        deviceManager.write( serviceUUID, charUUID, value);

        return true;
    }

    public boolean read(){
        deviceManager.read(serviceUUID, charUUID);

        return true;
    }

    public boolean enableNotification( boolean enable ){
        deviceManager.enableNotification( serviceUUID, charUUID, enable);

        return true;
    }

    private class BleGattListenerImpl implements BleGattListener{

        @Override
        public void onReadResult(final BluetoothGattCharacteristic characteristic, int status) {
            if( null != listener && isThisCharactertistic(characteristic)){
                listener.onRead( isSuccess( status),
                        isSuccess(status)? characteristic.getValue():null);
            }
        }

        @Override
        public void onWriteResult(final BluetoothGattCharacteristic characteristic, int status) {
            if( null != listener && isThisCharactertistic(characteristic)){
                listener.onWrite( isSuccess(status));
            }
        }

        @Override
        public void onChanged(final BluetoothGattCharacteristic characteristic) {
            if( null != listener && isThisCharactertistic(characteristic)){
                listener.onUpdate( characteristic.getValue());
            }
        }

        @Override
        public void onDescriptorWrite( final BluetoothGattDescriptor descriptor, int status) {
            if( null != listener && isThisCharactertistic(descriptor)){
                listener.onDescriptorWrite(isSuccess(status));
            }
        }
    }

    boolean isSuccess( final int status ){
        return BluetoothGatt.GATT_SUCCESS == status;
    }

    boolean isThisCharactertistic(final BluetoothGattCharacteristic characteristic ){
        if( null != characteristic && null != characteristic.getService() ){
            if( characteristic.getUuid().equals( this.charUUID ) &&
                    characteristic.getService().getUuid().equals( this.serviceUUID )){
                return true;
            }
        }

        return false;
    }

    boolean isThisCharactertistic(final BluetoothGattDescriptor descriptor ){
        if( null != descriptor && null != descriptor.getCharacteristic() ){
            if( descriptor.getCharacteristic().getUuid().equals( this.charUUID )){
                return true;
            }
        }

        return false;
    }
}
