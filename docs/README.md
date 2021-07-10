<div align="center">

<img src="https://github.com/InnoFang/FamilyBond/blob/master/app/src/main/ic_launcher-web.png?raw=true" width="150" height="150"/>

<br/>

<h1>FamilyBond</h1>

An application, combine with Children side port and Parents side port, which let children take good care of their parents and parents can use smart phone to detect their heart rate.

<br/>

<img src="https://github.com/InnoFang/FamilyBond/blob/master/screenshot/structure.jpg?raw=true"/>

</div>

<br />

# Function

 + **Parents-end**

   - **Heart Rate Detection**

     When the elderly use the function, there will be voice prompts, just click the screen to start timing, and then put the index finger on the camera, the phone can automatically detect heart rate.

   - **Suspicious SMS Intercept**

     When the mobile phone receives the message, App can automatically identify the reliability of sms. When the suspicious message are identified as suspicious messages, the content of messages will be sent to children automatically, so the children can identify the contents of the messages and effectively prevent the old people from being defrauded.

 + **Children-end**
 
   - **Message Reminder**

     Children can send text messages of voice messages to the elderly.

   - **Location Display For The Elderly**

     Children can see the location and information of the elderly on the map.

<br />

# Core technology introduction

 1. **Use the phone camera to measure the heart rate of the elderly**      
    The principle is the use of photoplethysmography (ie, PPG). We use the phone's camera and flashlight to act as a PPG sensor and light source. When an elderly person puts his finger on the camera, we capture the image of each frame, calculate the eigenvalues ​​and save them. When a certain amount of data is collected After that, we will preprocess the data for this period of time, filter out the noise, detect the peak and calculate the signal frequency, and finally get the heart rate.

 2. **Use NLP model to screen suspicious SMS**        
    We got 800,000 open-source Chinese messages on Github, of which 80,000 suspicious messages, using the Chinese text classification toolkit THUCTC, to achieve a custom identification of suspicious messages. In the process of model establishment, the bigram of the word string is selected as the characteristic unit, the tfidf is used for the weight calculation, and the liblinear algorithm is selected as the classification model. Finally got a good accuracy. We will train a good model on the background and the core algorithm is ported to Android, so that when the elderly receive text messages, SMS reliability can be screened, when judged as suspicious messages, will forward a SMS content to their children, thus effectively reducing suspicion of short messages to the elderly.

 3. **Messaging and location sharing based on the implementation of instant messaging**       
    In order to achieve integration, to ensure the timely and reliable messaging, we use a messaging mechanism based on instant messaging. For example, the location of the map is shared. When a child wants to view the location of an elderly person, he / she needs only to open the corresponding function page. The child side port sends an instruction to the parent side port. When the elderly person receives the instruction, The current location back to the children side, so that children can timely view the location of the elderly. Using this messaging mechanism, when the elderly after measuring the heart rate, APP will automatically send the heart rate report to their children, the children will be able to understand the health status of the elderly and to simplify the elderly operation, while the elderly test To the suspicious sms, the child will receive the parental end of the SMS message content, children can help identify the elderly, double insurance, more effectively reduce suspicious texting damage to the elderly.

<br />


# Third-party Use

 + [Bmob](https://www.bmob.cn/)
 + [amap](http://lbs.amap.com/)
 + [xfyun](http://www.xfyun.cn/)
 + [Glide](https://github.com/bumptech/glide)
 + [EventBus](https://github.com/greenrobot/EventBus)
 + [greenDAO](https://github.com/greenrobot/greenDAO)
 + [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
 + [ARouter](https://github.com/alibaba/ARouter)
 + [android-floating-action-button](https://github.com/futuresimple/android-floating-action-button)

<br />



# Screenshot

<div align="center">

<img src="https://cdn.jsdelivr.net/gh/innofang/familybond/screenshot/children_main.png" width="300" height="500"/>&nbsp;<img src="https://cdn.jsdelivr.net/gh/innofang/familybond/screenshot/children_receive_parents_heart_rate_report_notification.png" width="300" height="500"/>&nbsp;&nbsp;<img src="https://cdn.jsdelivr.net/gh/innofang/familybond/screenshot/children_receive_suspicious_sms_notification.png" width="300" height="500"/>&nbsp;<img src="https://cdn.jsdelivr.net/gh/innofang/familybond/screenshot/children_receive_suspicious_sms_toast.png" width="300" height="500"/>&nbsp;&nbsp;<img src="https://cdn.jsdelivr.net/gh/innofang/familybond/screenshot/parents_check_hert_rate.jpg" width="300" height="500"/>&nbsp;<img src="https://cdn.jsdelivr.net/gh/innofang/familybond/screenshot/parets_heart_rate_report.jpg" width="300" height="500"/>

</div>

<br />

# [License](https://github.com/InnoFang/FamilyBond/blob/master/LICENSE)


    FamilyBond  Copyright (C) 2017  InnoFang
    This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
    This is free software, and you are welcome to redistribute it
    under certain conditions; type `show c' for details.
