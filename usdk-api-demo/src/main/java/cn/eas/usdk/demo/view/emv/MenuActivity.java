package cn.eas.usdk.demo.view.emv;

import android.os.Bundle;
import android.view.View;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseMenuActivity;

public class MenuActivity extends BaseMenuActivity {
    private final static String[] TITLES = new String[]{
            "SearchCardFirst",
            "StartEMVFirst",
            "StartProcessActivity",
    };
    private static final int[] DESCRIPTION_STRING_IDS = new int[] {
            R.string.description_searchcard_first,
            R.string.description_startemv_first,
            R.string.description_startprocess,
    };
    private final static String[] CLASSES = new String[] {
            ".view.emv.SearchCardFirstActivity",
            ".view.emv.StartEMVFirstActivity",
            ".view.emv.StartProcessActivity",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.emv_title));
        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);

        setSettingClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(EMVSettingActivity.class);
            }
        });
    }
}
