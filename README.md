#48D597 PANTONE PQ-2412C 是默认主题颜色

**消息适配器（Adapter）还没做**

package 划分还没做

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

new new_owner_head_id 文章创作头像链接 字段修改为 article_cover_url 文章封面链接

```sql
comment 删掉 new_owner_head_url 字段
alter table new drop column new_owner_head_url;
comment 新增 article_cover_url 字段
alter table new add column article_cover_url varchar(100) default "";
```

1. 优点，向前端传递数据时，只需查询一次 new table，就可以拿到页面初始化所需要的所有数据
2. 缺点，每次用户更新名称的时候需要更新它的在 new 表中所有相关文章（考虑到用户小概率会修改名称，所以方案具有一定可行性），点击文章查看时也需要发出一次请求（获取头像以及其他相关可视信息）
3. 替代方案，不新增此字段，则需在后端中对 new_owner_id 和 user 表进行连接查询（这样能够拿到一次刷新所有文章作者的可见信息，点击文章查看时文章作者信息栏（获取头像和名称）也无需再次发出请求），而且用户以后更新信息时也不需要对 new 表更新
4. 总结，如果需要做到一次返回数据，那么 new 表可能需要频繁加入多个新字段，同时虽然用户更新自身信息的可能性小，但还是有可能的，我个人认为，数据量很大的情况下，原始方案好，存放大量可视信息，但小数据的情况下，new 表字段小一点方便观察
5. 结果，选择替代方案

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

# 新增外键约束
ALTER TABLE new ADD CONSTRAINT new_comment_area_id FOREIGN KEY(new_comment_area_id) REFERENCES comment(comment_id)
# 删除外键约束
# 先查看约束名称
SHOW CREATE TABLE new;
alter table new drop foreign key new_comment_area_id;
alter table new drop column new_comment_area_id;
```

### 存储过程 proc_newByInsert

```sql
CREATE PROCEDURE proc_newByInsert (
  in new_owner_id int,
  in new_owner_head_url varchar(100),
  in new_name varchar(100)
)
BEGIN
  declare newUrl varchar(100);
  select concat("http://116.63.152.202:5002/News/",max(new_id),".html") into newUrl from new;
  INSERT INTO new(new_url,new_owner_id,new_owner_head_url,new_name) VALUES (newUrl,new_owner_id,new_owner_head_url,new_name);
