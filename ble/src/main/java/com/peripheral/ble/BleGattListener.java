package com.peripheral.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public interface BleGattListener {
    void onReadResult(final BluetoothGattCharacteristic characteristic, int status );

    void onWriteResult(final BluetoothGattCharacteristic characteristic, int status );

    void onChanged( final BluetoothGattCharacteristic characteristic );

    void onDescriptorWrite(final BluetoothGattDescriptor descriptor, int status );
}
