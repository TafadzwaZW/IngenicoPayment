package cn.eas.usdk.demo.view.networkmanager.ppp;

import android.os.Bundle;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseMenuActivity;

public class PPPMenuActivity extends BaseMenuActivity {
    private final static String[] TITLES = new String[]{
            "Server",
            "Client"
    };
    private static final int[] DESCRIPTION_STRING_IDS = new int[] {
            R.string.description_ppp_service,
            R.string.description_ppp_client
    };
    private final static String[] CLASSES = new String[] {
            ".view.networkmanager.ppp.PPPServerActivity",
            ".view.networkmanager.ppp.PPPClientActivity"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppp_menu);

        setTitle(getString(R.string.net_work_manager_title));
        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);
    }
}
