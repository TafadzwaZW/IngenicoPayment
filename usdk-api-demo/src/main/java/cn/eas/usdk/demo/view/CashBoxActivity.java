package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.cashbox.BoxError;
import com.usdk.apiservice.aidl.cashbox.OnBoxListener;
import com.usdk.apiservice.aidl.cashbox.UCashBox;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.DialogUtil;

public class CashBoxActivity extends BaseDeviceActivity {

    private UCashBox cashBox;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cashbox);
        setTitle("CashBox Module");

        DialogUtil.showOption(this,
                "Warm prompt",
                "Make sure the cash box is connected?",
                new DialogUtil.OnChooseListener() {
                    @Override
                    public void onConfirm() {
                        initDeviceInstance();
                    }

                    @Override
                    public void onCancel() {
                        finish();
                    }
        });
    }

    protected void initDeviceInstance() {
        cashBox = DeviceHelper.me().getCashBox();
    }

    public void open(View v) {
        outputBlueText(">>> open ");
        try {
            cashBox.open(new OnBoxListener.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    outputText("=> open sucess");
                }

                @Override
                public void onFail(int error) throws RemoteException {
                    outputRedText("=> open fail : " + getErrorDetail(error));
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case BoxError.ERROR_DEVICE_NOT_EXIST: message = "Device not exist"; break;
            case BoxError.ERROR_FAIL: message = "Operate fail"; break;
            case BoxError.ERROR_IS_ALREADY_OPEN: message = "Is already opened"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
