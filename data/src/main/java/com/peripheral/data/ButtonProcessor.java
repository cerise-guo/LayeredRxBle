package com.peripheral.data;

public class ButtonProcessor implements DataProcessor {

    public byte decode( byte value[] ){
        return value[0];
    }

    public byte[] encode( byte value ){
        byte[] result = {value};
        return result;
    }
}
