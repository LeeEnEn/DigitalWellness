# Welcome to Digital Wellness Project
This is a project created by En En and Wei Jian, from NUS School of Computing for CP2106: Independent Software Development Project. Our aim of this project is to create something intuitive for users to manage their digital health. In this time of pandemic, many people have shifted to home-based learning, or work-from-home. The blurred boundaries of work-play time, unlike pre-Covid has caused digital usage to increased tremendously. Our application hopes to help users to have a healthier work-life balance with features such as Screen Usage Tracker, Steps Tracker, Workout Tutorials, and even a Focus Mode to block off your phone when doing work. 

# Information 
The application is running on Android platform. It is recommended to run this application on Android 10 and above. Do allow all permissions as requested in the app and have a stable internet connection. List of all libraries and Android's functionally will be listed below.

# Steps Tracker
This functionality aims to create an awareness of how much time each individual user spends on their devices and to notify users to take a break from the screen. Notification timings could be adjusted by the number of hours depending on the user, furthermore an overview of how much time has been spent, and or remaining would be shown in terms of a percentage.

This function made use of Android's phone innate motion step tracking ability, combining with MPAndroidChart, we plotted graphs based on the data we collected from the each individual user. Users could see their realtime updates through the progress bar in the app, as well as deciding to allow the app to run in the background, otherwise data would only be collected when the app is in the foreground. 

![image](https://user-images.githubusercontent.com/77260893/126973843-438f07f7-11c4-4c8a-9dff-45855145ae6d.png) ![image](https://user-images.githubusercontent.com/77260893/126973681-5544ac07-4061-48fe-b4a4-1ace92bc648b.png) ![image](https://user-images.githubusercontent.com/77260893/126973770-dd3f06bf-18a8-4fac-a279-e9899c888401.png)

# Screen Time Tracker
This functionality aims to create an awareness of how much time each individual user spends on their devices and to notify users to take a break from the screen. Notification timings could be adjusted by the number of hours depending on the user, furthermore an overview of how much time has been spent, and or remaining would be shown in terms of a percentage.

This function leverages the use of alarm manager and broadcast receiver's intent, Intent.ACTION_SCREEN_ON/OFF. By doing so, we are able to calculate the amount of time the user's screen is turn on, data collected would be updated to our database.

![image](https://user-images.githubusercontent.com/77260893/126974701-74552dff-7c35-456e-b956-8c513c04f113.png) ![image](https://user-images.githubusercontent.com/77260893/126974744-5c483f79-30aa-4d6d-80e8-1a1ccc1aef39.png)

# Workout Tutorials
This functionality displays a wide array of videos, ranging from beginner to expert levels. As such the user could filter by type of exercise and level of difficulty, and by following the videos, users would have a better understanding of how each workout is supposed to be done, reducing unnecessary injuries while imparting some basic exercise knowledge.

Video's URI are fetched from our database and is loaded into serveral arrays corresponding to the video's tagged intensity, this initial loading of all videos would cause a longer initial loading time. However, for subsequent visits into this activity, loading time is minimal. We replaced Android's default Media Player with ExoPlayer for more customizability, such as it's seek bar, SmoothStreaming and autoplaying of video to name a few. Users could also use the search bar provided instead of the predefined categories.

![image](https://user-images.githubusercontent.com/77260893/126975432-6625e6f0-33aa-4f5b-b7db-2f9e252ec5c6.png) ![image](https://user-images.githubusercontent.com/77260893/126975522-844c8b54-424c-4b56-8f93-bc37f0952a5a.png)

# Focus Mode 
This functionality intends to help user to be able to remove any distractions stemming from their mobile devices. This focus mode would block off all other app’s notifications and disables Wi-Fi connections, as well as not letting the user to shut down the app or kill the app for a certain period of time. This ensures that in that stated time frame, the user would not be distracted from their devices. 

Android's default Do Not Disturb mode is utilized here, however, we made sure that WiFi and cellular services are still available in case the user is met with an emergency. The alarm manager is once again in play, toggling the mode on off when the desired time is set by the user.

![image](https://user-images.githubusercontent.com/77260893/126975653-650a9b44-da0c-4b0d-a6cc-6ac3dbd6c914.png) ![image](https://user-images.githubusercontent.com/77260893/126975671-0b4ecb62-2c64-457f-ab35-6df197f6e514.png)

# Social System
The application allows users to view other users using the application and to add them as friends in the application. This allows the users to keep track of their friend’s progress in their steps and screen usage. This motivates them to encourage each other to have a healthy lifestyle. There are limited personalization functionalities right now in the application, such as changing profile pictures, and accepting friend requests.

Upon sending a request to a user, that user would then receive a notification in app. Once accepted, both users will be able to view each other's profiles, and see their steps progress, and as well as screen usage.

![image](https://user-images.githubusercontent.com/77260893/126977087-9df774e8-b040-41a2-bc14-5c931c79f7a7.png) ![image](https://user-images.githubusercontent.com/77260893/126977123-08c78106-6d80-465f-a3e2-b9374f97f17a.png)

# Distance Tracker
This feature allows users to set a certain mileage that they want to clock, and the phone will update them the distance they have clocked as well as when the user has reached the target they have set. This feature encourages users to leave their seats occasionally and prevent a sedentary lifestyle. 

The distance tracker uses Google Maps API to locate the user’s current location as well as to provide the route that has been taken throughout the use of the activity. To ensure that this feature is working as intended, we first check if Location Services is enabled. Secondly, internet access such as Wi Fi or mobile data must be turned on before we allow the user to interact with the features.

![image](https://user-images.githubusercontent.com/77260893/126977334-b2c4118e-1a68-42e8-b822-500471518fae.png) ![image](https://user-images.githubusercontent.com/77260893/126977352-4c622c96-4d21-468c-93bc-b18315c684a9.png)

# List of libraries and references
Design reference - https://material.io 
Icon packs - https://icons8.com 
Firebase Tutorials - https://console.firebase.google.com/u/0/
ExoPlayer – https://exoplayer.dev
Google Maps – https://developers.google.com/maps/documentation/android-sdk/overview
MPAndroidChart – https://github.com/PhilJay/MPAndroidChart 
Motion Sensor - https://developer.android.com/guide/topics/sensors/sensors_motion#sensors-motion-stepcounter
