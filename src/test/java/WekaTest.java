import I2P.I2PClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;

//test offline weka
public class WekaTest {
    public static void main(String[] args) throws Exception {
        Classifier m_classifier = new J48();
        // 训练文件
        File inputFile = new File("Weka/newtraintcp.arff");
        ArffLoader atf = new ArffLoader();
        atf.setFile(inputFile);
        // 读入训练文件
        Instances instancesTrain = atf.getDataSet();

        // 测试文件
        inputFile = new File("Weka/newtraintcp.arff");// 测试语料文件
        atf.setFile(inputFile);
        // 读入测试文件
        Instances instancesTest = atf.getDataSet();

        // 设置分类属性所在行号（第一行为0号），instancesTest.numAttributes()可以取得属性总数
        instancesTest.setClassIndex(instancesTest.numAttributes() -1);
        instancesTrain.setClassIndex(instancesTrain.numAttributes() -1);
        // 测试实例个数
        double sum = instancesTest.numInstances(), right = 0.0f;
        System.out.println(sum);

        // 训练
        m_classifier.buildClassifier(instancesTrain);

        //测试分类结果
        for (int i = 0; i < sum; i++)
        {
            if (m_classifier.classifyInstance(instancesTest.instance(i)) == instancesTest.instance(i).classValue())// 如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
            {
                right++;// 正确值加1
            }
        }
        int TP=0,FP=0,FN=0,TN=0;
        for (int i = 0; i < sum; i++) {
            if (m_classifier.classifyInstance(instancesTest.instance(i)) == 1.0
                    && instancesTest.instance(i).classValue() == 1.0) {
                TN++;
            } else if (m_classifier.classifyInstance(instancesTest.instance(i)) == 1.0
                    && instancesTest.instance(i).classValue() == 0.0) {
                FN++;
            } else if (m_classifier.classifyInstance(instancesTest.instance(i)) == 0.0
                    && instancesTest.instance(i).classValue() == 1.0) {
                FP++;
            } else if (m_classifier.classifyInstance(instancesTest.instance(i)) == 0.0
                    && instancesTest.instance(i).classValue() == 0.0) {
                TP++;
            }
        }
        System.out.println("tp: " + TP);
        System.out.println("fp: " + FP);
        System.out.println("fn: " + FN);
        System.out.println("tn: " + TN);
        System.out.println("classification precision:" + (right / sum));
    }
}
