package com.peripheral.ble;

import android.support.annotation.NonNull;

import java.util.UUID;

public interface DeviceManager {

    //boolean initiate(Context context);

    void write(UUID serviceUUID, UUID characteristicUUID, byte[] value);

    void read(UUID serviceUUID, UUID characteristicUUID);

    void enableNotification( UUID serviceUUID,
                             UUID characteristicUUID,
                             boolean enable);

    //boolean isConnected();

    boolean isReadyToUse();

    void startConnect( String targetDeviceName ); //ToDo: needs return value to return immediate result

    void RegisterGattListener( @NonNull BleGattListener gattListener );
}
