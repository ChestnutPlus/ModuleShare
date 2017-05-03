package com.chestnut.Share.WX;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.IntDef;

import com.chestnut.Common.utils.ConvertUtils;
import com.chestnut.Common.utils.ImageUtils;
import com.chestnut.Common.utils.LogUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/5/3 22:41
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */

public class WXHelper {

    public static final int TIME_LINE = SendMessageToWX.Req.WXSceneTimeline;
    public static final int DIALOG = SendMessageToWX.Req.WXSceneSession;
    public static final int BACKUP = SendMessageToWX.Req.WXSceneFavorite;
    @IntDef({TIME_LINE, DIALOG, BACKUP})
    @Retention(RetentionPolicy.SOURCE)
    private @interface SCENE {}

    private IWXAPI iwxapi;
    private boolean OpenLog = true;
    private String TAG = "WXHelper";

    /**
     * 初始化
     * @param context   上下文
     * @param APP_ID    APP_ID
     */
    public WXHelper(Context context, String APP_ID) {
        if (iwxapi==null) {
            iwxapi = WXAPIFactory.createWXAPI(context, APP_ID, true);
            iwxapi.registerApp(APP_ID);
        }
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
     * 分享图片
     * @param context   上下文
     * @param src   图片的bitmap
     * @param frameWidthDp  边框大小
     * @param THUMB_SIZE    缩略图大小
     * @param scene 场景
     * @return  是否成功
     */
    public boolean sharePic(Context context, Bitmap src, float frameWidthDp, int THUMB_SIZE, @SCENE int scene) {
        try {
            Bitmap bmp = ImageUtils.addFrame(src, ConvertUtils.dp2px(context,frameWidthDp), Color.WHITE,true);
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
