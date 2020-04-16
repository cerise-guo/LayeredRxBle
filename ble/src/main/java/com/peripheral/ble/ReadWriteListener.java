package com.peripheral.ble;

import androidx.annotation.NonNull;

//Note: this interface is NOT public and only accessible internally by BLE module.
interface ReadWriteListener {
    void onRead( boolean success, @NonNull final byte[] value );

    void onWrite( boolean success );

    void onUpdate( final byte[] value );

    void onDescriptorWrite( boolean success );
}
