package com.peripheral.bl;

import com.peripheral.data.ButtonCharacteristic;
import com.peripheral.data.DeviceHelper;
import com.peripheral.logger.SimpleLogger;

import io.reactivex.disposables.Disposable;

public class ButtonController {
    private final String TAG_NAME = "ButtonController";

    private ButtonCharacteristic btnCharactertistic;

    Disposable buttonDisposable = null;

    public ButtonController(){
        this.btnCharactertistic = DeviceHelper.getDongleService().getButtonCharacter();
        if( null == btnCharactertistic ){
            throw new NullPointerException("Null Button Characteristic in ButtonController ctor");
        }
    }

    Disposable buttonValueDisposable;

    public void MonitorButtonClick( ButtonClickCallback callback ){

        if( buttonDisposable != null && !buttonDisposable.isDisposed()){
            throw new RuntimeException("Button Controller get led status overlapped.");
        }

        if (null != buttonValueDisposable) {
            buttonDisposable.dispose();
            SimpleLogger.getInstance().log(TAG_NAME, "disposed previous button observer.");
        }

        buttonValueDisposable = btnCharactertistic.monitorValue().subscribe( value->{
           SimpleLogger.getInstance().log(TAG_NAME, "button value: " + value.intValue());
           callback.onButtonClick( value );
        });

        buttonDisposable = btnCharactertistic.enableNotification( true )
                .doOnSubscribe( disposable -> {
            SimpleLogger.getInstance().log(TAG_NAME,"subscribe button notification result");
        }).doOnError( throwable -> {
                    SimpleLogger.getInstance().log(TAG_NAME,"subscribe button notification exception: " + throwable );
                }
        ).subscribe( result ->{
            SimpleLogger.getInstance().log(TAG_NAME, "RX Button notification : " + result );
            if( null != buttonDisposable ) {
                buttonDisposable.dispose();
                buttonDisposable = null;
            }
        });
    }

    public interface ButtonClickCallback{
        void onButtonClick( int value );
    }
}
