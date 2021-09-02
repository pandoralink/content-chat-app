#48D597 PANTONE PQ-2412C 是默认主题颜色

解决 RadioGroup 设置默认选中导致该 RadioButton 一直是选中状态
解决方法：需要控制一个 RadioButton 默认选中，就需要给它设置一个 id，否则会一致默认选中，即使选中到另一个 RadioButton

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

线性布局的 item 的宽度（width）应该设置为 0dp，才能使用 layout_wight，否则不能按比例划分空间

8/27 预计完成创作页面的 webview（技术选型为 wangeEdit，首先适配移动端）
同时明天需要分配任务，创作 page 的任务最轻，而 News page 的任务最重，聊天界面后台给罗淳日去做
我先做把 News 里面的东西做了

1. [粉丝与关注数据库设计选型（选择方案 C 列表设计）](https://www.bilibili.com/read/cv2286737/)
2. [评论和回复的前端设计](https://www.bilibili.com/read/cv6268543) 这个我以前在前端做过，可以参考以往的设计
3. 由于是个新闻阅读器，所以在文章的存放上，设计为存放 URL

4. user_id user_new_url new_comment_id

在数据库设计上我没有找到一个好的原型（未来几天在开发的过程中会不断去查阅和 copy 完善目前设计的系统）

单是评论系统有非常的项目，但是带文章的不是非常多见，目前我已经有了初步的想法，但不知道和一些成熟的方案有什么区别

## 文章系统数据库设计

账号的设计上采用上一条记录的主键（因为它本身的主键在插入的时候是获取不到的）加随机四位的方式去生成并返回客户端

**注意事项**

1. 注意如果创建账号请求同时多次发生，获取到的 max()可能是一致的，因为插入还没完成，如果后四位的随机数依然相同则会出现创建账号失败的情况（user_account 将会是）

```SQL
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(100) COMMENT '用户名称',
  `user_head` varchar(100) COMMENT '用户头像',
  `user_account` varchar(10) UNIQUE COMMENT '用户账号',
  `user_password` varchar(8) NOT NULL COMMENT '用户密码',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

```SQL
CREATE TABLE `new` (
  `new_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `new_url` varchar(100) NOT NULL COMMENT '文章链接',
  `new_owner_id` int COMMENT '文章创作人',
  `new_owner_head_id` varchar(100)  COMMENT '文章创作头像链接',
  `new_comment_area_id` int COMMENT '文章评论区id，外键',
  KEY `new_owner_id` (`new_owner_id`),
  CONSTRAINT `new_owner_id` FOREIGN KEY (`new_owner_id`) REFERENCES `user` (`user_id`),
  PRIMARY KEY (`new_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

## 关注系统数据库设计

新增一条粉丝数据就会在这里插入一条数据

```SQL
CREATE TABLE fans (
  blogger_id int NOT NULL AUTO_INCREMENT COMMENT '主键',
  fan_id int UNIQUE COMMENT '用户账号',
  FOREIGN KEY(blogger_id) REFERENCES user(user_id),
  FOREIGN KEY(fan_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

user_id user_new_url new_comment_id

## 评论系统数据库设计

点赞功能先不做，有如下问题

1. 如果加入 thumb（点赞数）字段，怎么防止重复点赞？如果记录每个点赞人的 id，作为另一个字段（假设为 thumber），那么在上千点赞数的情况就会非常冗余，而且更新评论点赞时还需要查询一下 thumber
2. 打算加入一个 subId（子 id）的功能，每篇文章拥有一个评论区 id，而每个评论区又有自己的子 id

```SQL
CREATE TABLE `comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` text COMMENT '评论内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `commentator_id` int COMMENT '评论者id',
  `commentator_name` varchar(100) COMMENT '评论者id',
  `commentator_head_url` varchar(100) COMMENT '头像URL',
  `parent_id` int COMMENT '回复人id',
  PRIMARY KEY (`id`),
  KEY `commentator_id` (`commentator_id`),
  CONSTRAINT `commentator_id` FOREIGN KEY (`commentator_id`) REFERENCES `user` (`user_id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

9/3 号需要完善的地方

1. 新闻页面中的新闻 item 长按后出现的两个图标的 padding-top 需要增大，图标直接黏顶部了
2. 新闻加载的有点卡，比如 vue 并没有及时的将图片渲染到位，一些数据也没能成功渲染（而是渲染了原始 HTML 元素，比如 `{{ followStatus ? "+ 关注" : "✔ 已关注" }}`）