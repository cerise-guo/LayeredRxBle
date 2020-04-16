package com.peripheral.bl;


import android.util.Log;

import com.peripheral.data.DeviceHelper;
import com.peripheral.data.LEDCharacteristic;
//import com.peripheral.data.LEDStatusListener;
import com.peripheral.logger.SimpleLogger;

//import java.lang.ref.WeakReference;

//import io.reactivex.Single;
//import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class LEDController {

    private final String TAG_NAME = "LEDController";

    private final LEDCharacteristic ledCharactertistic;

    Disposable ledDisposable = null;

    public LEDController(){
        this.ledCharactertistic = DeviceHelper.getDongleService().getLEDCharacter();
        if( null == ledCharactertistic ){
            throw new NullPointerException("Null LEDCharacteristic in LEDController ctor");
        }
    }

    public void TurnOnLED( Boolean OnOrOff ){

        final byte value = (byte)(OnOrOff?1:0);

        if( ledDisposable != null && !ledDisposable.isDisposed()){
            throw new RuntimeException("LED Controller get led status overlapped.");
        }

        ledDisposable = ledCharactertistic.writeValue(value).doOnSubscribe( disposable -> {
            Log.d(TAG_NAME,"subscribe led write result");
        }).doOnError( throwable -> {
            Log.d(TAG_NAME,"subscribe led write exception: " + throwable );
         }
        ).subscribe( result ->{
            SimpleLogger.getInstance().log(TAG_NAME, "RX LED write : " + result );
            if( null != ledDisposable ) {
                ledDisposable.dispose();
                ledDisposable = null;
            }
        });
    }

    public void getLEDStatus( LEDStatusCallback callback ){

        if( ledDisposable != null && !ledDisposable.isDisposed()){
            throw new RuntimeException("LED Controller get led status overlapped.");
        }

        ledDisposable = ledCharactertistic.readValue().doOnSubscribe( disposable -> {
            Log.d(TAG_NAME,"subscribe led status");
        }).subscribe( value->{
            SimpleLogger.getInstance().log(TAG_NAME, "RX LED value: " + value );
            callback.ledStatus( value );
            if( null != ledDisposable ) {
                ledDisposable.dispose();
                ledDisposable = null;
            }
        });
    }

    public interface LEDStatusCallback{
        void ledStatus( boolean OnOrOff );
    }
}
