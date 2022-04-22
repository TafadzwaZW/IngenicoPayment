package cn.eas.usdk.demo.view.icreader;

import android.os.Bundle;
import androidx.annotation.Nullable;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseMenuActivity;

public class MenuActivity extends BaseMenuActivity {
    private final static String[] TITLES = new String[]{
            "AT1604", "AT1608", "AT24CXX", "ICCPU", "PSam", "SIM4428", "SIM4442"
    };
    private static final int[] DESCRIPTION_STRING_IDS = new int[]{
            R.string.description_at1604,
            R.string.description_at1608,
            R.string.description_at24cxx,
            R.string.description_iccpu,
            R.string.description_psam,
            R.string.description_sim4428,
            R.string.description_sim4442,
    };
    private final static String[] CLASSES = new String[] {
            ".view.icreader.AT1604Activity",
            ".view.icreader.AT1608Activity",
            ".view.icreader.AT24CXXActivity",
            ".view.icreader.ICCpuActivity",
            ".view.icreader.PSamActivity",
            ".view.icreader.SIM4428Activity",
            ".view.icreader.SIM4442Activity",
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.ic_title));
        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);
    }
}
