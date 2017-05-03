package com.chestnut.Share.wxapi;

import android.app.Activity;

import com.chestnut.Common.utils.LogUtils;
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

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

    private boolean OpenLog = true;
    private String TAG = "WXEntryActivity";

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.w(OpenLog,TAG,"OpenId:"+baseReq.openId);
        LogUtils.w(OpenLog,TAG,"checkArgs:"+baseReq.checkArgs());
    }

    @Override
    public void onResp(BaseResp baseResp) {

    }
}
