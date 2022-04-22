package cn.eas.usdk.demo.view.system;

import android.os.Bundle;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseMenuActivity;

public class MenuActivity extends BaseMenuActivity {
    private final static String[] TITLES = new String[]{
            "Application",
            "InputManager",
            "Keyboard",
            "KeyChain",
            "Location",
            "NFC",
            "Process",
            "Setting",
            "Status bar",
            "Storage",
            "System property",
            "Telephony",
            "USB",
    };
    private static final int[] DESCRIPTION_STRING_IDS = new int[]{
            R.string.description_application,
            R.string.description_input,
            R.string.description_keyboard,
            R.string.description_keychain,
            R.string.description_location,
            R.string.description_nfc,
            R.string.description_process,
            R.string.description_setting,
            R.string.description_status_bar,
            R.string.description_storage,
            R.string.description_system_property,
            R.string.description_telephony,
            R.string.description_usb,
    };
    private final static String[] CLASSES = new String[]{
            ".view.system.ApplicationActivity",
            ".view.system.InputActivity",
            ".view.system.KeyboardActivity",
            ".view.system.KeyChainActivity",
            ".view.system.LocationActivity",
            ".view.system.NfcActivity",
            ".view.system.ProcessActivity",
            ".view.system.SettingActivity",
            ".view.system.StatusBarActivity",
            ".view.system.StorageActivity",
            ".view.system.SystemPropertyActivity",
            ".view.system.TelephonyActivity",
            ".view.system.UsbActivity",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.system_title));
        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);
    }
}
