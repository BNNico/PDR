public class IntShared_stub extends SharedObject implements IntShared_itf {
    public IntShared_stub(int id, Object obj) {
        super(id, obj);
    }
    
    public void write(int n) {
        IntShared i  = (IntShared) obj;
        i.write(n);

    }
    
    public int read() {
        IntShared i  = (IntShared) obj;
        return i.read();
    }
}
    

