package com.peripheral.data;

import androidx.annotation.NonNull;
import android.util.Log;

import com.peripheral.ble.BLECharacteristic;
import com.peripheral.ble.DeviceManager;
import com.peripheral.ble.ReadWriteListenerImpl;

import java.util.UUID;

import io.reactivex.Single;

public class LEDCharacteristic{ //extends BLECharacteristic {

    final String TAG_NAME = "LEDCharacteristic";

    final private static UUID LED_CHAR_UUID = UUID.fromString("00001525-1212-efde-1523-785feabcd123");
    final private DataProcessor dataProcessor;

    //This listener is to return LED status to caller layer, e.g. business logic and/or app layer.
    //private LEDStatusListener ledStatusListener;

    final BLECharacteristic bleCharacteristic;

    public LEDCharacteristic(
            DeviceManager deviceManager,
            UUID serviceUUID,
            DataProcessor processor
    ){
        this.bleCharacteristic = DataHelper.buildCharacteristic(
                deviceManager, serviceUUID, LED_CHAR_UUID );
        this.dataProcessor = processor;
    }

    public Single<Boolean> writeValue( byte value ){
        return bleCharacteristic.rxWrite( dataProcessor.encode(value));
    }

    public Single<Boolean> readValue(){
        return bleCharacteristic.rxRead().map( rawValue ->{
                    return 0 != dataProcessor.decode(rawValue); }
                );
    }
}
