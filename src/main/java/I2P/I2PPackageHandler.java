package I2P;

import java.util.concurrent.ConcurrentHashMap;

//i2p package handler
public class I2PPackageHandler implements Runnable{

    //streams
    private ConcurrentHashMap<Integer,I2PStream> map;

    //i2p classifier
    private I2PClassifier i2PClassifier;

    //package info
    private byte[] package_info;

    public I2PPackageHandler(ConcurrentHashMap<Integer, I2PStream> map, I2PClassifier i2PClassifier, byte[] package_info) {
        this.map = map;
        this.i2PClassifier = i2PClassifier;
        this.package_info = package_info;
    }

    @Override
    public void run() {
        String[] info = new String(package_info).split(",");
        I2PPackage pack = new I2PPackage(info[0], info[1], info[2], info[3], Integer.parseInt(info[4]));
        int pack_id = Integer.parseInt(info[4]);
        if (!map.containsKey(pack_id)) {
            I2PStream i2pStream = new I2PStream();
            i2pStream.getConcurrentLinkedQueue().add(pack);
            map.put(pack_id, i2pStream);
        }else {
            int num = map.get(pack_id).addAndIncrease(pack);

        }
    }
}
