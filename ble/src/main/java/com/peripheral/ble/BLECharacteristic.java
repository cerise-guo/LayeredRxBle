package com.peripheral.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import androidx.annotation.NonNull;
import android.util.Log;

import com.peripheral.logger.SimpleLogger;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class BLECharacteristic {

    final String TAG_NAME = "LEDCharactertistic";

    final private DeviceManager deviceManager;
    final private UUID serviceUUID;
    final private UUID charUUID;

    private List<ReadWriteListener> rxListeners = new CopyOnWriteArrayList<>();


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
        charUUID = null;
        throw new RuntimeException("this ctor doesn't suppose to be called");
    }

    public Flowable<byte[]> rxObserve(){
        return Flowable.create( emitter -> {
            ReadWriteListener listener = new ReadWriteListenerImpl() {
                @Override
                public void onUpdate(byte[] value) {
                    SimpleLogger.getInstance().log(TAG_NAME, "onUpdate rx observer");

                    if( !emitter.isCancelled()){
                        emitter.onNext( value );
                    }
                }
            };

            emitter.setCancellable(()->{ removeRxListener(listener);});
            addRxListener(listener);

        }, BackpressureStrategy.BUFFER);
    }

    public Single<Boolean> rxWriteDescriptor( boolean enableNotification ){

        return Single.create( emitter -> {
            ReadWriteListener listener = new ReadWriteListenerImpl() {
                @Override
                public void onDescriptorWrite(boolean success) {
                    emitter.onSuccess( success);
                }
            };

            emitter.setCancellable(()-> removeRxListener(listener));
            addRxListener(listener);

            final byte[] values = enableNotification?BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE:
                                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;

            deviceManager.writeDescriptor( serviceUUID, charUUID, values);
        });
    }

    public Single<Boolean>  rxWrite( byte[] value ){

        return Single.create( emitter -> {

            ReadWriteListener listener = new ReadWriteListenerImpl(){
              @Override
              public void onWrite( boolean success ){
                  SimpleLogger.getInstance().log(TAG_NAME, "onWrite");

                  emitter.onSuccess( success);
              }
            };

            emitter.setCancellable(()-> removeRxListener(listener));
            addRxListener(listener);

            write( value );
        });
    }

    public Single<byte[]> rxRead(){

        return Single.create( emitter ->{

            ReadWriteListener listener = new ReadWriteListenerImpl() {
                @Override
                public void onRead(boolean success, @NonNull byte[] value) {
                    SimpleLogger.getInstance().log(TAG_NAME, "onRead");

                    if( success ){
                        emitter.onSuccess( value );
                    }else{
                        emitter.onError( new Throwable("Failed to read :" + charUUID));
                    }
                }
            };

            emitter.setCancellable(() -> removeRxListener(listener));
            addRxListener(listener);

            read();
        });
    }

    void addRxListener( @NonNull final ReadWriteListener listener ){
        rxListeners.add( listener);
    }
    void removeRxListener( @NonNull final ReadWriteListener listener){
        rxListeners.remove(listener);
    }

    private boolean read() //ToDo: this function could throw exception if native ble call gets error.
    {
        deviceManager.read(serviceUUID, charUUID);

        return true;
    }

    private boolean write( byte[] value ){
        Log.d(TAG_NAME, "Turn off LED");

        deviceManager.write( serviceUUID, charUUID, value);

        return true;
    }

    public boolean enableNotification( boolean enable ){

        try {
            deviceManager.enableNotification(serviceUUID, charUUID, enable);
        }
        catch (Exception e){
            SimpleLogger.getInstance().log(TAG_NAME, "failed to enable notification: " +
                    serviceUUID + " , " + charUUID + ", " + enable);
            return false;
        }

        return true;
    }

    private class BleGattListenerImpl implements BleGattListener{

        @Override
        public void onReadResult(final BluetoothGattCharacteristic characteristic, int status) {

            if( 0 < rxListeners.size() && isThisCharactertistic(characteristic)){
                for( final ReadWriteListener listener: rxListeners){
                    listener.onRead( isSuccess( status),
                            isSuccess(status)? characteristic.getValue():null);
                }
            }
        }

        @Override
        public void onWriteResult(final BluetoothGattCharacteristic characteristic, int status) {

            if( 0 < rxListeners.size() && isThisCharactertistic(characteristic)){
                for( final ReadWriteListener listener: rxListeners){
                    listener.onWrite( isSuccess( status));
                }
            }
        }

        @Override
        public void onChanged(final BluetoothGattCharacteristic characteristic) {
            if( 0 < rxListeners.size() && isThisCharactertistic(characteristic)){
                for( final ReadWriteListener listener: rxListeners){
                    listener.onUpdate( characteristic.getValue());
                }
            }
        }

        @Override
        public void onDescriptorWrite( final BluetoothGattDescriptor descriptor, int status) {

            if( 0 < rxListeners.size() && isThisCharactertistic(descriptor)){
                for( final ReadWriteListener listener: rxListeners){
                    listener.onDescriptorWrite( isSuccess( status));
                }
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
