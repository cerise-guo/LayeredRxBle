package com.peripheral.bl;

import java.util.function.Consumer;

public interface StateProvider {

    void registerListener( Consumer<String> listener );
}
