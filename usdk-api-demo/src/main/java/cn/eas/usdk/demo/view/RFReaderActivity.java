package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.data.ApduResponse;
import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.rfreader.CardType;
import com.usdk.apiservice.aidl.rfreader.OnPassAndActiveListener;
import com.usdk.apiservice.aidl.rfreader.OnPassListener;
import com.usdk.apiservice.aidl.rfreader.PollMode;
import com.usdk.apiservice.aidl.rfreader.RFError;
import com.usdk.apiservice.aidl.rfreader.URFReader;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.BytesUtil;

public class RFReaderActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> pollModeList = new LinkedList<>();
    static {
        pollModeList.add(new SpinnerItem(PollMode.DEFAULT, "Default"));
        pollModeList.add(new SpinnerItem(PollMode.ULTRALIGHT, "Ultralight"));
    }

    private URFReader rfReader;
    private int pollMode;
    private int cardType = CardType.PRO_CARD;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_rfreader);
        setTitle("RFReader Module");
        initSpinner();
    }

    protected void initDeviceInstance() {
        rfReader = DeviceHelper.me().getRFReader(DemoConfig.RF_DEVICE_NAME);
    }

    private void initSpinner() {
        // 波特率 Baud rate
        pollMode = pollModeList.get(0).getValue();
        NiceSpinner pollModeSpinner = (NiceSpinner) findViewById(R.id.pollModeSpinner);
        pollModeSpinner.attachDataSource(pollModeList);
        pollModeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                pollMode = pollModeList.get(position).getValue();
            }
        });
    }

    public void pollCard(View v) {
        outputBlueText(">>> pollCard");
        try {
            rfReader.pollCard(pollMode, createOnPassListener());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void searchCardAndActivate(View v) {
        outputBlueText(">>> searchCardAndActivate");
        try {
            rfReader.searchCardAndActivate(createOnPassAndActiveListener());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void searchCard(View v) {
        outputBlueText(">>> searchCard");
        try {
            rfReader.searchCard(createOnPassListener());
        } catch (Exception e) {
            handleException(e);
        }
    }

    private OnPassAndActiveListener createOnPassAndActiveListener() {
        return new OnPassAndActiveListener.Stub() {
            @Override
            public void onActivate(byte[] response) throws RemoteException {
                outputText("=> onActivate | responseData = " + BytesUtil.bytes2HexString(response));
            }

            @Override
            public void onFail(int i) throws RemoteException {
                outputRedText("=> onFail: " + getErrorDetail(i));
            }
        };
    }

    private OnPassListener createOnPassListener() {
        return new OnPassListener.Stub() {
            @Override
            public void onCardPass(int cardtype) throws RemoteException {
                outputText("=> onCardPass | cardType = " + cardtype);

                cardType = cardtype;
            }

            @Override
            public void onFail(int i) throws RemoteException {
                outputRedText("=> onFail: " + getErrorDetail(i));
            }
        };
    }

    public void activate(View v) {
        outputBlueText(">>> activate");
        try {
            BytesValue dataOut = new BytesValue();
            int ret = rfReader.activate(cardType, dataOut);
            if (ret != RFError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("activate success: " + dataOut.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void stopSearch(View v) {
        outputBlueText(">>> stopSearch");
        try {
            rfReader.stopSearch();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isExist(View v) {
        try {
            outputBlueText(">>> isExist: " + rfReader.isExist());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void exchangeApdu(View v) {
        outputBlueText(">>> exchangeApdu");
        try {
            byte[] cmd = BytesUtil.hexString2Bytes("00a4040008a000000333010102");
            ApduResponse response = rfReader.exchangeApdu(cmd);
            if (response.getAPDURet() != RFError.SUCCESS) {
                outputRedText("exchangeApdu fail: " + getErrorDetail(response.getAPDURet()));
                return;
            }
            outputText("sw1 = " + Integer.toHexString(response.getSW1() & 0xFF));
            outputText("sw2 = " + Integer.toHexString(response.getSW2() & 0xFF));
            outputText("data = " + BytesUtil.bytes2HexString(response.getData()));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void cardTransparent(View v) {
        outputBlueText(">>> cardTransparent");
        try {
            byte[] data = new byte[]{0x02, 0x00, (byte)0x84, 0x00, 0x00, 0x04};
            BytesValue response = new BytesValue();
            int ret = rfReader.cardTransparent(data, response);
            if (ret != RFError.SUCCESS) {
                outputRedText("cardTransparent fail: " + getErrorDetail(ret));
                return;
            }
            outputText("response = " + response.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void halt(View v) {
        outputBlueText(">>> halt");
        try {
            rfReader.halt();
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case RFError.ERROR_ERRPARAM: message = "ERROR_ERRPARAM"; break;
            case RFError.ERROR_IC_SWDIFF: message = "ERROR_IC_SWDIFF"; break;
            case RFError.ERROR_TRANSERR: message = "ERROR_TRANSERR"; break;
            case RFError.ERROR_PROTERR: message = "ERROR_PROTERR"; break;
            case RFError.ERROR_MULTIERR: message = "ERROR_MULTIERR"; break;
            case RFError.ERROR_CARDTIMEOUT: message = "ERROR_CARDTIMEOUT"; break;
            case RFError.ERROR_CARDNOACT: message = "ERROR_CARDNOACT"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
