package yun.utills.xunfei;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ContactManager.ContactListener;

/**
 * @author yun
 * @time 2015/03/08
 * 
 *       讯飞语音 工具类 封装讯飞语音主要代码 对外提供 接口 用户自定义 
 *       文字转语音部分 
 *       ToVoice(String text)文字 转 语音text需要转语音的文字 
 *       SpeakPause() 暂停读。。。 
 *       setOnSpeakListener(OnSpeakListener  sol)设置 文字传语音 读的监听器 
 *       setVoicer(String voicer) 设置发音人 
 *       setSpeed(String speed) 设置 读的 语速 默认 50 
 *       setVolume(String volume) 设置 读 音量 默认80
 *       SpeakResumeS() 继续读 
 *       SpeakStop() 停止读
 * 
 *       语音转文字部分 
 *       VoiceTo(TextView show) show 将声音转换的文字 显示在edittext或者textView上当传入
 *       为空的时候  需要通过 听的监听器获取听到后转换的文字listenerContentOver(String content)
 *       setOnListeneListener(OnListenListener lol) 设置语音转文字 听的监听器
 *       setShowDialog(boolean isShowDialog)是否显示语言交互动画  默认为true显示
 *       ListenerPause()停止听 
 *       ListenerCancel() 取消听语音转文字 取消
 * 
 *       语义理解部分 
 *       understander(TextView tv) tv用于显示 理解后的语义 传入null 则需要通过
 *       设置监听器获取理解好的语义
 *       public void understanderShowUI(final TextView tv) 语义理解有界面部分 tv为null则通过监听获取 理解的语义
 *       setOnUnderStanderListener(OnUnderStanderListener osl) 设置语义理解监听器 通过语义理解监听器的 underStood(String understoodContent);可以获取最后理解好的语义 
 *       stopUnderStander() 停止语义理解 
 *       cancelUnderstander() 取消 语义理解
 * 
 *       其他 
 *       upContact()上传联系人
 *        destroyResource() 释放资源释放链接 
 *        public boolean toast = true;是否打印吐司 默认true打印
 * 
 */
public class XunFeiUtill {

	private Context context;
	/**
	 * 将声音转换的文字 显示
	 */
	private TextView show;
	/**
	 * 用于 显示 语音后理解后的语义文本 的容器
	 */
	private TextView tv;

	/**
	 * 是否显示 听的对话框
	 */
	private boolean isShowDialog = true;

	// 语音合成对象
	private SpeechSynthesizer mTts;

	// 默认发音人
	private String voicer = "xiaoyan";

	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;
	/**
	 * 语速
	 */
	private String speed = "50";
	/**
	 * 音量
	 */
	private String volume = "50";
	/**
	 * 听写结束监听器
	 */
	private OnListenListener lol;
	/**
	 * 读结束监听器
	 */
	private OnSpeakListener sol;

	/**
	 * 设置 听 结束监听器
	 * 
	 * @param lol
	 */
	public void setOnListeneListener(OnListenListener lol) {
		this.lol = lol;
	}

	/**
	 * 设置 读 监听器
	 * 
	 * @param sol
	 */
	public void setOnSpeakListener(OnSpeakListener sol) {
		this.sol = sol;
	}

	public XunFeiUtill(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 是否显示 语音交互动画
	 * 
	 * @param isShowDialog
	 */
	public void setShowDialog(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}

	/**
	 * 设置发音人 vixm
	 * 
	 * @param voicer
	 */
	public void setVoicer(String voicer) {
		this.voicer = voicer;
	}

	/**
	 * 设置 读 语速 默认 50
	 * 
	 * @param speed
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
	}

	/**
	 * 设置 读 音量 默认80
	 * 
	 * @param volume
	 */
	public void setVolume(String volume) {
		this.volume = volume;
	}

	/**
	 * 文字 转 语音
	 * 
	 * @param text
	 *            需要转语音的文字
	 */
	public void ToVoice(String text) {

		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(context, mInitListener);

		// 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
		mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);// 设置发音人
		mTts.setParameter(SpeechConstant.SPEED, speed);// 设置语速
		mTts.setParameter(SpeechConstant.VOLUME, volume);// 设置音量
		// 设置音量，范围0~100
		// 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
		// 保存在SD卡需要在AndroidManifest.xml添加写SD卡权限 //如果不需要保存合成音频，注释该行代码
		// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
		// "./sdcard/iflytek.pcm");
		// 3.开始合成
		mTts.startSpeaking(text, new MySynthesizerListener());
	}

