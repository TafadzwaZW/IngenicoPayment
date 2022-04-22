package cn.eas.usdk.demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.usdk.apiservice.aidl.scanner.ScannerData;

import cn.eas.usdk.demo.util.BytesUtil;


public class ScanResultReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceiver | action = " + intent.getAction());
        Log.d(TAG, "scanresult code = " + intent.getStringExtra(ScannerData.SCAN_RESULT));
        Log.d(TAG, "scanresult code hex = " + BytesUtil.bytes2HexString(intent.getStringExtra(ScannerData.SCAN_RESULT).getBytes()));
    }
}
