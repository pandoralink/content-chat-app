#48D597 PANTONE PQ-2412C 是默认主题颜色

解决RadioGroup设置默认选中导致该RadioButton 一直是选中状态
解决方法：需要控制一个RadioButton 默认选中，就需要给它设置一个id，否则会一致默认选中，即使选中到另一个RadioButton

**消息适配器（Adapter）还没做**

package 划分还没做

RecyclerView Item 跳转 Activity https://ask.csdn.net/questions/645359

E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.example.newslist, PID: 9195
    android.content.ActivityNotFoundException: Unable to find explicit activity class {com.example.newslist/com.example.newslist.message.MsgContentActivity}; have you declared this activity in your AndroidManifest.xml?
        at android.app.Instrumentation.checkStartActivityResult(Instrumentation.java:2065)
        at android.app.Instrumentation.execStartActivity(Instrumentation.java:1727)
        at android.app.Activity.startActivityForResult(Activity.java:5320)
        at androidx.fragment.app.FragmentActivity.startActivityForResult(FragmentActivity.java:676)
        at androidx.core.app.ActivityCompat.startActivityForResult(ActivityCompat.java:234)
        at androidx.fragment.app.FragmentActivity.startActivityFromFragment(FragmentActivity.java:791)
        at androidx.fragment.app.FragmentActivity$HostCallbacks.onStartActivityFromFragment(FragmentActivity.java:933)
        at androidx.fragment.app.Fragment.startActivity(Fragment.java:1185)
        at androidx.fragment.app.Fragment.startActivity(Fragment.java:1173)
        at com.example.newslist.message.MsgFragment$1.onItemClick(MsgFragment.java:52)
        at com.example.newslist.message.MessagesAdapter$1.onClick(MessagesAdapter.java:63)
        at android.view.View.performClick(View.java:7448)
        at android.view.View.performClickInternal(View.java:7425)
        at android.view.View.access$3600(View.java:810)
        at android.view.View$PerformClick.run(View.java:28305)
        at android.os.Handler.handleCallback(Handler.java:938)
        at android.os.Handler.dispatchMessage(Handler.java:99)
        at android.os.Looper.loop(Looper.java:223)
        at android.app.ActivityThread.main(ActivityThread.java:7656)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:592)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:947)
原因：没有在 AndroidMainfest 中声明 Activity

Intent 与 Fragment 的跳转
可以使用 Intent.putExtra() 的方式解决
注意 Key id expected String but value was a java.lang.Integer. The default value <null> was reurned
在项目的开发中，使用 getIntent().getStringExtra() 就出现这个问题
这个 getStringExtra() 中的 `String` 指的是值(value)的类型而不是 key 的类型
因此当我们在 MsgContentActivity 给 MainActivity 传值的过程中，传的值是 int 类型，它自然会报错

相对布局中两个使用 layout_toLeftOf 定位的组件不可以同时使用，举个栗子
<ImageView
            android:id="@+id/left"
            android:layout_toLeftOf="@id/right"
            />
<ImageView
            android:id="@+id/right"
            android:layout_toRightOf="@id/left"
            />
此时两者会冲突，导致两个 <ImageView> 消失


android 之 难以理解的错误
父容器和子容器同时使用 layout_alignParentBottom="true" 会让父容器撑满它的父容器（即爷容器）
设置 layout_above 会被下面的控件挤掉剩余空间

线性布局的item的宽度（width）应该设置为0dp，才能使用 layout_wight，否则不能按比例划分空间

8/27 预计完成创作页面的webview（技术选型为 wangeEdit，首先适配移动端）
同时明天需要分配任务，创作page的任务最轻，而News page 的任务最重，聊天界面后台给罗淳日去做
我先做把 News 里面的东西做了