	/**
	 * 暂停 读
	 */
	public void SpeakPause() {
		mTts.pauseSpeaking();
		toast("暂停读");
		if (sol != null) {
			sol.speakPause();
		}
	}

	/**
	 * 继续读
	 */
	public void SpeakResumeS() {
		mTts.resumeSpeaking();
		toast("继续读");
		if (sol != null) {
			sol.speakResume();
		}
	}

	/**
	 * 停止 读
	 */
	public void SpeakStop() {
		mTts.stopSpeaking();
		toast("停止读");
		if (sol != null) {
			sol.speakSop();
		}
	}

	class MySynthesizerListener implements SynthesizerListener {

		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {

		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				toast("读完");
				if (sol != null) {
					sol.speakOver();
				}
			} else if (error != null) {
				toast("播读取文字出错" + error.getPlainDescription(true));
			}

		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onSpeakBegin() {
			toast("开始读");
			if (sol != null) {
				sol.speakStart();
			}
		}

		@Override
		public void onSpeakPaused() {
			toast("暂停读");
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			mPercentForPlaying = percent;
			// Toast.makeText("播放进度"+percent, 0).show();
		}

		@Override
		public void onSpeakResumed() {
			toast("读继续");
		}

	}

	private boolean biaodian = false;

	/**
	 * 设置是否显示标点 默认false 无标点符号
	 * 
	 * @param biaodian
	 */
	public void setBiaodian(boolean biaodian) {
		this.biaodian = biaodian;
	}

	/**
	 * 语音 转 文字
	 * 
	 * @param show
	 *            文字显示在edittext 没有则通过听写监听器获取
	 * @return
	 */
	public void VoiceTo(TextView show) {
		this.show = show;
		mIat = SpeechRecognizer.createRecognizer(context, null);
		iatDialog = new RecognizerDialog(context, null);

		// 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
		// 设置标点符号
		if (biaodian) {
			mIat.setParameter(SpeechConstant.ASR_PTT, "1");
		} else {
			mIat.setParameter(SpeechConstant.ASR_PTT, "0");
		}
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
		// show.setText(null);// 清空显示内容
		builder.delete(0, builder.length());// 清空显示内容
		// 设置参数
		if (isShowDialog) {
			// 显示听写对话框
			iatDialog.setListener(recognizerDialogListener);
			iatDialog.show();
		} else {
			// 不显示听写对话框,开始听写
			mIat.startListening(new MyRecognizerListener());
		}
	}

	/**
	 * 语音转文字 暂停
	 */
	public void ListenerPause() {
		mIat.stopListening();
		toast("停止听");
	}

