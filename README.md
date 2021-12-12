# BinderHackDemo
trace all binder-funcion calls on android-platform

## 该demo展示了如何使用libbinderhack.so模块，trace-app自身进程binder调用情况

您可以通过该次[提交](https://github.com/whulzz1993/BinderHackDemo/commit/6f4342262de47771340d9e4f959ecd10bf7a2f10)，查看如何使用libbinderhack.so

## libbinderhack.so用途:

1.可以作为一个逆向工具，分析app行为

2.可以作为一款性能分析工具，查看进程是否有非必要的、频繁跨进程调用binder

3.可以作为一款安全工具，分析本app是否有不合规的api调用(可以参考工信部移动互联网安全)

## 缺点：

1.目前只支持安卓5.0以上平台(art)

2.由于hook的仅仅是BinderProxy.transactNative函数，所以仅能trace到proxy调用

###

## 输出的demo样例:

```
com.example.myapplication D/WHULZZ: android.content.pm.IPackageManager getInstalledApplications
com.example.myapplication D/WHULZZ: android.view.accessibility.IAccessibilityManager getEnabledAccessibilityServiceList
com.example.myapplication D/WHULZZ: android.app.IActivityTaskManager activityTopResumedStateLost
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager removeImeSurfaceFromWindow
com.example.myapplication D/WHULZZ: android.view.IWindowSession relayout
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager removeImeSurfaceFromWindow
com.example.myapplication D/WHULZZ: android.app.IActivityTaskManager activityStopped
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager removeImeSurfaceFromWindow
com.example.myapplication D/WHULZZ: android.view.accessibility.IAccessibilityManager getEnabledAccessibilityServiceList
com.example.myapplication D/WHULZZ: android.view.IWindowSession relayout
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager removeImeSurfaceFromWindow
com.example.myapplication D/WHULZZ: android.app.IActivityTaskManager getActivityOptions
com.example.myapplication D/WHULZZ: miui.security.ISecurityManager activityResume
com.example.myapplication D/WHULZZ: android.app.IActivityTaskManager activityResumed
com.example.myapplication D/WHULZZ: android.view.IWindowSession relayout
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager removeImeSurfaceFromWindow
com.example.myapplication D/WHULZZ: android.view.IWindowSession finishDrawing
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager removeImeSurfaceFromWindow
com.example.myapplication D/WHULZZ: android.app.IActivityTaskManager activityIdle
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager startInputOrWindowGainedFocus
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager reportPerceptible
com.example.myapplication D/WHULZZ: com.android.internal.view.IInputMethodManager removeImeSurfaceFromWindow
```



## 使用方式

### 1.必须在您的apk资源目录中提供bm.properties文件，并定义BinderCareEntry

![image-20211212122519265](C:\Users\dell\AppData\Roaming\Typora\typora-user-images\image-20211212122519265.png)



该文件中定义了BinderCareEntry="java class name"

便于后文叙述，此java class name简称为ENTRY

BinderHackDemo中将此ENTRY定义为com.example.myapplication.MainActivity

该ENTRY会被libbinderhack.so加载时使用，如未定义，将导致link失败

### 2.必须在ENTRY class中定义这两个native-jni函数

#### 正确声明这两个native函数:

必须将这两个函数放一起声明

```java
    /**
     * start binder monitor
     */
    private static native void start();

    /**
     * end binder monitor
     */
    private static native void end();
```



#### 错误声明方式如下：

```
private static native void start();

public void xx();//不能在start/end函数之间存放其他声明

private static native void end();
```



### 3.如果您只关注部分binder调用，可在ENTRY中提供getInterestBinders函数

#### 您可以仿照BinderHackDemo中的样例：

```java
    /**
     * This function is not necessary!
     * If not provided, binderhack will print all the binder calls.
     * This function will be called by native-c code.
     *
     * @return HashMap<String, Set<String>>. see demo below for detail
     */
    @Keep
    private static HashMap getInterestBinders() {
        //关注IActivityManager->activityPaused
        HashMap<String, Set<String>> monitorBinderMap = new HashMap<>();
        HashSet<String> amFuncs = new HashSet<>();
        amFuncs.add("activityPaused");
        monitorBinderMap.put("android.app.IActivityManager", amFuncs);

        //关注IPackageManager->getInstalledApplications
        HashSet<String> pmFuncs = new HashSet<>();
        pmFuncs.add("getInstalledApplications");
        monitorBinderMap.put("android.content.pm.IPackageManager", pmFuncs);
        return monitorBinderMap;
    }
```



### 4.如果您要拦截binder调用，您可以在ENTRY中提供transactStart/transactEnd函数

同样可以在样例中找到demo

#### transactStart

```java
    /**
     *
     * @param interfaceName likely as android.content.pm.IPackageManager
     * @param funcName likely as getInstalledApplications
     * @param data see {@link android.os.IBinder}->transact(...)
     * @param reply see {@link android.os.IBinder}->transact(...)
     * @return TRUE represents you've decided to intercept the origin call.
     */
    @Keep
    private static boolean transactStart(Object interfaceName, Object funcName, Parcel data, Parcel reply) {
        Log.d("WHULZZ", String.format("transactStart %s %s", interfaceName, funcName));
        return false;
    }
```

#### transactEnd

```java
    /**
     *
     * @param interfaceName likely as android.content.pm.IPackageManager
     * @param funcName likely as getInstalledApplications
     * @param data see {@link android.os.IBinder}->transact(...)
     * @param reply reply see {@link android.os.IBinder}->transact(...)
     * @param originRet this is the origin result
     * @return I advice you to use {@param originRet}
     */
    @Keep
    private static boolean transactEnd(Object interfaceName, Object funcName, Parcel data, Parcel reply, boolean originRet) {
        Log.d("WHULZZ", String.format("transactEnd %s %s", interfaceName, funcName));
        return originRet;
    }
```





## libbinderhack.so模块后续也会开源，请耐心等候



## 欢迎脑暴...

contact with 1040882146@qq.com


