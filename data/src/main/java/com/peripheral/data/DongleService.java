package com.peripheral.data;

import com.peripheral.ble.DeviceManager;

import java.util.UUID;

public class DongleService {

    final String TAG_NAME = "LEDService";

    final private LEDCharacteristic ledCharactertistic;
    final private ButtonCharacteristic btnCharactertistic;

    final String LED_SERVICE_UUID = "00001567-1212-efde-1523-785feabcd123";

    public DongleService(
            DeviceManager deviceManager,
            DataHelper dataHelper){

        if( null == deviceManager ){
            throw new NullPointerException("Null DeviceManager in LEDService ctor");
        }

        ledCharactertistic = new LEDCharacteristic(
                deviceManager, UUID.fromString(LED_SERVICE_UUID), dataHelper.getProcessor(LEDCharacteristic.class));

        btnCharactertistic = new ButtonCharacteristic(
                deviceManager, UUID.fromString(LED_SERVICE_UUID), dataHelper.getProcessor(ButtonCharacteristic.class));
    }

    public LEDCharacteristic getLEDCharacter(){
        return ledCharactertistic;
    }

    public ButtonCharacteristic getButtonCharacter() { return btnCharactertistic; }
}
