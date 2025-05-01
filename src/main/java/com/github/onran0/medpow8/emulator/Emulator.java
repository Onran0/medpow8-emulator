package com.github.onran0.medpow8.emulator;

public class Emulator {
    public static final boolean TERMINATE_ON_HALT = Boolean.getBoolean("medpow8.terminateOnHalt");

    private final byte[] memory = new byte[256];
    private final byte[] stack = new byte[16];
    private final byte[] registers = new byte[4];
    private int stackPos;
    private int commandPos;

    private final Port[] ports = new Port[3];
    private Dial dial;
    private Display display;

    private int freqHz = 1;

    private final CommandsPipeline pipeline = new CommandsPipeline(this);

    private final boolean[] flags = new boolean[3];

    private boolean paused;

    private int selectedPort;

    private boolean running;

    public byte getRegister(int index) {
        return registers[index];
    }

    public void setRegister(int index, byte value) {
        registers[index] = value;
    }

    public byte[] getMemory() {
        return memory;
    }

    public void stackPush(byte data) {
        stack[stackPos++] = data;
    }

    public byte stackPop() {
        byte tmp = stack[stackPos];

        stack[stackPos--] = 0;

        return tmp;
    }

    public Dial getDial() {
        return dial;
    }

    public void setDial(Dial dial) {
        this.dial = dial;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public Port getPort(int index) {
        if(index >= ports.length)
            return null;
        else
            return ports[index];
    }

    public void setPort(int index, Port port) {
        ports[index] = port;
    }

    public int getSelectedPort() {
        return selectedPort;
    }

    public void setSelectedPort(int selectedPort) {
        this.selectedPort = selectedPort;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;

        if(paused && TERMINATE_ON_HALT && running)
            terminate();
    }

    public boolean hasFlag(int index) {
        return flags[index];
    }

    public void setFlag(int index, boolean value) {
        flags[index] = value;
    }

    public int getCommandPosition() {
        return commandPos;
    }

    public void setCommandPosition(int commandPos) {
        this.commandPos = commandPos;
    }

    public CommandsPipeline getPipeline() {
        return pipeline;
    }

    public int getFreqHz() {
        return freqHz;
    }

    public void setFreqHz(int freqHz) {
        this.freqHz = freqHz;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void terminate() {
        if(!isRunning())
            throw new IllegalStateException("can't terminate emulator because it is not running");

        this.running = false;
    }

    public void tick() {
        pipeline.tick();
    }

    public void startLoop() {
        this.running = true;

        try {
            while(running) {
                long tickInNs = (long) (1f/freqHz*1_000_000_000);

                long start = System.nanoTime();

                pipeline.tick();

                long toSleepNs = tickInNs-(System.nanoTime()-start);

                if(toSleepNs > 0) {
                    if(toSleepNs % 1_000_000 == 0)
                        Thread.sleep(toSleepNs / 1_000_000);
                    else {
                        long end = System.nanoTime() + toSleepNs;

                        while(end > System.nanoTime());
                    }
                }
            }
        } catch(Exception e) {
            System.err.println("Unexpected exception while emulator running: ");
            e.printStackTrace();
        }
    }
}