package com.github.onran0.medpow8.emulator.test;

import com.github.onran0.medpow8.assembler.AssemblyException;
import com.github.onran0.medpow8.disassembler.DisassemblyException;
import com.github.onran0.medpow8.emulator.Emulator;
import com.github.onran0.medpow8.util.IO;

import java.io.*;

public final class Test {
    public static void main(final String[] args) throws DisassemblyException, AssemblyException, IOException {
        Emulator emulator = new Emulator();

        FileInputStream fin;

        emulator.getPipeline().assembleAndAddCommands(IO.readStream(
                fin = new FileInputStream("src/test/asm/program.asm")
        ));

        fin.close();

        emulator.setDial(b -> System.out.println(b & 0xFF));

        emulator.setFreqHz(5000);

        emulator.startLoop();
    }
}