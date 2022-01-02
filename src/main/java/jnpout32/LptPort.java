package jnpout32;

public class LptPort {
    private static pPort lpt = new pPort();

    public static short read(short address) {
        return lpt.input(address);
    }

    public static void write(short address, short datum) {
        lpt.output(address, datum);
    }
}
