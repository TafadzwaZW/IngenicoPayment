package cn.eas.usdk.demo.view.dock;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.DeviceServiceData;
import com.usdk.apiservice.aidl.dock.DockName;

import org.angmarch.views.NiceSpinner;

import java.util.List;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.view.BaseActivity;

/**
 * Serial port related data setting of Dock
 */
public class DockPortSettingActivity extends BaseActivity {

    private boolean isBTDock = false;

    private String portName;
    private int bps;
    private int par;
    private int dbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dock_port_setting);
        setTitle("Dock Port Setting");

        String dockName = getIntent().getStringExtra(DeviceServiceData.DOCK_NAME);
        isBTDock = DockName.BT_DOCK.equals(dockName);

        initSpinner(isBTDock);
    }

    private void initSpinner(boolean isBTDock) {
        List<String> portNameList;
        List<SpinnerItem> bpsList;
        if (isBTDock) {
            portNameList = DemoConfig.LIST_BT_DOCK_PORT_NAME;
            portName = DemoConfig.BT_DOCK_PORT_NAME;
            bpsList = DemoConfig.LIST_BT_DOCK_BPS;
            bps = DemoConfig.BT_DOCK_BPS;
        } else {
            portNameList = DemoConfig.LIST_WIFI_DOCK_PORT_NAME;
            portName = DemoConfig.WIFI_DOCK_PORT_NAME;
            bpsList = DemoConfig.LIST_WIFI_DOCK_BPS;
            bps = DemoConfig.WIFI_DOCK_BPS;
        }
        initPortNameSpinner(portNameList, portName);
        initBpsSpinner(bpsList, bps);

        NiceSpinner parSpinner = findViewById(R.id.parSpinner);
        parSpinner.attachDataSource(DemoConfig.LIST_DOCK_PAR);
        parSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                par = DemoConfig.LIST_DOCK_PAR.get(position).getValue();
            }
        });
        setSpinnerDefaultValue(parSpinner, DemoConfig.LIST_DOCK_PAR, DemoConfig.DOCK_PAR);
        par = DemoConfig.DOCK_PAR;

        NiceSpinner dbsSpinner = findViewById(R.id.dbsSpinner);
        dbsSpinner.attachDataSource(DemoConfig.LIST_DOCK_DBS);
        dbsSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dbs = DemoConfig.LIST_DOCK_DBS.get(position).getValue();
            }
        });
        setSpinnerDefaultValue(dbsSpinner, DemoConfig.LIST_DOCK_DBS, DemoConfig.DOCK_DBS);
        dbs = DemoConfig.DOCK_DBS;
    }

    private void initPortNameSpinner(final List<String> portNameList, String curPortName) {
        NiceSpinner portNameSpinner = findViewById(R.id.portNameSpinner);
        portNameSpinner.attachDataSource(portNameList);
        portNameSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                portName = portNameList.get(position);
            }
        });
        setSpinnerDefaultValue(portNameSpinner, portNameList, curPortName);
    }

    private void initBpsSpinner(final List<SpinnerItem> bpsList, int curBps) {
        NiceSpinner bpsSpinner = findViewById(R.id.bpsSpinner);
        bpsSpinner.attachDataSource(bpsList);
        bpsSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                bps = bpsList.get(position).getValue();
            }
        });
        setSpinnerDefaultValue(bpsSpinner, bpsList, curBps);
    }

    public void save(View v) {
        if (isBTDock) {
            DemoConfig.BT_DOCK_PORT_NAME = portName;
            DemoConfig.BT_DOCK_BPS = bps;
        } else {
            DemoConfig.WIFI_DOCK_PORT_NAME = portName;
            DemoConfig.WIFI_DOCK_BPS = bps;
        }
        DemoConfig.DOCK_PAR = par;
        DemoConfig.DOCK_DBS = dbs;

        finish();
    }
}