END;
```

1. 会直接拿 new 表中的最大值作为 new_url 的值，因为该值最后会返回给后端进行一个 HTML 文件命名
2. 如果有多个请求同时拿到最大值也只会有一个能够插入成功，因为 new_url 将会是唯一的

## 关注系统数据库设计

新增一条粉丝数据就会在这里插入一条数据

```SQL
CREATE TABLE fans (
  blogger_id int NOT NULL COMMENT '被关注账号id',
  fan_id int NOT NULL COMMENT '粉丝账号id',
  FOREIGN KEY(blogger_id) REFERENCES user(user_id),
  FOREIGN KEY(fan_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

user_id user_new_url new_comment_id

## 评论区设计

测试可能出现错误的不同情况

1. 未点击任意评论区用户时回复文章，正确的 currentReplyId 为 0
2. 点击任意评论区用户时回复用户，正确的 currentReplyId 为当前点击用户的 id
3. 点击任意评论区用户后再回复文章，正确的 `currentReplyId` 应该为 0

如何去避免 `currentReplyId` 点击任意评论区用户恢复 `currentReplyId` 的正确状态呢？
1. 第一种方案，发出一条评论后，`currentReplyId` 置 0
   1. √
   2. √
   3. ×，此时 `currentReplyId` 仍然会是 当前点击用户的 id
2. 第二种方案，设定每次回复，评论组件，`<post-comment>` 都会获得焦点（注意，按钮点击也不可失去焦点），给 textarea 设置 `@blur` 监听，一旦失去焦点，`currentReplyId` 就会置 0
   1. √
   2. √
   3. √
3. 第三种方案，实际上是第二种方案的完善版，由于焦点获取只能应用于表单元素，即 `<input>`，但我们回复时需要点击发送按钮，此时我们会失去焦点，从而导致 `currentReplyId` 置 0，则回复的第二种情况（）会失败，问题的关键是如何使得点击按钮不失去焦点或者失去焦点后可以知道是回复的第二种情况，实际操控中无法解决这两种问题中的一个
4. 第四种方案，可能获取焦点的方案可能不太合适，不太好操控，选择第四种方案，我们点击 `<message>` 组件时，会 `emit` 一个事件出来，然后再对 `currentReplyId` 赋值，回复逻辑完成后也会直接 `currentReplyId` 置 0，而如果是直接点击 `<post-comment>` 直接将 `currentReplyId` 置 0
   1. √
   2. √
   3. √
5. 第五种方案，第四种方案有缺点就是，用户评论时可能会二次点击评论框，此时 `currentReplyId` 置 0，第二种回复情况失效，解决方案是，回复的时候设置一个 `flag`，如果 flag 为真，则此时再次点击评论区则不会置 0，如果评论区失去焦点则 flag = false 并且置 0，
## 评论系统数据库设计

点赞功能先不做，有如下问题

1. 如果加入 thumb（点赞数）字段，怎么防止重复点赞？如果记录每个点赞人的 id，作为另一个字段（假设为 thumber），那么在上千点赞数的情况就会非常冗余，而且更新评论点赞时还需要查询一下 thumber
2. 文章 id 即是评论区 id，唯一
3. 评论中新增 new_id 字段，代表为某篇文章的评论区，就算评论数量只需找到相应的文章 id 然后计算条数即可
4. 先删去自增属性，改为，在固定文章 id 的情况下，以 1 为起始点，加一一条评论

```SQL
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `comment_id` int NOT NULL COMMENT '评论id',
  `new_id` int COMMENT '文章id',
  `content` text COMMENT '评论内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `commentator_id` int COMMENT '评论者id',
  `commentator_name` varchar(100) COMMENT '评论者名称',
  `commentator_head_url` varchar(100) COMMENT '头像URL',
  `parent_id` int COMMENT '回复人id',
  PRIMARY KEY (`id`),
  KEY `commentator_id` (`commentator_id`),
  CONSTRAINT `commentator_id` FOREIGN KEY (`commentator_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

### proc_commentByInsert

```SQL
CREATE PROCEDURE proc_commentByInsert(
  in newId int,
  in content text,
  in create_time datetime,
  in commentator_id int,
  in commentator_name varchar(100),
  in commentator_head_url varchar(100),
  in parent_id int
)
BEGIN
  declare commentId int;
  select max(comment_id) into commentId from comment where new_id = newId;
  if commentId is NULL then
    set commentId = 1;
  end if;
  INSERT INTO comment(comment_id,new_id,content,create_time,commentator_id,commentator_name,commentator_head_url,parent_id)
  VALUES (commentId,newId,content,create_time,commentator_id,commentator_name,commentator_head_url,parent_id);
END;

call proc_commentByInsert(2,"test","",1005,"庞老闆","http://116.63.152.202:5002/userHead/default_head.png",0);

```

新增 proc_newByInsert（文章插入的存储过程）

基于 Vue 本身渲染 Dom 和加载数据以及 Android webkit 绘图引擎和 PC 端的有所差别，选择使用 Android 展示作者信息栏

9/3 号需要完善的地方

1. 新闻页面中的新闻 item 长按后出现的两个图标的 padding-top 需要增大，图标直接黏顶部了 9 月 3 号 16 点 15 分 解决
2. 新闻加载的有点卡，比如 vue 并没有及时的将图片渲染到位，一些数据也没能成功渲染（而是渲染了原始 HTML 元素，比如 `{{ followStatus ? "+ 关注" : "✔ 已关注" }}`）

9/3 号写了 4，5 个小时啊，算上中午的时间，少说也有个 6，7 小时了！

9/4 号需要完善的地方

1. APP 上的逻辑需要处理好，就是一些交互什么的
2. 比如关注了，需要返回相应的数据什么的
3. 还有很多要写啊

9/7 号需要完善的地方

图片加载的时侯，vue.js 的导入未生效，此时 vue.js 和其他模块组已经导入完成但未生效 9/7 已解决

9/3 号错误

1. 文件路径出错，./html/xx.html 写成 ./htmlxx.html
2. 异步嵌套循环导致每次循环开始都是同一个值，因为最后一异步没有完成，没有插入数据，就已经循环完毕了，所以每次循环都是拿到没插入任何一条数据时的 max(new_id)

实现 HTML 更新脚本（updateHtml.js）

注意 url（后端 API）拼接的规则，不要出现 `hostname//api` 的情况，hostname 后面应该跟单个 `/`

java.lang.IllegalArgumentException: class com.example.newslist.News declares multiple JSON fields named new_owner_id

com.google.gson.JsonSyntaxException: java.lang.NumberFormatException: empty String

基类和后端数据不匹配

android.content.res.Resources$NotFoundException: String resource ID #0x2b

9/9 号

1. 首要任务，关注以后能否查看关注作者的文章（可以先搁置关注后端的实现） 9/10 00 点 33 分
2. 作者信息栏 android 和 vue 两个方案中选择一个
   1. 9/10 01 点 19 分 选择 android 方案，后端获取信息已经实现，但是没有渲染到 android 页面上
3. 次要任务
   1. 点击作者信息跳转到作者信息详情页（私信功能需要罗淳日去完善）
      1. 2021 年 9 月 11 日 00 点 51 分 完成 由于私信逻辑功能未完善，所以只有 UI 跳转，具体 Intent 传递数据未定
   2. 点击关注后关注表新增数据的一系列后端实现
   3. 跳转文章内容并获取到作者信息后需要更新页面相关信息 2021 年 9 月 11 日 00 点 51 分 完成

其他需要完善的，

1. 回复框的解耦，不需要每个评论配置一个回复框
2. 评论区样式修改
3. 评论区点赞功能的实现
4. 评论后需要后台提醒
5. 评论区 android 和 vue 两个方案中选择一个
6. BaseResponse 基类以及 News（后期可能会修改名称为 Article）中对应云端默认值为空的处理
7. article 头像的阴影制作

9/10 由于 9/9 号还有大量的任务没有完成，所以 9/10 号不设置任务

9/11 号

1. 首要任务
   1. 修改 HTML 模板，舍弃原有 HTML 生成头像（记得加入 Git 管理，避免后期可能会返回存在头像的版本）2021 年 9 月 12 日 00 点 51 分
   2. 后端 new 表或者 new 接口返回数据应该有作者名称 2021 年 9 月 11 日 21 点 23 分
   3. 点击关注后关注表新增数据的一系列后端实现以及 UI 交互 2021 年 9 月 12 日 00 点 40 分
2. 次要任务
   1. 跳转到用户页面时的账号和粉丝数量的获取的后端 API 实现 2021 年 9 月 11 日 22 点 06 分
   2. 当封面图为空时，文章标题应该自动延伸至 item 末尾 2021 年 9 月 11 日 22 点 06 分

其他需要完善的，

1. 回复框的解耦，不需要每个评论配置一个回复框
2. 评论区样式修改
3. 评论区点赞功能的实现
4. 评论后需要后台提醒
5. BaseResponse 基类以及 News（后期可能会修改名称为 Article）中对应云端默认值为空的处理
6. article 头像的阴影制作 取消头像阴影制作
7. okhttp 同步请求的实现

9/12 号

1. 主要任务
   1. 回复框的解耦，不需要每个评论配置一个回复框 2021年9月12日23点58分
   2. 评论后需要后台提醒
   3. 评论区样式修改
2. 次要任务
   1. 评论区点赞功能的实现
   2. 点击到用户信息栏后需要返回相应的文章信息
   3. 长按相应的新闻后出现的功能栏实现
      1. 复制链接 2021年9月13日00点22分
      2. 不感兴趣 2021年9月13日00点47分

**其他需要完善的**

1. 需要爬取更多的数据

9/13 号

1. 主要任务
   1. 评论后需要后台提醒（websoket）
   2. 点击到用户信息栏后需要返回相应的文章信息
2. 次要任务
   1. 评论区样式修改（回复框应该在页面底部，fix）
   2. 评论区点赞功能的实现

**其他需要完善的**

1. 需要爬取更多的数据

9/14 号

websoket 进度竟然才刚开始！继续维持原有的任务

# 问题

RecyclerView Item 跳转 Activity https://ask.csdn.net/questions/645359

```
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
```

原因：没有在 AndroidMainfest 中声明 Activity

W/System.err: java.io.IOException: unexpected end of stream on http://116.63.152.202:3002/...

这个错误一般是 Url 写错了，建议访问一下项目所用相关的后端接口

解决 RadioGroup 设置默认选中导致该 RadioButton 一直是选中状态

解决方法：需要控制一个 RadioButton 默认选中，就需要给它设置一个 id，否则会一致默认选中，即使选中到另一个 RadioButton

Failed to connect server!

Url 出了问题

com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 1 column 37 path $.data

BaseResponse 里面的 data 是 object，前后的 Type 和 BaseResponse 不一致

android 虚拟机一直访问失败，连接 websocket 直接跳转到 onFailure()，原因是连接 websocket 使用的链接是 `localhost:port`，这样只会连接到虚拟机本身的 `localhost:port`！！！困扰我一整天，解决方法是将 localhost 改成 10.0.0.2 或者你 cmd 查出来的 ip
