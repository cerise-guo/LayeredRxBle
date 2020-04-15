package com.peripheral.ble;

import android.support.annotation.NonNull;

import com.peripheral.logger.SimpleLogger;

public class ReadWriteListenerImpl implements ReadWriteListener {

    final static String TAG_NAME = "ReadWriteListenerImpl";

    @Override
    public void onRead(boolean success, @NonNull byte[] value) {
        SimpleLogger.getInstance().log(TAG_NAME, "onRead default");
    }

    @Override
    public void onWrite(boolean success) {
        SimpleLogger.getInstance().log(TAG_NAME, "onWrite default");
    }

    @Override
    public void onUpdate(byte[] value) {
        SimpleLogger.getInstance().log(TAG_NAME, "onUpdate default");
    }

    @Override
    public void onDescriptorWrite(boolean success) {
        SimpleLogger.getInstance().log(TAG_NAME, "onDescription default");
    }
}
