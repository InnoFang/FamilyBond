package io.innofang.sms_intercept;

/**
 * Author: Inno Fang
 * Time: 2017/10/7 10:40
 * Description:
 */

import org.thunlp.text.classifiers.BasicTextClassifier;
import org.thunlp.text.classifiers.ClassifyResult;
import org.thunlp.text.classifiers.LinearBigramChineseTextClassifier;

public class SMSIdentification {
    public static void runTrainAndTest() {

        // 新建分类器对象
        BasicTextClassifier classifier = new BasicTextClassifier();

        // 设置参数
        String defaultArguments = ""
                + "-train E:\\data\\ "  // 设置您的训练路径，这里的路径只是给出样例
                + "-test E:\\data\\ "
                //	+ "-l C:\\Users\\do\\workspace\\TestJar\\my_novel_model "
                //	+ "-cdir E:\\Corpus\\书库_cleared "
                //	+ "-n 1 "
                // + "-classify E:\\Corpus\\书库_cleared\\言情小说 "  // 设置您的测试路径。一般可以设置为与训练路径相同，即把所有文档放在一起。
                + "-d1 0.7 "  // 前70%用于训练
                + "-d2 0.3 "  // 后30%用于测试
                + "-f 3500 " // 设置保留特征数，可以自行调节以优化性能
                + "-s E:/my_novel_model"  // 将训练好的模型保存在硬盘上，便于以后测试或部署时直接读取模型，无需训练
                ;

        // 初始化
        classifier.Init(defaultArguments.split(" "));

        // 运行
        classifier.runAsBigramChineseTextClassifier();

    }

    public static SMSClassifyResult runLoadModelAndUse(String sms) {
        // 新建分类器对象
        BasicTextClassifier classifier = new BasicTextClassifier();

        // 设置分类种类，并读取模型
        classifier.loadCategoryListFromFile(SMSModelUtil.CATEGORY_FILE_PATH);
        classifier.setTextClassifier(new LinearBigramChineseTextClassifier(classifier.getCategorySize()));
        classifier.getTextClassifier().loadModel(SMSModelUtil.DIRECTORY);

        // 之后就可以使用分类器进行分类
        int topN = 1;  // 保留最有可能的1个结果
        ClassifyResult[] result = classifier.classifyText(sms, topN);
        SMSClassifyResult scr = new SMSClassifyResult();
        scr.setClassifyLabel(result[0].label);
        scr.setProbability(result[0].prob);
        return scr;
    }

}
