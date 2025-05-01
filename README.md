# MedPOW-8 Emulator

---

This project is an emulator of a processor on the MedPOW-8 architecture

You can upload code to it and connect ports via the
Emulator class

You can also set a higher emulator frequency,
up to ~10 MHz

## How to use the emulator?

### 1. Get the emulator

You can download the ready-made emulator jar from the [releases](https://github.com/Onran0/medpow8-emulator/releases) tab
or build it yourself (there is a point about this at the end of the
documentation)

You also need to get the assembler jar and
add it to the local repository. How to do this
can be found at the end of the documentation.

After that, add the jar to your local Maven repository, after which you can add it to **pom.xml** like this:

```xml
<dependencies>
<dependency>
<groupId>com.github.onran0.medpow8</groupId>
<artifactId>medpow8-emulator</artifactId>
</dependency>

...
</dependencies>
```

### 2. Create an instance of the Emulator class

```java
Emulator emulator = new Emulator();
```

### 3. Load the code into the emulator

You can do this via the CommandsPipeline, which can be
obtained using the getPipeline() method

You can see all possible methods for loading code,
among them are:

1. Loading machine code from a file or byte array
2. Compiling the sources into machine code and loading using
   the first method

```java
emulator.getPipeline().assembleAndAddCommands(
"""
dial 0
dial 3
hlt
"""
);

```

### 4. Set the ports

You can do this using the setDial(), setPort() and
setDisplay() methods

```java
emulator.setDial(b -> System.out.println(b & 0xFF));
```

### 5. Set the emulator refresh rate

Let's say you want to set 5 kHz

```java
emulator.setFreqHz(5000);
```

### 6. Start the emulator

```java
emulator.startLoop();
```

Starting the emulator blocks the current thread. The emulator will update
at the specified frequency until you disable it using the
terminate() method. You can also configure the system properties to automatically disable the emulator when calling `hlt` from the program code

You can enable this behavior by adding this line to the JVM arguments:

`-Dmedpow8.terminateOnHalt=true`

## Problems

At the moment, the emulator does not support commands for copying
flags to registers or memory, and does not support copying
the register of the selected port. This is due to the fact that the assembler and disassembler are currently in beta version, and do not support the above-mentioned commands themselves. Gradually, with the update of the assembler, these commands will be supported.

## How to build the emulator?

### 1. Get medpow8-assembler

You can also download the ready-made assembler jar from the
[releases](https://github.com/Onran0/medpow8-assembler/releases) tab on its repository, or build it yourself according to the
instructions in its repository.

### 2. Add the assembler jar to the local Maven repository

### 3. Clone the medpow8-emulator repository

### 4. Configure the project

Add your launch configuration and list of artifacts

### 5. Build the emulator and add it to the local Maven repository