package com.peripheral.data;

import android.util.Log;

import com.peripheral.ble.BLECharacteristic;
import com.peripheral.ble.DeviceManager;
import com.peripheral.ble.ReadWriteListenerImpl;
import com.peripheral.logger.SimpleLogger;

import java.util.UUID;

public class ButtonCharacteristic {

    final String TAG_NAME = "ButtonCharacteristic";

    final private static UUID BUTTON_CHAR_UUID = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
    final private DataProcessor dataProcessor;

    //This listener is to return LED status to caller layer, e.g. business logic and/or app layer.
    private ButtonStatusListener buttonStatusListener;

    final BLECharacteristic bleCharacteristic;

    public ButtonCharacteristic(
            DeviceManager deviceManager,
            UUID serviceUUID,
            DataProcessor processor
    ){
        this.bleCharacteristic = DataHelper.buildCharacteristic(
                deviceManager, serviceUUID, BUTTON_CHAR_UUID );
        this.dataProcessor = processor;
        this.bleCharacteristic.setListener( new ButtonListener() );

    }

    public void enableNotification( boolean enable ){
        bleCharacteristic.enableNotification( enable );
    }

    public void setListener( ButtonStatusListener listener ){
        if(null != this.buttonStatusListener){
            throw new RuntimeException( "shouldn't set listener twice for ButtonCharacteristic");
        }
        this.buttonStatusListener = listener;
    }

    private class ButtonListener extends ReadWriteListenerImpl {
        /*@Override
        public void onRead(boolean success, @NonNull byte[] value) {

            byte val = (null == value)?0:value[0];
            Log.d(TAG_NAME, "onRead : " + success + " : " + val );

            if( null != buttonStatusListener ) {
                //ToDo: will this block further call ?
                buttonStatusListener.onClick( dataProcessor.decode(value) );
            }
        }*/

        @Override
        public void onUpdate(byte[] value) {
            Log.d(TAG_NAME, "onUpdate : " + value[0] );
        }

        @Override
        public void onDescriptorWrite(boolean success) {
            SimpleLogger.getInstance().log(TAG_NAME, "onDescription default");
        }

    }
}
