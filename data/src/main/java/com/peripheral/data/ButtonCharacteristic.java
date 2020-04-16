package com.peripheral.data;

import android.util.Log;

import com.peripheral.ble.BLECharacteristic;
import com.peripheral.ble.DeviceManager;
import com.peripheral.ble.ReadWriteListenerImpl;
import com.peripheral.logger.SimpleLogger;

import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class ButtonCharacteristic {

    final String TAG_NAME = "ButtonCharacteristic";

    final private static UUID BUTTON_CHAR_UUID = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
    final private DataProcessor dataProcessor;

    final BLECharacteristic bleCharacteristic;

    public ButtonCharacteristic(
            DeviceManager deviceManager,
            UUID serviceUUID,
            DataProcessor processor
    ){
        this.bleCharacteristic = DataHelper.buildCharacteristic(
                deviceManager, serviceUUID, BUTTON_CHAR_UUID );
        this.dataProcessor = processor;
    }

    public Single<Boolean> enableNotification(boolean enable ){

        return bleCharacteristic.rxWriteDescriptor( enable ).map( result -> {

            if( result ){
                SimpleLogger.getInstance().log(TAG_NAME, "set notification now");
                return bleCharacteristic.enableNotification( enable );
            }

            return false;
        });
    }

    public Flowable<Integer> monitorValue(){
        return bleCharacteristic.rxObserve().map( value ->{
            return new Integer( (int)dataProcessor.decode( value ));
        });
    }
}
