package com.github.onran0.medpow8.emulator;

import com.github.onran0.medpow8.assembler.Command;
import com.github.onran0.medpow8.assembler.Operand;

public class Processor {
    private final Emulator emulator;

    public Processor(final Emulator emulator) {
        this.emulator = emulator;
    }

    private byte getValue(Operand op) {
        byte value;

        if(op.isRegister()) value = emulator.getRegister(op.getIntValue());
        else value = (byte) op.getIntValue();

        if(op.isPointer())
            value = emulator.getMemory()[value & 0xFF];

        return value;
    }

    private void mov(Operand from, Operand to) {
        assert from != null && to != null;

        mov(getValue(from), to);
    }

    private void mov(byte value, Operand dest) {
        assert dest != null;

        if(dest.isPointer()) {
            int addr = getValue(dest) & 0xFF;
            emulator.getMemory()[addr] = value;
        } else emulator.setRegister(dest.getIntValue(), value);
    }

    public void processCommand(Command command) {
        Operand f = !command.getOperands().isEmpty() ? command.getOperands().get(0) : null;
        Operand s = command.getOperands().size() >= 2 ? command.getOperands().get(1) : null;

        byte fv = f != null ? getValue(f) : 0;
        byte sv = s != null ? getValue(s) : 0;

        Port port = emulator.getPort(fv & 0xFF);

        switch(command.getType()) {
            case NOP: break;

            case HLT:
                emulator.setPaused(true);
                break;

            case MOV:
                mov(f, s);
                break;

            case ADD: mov((byte) (fv+sv), s); break;

            case SUB: mov((byte) (fv-sv), s); break;

            case MUL: mov((byte) (fv*sv), s); break;

            case DIV: mov((byte) (fv/sv), s); break;

            case MOD: mov((byte) (fv%sv), s); break;

            case NOT: mov((byte) ~fv, s == null ? f : s); break;

            case OR: mov((byte) (fv | sv), s); break;

            case AND: mov((byte) (fv & sv), s); break;

            case XOR: mov((byte) (fv ^ sv), s); break;

            case LSH: mov((byte) (fv << sv), s); break;

            case RSH: mov((byte) (fv >> sv), s); break;

            case SUBL: mov((byte) (fv-sv), f); break;

            case DIVL: mov((byte) (fv/sv), f); break;

            case MODL: mov((byte) (fv%sv), f); break;

            case LSHL: mov((byte) (fv << sv), f); break;

            case RSHL: mov((byte) (fv >> sv), f); break;

            case CMP:
                emulator.setFlag(0, fv == sv);
                emulator.setFlag(1, (fv & 0xFF) < (sv & 0xFF));
                emulator.setFlag(2, (fv & 0xFF) > (sv & 0xFF));
                break;

            case JMP: emulator.setCommandPosition(fv & 0xFF); break;
            case JE: if(emulator.hasFlag(0)) emulator.setCommandPosition(fv & 0xFF); break;
            case JL: if(emulator.hasFlag(1)) emulator.setCommandPosition(fv & 0xFF); break;
            case JH: if(emulator.hasFlag(2)) emulator.setCommandPosition(fv & 0xFF); break;
            case JEL: if(emulator.hasFlag(0) || emulator.hasFlag(1)) emulator.setCommandPosition(fv & 0xFF); break;
            case JEH: if(emulator.hasFlag(0) || emulator.hasFlag(2)) emulator.setCommandPosition(fv & 0xFF); break;

            case PUSH: emulator.stackPush(fv); break;
            case POP: mov(emulator.stackPop(), f); break;

            case DIAL:
                if(emulator.getDial() != null)
                    emulator.getDial().set(fv);
                break;

            case DISPL:
                if(emulator.getDisplay() != null)
                    emulator.getDisplay().setLine(fv, sv);
                break;

            case CPORT:
                emulator.setSelectedPort(fv & 0xFF);
                break;

            case WPORT:
                if(port != null)
                    port.write(fv);
                break;

            case RPORT:
                if(port != null)
                    mov(port.read(), f);
                break;

            case IPORT:
                if(port != null)
                    emulator.setFlag(0, port.hasUnreaded());
                else
                    emulator.setFlag(0, false);
                break;
        }
    }
}