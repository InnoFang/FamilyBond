package io.innofang.sms_intercept;

/**
 * Author: Inno Fang
 * Time: 2017/10/11 16:09
 * Description:
 */


public class SMSClassifyResult {

    // 是否为可疑短信
    private boolean suspiciousSMS;
    // 可疑概率
    private double probability;

    public boolean isSuspiciousSMS() {
        return suspiciousSMS;
    }

    public void setClassifyLabel(int label) {
        if (label == 0) {
            suspiciousSMS = true;
        } else {
            suspiciousSMS = false;
        }
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
