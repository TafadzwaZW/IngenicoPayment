package cn.eas.usdk.demo.view.mifare;

import android.os.Bundle;
import androidx.annotation.Nullable;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseMenuActivity;

public class MenuActivity extends BaseMenuActivity {
    private final static String[] TITLES = new String[]{
            "DesFire",
            "DesFire Application",
            "DesFire File",
    };
    private static final int[] DESCRIPTION_STRING_IDS = new int[]{
            R.string.description_desfire,
            R.string.description_desfire_application,
            R.string.description_desfire_file,
    };
    private final static String[] CLASSES = new String[] {
            ".view.mifare.DesFireActivity",
            ".view.mifare.DesFireApplicationActivity",
            ".view.mifare.DesFireFileActivity",
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.mifare_title));
        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);
    }
}
