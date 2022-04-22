package cn.eas.usdk.demo.constant;

import android.os.Environment;

import com.usdk.apiservice.aidl.emv.LogLevel;
import com.usdk.apiservice.aidl.constants.RFDeviceName;
import com.usdk.apiservice.aidl.dock.PortName;
import com.usdk.apiservice.aidl.dock.serialport.BaudRate;
import com.usdk.apiservice.aidl.dock.serialport.DataBit;
import com.usdk.apiservice.aidl.dock.serialport.Parity;
import com.usdk.apiservice.aidl.pinpad.DeviceName;

import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.LRKMMode;

import java.util.ArrayList;
import java.util.List;

import cn.eas.usdk.demo.entity.SpinnerItem;
import static com.usdk.apiservice.aidl.pinpad.KMSMode.SCHEME_V2;

public class DemoConfig {
    public static final String TAG = "[DemoUSDK]";

    public static String PINPAD_DEVICE_NAME = DeviceName.IPP;
    public static String RF_DEVICE_NAME = RFDeviceName.INNER;
    public static boolean USDK_LOG_OPEN = true;
    public static int REGION_ID = 0;
    public static int KAP_NUM = 0;


    /** 寻卡超时时间 */
    public static int TIMEOUT = 20;
    public static final int KEYID_PIN = 10;
    public static final String TEST_FILES_PATH = Environment.getExternalStorageDirectory() + "/usdk_test/";


    /** RKIS */
    public static List<String> LIST_KMS_IP = new ArrayList<>();
    public static List<SpinnerItem> LIST_LRKM_MODE = new ArrayList<>();
    public static List<SpinnerItem> LIST_KMS_MODE = new ArrayList<>();
    public static List<SpinnerItem> LIST_KEY_SYSTEM = new ArrayList<>();

    public static String KMS_IP;
    public static String KMS_PORT = "30000";
    public static byte[] KMS_TPDU = new byte[]{0x60, 0x00, 0x21, 0x00, 0x00};
    public static byte[] TRADE_TYPE = new byte[] {0x30, 0x30, 0x30, 0x31, 0x30, 0x30, 0x30, 0x34, 0x30, 0x30, 0x30, 0x32};
    public static String KMS_ID = "PCIPTS00ID000039"/*"KMS00000ID000001"*/;
    public static String MERCH_ID = "123456789012345";
    public static String TERM_ID = "12345678";

    public static int LRKM_MODE;
    public static int KMS_MODE;
    public static int RKIS_KEY_SYSTEM;
    public static int RKIS_REGION_ID = 0;
    public static int RKIS_KAP_NUM = 0;
    public static int RKIS_KEY_ID = 2;

    /** EMV */
    public static int EMV_LOG_LEVEL = LogLevel.REALTIME;

    /** DOCK */
    public static List<String> LIST_WIFI_DOCK_PORT_NAME = new ArrayList<>();
    public static List<String> LIST_BT_DOCK_PORT_NAME = new ArrayList<>();
    public static List<SpinnerItem> LIST_WIFI_DOCK_BPS = new ArrayList<>();
    public static List<SpinnerItem> LIST_BT_DOCK_BPS = new ArrayList<>();
    public static List<SpinnerItem> LIST_DOCK_PAR = new ArrayList<>();
    public static List<SpinnerItem> LIST_DOCK_DBS = new ArrayList<>();
    public static String WIFI_DOCK_PORT_NAME;
    public static String BT_DOCK_PORT_NAME;
    public static int WIFI_DOCK_BPS;
    public static int BT_DOCK_BPS;
    public static int DOCK_PAR;
    public static int DOCK_DBS;


