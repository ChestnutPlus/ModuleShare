package com.chestnut.Share.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.chestnut.Common.ui.Toastc;
import com.chestnut.Common.utils.LogUtils;
import com.chestnut.Share.R;
import com.chestnut.Share.WX.WXHelper;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/5/3 23:40
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    /*  参考
            1.  http://m.blog.csdn.net/article/details?id=44887341
    * */
    private boolean OpenLog = true;
    private String TAG = "WXEntryActivity";
    private WXHelper wxHelper;
    private Toastc toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx);
        toast = new Toastc(this, Toast.LENGTH_LONG);
        LogUtils.w(OpenLog,TAG,"onCreate");
//        wxHelper = new WXHelper(this,this.getString(R.string.wechatAppID));
        wxHelper.registerWXCallBack(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.w(OpenLog,TAG,"onNewIntent");
        wxHelper.registerWXCallBack(getIntent(),this);
    }

    /**
     * 微信主动请求我们
     * @param baseReq   baseReq
     */
    @Override
    public void onReq(BaseReq baseReq) {
        //LogUtils.w(OpenLog,TAG,"onReq-OpenId:"+baseReq.openId);
        //LogUtils.w(OpenLog,TAG,"onReq-checkArgs:"+baseReq.checkArgs());
        //try {
        //    Intent intent = new Intent(this, MainActivity.class);
        //    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //    this.startActivity(intent);
        //} catch (Exception e) {
        //    LogUtils.w(OpenLog,TAG,"onReq:"+e.getMessage());
        //}
    }

    @Override
    public void onResp(BaseResp baseResp) {
        toast.setText(wxHelper.getCallbackStrOnResp(baseResp)).show();
        finish();
    }
}
