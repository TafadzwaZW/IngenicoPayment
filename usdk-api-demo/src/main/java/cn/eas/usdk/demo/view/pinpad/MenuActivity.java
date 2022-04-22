package cn.eas.usdk.demo.view.pinpad;

import android.os.Bundle;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseMenuActivity;

public class MenuActivity extends BaseMenuActivity {
    private final static String[] TITLES = new String[]{
            "MKSK", "DUKPT", "FIXED_KEY", "External Specail Interface", "RKIS",
            "Derive Key","Ctrled permission api", "TouchScreenPinEntry"
    };
    private static final int[] DESCRIPTION_STRING_IDS = new int[]{
            R.string.description_mksk,
            R.string.description_dukpt,
            R.string.description_fixed,
            R.string.description_external,
            R.string.description_rkis,
            R.string.description_derive_key,
            R.string.admin_ctrled_api,
            R.string.touch_screen_pin,
    };
    private final static String[] CLASSES = new String[]{
            ".view.pinpad.MKSKActivity",
            ".view.pinpad.DUKPTActivity",
            ".view.pinpad.FixedKeyActivity",
            ".view.pinpad.ExternalActivity",
            ".view.pinpad.RkisActivity",
            ".view.pinpad.DeriveKeyActivity",
            ".view.pinpad.ControlledApiActivity",
            ".view.pinpad.TouchScreenPinEntryActivity",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.pinpad_title));
        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);
    }
}
