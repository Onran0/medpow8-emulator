package com.github.onran0.medpow8.emulator;

import com.github.onran0.medpow8.assembler.Assembler;
import com.github.onran0.medpow8.assembler.AssemblyException;
import com.github.onran0.medpow8.assembler.Command;
import com.github.onran0.medpow8.disassembler.Disassembler;
import com.github.onran0.medpow8.disassembler.DisassemblyException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public final class CommandsPipeline {
    private final Processor processor;
    private final Emulator emulator;
    private final List<Command> commands = new ArrayList<>();

    public CommandsPipeline(final Emulator emulator) {
        this.emulator = emulator;
        this.processor = new Processor(emulator);
    }

    public Processor getProcessor() {
        return processor;
    }

    public void addCommand(final Command command) {
        commands.add(command);
    }

    public void addCommands(final Collection<Command> commands) {
        this.commands.addAll(commands);
    }

    public void assembleAndAddCommands(String source) throws AssemblyException, DisassemblyException {
        disassembleAndAddCommands(Assembler.assemble(source));
    }

    public void disassembleAndAddCommands(String binaryPath) throws DisassemblyException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        FileInputStream in = new FileInputStream(binaryPath);

        int len;

        while((len = in.read()) != -1)
            out.write(len);

        in.close();

        disassembleAndAddCommands(out.toByteArray());
    }

    public void disassembleAndAddCommands(byte[] machineCode) throws DisassemblyException {
        addCommands(Disassembler.disassembleToList(machineCode));
    }

    public void tick() {
        if(commands.isEmpty() || emulator.isPaused())
            return;

        int pos = emulator.getCommandPosition();

        emulator.setCommandPosition(pos + 1);

        if(pos < commands.size())
            processor.processCommand(commands.get(pos));
    }
}