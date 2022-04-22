package cn.eas.usdk.demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.usdk.apiservice.aidl.update.ResultCode;
import com.usdk.apiservice.aidl.update.UpdateData;
import com.usdk.apiservice.aidl.update.UpdateResult;

import java.util.List;

import cn.eas.usdk.demo.view.update.UpdateManager;


public class UpdateResultReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private List<UpdateResult> list;
    private UpdateManager.UpdateListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceiver | action = " + intent.getAction());

        if (UpdateData.UPDATE_RESULT_ACTION.equals(intent.getAction())) {
            Bundle params = intent.getExtras();
            list = params.getParcelableArrayList(UpdateData.UPDATE_RESULT_LIST);
            long updateId = params.getLong(UpdateData.UPDATE_ID, -1);

            Log.d(TAG, "onReceiver | updateId = " + updateId);

            if (list != null) {
                for (UpdateResult result : list) {
                    Log.d(TAG, "UpdateTaskId= " + result.getUpdateTaskId() +
                            ", code = " + result.getErrorCode() +
                            ", msg = " + result.getErrorMsg() +
                            ", fileName = " + result.getErrorFileName());
                    if (ResultCode.SUCCESS == result.getErrorCode()) {
                        Log.d(TAG, result.getUpdateTaskId() + ", this updateTask update successfully");
                    }
                }
            }

            listener = UpdateManager.getInstance().getUpdateListener();
            if (listener != null) {
                listener.onFinish(updateId, list);
            }
        }
    }
}
