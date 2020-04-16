package com.peripheral.data;

import android.content.Context;

import io.reactivex.Flowable;

public interface DataManager {

    boolean isInitialized();

    boolean initiate( Context context );

    void updateDeviceInfo( String targetDeviceName );

    Flowable<Boolean> isDeviceReady();
}
