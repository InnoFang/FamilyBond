package io.innofang.sms_intercept;

/**
 * Author: Inno Fang
 * Time: 2017/10/7 10:40
 * Description:
 */

import android.util.Log;

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

    public static void runLoadModelAndUse() {
        // 新建分类器对象
        BasicTextClassifier classifier = new BasicTextClassifier();

        // 设置分类种类，并读取模型
        classifier.loadCategoryListFromFile("E:/my_novel_model/category");
        classifier.setTextClassifier(new LinearBigramChineseTextClassifier(classifier.getCategorySize()));
        classifier.getTextClassifier().loadModel("E:/my_novel_model");

	/*
     * 上面三行代码等价于设置如下参数，然后初始化并运行：
	 *
	   String defaultArguments = ""
	 +  "-l  my_novel_model"  // 设置您的训练好的模型的路径，这里的路径只是给出样例
	 ;
	 classifier.Init(defaultArguments.split(" "));
	 classifier.runAsLinearBigramChineseTextClassifier();
	 *
	 */

        // 之后就可以使用分类器进行分类
        //String text = "尊敬用户您好：您的工行账户因未分类核实个人信息已被冻结，请登录后按提示核实解冻……";
        String text="客户您好，您刚持××银行信用卡在××百货消费了×××元，咨询电话021-510×××××，银联电话021-510×××××";
        // String text="恭喜你， 您的号码已被“跑男”栏目组（或者爸爸去哪儿）抽中，请您登陆我们的官方网站。站http://www.xxxxx领取，验证码9188查询，速回电××号码。";
        //String text="恭喜您！您的号码已被节目砸蛋抽中，请您登录网站领取http://www.xxxxx。";
        // String text="我是吴彦祖（李连杰……），我正在一个深山里拍戏，有一段武打戏我被打飞，现在我和剧组失去了联系，一个人在山里身无分文，我乱输入的一个号码，就找到了你。实在是缘分，你能给我打1000块钱吗？";
        //String text="我是房东，换了个号码你记一下，另外这次租金请汇我的爱人卡上，工行61222621020114***王茗，谢了。";
        //String text="我急转一汽、丰田5万，货到付款，速回电××号码。4、低利息、无需担保！办理贷款业务，请速联系××号码。";
        //   String text="“还是打到这个XX行卡上，办好来信，速回电××号码”。";
        int topN = 1;  // 保留最有可能的3个结果
        ClassifyResult[] result = classifier.classifyText(text, topN);
        for (int i = 0; i < topN; ++i) {
            // 输出分类编号，分类名称，以及概率值。
            System.out.println(result[i].label + "\t" +
                    classifier.getCategoryName(result[i].label) + "\t" +
                    result[i].prob);
        }
    }

    public static void runLoadModelAndUse(String sms) {
        // 新建分类器对象
        BasicTextClassifier classifier = new BasicTextClassifier();

        // 设置分类种类，并读取模型
        classifier.loadCategoryListFromFile("file://android_asset/category");
        classifier.setTextClassifier(new LinearBigramChineseTextClassifier(classifier.getCategorySize()));
        classifier.getTextClassifier().loadModel("file://android_asset");
    /*
	 * 上面三行代码等价于设置如下参数，然后初始化并运行：
	 *
	   String defaultArguments = ""
	 +  "-l  my_novel_model"  // 设置您的训练好的模型的路径，这里的路径只是给出样例
	 ;
	 classifier.Init(defaultArguments.split(" "));
	 classifier.runAsLinearBigramChineseTextClassifier();
	 *
	 */

        // 之后就可以使用分类器进行分类
        //String text = "尊敬用户您好：您的工行账户因未分类核实个人信息已被冻结，请登录后按提示核实解冻……";
        String text = "客户您好，您刚持××银行信用卡在××百货消费了×××元，咨询电话021-510×××××，银联电话021-510×××××";
        // String text="恭喜你， 您的号码已被“跑男”栏目组（或者爸爸去哪儿）抽中，请您登陆我们的官方网站。站http://www.xxxxx领取，验证码9188查询，速回电××号码。";
        //String text="恭喜您！您的号码已被节目砸蛋抽中，请您登录网站领取http://www.xxxxx。";
        // String text="我是吴彦祖（李连杰……），我正在一个深山里拍戏，有一段武打戏我被打飞，现在我和剧组失去了联系，一个人在山里身无分文，我乱输入的一个号码，就找到了你。实在是缘分，你能给我打1000块钱吗？";
        //String text="我是房东，换了个号码你记一下，另外这次租金请汇我的爱人卡上，工行61222621020114***王茗，谢了。";
        //String text="我急转一汽、丰田5万，货到付款，速回电××号码。4、低利息、无需担保！办理贷款业务，请速联系××号码。";
        //   String text="“还是打到这个XX行卡上，办好来信，速回电××号码”。";
        int topN = 1;  // 保留最有可能的3个结果
        ClassifyResult[] result = classifier.classifyText(sms, topN);
        for (int i = 0; i < topN; ++i) {
            // 输出分类编号，分类名称，以及概率值。
            System.out.println(result[i].label + "\t" +
                    classifier.getCategoryName(result[i].label) + "\t" +
                    result[i].prob);

            Log.i("tag", result[i].label + "\t" +
                    classifier.getCategoryName(result[i].label) + "\t" +
                    result[i].prob);
        }
    }

    public static void main(String args[]) {
        //runTrainAndTest();
        //String text = "客户您好，您刚持××银行信用卡在××百货消费了×××元，咨询电话021-510×××××，银联电话021-510×××××";
        runLoadModelAndUse();

    }


}
