package cn.eas.usdk.demo.view.dock;

import android.os.Bundle;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseMenuActivity;

public class MenuActivity extends BaseMenuActivity {
    private final static String[] TITLES = new String[]{
            "Wifi Dock",
            "BT Dock",
    };
    private static final int[] DESCRIPTION_STRING_IDS = new int[] {
            R.string.description_wifi,
            R.string.description_bt,
    };
    private final static String[] CLASSES = new String[] {
            ".view.dock.WifDockActivity",
            ".view.dock.BTDockActivity",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.dock_title));
        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);
    }
}
