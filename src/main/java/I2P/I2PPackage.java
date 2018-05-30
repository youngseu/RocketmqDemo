package I2P;

//delegate every package
public class I2PPackage {
    private String srcip;
    private String dstip;
    private String srcport;
    private String dstport;
    private int packagelen;

    public I2PPackage(String srcip, String dstip, String srcport, String dstport, int packagelen) {
        this.srcip = srcip;
        this.dstip = dstip;
        this.srcport = srcport;
        this.dstport = dstport;
        this.packagelen = packagelen;
    }

    public String getSrcip() {
        return srcip;
    }

    public void setSrcip(String srcip) {
        this.srcip = srcip;
    }

    public String getDstip() {
        return dstip;
    }

    public void setDstip(String dstip) {
        this.dstip = dstip;
    }

    public String getSrcport() {
        return srcport;
    }

    public void setSrcport(String srcport) {
        this.srcport = srcport;
    }

    public String getDstport() {
        return dstport;
    }

    public void setDstport(String dstport) {
        this.dstport = dstport;
    }

    public int getPackagelen() {
        return packagelen;
    }

    public void setPackagelen(int packagelen) {
        this.packagelen = packagelen;
    }
}
