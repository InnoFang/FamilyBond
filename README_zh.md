# 家宝

<img src="https://github.com/InnoFang/FamilyBond/blob/master/app/src/main/ic_launcher-web.png?raw=true" width="150" height="150"/>

[English](https://github.com/InnoFang/FamilyBond/blob/master/README.md) | 中文

家宝分为子女端和父母端，子女可以使用家宝来更好的了解到老人的健康状况，老人可以使用家宝来测量心率

![](https://github.com/InnoFang/FamilyBond/blob/master/screenshot/structure.jpg?raw=true)

# 功能

 + 子女端

   - **心率检测**

    	老人使用功能时，会有语音提示，只需要点击屏幕开始计时，然后将食指放在摄像头上，手机就可以自动检测心率。

   - **可疑短信拦截**

      当手机收到短信时，APP可以自动对短信可靠性进行鉴别。当识别出的短信为可疑短信时，将会自动将短信内容发送给子女，让子女对短信内容进行甄别，有效防止老人被诈骗。

 + 父母端

   - **消息提醒**

      孩子可以把文本消息或语音消息一键发送给老人

   - **老人位置显示**

      孩子可以在地图上查看到老人位置和信息

# 核心技术介绍

 1. 使用手机摄像头来测量老人的心率

   原理是使用光电容积脉搏波描记法（即PPG）。我们使用手机的摄像头和闪关灯来充当 PPG 的传感器与光源，当老人把手指放在摄像头上时，我们会采集每一帧的图像，计算特征值并保存起来，当收集到一定量的数据过后，我们会对这一段时间内的数据进行预处理，过滤掉噪声，对波峰进行检测以及信号频率的计算，最终得到心率。

 2. 使用 NLP 模型对可疑短信进行甄别

   我们在 Github 上获取了 80 万条开源的中文短信，其中有 8 万条可疑短信，借助 THUCTC 中文文本分类工具包，实现自定义的对可疑短信的识别。在模型的建立过程中，选取二字串 bigram 作为特征单元，权重计算采用的是 tfidf，分类模型选用的是 liblinear 算法。最终得到了不错的准确率。我们将训练好的模型放在了后台上并将核心算法一直到 Android 上，这样当老人端收到短信时，就可以对短信的可靠性进行甄别，当判定为可疑短信后，会转发一份短信内容给子女，从而有效地减少可疑短信对老人的坑害。


 3. 基于即时通讯的实施的消息传递与位置共享

   为了实现一体化，保证消息传递的及时与可靠性，我们采用了基于即时通讯的消息传递机制。举例来说，地图位置共享，当子女想要查看老人的位置时，只需要打开对应的功能页即可，子女端会发送一条指令给老人端，当老人端收到该指令过后，就会将当前位置回传给子女端，这样子女就能及时的查看到老人位置。采用这种消息传递机制，当老人测完心率过后，APP 会自动的将心率报告发送给子女端，子女就能及时的了解到老人的健康状况并简化了老人的操作，同时，当老人端检测到可疑短信后，子女端会收到父母段传来的短信内容，子女就可以帮助老人进行鉴别，双重保险下，更有效的减少可疑短信对老人的坑害。

# 截图

<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_main.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_receive_parents_heart_rate_report_notification.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_receive_suspicious_sms_list.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_receive_suspicious_sms_notification.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_receive_suspicious_sms_toast.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_shared_map.jpg?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/parents_check_hert_rate.jpg?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/parents_main.jpg?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/parets_heart_rate_report.jpg?raw=true" width="300" height="500"/>

# 第三方库使用

 + [Bmob](https://www.bmob.cn/)
 + [amap](http://lbs.amap.com/)
 + [xfyun](http://www.xfyun.cn/)
 + [Glide](https://github.com/bumptech/glide)
 + [EventBus](https://github.com/greenrobot/EventBus)
 + [greenDAO](https://github.com/greenrobot/greenDAO)
 + [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
 + [ARouter](https://github.com/alibaba/ARouter)
 + [butterknife](https://github.com/JakeWharton/butterknife)
 + [android-floating-action-button](https://github.com/futuresimple/android-floating-action-button)

# [License](https://github.com/InnoFang/FamilyBond/blob/master/LICENSE)

```
Copyright 2017 InnoFang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
