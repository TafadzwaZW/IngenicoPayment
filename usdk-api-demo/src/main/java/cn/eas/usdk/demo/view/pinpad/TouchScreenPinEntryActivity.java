package cn.eas.usdk.demo.view.pinpad;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import com.usdk.apiservice.aidl.pinpad.KAPId;
import com.usdk.apiservice.aidl.pinpad.KeyHandle;
import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.OnTouchScreenPinEntryListener;
import com.usdk.apiservice.aidl.pinpad.PinTriggerMode;
import com.usdk.apiservice.aidl.pinpad.PinpadData;
import com.usdk.apiservice.aidl.pinpad.SoftKeyLayout;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;

public class TouchScreenPinEntryActivity extends BasePinpadActivity {
    private final String TAG = "TouchScreenPinEntry";
    private TextView btn1;
    private TextView btn2;
    private TextView btn3;
    private TextView btn4;
    private TextView btn5;
    private TextView btn6;
    private TextView btn7;
    private TextView btn8;
    private TextView btn9;
    private TextView btn0;
    private TextView btnCancel;
    private TextView btnConfirm;
    private TextView btnClear;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_touch_screen_pin_entry);
        setTitle("Touch Screen Pin Entry");
        initView();
        outputRedText("Please confirm whether the MK/SK pin key has been imported");
        open();
    }

    private void initView() {
        btn0 = findViewById(R.id.btn_0);
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);
        btn8 = findViewById(R.id.btn_8);
        btn9 = findViewById(R.id.btn_9);
        btnCancel = findViewById(R.id.btn_cancel);
        btnClear = findViewById(R.id.btn_clear);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    public void startImpairedVisualPin(View v) {
        outputText(">>>>>>startImpairedVisualPin");
        Bundle inData = new Bundle();
        fillPinCfg(inData);
        fillTModeParam(inData, true);
        inData.putParcelable(PinpadData.SOFT_KEY_LAYOUT, generateSoftKeyLayout());
        startPinInput(inData);
    }

    private void fillPinCfg(Bundle inData) {
        inData.putByte(PinpadData.PIN_ENC_MODE, (byte) 0);
        inData.putInt(PinpadData.TIMEOUT, 300);
        inData.putInt(PinpadData.BETWEEN_PINKEY_TIMEOUT, 30);
        inData.putByteArray(PinpadData.PIN_LIMIT, new byte[] {0, 4});
        inData.putString(PinpadData.CARD_NO, "6224242912345678901");
    }

    private void fillTModeParam(Bundle inData, boolean isImpairedVisualPin) {
        inData.putInt(PinpadData.T_MODE, 1);
        inData.putInt(PinpadData.PIN_TRIGGER_MODE, isImpairedVisualPin ? PinTriggerMode.IMPAIRED_VISUAL : PinTriggerMode.NORMAL);
        inData.putInt(PinpadData.T_1ST_DOWN_MS, 1000);
        inData.putInt(PinpadData.T_2ND_DOWN_MS, 1000);
        inData.putInt(PinpadData.T_UP_MS, 1000);
        inData.putInt(PinpadData.HITS_NUM, 2);
        inData.putInt(PinpadData.MAX_KEEP_ACTIVE_MS, 3000);
        inData.putInt(PinpadData.MIN_REPORT_MOVE_MS, 300);
        inData.putInt(PinpadData.MISC, 2);
    }

    private SoftKeyLayout generateSoftKeyLayout() {
        int[] layoutOutLocation = new int[2];
        btnConfirm.getLocationOnScreen(layoutOutLocation);
        int width = layoutOutLocation[0] + btnConfirm.getWidth();
        btn0.getLocationOnScreen(layoutOutLocation);
        int height = layoutOutLocation[1] + btn0.getHeight();
        btn1.getLocationOnScreen(layoutOutLocation);
        SoftKeyLayout softKeyLayout = new SoftKeyLayout(layoutOutLocation[0], layoutOutLocation[1], width, height);
        createBtnLayout(softKeyLayout, btn0, SoftKeyLayout.PIN_PAD_KEY_0);
        createBtnLayout(softKeyLayout, btn1, SoftKeyLayout.PIN_PAD_KEY_1);
        createBtnLayout(softKeyLayout, btn2, SoftKeyLayout.PIN_PAD_KEY_2);
        createBtnLayout(softKeyLayout, btn3, SoftKeyLayout.PIN_PAD_KEY_3);
        createBtnLayout(softKeyLayout, btn4, SoftKeyLayout.PIN_PAD_KEY_4);
        createBtnLayout(softKeyLayout, btn5, SoftKeyLayout.PIN_PAD_KEY_5);
        createBtnLayout(softKeyLayout, btn6, SoftKeyLayout.PIN_PAD_KEY_6);
        createBtnLayout(softKeyLayout, btn7, SoftKeyLayout.PIN_PAD_KEY_7);
        createBtnLayout(softKeyLayout, btn8, SoftKeyLayout.PIN_PAD_KEY_8);
        createBtnLayout(softKeyLayout, btn9, SoftKeyLayout.PIN_PAD_KEY_9);
        createBtnLayout(softKeyLayout, btnCancel, SoftKeyLayout.PIN_PAD_KEY_CANCEL);
        createBtnLayout(softKeyLayout, btnClear, SoftKeyLayout.PIN_PAD_KEY_CLEAR);
        createBtnLayout(softKeyLayout, btnConfirm, SoftKeyLayout.PIN_PAD_KEY_ENTER);
        return softKeyLayout;
    }

    private void createBtnLayout(SoftKeyLayout softKeyLayout, TextView view, int keyCode) {
        int[] layoutOutLocation = new int[2];
        view.getLocationOnScreen(layoutOutLocation);
        softKeyLayout.addPinBtnLayout(new SoftKeyLayout.PinButtonLayout(layoutOutLocation[0], layoutOutLocation[1], view.getWidth(), view.getHeight(), keyCode));
    }

    private void startPinInput(Bundle inData) {
        try {
            pinpad.startTouchScreenPinEntry(getKeyHandle(KEYID_PIN), inData, new OnTouchScreenPinEntryListener.Stub() {
                @Override
                public void onEvent(int event, Bundle bundle) throws RemoteException {
                    outputBlueText(">>onEven t | event = " + event + ", bundle.hover_target = " + bundle.getInt("hover_target"));
                }

                @Override
                public void onInput(int len, int key) throws RemoteException {
                    outputBlueText(">>onInput | len = " + len + ", key = " + key);
                }

                @Override
                public void onConfirm(byte[] data, boolean isNonePin) throws RemoteException {
                    if (data != null) {
                        outputBlueText(">>onConfirm | data = " + new String(data) + ", isNonePin = " + isNonePin);
                    } else {
                        outputBlueText(">>onConfirm | data is null" + ", isNonePin = " + isNonePin);
                    }
                }

                @Override
                public void onCancel() throws RemoteException {
                    outputBlueText(">>onCancel");

                }

                @Override
                public void onError(int code) throws RemoteException {
                    outputBlueText(">>onError | code = " + code);

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected KeyHandle getKeyHandle(int keyId) {
        KeyHandle keyHandle = new KeyHandle();
        keyHandle.setKapId(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM));
        keyHandle.setKeySystem(KeySystem.KS_MKSK);
        keyHandle.setKeyId(keyId);
        return keyHandle;
    }

    public void startNormalPin(View v) {
        outputText(">>>>>>startNormalPin");
        Bundle inData = new Bundle();
        fillPinCfg(inData);
        fillTModeParam(inData, false);
        inData.putParcelable(PinpadData.SOFT_KEY_LAYOUT, generateSoftKeyLayout());
        startPinInput(inData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
    }
}