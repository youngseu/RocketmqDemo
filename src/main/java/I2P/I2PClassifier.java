package I2P;

import Config.CONFIG;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

//i2p streams classifier
public class I2PClassifier {
    // weka classifier
    private Classifier classifier;

    //attribute
    ArrayList<Attribute> attributes = new ArrayList<Attribute>(8);

    public I2PClassifier() {
        /**
         * classifier
         */
        classifier = new J48();
        // read train file
        File inputFile = new File("Weka/newtraintcp.arff");
        ArffLoader atf = new ArffLoader();
        try {
            atf.setFile(inputFile);
            Instances instancesTrain = atf.getDataSet();
            instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
            // train
            classifier.buildClassifier(instancesTrain);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.attributes.add(new Attribute("avg_payload_len"));
        this.attributes.add(new Attribute("max_payload_len"));
        this.attributes.add(new Attribute("fragment_ratio"));
        this.attributes.add(new Attribute("entopy_len"));
        this.attributes.add(new Attribute("variance"));
        this.attributes.add(new Attribute("seq_exist"));
        ArrayList<String> judge = new ArrayList<String>(2);
        judge.add("yes");
        judge.add("no");
        this.attributes.add(new Attribute("isI2P", judge));
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public boolean classifier(ConcurrentLinkedQueue<I2PPackage> concurrentLinkedQueue) {
        Iterator<I2PPackage> iterator = concurrentLinkedQueue.iterator();
        // avg_payload_len
        double total_payload_len = 0;
        double avg_payload_len = 0;

        // max_payload_len
        int max_payload_len = 0;

        // fragment_ratio
        double fragment_ratio = 0;
        double load_packet_num = 0;

        // entopy_len
        double entopy_len = 0;

        // variance
        double variance = 0;

        // seq_exist
        int seq_exit = 0;
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();

        int count = 0;
        int t_ord = 0;

        while (iterator.hasNext()) {
            I2PPackage i2pPackage = iterator.next();

            int packetlen = i2pPackage.getPackagelen();
            total_payload_len += packetlen;
            if (packetlen > 0) load_packet_num++;

            // max
            max_payload_len = Math.max(max_payload_len, packetlen);

            // each packagelen sum
            if (hashMap.containsKey(packetlen)) {
                // exist
                hashMap.put(packetlen, hashMap.get(packetlen) + 1);
            } else {
                // not
                hashMap.put(packetlen, 1);
            }

            if (count <= 100) {
                if (i2pPackage.getPackagelen() == 288 && t_ord == 0)
                    t_ord++;
                if (i2pPackage.getPackagelen() == 304 && t_ord == 1)
                    t_ord++;
                if (i2pPackage.getPackagelen() >= 448 && i2pPackage.getPackagelen() % 16 == 0 && t_ord == 2)
                    t_ord++;
                if (i2pPackage.getPackagelen() >= 48 && i2pPackage.getPackagelen() % 16 == 0 && t_ord == 3)
                    t_ord++;
            }
            count++;
        }

        // seq judge
        if (t_ord == 4) {
            seq_exit = 1;
        } else {
            seq_exit = 0;
        }

        // avg_payload_len
        avg_payload_len = (double) total_payload_len / load_packet_num;

        // count entpy
        for (Integer temp_count : hashMap.values()) {
            double temp = (double) temp_count / CONFIG.SLIDING_WINDOW_SIZE;
            entopy_len -= temp * Math.log(temp) / Math.log(2.0);
        }

        //fragment_ratio
        if (hashMap.containsKey(1056)) {
            fragment_ratio = (double) hashMap.get(1056) / (double) load_packet_num;
        }
        // variance
        Iterator<I2PPackage> it_var_packet = concurrentLinkedQueue.iterator();
        while (it_var_packet.hasNext()) {
            I2PPackage packetInfo = (I2PPackage) it_var_packet.next();
            variance += Math.pow(packetInfo.getPackagelen() - avg_payload_len, 2);
        }
        variance = Math.sqrt(variance / (CONFIG.SLIDING_WINDOW_SIZE - 1));

        Instance instance = new DenseInstance(attributes.size());
        instance.setValue(attributes.get(0), avg_payload_len);
        instance.setValue(attributes.get(1), max_payload_len);
        instance.setValue(attributes.get(2), fragment_ratio);
        instance.setValue(attributes.get(3), entopy_len);
        instance.setValue(attributes.get(4), variance);
        instance.setValue(attributes.get(5), seq_exit);
        instance.setValue(attributes.get(6), "no");

        Instances instances = new Instances("classifier", attributes, 0);
        instances.setClassIndex(instances.numAttributes()-1);
        instances.add(instance);

        try {
            if (classifier.classifyInstance(instances.get(0))==0){
                System.out.println(instance);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
