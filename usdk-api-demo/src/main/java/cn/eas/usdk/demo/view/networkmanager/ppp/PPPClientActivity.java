package cn.eas.usdk.demo.view.networkmanager.ppp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;

import com.usdk.apiservice.aidl.networkmanager.AuthMode;
import com.usdk.apiservice.aidl.networkmanager.BaudRate;
import com.usdk.apiservice.aidl.networkmanager.DataBit;
import com.usdk.apiservice.aidl.networkmanager.OnConnectListener;
import com.usdk.apiservice.aidl.networkmanager.PPPData;
import com.usdk.apiservice.aidl.networkmanager.ParityBit;
import com.usdk.apiservice.aidl.networkmanager.UNetWorkManager;

import org.angmarch.views.NiceSpinner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class PPPClientActivity extends BaseDeviceActivity {

    private boolean isPPPConnect = false;
    private UNetWorkManager netWorkManager;
    private String TAG = "PPPClientActivity";
    private CheckBox cbAuth, cbBeAuth;
    private EditText edtUser, edtPassword, edtScrtUser, edtScrtPassword, edtDeviceName;
    private static List<String> modeList = new ArrayList<>();
    static {
        modeList.add(AuthMode.REQUIRE_CHAP);
        modeList.add(AuthMode.REQUIRE_MSCHAP);
        modeList.add(AuthMode.REQUIRE_MSCHAP_V2);
        modeList.add(AuthMode.REQUIRE_EAP);
    }

    private static final String CONNECT_USB = "USB";
    private static final String CONNECT_BT = "BT";
    private static final String CONNECT_WIFI_BASE = "WIFI BASE";
    private static List<String> connectTypeList = new ArrayList<>();
    static {
        connectTypeList.add(CONNECT_USB);
        connectTypeList.add(CONNECT_BT);
        connectTypeList.add(CONNECT_WIFI_BASE);
    }

    private static List<String> handshakeProtocolTypeList = new ArrayList<>();
    static {
        handshakeProtocolTypeList.add("");
        handshakeProtocolTypeList.add("one_way_handshake");
    }

    private String mode = modeList.get(0);
    private String connectType = connectTypeList.get(0);
    private String handshakeProtocol = handshakeProtocolTypeList.get(0);

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_ppp_client);
        setTitle("PPP Client Module");
        initView();
    }

    protected void initDeviceInstance() {
        netWorkManager = DeviceHelper.me().getNetWorkManager();
    }

    private void initView() {
        NiceSpinner deviceSpinner = (NiceSpinner) findViewById(R.id.modeSpinner);
        deviceSpinner.attachDataSource(modeList);
        deviceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mode = modeList.get(position);
            }
        });

        NiceSpinner connectSpinner = (NiceSpinner) findViewById(R.id.connectSpinner);
        connectSpinner.attachDataSource(connectTypeList);
        connectSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                connectType = connectTypeList.get(position);
                initSpecialView();
            }
        });

        NiceSpinner handshakeProtocolSpinner = (NiceSpinner) findViewById(R.id.handshakeProtocolSpinner);
        handshakeProtocolSpinner.attachDataSource(handshakeProtocolTypeList);
        handshakeProtocolSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                handshakeProtocol = handshakeProtocolTypeList.get(position);
            }
        });

        cbAuth = (CheckBox) findViewById(R.id.cbAuth);
        cbBeAuth = (CheckBox) findViewById(R.id.cbBeAuth);
        edtUser = (EditText) findViewById(R.id.edtUser);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtScrtUser = (EditText) findViewById(R.id.edtScrtUser);
        edtScrtPassword = (EditText) findViewById(R.id.edtScrtPassword);
        edtDeviceName = (EditText) findViewById(R.id.edtDeviceName);
        initSpecialView();
    }

    private void initSpecialView() {
        switch (connectType) {
            case CONNECT_USB:
                edtDeviceName.setText("/dev/USBD");
                edtDeviceName.setEnabled(false);
                findViewById(R.id.handshakeProtocolLayout).setVisibility(View.GONE);
                break;
            case CONNECT_BT:
                edtDeviceName.setText("BT-xx:xx:xx:xx:xx:xx");
                edtDeviceName.setEnabled(true);
                findViewById(R.id.handshakeProtocolLayout).setVisibility(View.VISIBLE);
                BluetoothDevice bondDevice = getFirstBondedBlueToothDevice();
                if (bondDevice == null) {
                    outputBlackText("no find server blueTooth device");
                } else {
                    String btMac = bondDevice.getAddress();
                    String name = bondDevice.getName();
                    edtDeviceName.setText("BT-" + btMac);
                    outputBlackText("find server blueTooth device name = " + name + ",mac = " + btMac);
                }
                break;
            case CONNECT_WIFI_BASE:
                //The Host(server) side is connected to wifi base USB port
                //edtDeviceName.setText("WB-BASEUSBCOM1");
                //The Host(server) side is connected to wifi base USB-B port
                //edtDeviceName.setText("WB-BASEUSBD");
                //The Host(server) side is connected to wifi base COM port
                edtDeviceName.setText("WB-BASECOM0");
                edtDeviceName.setEnabled(true);
                findViewById(R.id.handshakeProtocolLayout).setVisibility(View.GONE);
                break;
        }
    }

    public void startPPP(View v) {
        outputBlueText(">>> startPPP ");
        try {
            Bundle param = new Bundle();
            param.putString(PPPData.DEVICE_NAME, edtDeviceName.getText().toString());
            param.putString(PPPData.LCP_ECHO_FAILURE, "3");
            param.putString(PPPData.LCP_ECHO_INTERVAL, "5");
            if (cbAuth.isChecked()) {
                param.putString(PPPData.AUTH, "1");
                param.putString(PPPData.SECRET_USER, edtScrtUser.getText().toString().trim());
                param.putString(PPPData.SECRET_PASSWORD, edtScrtPassword.getText().toString().trim());
                param.putString(PPPData.AUTH_MODE, mode);

                // Both the authenticator and the authenticated
                if (cbBeAuth.isChecked()) {
                    param.putString(PPPData.USER, edtUser.getText().toString().trim());
                    param.putString(PPPData.PASSWORD, edtPassword.getText().toString().trim());
                }
            } else if (cbBeAuth.isChecked()){
                param.putString(PPPData.AUTH, "0");
                param.putString(PPPData.USER, edtUser.getText().toString().trim());
                param.putString(PPPData.PASSWORD, edtPassword.getText().toString().trim());
            }

            //The parameters required for different connections
            switch (connectType) {
                case CONNECT_BT:
                    if (!TextUtils.isEmpty(handshakeProtocol)) {
                        param.putString(PPPData.HANDSHAKE_PROTOCOL, handshakeProtocol);
                    }
                    break;
                case CONNECT_WIFI_BASE:
                    param.putString(PPPData.SPEED, BaudRate.BPS_115200);
                    param.putString(PPPData.DATA_BITS, DataBit.DBS_8);
                    param.putString(PPPData.PARITY, ParityBit.NOPAR);
                    break;
            }


            netWorkManager.startPPP(param, new OnConnectListener.Stub() {
                @Override
                public void onConnected(Bundle bundle) throws RemoteException {
                    bundle.keySet();
                    outputBlueText(">>> onConnected success " + bundle);
                    isPPPConnect = true;
                    String remoteIp = bundle.getString("remote_address");
                    // The client needs to delay the creation of the socket to ensure that the server has created the socket monitor.
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    startConnectToService(remoteIp);
                }

                @Override
                public void onDisconnected() throws RemoteException {
                    outputRedText(">>> onDiscinnected ");
                    stopSocketConnect();
                }

                @Override
                public void onError(int code) throws RemoteException {
                    outputRedText(">>> onError code " + code + ", " + getErrorMessage(code));
                    stopSocketConnect();
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "startPPP | Exception =" + e.getLocalizedMessage());
            handleException(e);
            stopSocketConnect();
        }
    }

    public void stopPPP(View v) {
        outputBlueText(">>> stopPPP");
        stopSocketConnect();
        try {
            netWorkManager.stopPPP();
        } catch (Exception e) {
            handleException(e);
        }
    }

    Socket socket;
    private void startConnectToService(final String serviceIp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    outputBlueText("--Try to connect to the server--");
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(serviceIp, 9999), 10 * 1000);
                    outputBlueText("--Successfully connected to the server--");
                    startReadAndWrite(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                    outputRedText("Exception = " + e.getLocalizedMessage());
                }
            }
        }).start();
    }

    private void startReadAndWrite(final Socket socket) throws IOException {
        DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
        DataInputStream reader = new DataInputStream(socket.getInputStream());

        try {
            long count = 0;
            Log.i(TAG, "start write isPPPConnect = " + isPPPConnect);
            while (isPPPConnect && count < 11) {
                Thread.sleep(2000);
                Log.i(TAG, "try write count = " + count);
                writer.writeUTF(String.valueOf(count));
                Log.i(TAG, "try read ");
                outputBlueText("Receive server feedback:" + reader.readUTF());
                count++;
            }
            outputBlueText("Data transfer completed!");
        } catch (InterruptedException e) {
            isPPPConnect = false;
            e.printStackTrace();
            outputRedText("Exception = " + e.getLocalizedMessage());
        } finally {
            writer.close();
            reader.close();
        }

    }

    private synchronized void stopSocketConnect() {
        Log.i(TAG, "stopSocketConnect");
        isPPPConnect = false;
        if (socket != null) {
            try {
                Log.i(TAG, "close socket");
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BluetoothDevice getFirstBondedBlueToothDevice() {
        Set<BluetoothDevice> deviceList =  BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (deviceList != null && deviceList.size() > 0) {
            BluetoothDevice bluetoothDevice = deviceList.iterator().next();
            return bluetoothDevice;
        }
        return null;
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case 0x2D301: message = "PPP_STATE_ERROR"; break;
            case 0x2D302: message = "PPP_CONNECT_FAIL"; break;
            case 0x02210001: message = "ERROR_UNKNOWN"; break;
            case 0x02210002: message = "ERROR_RECV_SOCKET"; break;
            case 0x02210003: message = "ERROR_INPUT_CMD_TYPE"; break;
            case 0x02210004: message = "ERROR_INPUT_TOTAL_LEN"; break;
            case 0x02210005: message = "ERROR_INPUT_KEY_PARAM"; break;
            case 0x02210006: message = "ERROR_INPUT_VALUE_PARAM"; break;
            case 0x02210007: message = "ERROR_INPUT_DEV_NAME"; break;
            case 0x02210008: message = "ERROR_INPUT_IP_ADDR"; break;
            case 0x02210009: message = "ERROR_PPPD_NOT_FOUND"; break;
            case 0x0221000A: message = "ERROR_KILL_PPPD"; break;
            case 0x0221000B: message = "ERROR_CMD_TOO_LONG"; break;
            case 0x0221000C: message = "ERROR_DO_COMMAND"; break;
            case 0x0221000D: message = "ERROR_PPP_CONNECT"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
