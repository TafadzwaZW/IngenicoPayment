package cn.eas.usdk.demo.view.networkmanager;

import android.os.Bundle;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseMenuActivity;

public class MenuActivity extends BaseMenuActivity {
    private final static String[] TITLES = new String[]{
            "PPP"
    };
    private static final int[] DESCRIPTION_STRING_IDS = new int[] {
            R.string.description_ppp
    };
    private final static String[] CLASSES = new String[] {
            ".view.networkmanager.ppp.PPPMenuActivity"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networkmanager);

        setTitle(getString(R.string.description_net_work_manager));
        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);
    }
}
