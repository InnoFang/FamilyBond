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


# 截图

<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_main.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_receive_parents_heart_rate_report_notification.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_receive_suspicious_sms_list.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_receive_suspicious_sms_notification.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_receive_suspicious_sms_toast.png?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/children_shared_map.jpg?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/parents_check_hert_rate.jpg?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/parents_main.jpg?raw=true" width="300" height="500"/>&nbsp;<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/parets_heart_rate_report.jpg?raw=true" width="300" height="500"/>

# 第三方库

```
    /* Gson */
    compile 'com.google.code.gson:gson:' + rootProject.gson

    /* Glide */
    compile "com.github.bumptech.glide:glide:rootProject.glide"

    /* RxJava2 */
    compile 'io.reactivex.rxjava2:rxjava:' + rootProject.rxjava2
    compile 'io.reactivex.rxjava2:rxandroid:' + rootProject.rxandroid

    /* EventBus */
    compile 'org.greenrobot:eventbus:' + rootProject.eventbus

    /* GreenDao */
    compile 'org.greenrobot:greendao:' + rootProject.greendao
    compile 'org.greenrobot:greendao-generator:' + rootProject.greendao

    /* MPAndroidChart */
    compile 'com.github.PhilJay:MPAndroidChart:' + rootProject.mp_android_chart

    /* ARouter */
    compile 'com.alibaba:arouter-api:' + rootProject.arouter_api
    annotationProcessor 'com.alibaba:arouter-compiler:' + rootProject.arouter_comiler

    /* ButterKnife */
    compile 'com.jakewharton:butterknife:' + rootProject.butter_knife
    annotationProcessor 'com.jakewharton:butterknife-compiler:' + rootProject.butter_knife

    /* FloatingActionButton */
    compile 'com.getbase:floatingactionbutton:' + rootProject.fab
```


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
