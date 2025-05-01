package com.github.onran0.medpow8.emulator;

public interface Port {
    void write(byte value);

    byte read();

    boolean hasUnreaded();
}