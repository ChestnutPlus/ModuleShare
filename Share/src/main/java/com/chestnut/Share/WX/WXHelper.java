package com.chestnut.Share.WX;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.IntDef;

import com.chestnut.Common.utils.ImageUtils;
import com.chestnut.Common.utils.LogUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/5/3 22:41
 *     desc  :  封装了WX分享的代码
 *          集成微信分享：
 *              1.  注册微信开放平台（不是开发平台）
 *              2.  创建应用，通过审核：包名要填自己项目的包名
 *              3.  获取app的签名信息：app签名，装手机上，装个查看签名信息的app，填到应用里面，通过审核
 *              4.  得到appId，即可，然后集成SDK，使用以下代码
 *              5.  获取微信分享回调：
 *                  1）包名一定要正确，具体查看gradle中的配置
 *                  2）回调的Java类一定是：public class WXEntryActivity extends Activity implements IWXAPIEventHandler
 *                      名字不能变！
 *                  3）然后，在onCreate中和onNewIntent中调用：public void registerWXCallBack(Intent intent, IWXAPIEventHandler iwxapiEventHandler)
 *                  4）配置Manifest：
 *                      <activity android:name=".wxapi.WXEntryActivity"
 *                          android:launchMode="singleTop"
 *                          android:label="@string/app_name"
 *                          android:exported="true"/>
 *                  5）分享返回自己app的时候，会调起这个Activity，为了让用户察觉不出来，
 *                      我们在获取到结果后，kill this activity。
 *                      还有我们可以设置Activity的主题为透明：android:theme="@android:style/Theme.Translucent"
 *                      在布局文件中，我们保险起见，把控件都设置成：android:background="@android:color/transparent"
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */

public class WXHelper {

    /*  分享类型：
    *       朋友圈，朋友对话，收藏
    * */
    public static final int TIME_LINE = SendMessageToWX.Req.WXSceneTimeline;
    public static final int DIALOG = SendMessageToWX.Req.WXSceneSession;
    public static final int BACKUP = SendMessageToWX.Req.WXSceneFavorite;
    @IntDef({TIME_LINE, DIALOG, BACKUP})
    @Retention(RetentionPolicy.SOURCE)
    private @interface SCENE {}

    private IWXAPI iwxapi;
    private boolean OpenLog = true;
    private String TAG = "WXHelper";
    private String APP_ID;

    /**
     * 初始化
     * @param context   上下文
     * @param APP_ID    APP_ID
     */
    public WXHelper(Context context, String APP_ID) {
        if (iwxapi==null) {
            iwxapi = WXAPIFactory.createWXAPI(context, APP_ID, true);
            iwxapi.registerApp(APP_ID);
            this.APP_ID = APP_ID;
        }
    }

    /**
     * 注册回调
     *      请在：onCreate中调用
     *      和在：onNewIntent中调用
     * @param intent    intent
     * @param iwxapiEventHandler    callback
     */
    public void registerWXCallBack(Intent intent, IWXAPIEventHandler iwxapiEventHandler) {
        iwxapi.registerApp(APP_ID);
        iwxapi.handleIntent(intent,iwxapiEventHandler);
    }

    /**
     * 获取解析结果：分享成功/失败
     * @param baseResp  结果
     * @return  结果
     */
    public String getCallbackStrOnResp(BaseResp baseResp) {
        String result;
        switch (getCallbackIntCodeOnResp(baseResp)) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "分享失败";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享失败";
                break;
            default:
                result = "分享失败";
                break;
        }
        return result;
    }

    /**
     * 获取解析分享的结果
     * @param baseResp  结果
     * @return  int code
     */
    public int getCallbackIntCodeOnResp(BaseResp baseResp) {
        if(baseResp instanceof SendAuth.Resp){
            SendAuth.Resp newResp = (SendAuth.Resp) baseResp;
            //获取微信传回的code
            String code = newResp.code;
            LogUtils.w(OpenLog,TAG,"code:"+code);
        }
        return baseResp.errCode;
    }

    /**
     * 分享文字
     * @param txt   文字
     * @param scene 场景
     * @return  true/false
     */
    public boolean shareTxt(String txt, @SCENE int scene) {
        //初始化一个 WXTextObject 对象
        WXTextObject textObject  = new WXTextObject();
        textObject.text = txt;
        //用上面的 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = txt;
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = System.nanoTime()+"";//唯一标识一个请求
        req.message = msg;
        req.scene = scene;
        //发送
        return iwxapi.sendReq(req);
    }

    /**
     * 分享一个网页
     *
     * @param httpUrl     连接
     * @param scene        分享类型，朋友圈、收藏、好友
     * @param icon        连接前显示的图标
     * @param title       别人看到的标题
     * @param description 别人看到的描述
     */
    public void shareWebPage(String httpUrl, @SCENE int scene, Bitmap icon, String title, String description) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = httpUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        msg.thumbData = bmpToByteArray(icon,true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = System.nanoTime()+"";
        req.message = msg;
        req.scene = scene;
        iwxapi.sendReq(req);
    }

    /**
     * 分享图片
     * @param src   图片的bitmap
     * @param frameWidthDp  边框大小
     * @param THUMB_SIZE    缩略图大小
     * @param scene 场景
     * @return  是否成功
     */
    public boolean sharePic(Bitmap src, float frameWidthDp, int THUMB_SIZE, @SCENE int scene) {
        try {
            Bitmap bmp = ImageUtils.addFrame(src, (int) frameWidthDp, Color.WHITE,true);
            WXImageObject imgObj = new WXImageObject(bmp);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;

            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            //这里进行recycle的话，会在分享后，
            //bmp.recycle();
            msg.thumbData = bmpToByteArray(thumbBmp, true);  // 设置缩略图

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = System.nanoTime()+"";
            req.message = msg;
            req.scene = scene;
            return iwxapi.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(OpenLog,TAG,"sharePic:"+e.getMessage());
            return false;
        }
    }

    public void close() {
        iwxapi = null;
    }

    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
