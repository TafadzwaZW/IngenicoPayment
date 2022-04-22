package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.decodeengine.DecodeEngineError;
import com.usdk.apiservice.aidl.decodeengine.UDecodeEngine;

import java.io.IOException;
import java.io.InputStream;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BmpUtil;

public class DecodeEngineActivity extends BaseDeviceActivity{
    private UDecodeEngine decodeEngine;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_decodeengine);
        setTitle("DecodeEngine Module");
    }

    @Override
    protected void onResume() {
        super.onResume();

        initDeviceInstance();

        try {
            int ret = decodeEngine.init(new Bundle());
            if (DecodeEngineError.SUCCESS != ret) {
                outputRedText("init fail, ret = " + ret);
                return;
            }

            outputBlackText("init success");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    protected void initDeviceInstance() {
        decodeEngine = DeviceHelper.me().getDecodeEngine();
    }

    public void decode(View view) {
        try {
            InputStream inputStream = getAssets().open("qrcode.bmp");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            BmpUtil.GreyBmpData bmpData = BmpUtil.getGreyBmpData(buffer);
            if (bmpData == null) {
                outputRedText("getBmpGreyData failed");
                return;
            }
            String result = decodeEngine.decode(bmpData.data, bmpData.width, bmpData.height);

            outputBlackText("data.length = " + bmpData.data.length + " width = " + bmpData.width + " height = " + bmpData.height);
            outputBlackText("decode result = " + result);

        } catch (RemoteException | IOException e) {
            e.printStackTrace();
            outputRedText("decode fail, error = " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        try {
            decodeEngine.exit();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
