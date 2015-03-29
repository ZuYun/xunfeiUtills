# xunfeiUtills
讯飞语音



##xunfeilibrary使用步骤（需要注意的是 开发包需要重新导入到library(key对应开发包的) 或者在工程中使用library中的开发包对应的appid就可以）

1，引用xunfeilibrary
	
	// 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn 
	SpeechUtility.createUtility(this, SpeechConstant.APPID +"=讯飞开发包对应的appid");
	xunfei = new XunFeiUtill(this);

2，申请讯飞语音的appid  导入开发包中的assete文件(界面所需的图片)

3，设置appid 在清单文件配置权限
	
	配置权限
	<!-- 连接网络权限，用于执行云端语音能力 -->
    <uses-permission android:name="android.permission.INTERNET" /> <!-- 获取手机录音机使用权限，听写、识别、语义理解需要用到此权限 -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" /> <!-- 读取网络信息状态 -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> <!-- 获取当前wifi状态 -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> <!-- 允许程序改变网络连接状态 -->
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" /> <!-- 读取手机信息权限 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" /> <!-- 读取联系人权限，上传联系人需要用到此权限 -->
    <uses-permission android:name="android.permission.READ_CONTACTS" />

讯飞语音 工具类 封装讯飞语音主要代码 对外提供 接口 用户自定义 文字转语音部分 

	destroyResource()退出时 释放资源 链接
	public boolean toast = true;是否打印吐司 默认true打印

文字转语音部分

	setOnSpeakListener(OnSpeakListener sol)设置 文字传语音 读的监听器
	setVoicer(String voicer) 设置发音人
	setSpeed(String speed) 设置 读 的 语速  默认 50
	setVolume(String volume) 设置 读 音量 默认80
	ToVoice(String text)文字 转 语音 text 需要转语音的文字
	SpeakPause() 暂停读。。。
	SpeakResumeS() 继续读
	SpeakStop() 停止读

语音转文字部分
	
	setBiaodian(boolean biaodian)设置是否显示标点
	setOnListeneListener(OnListenListener lol) 设置语音转文字 听的监听器
	VoiceTo(Text Viewshow) show  将声音转换的文字 显示在edittext或者textView上 当传入为空的时候  需要通过 听的监听器获取听到后转换的文字listenerContentOver(String content)
	setShowDialog(boolean isShowDialog)是否显示语言交互动画 默认为true显示
	ListenerPause() 停止听
	ListenerCancel() 取消听  语音转文字 取消

语义理解部分
 
	 public void understanderShowUI(final TextView tv) 语义理解 有界面部分 tv为null则通过监听获取 理解的语义
	setOnUnderStanderListener(OnUnderStanderListener osl) 设置语义理解监听器  underStood(String understoodContent);可以获取最后理解好的语义
	understander(TextView tv) tv用于显示 理解后的语义 传入null 则需要通过 设置监听器获取理解好的语义
	stopUnderStander()  停止 语义理解
	cancelUnderstander()  取消 语义理解

其他

	upContact()上传联系人
	destroyResource() 释放资源释放链接 
	public boolean toast = true;//是否打印吐司  默认true 打印

	public void toast(Context context, String message) {
		if (toast) {
			Toast.makeText(context, message, 0).show();
		}
	}



语义返回的json数据

	{"semantic":{"slots":{"endLoc":{"type":"LOC_POI","city":"CURRENT_CITY","poi":"世界之窗"},"startLoc":{"type":"LOC_POI","city":"CURRENT_CITY","poi":"东部华侨城"}}},"rc":0,"operation":"ROUTE","service":"map","text":"东部华侨城去世界之窗。"}
	
	{"semantic":{"slots":{"name":"李世龙"}},"rc":0,"operation":"CALL","service":"telephone","text":"打电话给李世龙。"}
	
	{"semantic":{"slots":{"location":{"type":"LOC_POI","city":"CURRENT_CITY","poi":"CURRENT_POI"}}},"rc":0,"operation":"POSITION","service":"map","text":"我在哪里?"}
