package cn.eas.usdk.demo.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.usdk.apiservice.aidl.systemstatistics.FactorNo;
import com.usdk.apiservice.aidl.systemstatistics.StatisticInfo;
import com.usdk.apiservice.aidl.systemstatistics.TagNo;
import com.usdk.apiservice.aidl.systemstatistics.USystemStatistics;

import java.util.ArrayList;
import java.util.List;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.DeviceHelper;

public class SystemStatisticsActivity extends BaseDeviceActivity {

    private USystemStatistics systemStatistics;
    private MyAdapter mAdapter;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_systemstatistics);
        setTitle("SystemStatistics Module");
        initList();
    }

    protected void initDeviceInstance() {
        systemStatistics = DeviceHelper.me().getSystemStatistics();
    }

    private void initList() {
        ListView list = (ListView) findViewById(R.id.list);
        mAdapter = new MyAdapter(this, null);
        list.setAdapter(mAdapter);
    }

    public void getAllStatisticsAndStatus(View v) {
        try {
            List<StatisticInfo> info = systemStatistics.getAllStatisticsAndStatus();
            mAdapter.setInfo(info);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getAllStatistics(View v) {
        try {
            List<StatisticInfo> info = systemStatistics.getAllStatistics();
            mAdapter.setInfo(info);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getAllStatus(View v) {
        try {
            List<StatisticInfo> info = systemStatistics.getAllStatus();
            mAdapter.setInfo(info);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getStatisticItem(View v) {
        try {
            StatisticInfo offlinePinTimes = systemStatistics.getStatisticItem(TagNo.PINPAD, FactorNo.Pinpad.OFFLINE_PIN_INPUT_TIMES);
            StatisticInfo onlinePinTimes = systemStatistics.getStatisticItem(TagNo.PINPAD, FactorNo.Pinpad.ONLINE_PIN_INPUT_TIMES);
            StatisticInfo magModuleStatus = systemStatistics.getStatisticItem(TagNo.MAG_CARD, FactorNo.MagCard.MAG_MODULE_STATUS);
            StatisticInfo rfModuleStatus = systemStatistics.getStatisticItem(TagNo.RF_CARD, FactorNo.RFCard.RF_MODULE_STATUS);
            StatisticInfo motorPrintSteps = systemStatistics.getStatisticItem(TagNo.PRINTER, FactorNo.Printer.MOTOR_PRINT_STEPS);
            StatisticInfo batteryLevel = systemStatistics.getStatisticItem(TagNo.BATTERY, FactorNo.BATTERY.BATTERY_LEVEL);
            StatisticInfo batteryBUVolState = systemStatistics.getStatisticItem(TagNo.BATTERY, FactorNo.BATTERY.BU_VOL_LITHIUM_STATE);
            StatisticInfo batteryVolState = systemStatistics.getStatisticItem(TagNo.BATTERY, FactorNo.BATTERY.VOL_LITHIUM_STATE);
            StatisticInfo batteryChargeState = systemStatistics.getStatisticItem(TagNo.BATTERY, FactorNo.BATTERY.CHARGE_DISCHARGE_STATE);
            StatisticInfo batteryTemp = systemStatistics.getStatisticItem(TagNo.BATTERY, FactorNo.BATTERY.TEMP_LITHIUM_STATE);

            List<StatisticInfo> list = new ArrayList<StatisticInfo>();
            addItem(list, offlinePinTimes);
            addItem(list, onlinePinTimes);
            addItem(list, magModuleStatus);
            addItem(list, rfModuleStatus);
            addItem(list, motorPrintSteps);
            addItem(list, batteryLevel);
            addItem(list, batteryBUVolState);
            addItem(list, batteryVolState);
            addItem(list, batteryChargeState);
            addItem(list, batteryTemp);
            mAdapter.setInfo(list);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void addItem(List<StatisticInfo> list, StatisticInfo info) {
        if (info == null) {
            return;
        }
        list.add(info);
    }

    private class MyAdapter extends BaseAdapter {
        private List<StatisticInfo> mInfo;
        private LayoutInflater mInflater;

        public MyAdapter(Context context, ArrayList<StatisticInfo> info) {
            mInfo = info;
            mInflater = LayoutInflater.from(context);
        }

        public void setInfo(List<StatisticInfo> info){
            mInfo = info;
        }

        @Override
        public int getCount() {
            return mInfo == null ? 0 : mInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return mInfo == null ? null: mInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, null);
            }
            StatisticInfo info = mInfo.get(position);
            ((TextView)convertView.findViewById(R.id.name)).setText(info.getName());
            ((TextView)convertView.findViewById(R.id.displayName)).setText(info.getDisplayName());
            ((TextView)convertView.findViewById(R.id.tagNo)).setText("TagNo-" + info.getTagNo());
            ((TextView)convertView.findViewById(R.id.factorNo)).setText("FactorNo-" + info.getFactorNo());
            TextView value = (TextView)convertView.findViewById(R.id.value);
            if (TextUtils.isEmpty(info.getValue())) {
                value.setText("value is empty");
                value.setTextColor(Color.RED);
            } else {
                value.setText(info.getValue());
                value.setTextColor(Color.BLACK);
            }
            return convertView;
        }
    }
}
