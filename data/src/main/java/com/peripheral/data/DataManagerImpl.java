package com.peripheral.data;

import android.content.Context;
import android.util.Log;

import com.peripheral.ble.BLEDeviceInfo;
import com.peripheral.ble.DeviceFound;
import com.peripheral.ble.DeviceManager;
import com.peripheral.ble.DeviceManagerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class DataManagerImpl implements DataManager {
    final static String TAG_NAME = "DataManager";

    static private DataManager instance;
    private static DeviceManager deviceManager;
    private final DeviceFound deviceFound;
    private String targetDeviceName;
    boolean initialized = false;

    private Disposable deviceStatusDisposable;

    private final BehaviorSubject<Boolean> deviceStatus = BehaviorSubject.create();

    Map<String, BLEDeviceInfo> deviceList = new HashMap<String, BLEDeviceInfo>();
    public static DataManager getInstance(){

        if( null == instance ){
            instance = new DataManagerImpl();
        }

        return instance;
    }

    private DataManagerImpl(){
        deviceFound = new DeviceFound() {
            @Override
            public void onDeviceFound(BLEDeviceInfo deviceInfo) {
                saveScanResult( deviceInfo );

            }
        };
    }

    public void saveScanResult( BLEDeviceInfo deviceInfo ){
        if( deviceList.containsKey( deviceInfo.macAddress)){
            //Log.d(TAG_NAME, "duplicated device : " + deviceInfo.name );
        }
        else{
            Log.d(TAG_NAME, "add device to list: " + deviceInfo.name + " , " + deviceInfo.macAddress );
            deviceList.put( deviceInfo.macAddress, deviceInfo );
        }
    }

    public List<String> deviceList(){
        List<String> savedDeviceList = new ArrayList<String>();

        for( Map.Entry<String,BLEDeviceInfo> entry : deviceList.entrySet() ){
            savedDeviceList.add( entry.getValue().name + ": " + entry.getValue().macAddress);
        }

        return savedDeviceList;
    }

    public String foundDevice(String deviceName){

        for( Map.Entry<String,BLEDeviceInfo> entry : deviceList.entrySet() ){

            if( null != entry.getValue().name ){
                if( entry.getValue().name.equals(deviceName)){
                    return entry.getValue().macAddress;
                }
            }
        }

        return null;
    }


    public void updateDeviceInfo( String targetDeviceName ){
        if( null != deviceManager ){
            deviceManager.startConnect( targetDeviceName );
        }
        this.targetDeviceName = targetDeviceName;
    }

    public boolean initiate( Context context ){

        if (null == deviceManager) {
            DeviceManagerImpl.initiate(context, this.deviceFound);
            deviceManager = DeviceManagerImpl.getInstance();
        }

        deviceStatusDisposable = Flowable.interval(1, TimeUnit.SECONDS)
                .map( value ->{
                    return deviceManager.isReadyToUse();
                })
                .distinctUntilChanged()
                .subscribe( readyOrNot ->{
                    deviceStatus.onNext( readyOrNot );
                });

        initialized = true;
        return true;
    }

    public boolean isInitialized(){
        return initialized;
    }

    public Flowable<Boolean> isDeviceReady(){
        return deviceStatus.toFlowable(BackpressureStrategy.LATEST);
    }
}
