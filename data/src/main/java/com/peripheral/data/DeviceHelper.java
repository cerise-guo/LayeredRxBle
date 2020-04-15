package com.peripheral.data;

import com.peripheral.ble.DeviceManager;
import com.peripheral.ble.DeviceManagerImpl;

public class DeviceHelper {

    private static DeviceManager getDeviceManager(){

        DeviceManager deviceManager = DeviceManagerImpl.getInstance();
        return deviceManager;
    }

    //ToDo: singleton or inject
    final static private DataHelper dataHelper = new DataHelper();

    private static class DongleServiceSingleton{
        private static final DongleService dongleService = new DongleService( getDeviceManager(), dataHelper );
    }
    public static DongleService getDongleService(){
        return DongleServiceSingleton.dongleService;
    }

}