    static {
        // 测试
        LIST_KMS_IP.add("kdptest.unimarspay.com");
        // 内网
        LIST_KMS_IP.add("172.20.9.213");
        // 外网
        LIST_KMS_IP.add("47.91.93.223");
        // 自定义ip
        LIST_KMS_IP.add("user defined");
        KMS_IP = LIST_KMS_IP.get(0);

        LIST_LRKM_MODE.add(new SpinnerItem(LRKMMode.LRKM_HANDLE_DECIDE_BY_KMS, "by KMS"));
        LIST_LRKM_MODE.add(new SpinnerItem(LRKMMode.LRKM_HANDLE_DECIDE_BY_USER, "by USER"));
        LRKM_MODE = LIST_LRKM_MODE.get(0).getValue();

        LIST_KMS_MODE.add(new SpinnerItem(SCHEME_V2, "SCHEME_V2"));
        KMS_MODE = LIST_KMS_MODE.get(0).getValue();

        LIST_KEY_SYSTEM.add(new SpinnerItem(KeySystem.KS_MKSK, "MKSK"));
        LIST_KEY_SYSTEM.add(new SpinnerItem(KeySystem.KS_DUKPT, "DUKPT"));
        LIST_KEY_SYSTEM.add(new SpinnerItem(KeySystem.KS_FIXED_KEY, "FIXED_KEY"));
        RKIS_KEY_SYSTEM = LIST_KEY_SYSTEM.get(0).getValue();


        LIST_WIFI_DOCK_PORT_NAME.add(PortName.BASECOM0);
        LIST_WIFI_DOCK_PORT_NAME.add(PortName.BASEUSBD);
        LIST_WIFI_DOCK_PORT_NAME.add(PortName.BASEUSBCOM1);
        LIST_WIFI_DOCK_PORT_NAME.add(PortName.BASEUSBCOM1_1);
        LIST_WIFI_DOCK_PORT_NAME.add(PortName.BASEUSBCOM1_2);
        LIST_WIFI_DOCK_PORT_NAME.add(PortName.BASEUSBCOM1_3);
        LIST_WIFI_DOCK_PORT_NAME.add(PortName.BASEUSBCOM1_4);
        WIFI_DOCK_PORT_NAME = LIST_WIFI_DOCK_PORT_NAME.get(0);

        LIST_BT_DOCK_PORT_NAME.add(PortName.BASECOM0);
        BT_DOCK_PORT_NAME = LIST_BT_DOCK_PORT_NAME.get(0);

        LIST_WIFI_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_115200, "115200"));
        LIST_WIFI_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_1200, "1200"));
        LIST_WIFI_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_2400, "2400"));
        LIST_WIFI_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_4800, "4800"));
        LIST_WIFI_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_9600, "9600"));
        LIST_WIFI_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_19200, "19200"));
        LIST_WIFI_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_38400, "38400"));
        LIST_WIFI_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_57600, "57600"));
        WIFI_DOCK_BPS = LIST_WIFI_DOCK_BPS.get(0).getValue();

        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_115200, "115200"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_1200, "1200"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_2400, "2400"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_4800, "4800"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_9600, "9600"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_19200, "19200"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_38400, "38400"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_57600, "57600"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_14400, "14400"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_28800, "28800"));
        LIST_BT_DOCK_BPS.add(new SpinnerItem(BaudRate.BPS_256000, "256000"));
        BT_DOCK_BPS = LIST_BT_DOCK_BPS.get(0).getValue();

        LIST_DOCK_PAR.add(new SpinnerItem(Parity.NOPAR, "NOPAR"));
        LIST_DOCK_PAR.add(new SpinnerItem(Parity.EVEN, "EVEN"));
        LIST_DOCK_PAR.add(new SpinnerItem(Parity.ODD, "ODD"));
        DOCK_PAR = LIST_DOCK_PAR.get(0).getValue();

        LIST_DOCK_DBS.add(new SpinnerItem(DataBit.DBS_8, "DBS_8"));
        LIST_DOCK_DBS.add(new SpinnerItem(DataBit.DBS_7, "DBS_7"));
        DOCK_DBS = LIST_DOCK_DBS.get(0).getValue();
    }

}
