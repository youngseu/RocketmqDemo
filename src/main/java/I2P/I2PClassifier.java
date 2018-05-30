package I2P;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;

//i2p streams classifier
public class I2PClassifier {
    private Classifier classifier;

    public I2PClassifier() {
        classifier = new J48();
        // read train file
        File inputFile = new File("Weka/newtraintcp.arff");
        ArffLoader atf = new ArffLoader();
        try {
            atf.setFile(inputFile);
            Instances instancesTrain = atf.getDataSet();
            instancesTrain.setClassIndex(instancesTrain.numAttributes() -1);
            // train
            classifier.buildClassifier(instancesTrain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }
}
