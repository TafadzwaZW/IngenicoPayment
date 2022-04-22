package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.beeper.BeeperFrequency;
import com.usdk.apiservice.aidl.beeper.UBeeper;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;

public class BeeperActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> frequencyList = new LinkedList<>();
    static {
        frequencyList.add(new SpinnerItem(BeeperFrequency.FREQUENCY_300, "300HZ"));
        frequencyList.add(new SpinnerItem(BeeperFrequency.FREQUENCY_750, "750HZ"));
        frequencyList.add(new SpinnerItem(BeeperFrequency.FREQUENCY_1000, "1000HZ"));
        frequencyList.add(new SpinnerItem(BeeperFrequency.FREQUENCY_1500, "1500HZ"));
        frequencyList.add(new SpinnerItem(BeeperFrequency.FREQUENCY_2000, "2000HZ"));
        frequencyList.add(new SpinnerItem(2700, "2700HZ"));
        frequencyList.add(new SpinnerItem(BeeperFrequency.FREQUENCY_4000, "4000HZ"));
        frequencyList.add(new SpinnerItem(BeeperFrequency.FREQUENCY_6000, "6000HZ"));
    }

    private UBeeper beeper;
    private int frequency = BeeperFrequency.FREQUENCY_300;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_beeper);
        setTitle("Beeper Module");
        initSpinner();
    }

    protected void initDeviceInstance() {
        beeper = DeviceHelper.me().getBeeper();
    }

    private void initSpinner() {
        NiceSpinner trackTypeSpinner = findViewById(R.id.frequencySpinner);
        trackTypeSpinner.attachDataSource(frequencyList);
        trackTypeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                frequency = frequencyList.get(position).getValue();
            }
        });
    }

    public void beepWhenNormal(View v) {
        try {
            beeper.startBeep(500);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void beepWhenError(View v) {
        try {
            beeper.startBeep(1000);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void beepWhenInterval(View v) {
        new Thread() {
            @Override
            public void run() {
                try {
                    beeper.startBeep(200);
                    sleep(500);
                    beeper.startBeep(200);
                    sleep(500);
                    beeper.startBeep(200);
                } catch (Exception e ) {
                    handleException(e);
                }
            }
        }.start();
    }

    public void beepTwoSeconds(View v) {
        try {
            beeper.startBeep(2000);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void stopBeep(View v) {
        try {
            beeper.stopBeep();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void beepByFrequency(View v) {
        try {
            beeper.startBeepNew(frequency, 2000);
        } catch (Exception e) {
            handleException(e);
        }
    }
}
