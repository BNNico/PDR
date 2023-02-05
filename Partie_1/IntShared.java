public class IntShared implements java.io.Serializable {
    private int obj;
    public IntShared() {
        this.obj = 0;
    }
    public void write(int n) {
        this.obj += n;
    }
    public int read() {
        return obj;
    }
}
