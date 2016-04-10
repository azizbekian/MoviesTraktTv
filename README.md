# TraktTV Movies

This is a task/test app, that was given me from one of companies. Unfortunately, I didn't fit the position. Nevertheless, I decided to post it here.

The app get's popular movies list via JSON from TraktTV API and display them. Additionally, search functionality is implemented and corner cases are handled. Uses transition animations upto SDK 16.

<img src="screenshots/demo.gif" width="400" align="right" hspace="20">

### Tech

The app uses a number of open source projects to work properly:

* [Butterknife]
* [RxJava], [RxAndroid], [RxBinding]
* [Retrofit 2]
* [OkHttp3]
* [Picasso]
* [LeakCanary]
* [Greenrobot's EventBus]
* [Dagger 2]
* [RetroLambda]

### Before running

You need to put your TraktTv API key in `Constants.java` to communicate with server. If you do not have key, you can view typical JSON responses in assets.


   [Butterknife]: <http://jakewharton.github.io/butterknife/>
   [RxJava]: <https://github.com/ReactiveX/RxJava>
   [RxAndroid]: <https://github.com/ReactiveX/RxAndroid>
   [RxBinding]: <https://github.com/JakeWharton/RxBinding>
   [Retrofit 2]: <https://github.com/square/retrofit>
   [OkHttp3]: <https://github.com/square/okhttp>
   [Picasso]: <http://square.github.io/picasso>
   [LeakCanary]: <https://github.com/square/leakcanary>
   [Greenrobot's EventBus]: <https://github.com/greenrobot/EventBus>
   [node.js]: <http://nodejs.org>
   [Dagger 2]: <http://google.github.io/dagger>
   [RetroLambda]: <https://github.com/evant/gradle-retrolambda>