import jnpout32.LPTport;


class LPT_work {
    public LPTport lpt=null;

    public LPT_work(){
            lpt=new LPTport();
    }
    public void write(short port,byte data){
            lpt.Addr=port;
            lpt.datum=data;
            lpt.write();
    }
    public short read(short port){
        short data=0;
            lpt.Addr=port;
            lpt.read();
            data=lpt.datum;
        return data;
    }
}
