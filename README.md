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



## libbinderhack.so模块后续也会开源，请耐心等候