	/**
	 * 语音转文字 取消
	 */
	public void ListenerCancel() {
		mIat.cancel();
		toast("听取消");
		if (lol != null) {
			lol.listenerCancel();
		}
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				toast("初始化失败,错误码：" + code);
			}
		}
	};
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			if (show != null) {
				show.append(text);
				if (show instanceof EditText) {
					((EditText) show).setSelection(show.length());
				}
			}
			builder.append(text);
			if (isLast) {
				toast("听over");
				/**
				 * 说出的所有内容
				 */
				String content = builder.toString();
				if (lol != null) {
					lol.listenerContentOver(content);
				}

			}
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			toast("出错" + error.getPlainDescription(true));
		}

	};
	private StringBuilder builder = new StringBuilder();
	private SpeechRecognizer mIat;

	class MyRecognizerListener implements RecognizerListener {

		// 听写结果回调接口(返回Json格式结果，用户可参见附录)；
		// 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
		// 关于解析Json的代码可参见MscDemo中JsonParser类；
		// isLast等于true时会话结束。
		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			if (show != null) {
				show.append(text);
				if (show instanceof EditText) {
					((EditText) show).setSelection(show.length());
				}
			}
			builder.append(text);
			if (isLast) {
				toast("听over");
				/**
				 * 讯飞听到的所有内容
				 */
				String content = builder.toString();
				if (lol != null) {
					lol.listenerContentOver(content);
				}

			}
		}

		@Override
		public void onBeginOfSpeech() {
			toast("开始听");
			if (lol != null) {
				lol.listenerStart();
			}
		}

		@Override
		public void onEndOfSpeech() {
			toast("结束听");
			if (lol != null) {
				lol.listenerOver();
			}
		}

		@Override
		public void onError(SpeechError error) {
			error.getPlainDescription(true);// 获取错误码描述
			toast(error.getPlainDescription(true));
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onVolumeChanged(int volume) {
			// Toast.makeText("当前正在说话，音量大小：" + volume, 0).show();
		}
	}

	public interface OnListenListener {
		public void listenerStart();

		/**
		 * @param content
		 *            听到的所有内容 语音转文字的结果
		 */
		public void listenerContentOver(String content);

		public void listenerCancel();

		public void listenerOver();
	}

	public interface OnSpeakListener {
		public void speakStart();

		public void speakResume();

		public void speakSop();

		public void speakPause();

		public void speakOver();
	}

	/**
	 * 是否允许打印吐司
	 */
	public boolean toast = true;

	public void toast(String message) {
		if (toast) {
			Toast.makeText(context, message, 0).show();
		}
	}

	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	int ret = 0;// 函数调用返回值

	/**
	 * public void understanderShowUI(final TextView tv) 语义理解 有界面部分
	 * 
	 * @param tv
	 */
	public void understanderShowUI(final TextView tv) {
		mIat = SpeechRecognizer.createRecognizer(context, null);
		iatDialog = new RecognizerDialog(context, null);
		// 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
		// 设置标点符号
		if (biaodian) {
			mIat.setParameter(SpeechConstant.ASR_PTT, "1");
		} else {
			mIat.setParameter(SpeechConstant.ASR_PTT, "0");
		}
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
		// 语义理解
		mIat.setParameter("asr_sch", "1");

		mIat.setParameter(SpeechConstant.NLP_VERSION, "2.0");

		iatDialog.setListener(new RecognizerDialogListener() {

			@Override
			public void onResult(final RecognizerResult result, boolean arg1) {
				if (null != result) {
					// 显示
					String text = result.getResultString();
					System.out.println(text);
					if (tv != null) {
						tv.setText(text);
					}
					if (osl != null) {
						osl.underStood(text);
					}
				} else {
				}

			}

			@Override
			public void onError(SpeechError arg0) {
				// TODO Auto-generated method stub

			}
		});
		iatDialog.show();
	}

	/**
	 * 语义理解
	 * 
	 * @param tv
	 *            用于显示 理解后的语义 传入null 则需要通过 设置监听器获取理解好的语义
	 */
	public void understander(TextView tv) {
		this.tv = tv;
		// TODO
		// 初始化对象
		mSpeechUnderstander = SpeechUnderstander.createUnderstander(context,
				speechUnderstanderListener);
		// 设置参数
		setParam();

		if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
			mSpeechUnderstander.stopUnderstanding();
			toast("停止录音");
		} else {
			ret = mSpeechUnderstander.startUnderstanding(mRecognizerListener);
			if (ret != 0) {
				toast("语义理解失败,错误码:" + ret);
			} else {
				// showTip(getString(R.string.text_begin));
			}
		}
	}

	/**
	 * 识别回调。
	 */
	private SpeechUnderstanderListener mRecognizerListener = new SpeechUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {
			if (null != result) {
				// 显示
				String text = result.getResultString();
				System.out.println(text);
				if (tv != null) {
					tv.setText(text);
				}
				if (osl != null) {
					osl.underStood(text);
				}

			} else {
				toast("识别结果不正确。");
			}
		}

		@Override
		public void onVolumeChanged(int v) {
			toast("onVolumeChanged：" + v);
		}

		@Override
		public void onEndOfSpeech() {
			toast("onEndOfSpeech");
		}

		@Override
		public void onBeginOfSpeech() {
			toast("onBeginOfSpeech");
		}

		@Override
		public void onError(SpeechError error) {
			toast("onError Code：" + error.getErrorCode());
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}
	};

	private void setParam() {
		// 设置语言
		mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语音前端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000");
		// 设置语音后端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000");
		// 设置标点符号
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "1");
		// 设置音频保存路径
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
	}

	/**
	 * 初始化监听器（语音到语义）。
	 */
	private InitListener speechUnderstanderListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				toast("初始化失败,错误码：" + code);
			}
		}
	};

	/**
	 * 取消 语义理解
	 */
	public void cancelUnderstander() {
		mSpeechUnderstander.cancel();
		if (osl != null) {
			osl.cancelUnderStander();
		}
	}

	/**
	 * 停止 语义理解
	 */
	public void stopUnderStander() {
		mSpeechUnderstander.stopUnderstanding();
		if (osl != null) {
			osl.stopUnderStanding();
		}
	}

	private OnUnderStanderListener osl;
	private RecognizerDialog iatDialog;

	/**
	 * 设置 语义理解监听器
	 * 
	 * @param osl
	 */
	public void setOnUnderStanderListener(OnUnderStanderListener osl) {
		this.osl = osl;
	}

	public interface OnUnderStanderListener {
		/**
		 * 所理解的 内容
		 * 
		 * @param understoodContent
		 */
		public void underStood(String understoodContent);

		/**
		 * 取消了 语义理解
		 */
		public void cancelUnderStander();

		/**
		 * 停止了语义理解
		 */
		public void stopUnderStanding();
	}

	// ================上传联系人======================================

	/**
	 * 上传联系人
	 */
	public void upContact() {
		toast("上传联系人");
		ContactManager mgr = ContactManager.createManager(context,
				mContactListener);
		mgr.asyncQueryAllContactsName();
	}

	/**
	 * 上传联系人/词表监听器。
	 */
	private LexiconListener lexiconListener = new LexiconListener() {

		@Override
		public void onLexiconUpdated(String lexiconId, SpeechError error) {
			if (error != null) {
				toast(error.toString());
			} else {
				toast("上传成功");
			}
		}
	};
	/**
	 * 获取联系人监听器。
	 */
	private ContactListener mContactListener = new ContactListener() {
		@Override
		public void onContactQueryFinish(String contactInfos, boolean changeFlag) {
			// 注：实际应用中除第一次上传之外，之后应该通过changeFlag判断是否需要上传，否则会造成不必要的流量.
			if (changeFlag) {
				//当时 漏了这句 上传失败
				mIat = SpeechRecognizer.createRecognizer(context, null);
				// 指定引擎类型
				mIat.setParameter(SpeechConstant.ENGINE_TYPE,
						SpeechConstant.TYPE_CLOUD);
				mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
				ret = mIat.updateLexicon("contact", contactInfos,
						lexiconListener);
				if (ret != ErrorCode.SUCCESS)
					toast("上传联系人失败：" + ret);
			} else {
				toast("联系人没变化无需上传 ");
			}
		}
	};

	/**
	 * 退出时 释放资源 链接
	 */
	public void destroyResource() {
		if (mSpeechUnderstander != null) {
			mSpeechUnderstander.cancel();
			mSpeechUnderstander.destroy();
		}
		if (mIat != null) {
			mIat.cancel();
			mIat.destroy();
		}
		if (mTts != null) {
			mTts.destroy();
		}
		if (iatDialog != null) {
			iatDialog.destroy();
		}
	}
}
