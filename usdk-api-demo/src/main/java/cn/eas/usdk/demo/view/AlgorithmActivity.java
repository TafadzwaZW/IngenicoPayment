package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.view.View;

import com.usdk.apiservice.aidl.algorithm.AlgError;
import com.usdk.apiservice.aidl.algorithm.AlgMode;
import com.usdk.apiservice.aidl.algorithm.UAlgorithm;
import com.usdk.apiservice.aidl.data.BytesValue;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BytesUtil;

public class AlgorithmActivity extends BaseDeviceActivity {

    private UAlgorithm algorithm;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_algorithm);
        setTitle("Algorithm Module");
    }

    protected void initDeviceInstance() {
        algorithm = DeviceHelper.me().getAlgorithm();
    }

    public void TDES(View v) {
        try {
            outputBlueText(">>> TDES ");
            byte[] key = BytesUtil.hexString2Bytes("31313131313131313131313131313131");
            byte[] dataIn = BytesUtil.hexString2Bytes("1234567890");
            long mode = AlgMode.EM_alg_TDESENCRYPT | AlgMode.EM_alg_TDESTECBMODE;
            BytesValue encResult = new BytesValue();
            int ret = algorithm.TDES(mode, key, dataIn, encResult);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("Encrypt result : " + encResult.toHexString());

            BytesValue decResult = new BytesValue();
            mode = AlgMode.EM_alg_TDESDECRYPT | AlgMode.EM_alg_TDESTECBMODE;
            ret = algorithm.TDES(mode, key, encResult.getData(), decResult);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("Decrypt result : " + decResult.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void AES(View v) {
        try {
            outputBlueText(">>> AES ");
            byte[] key = BytesUtil.hexString2Bytes("31313131313131313131313131313131");
            byte[] dataIn = BytesUtil.hexString2Bytes("1234567890");
            long mode = AlgMode.EM_alg_AES_ENCRYPT | AlgMode.EM_alg_AES_ECBMODE;
            BytesValue encResult = new BytesValue();
            int ret = algorithm.AES(mode, key, dataIn, encResult);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("ECB Encrypt result : " + encResult.toHexString());

            mode = AlgMode.EM_alg_AES_DECRYPT | AlgMode.EM_alg_AES_ECBMODE;
            BytesValue decResult = new BytesValue();
            ret = algorithm.AES(mode, key, encResult.getData(), decResult);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("ECB Decrypt result : " + decResult.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getRandom(View v) {
        try {
            int length = 5;
            outputBlueText(">>> getRandom | length = " + length);

            BytesValue dataOut = new BytesValue();
            int ret = algorithm.getRandom(length, dataOut);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText(" result : " + dataOut.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void SMS4(View v) {
        try {
            outputBlueText(">>> SMS4");
            byte[] key = BytesUtil.hexString2Bytes("31313131313131313131313131313131");
            byte[] dataIn = BytesUtil.hexString2Bytes("1234567890");
            long mode = AlgMode.EM_alg_SMS4ENCRYPT | AlgMode.EM_alg_SMS4TECBMODE;
            BytesValue encResult = new BytesValue();
            int ret = algorithm.SMS4(mode, key, dataIn, encResult);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("ECB Encrypt result : " + encResult.toHexString());

            mode = AlgMode.EM_alg_SMS4DECRYPT | AlgMode.EM_alg_SMS4TECBMODE;
            BytesValue decResult = new BytesValue();
            ret = algorithm.SMS4(mode, key, encResult.getData(), decResult);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("ECB Decrypt result : " + decResult.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void SM3(View v) {
        try {
            outputBlueText(">>> SM3");
            byte[] dataIn = "1234567890122".getBytes();
            BytesValue dataOut = new BytesValue();
            int ret = algorithm.SM3(dataIn, dataOut);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("SM3 result : " + dataOut.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void calcMAC(View v) {
        try {
            outputBlueText(">>> calcMAC ");
            byte[] key = BytesUtil.hexString2Bytes("3131313131313131");
            byte[] dataIn = BytesUtil.hexString2Bytes("1234567890");
            long mode = AlgMode.EM_alg_MACALGORITHM1 | AlgMode.EM_alg_MACPADMODE1;
            BytesValue dataOut = new BytesValue();
            int ret = algorithm.calcMAC(mode, key, dataIn, dataOut);
            if (ret != AlgError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("MAC : " + dataOut.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case AlgError.ERROR_PARAM: message = "Parameter error"; break;
            case AlgError.ERROR: message = "Error"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }

}
