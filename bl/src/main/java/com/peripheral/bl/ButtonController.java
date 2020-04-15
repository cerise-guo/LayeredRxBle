package com.peripheral.bl;

import com.peripheral.data.ButtonCharacteristic;
import com.peripheral.data.ButtonStatusListener;
import com.peripheral.data.DeviceHelper;
import com.peripheral.logger.SimpleLogger;

import java.lang.ref.WeakReference;

public class ButtonController {
    private final String TAG_NAME = "ButtonController";

    private ButtonCharacteristic btnCharactertistic;

    private WeakReference<ButtonClickCallback> buttonStatusCallback;

    private ButtonClickCallback buttonCallback;

    public ButtonController(){
        this.btnCharactertistic = DeviceHelper.getDongleService().getButtonCharacter();
        if( null == btnCharactertistic ){
            throw new NullPointerException("Null Button Characteristic in ButtonController ctor");
        }

        btnCharactertistic.setListener(new ButtonStatusListener() {
            @Override
            public void onClick(int value) {

                SimpleLogger.getInstance().log( TAG_NAME, "button is clicked");

                if( null != buttonCallback ){
                    buttonCallback.onButtonClick( value );
                }
            }
        });
    }

    public void StopMonitorButton(){
        buttonCallback = null;
    }

    public void MonitorButton( ButtonClickCallback callback ){
        buttonCallback = callback;

        btnCharactertistic.enableNotification( true );
    }


    public interface ButtonClickCallback{
        void onButtonClick( int value );
    }
}
