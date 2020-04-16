package com.peripheral.data;


//Note: this interface is NOT public and only accessible internally by BLE module.
interface DataProcessor {

    byte[] encode( byte value );

    byte decode( byte value[] );
}
