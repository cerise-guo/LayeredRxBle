package com.peripheral.bl;

import java.util.function.Consumer;

public class StateProviderImpl implements StateProvider {

    private static StateProviderImpl instance;

    Consumer<String> listener;

    @Override
    public void registerListener(Consumer<String> newListener) {
        if( this.listener != null ){
            throw new RuntimeException("set state listener again");
        }

        this.listener = newListener;
        this.listener.accept("here is the first message");
    }

    static public StateProvider getInstance(){

        if( null == instance ){
            instance = new StateProviderImpl();
        }
        return instance;
    }
}
