package jnpout32;

import java.awt.*;

public class LPTport {
    public static short datum;
    public static short Addr;
    public static pPort lpt;
    
	public native void Out32(short PortAddress, short data);
	public native short Inp32(short PortAddress);
	static {  System.loadLibrary("jnpout32pkg");}

	public LPTport(){
		lpt = new pPort();
	}
	
	public static void read()
    {
    	datum = (short) lpt.input(Addr);
    }

	public static void write()
    {
    	lpt.output(Addr,datum);
    }
	
	public static void write_data()
    {
		Addr=888;
    	lpt.output(datum);
    }
	public static void write_control()
    {
		Addr=890;
    	lpt.output(Addr,datum);
    }
	public static void read_data()
    {
		Addr=888;
		datum = (short) lpt.input(Addr);
    }
	public static void read_control()
    {
		Addr=890;
		datum = (short) lpt.input(Addr);
    }
	public static void read_status()
    {
		Addr=889;
		datum = (short) lpt.input(Addr);
    }
}
