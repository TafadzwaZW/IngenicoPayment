package cn.eas.usdk.demo.view;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.usdk.apiservice.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.DialogUtil;
import cn.eas.usdk.demo.util.DialogUtil.OnChooseListener;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends BaseMenuActivity implements DeviceHelper.ServiceReadyListener {
    private final static int REQUEST_CODE_PERMISSION = 1;
    private final static String[] TITLES = new String[]{
            "Sale",
            "Ecocash",

            "Algorithm", "Beeper", "CashBox", "DeviceInfo", "Digeld", "Dock", "Emv", "Ethernet", "ExScanner",
            "FelicaReader", "Fiscal", "ICReader", "IndustryMagReader", "InnerScanner", "Led", "Lki",
            "MagReader", "MifareManager", "ParamFile", "Pinpad", "Printer", "VectorPrinter", "RFReader",
            "Scanner", "SerialPort", "UsbDeice", "SignPanel", "System", "SystemStatistics", "Tms", "DecodeEngine", "OnGuard",
            "ResetFactory","Update", "NetWorkManager", "DeviceAdmin"
    };
    private final static int[] DESCRIPTION_STRING_IDS = new int[] {
            R.string.description_sale,
            R.string.description_mobile,

            R.string.description_algorithm,
            R.string.description_beeper,
            R.string.description_cashbox,
            R.string.description_devicemanger,
            R.string.description_digled,
            R.string.description_dock,
            R.string.description_emv,
            R.string.description_ethernet,
            R.string.description_exscanner,
            R.string.description_felicareader,
            R.string.description_fiscal,
            R.string.description_icreader,
            R.string.description_industry_magreader,
            R.string.description_innerscanner,
            R.string.description_led,
            R.string.description_lki,
            R.string.description_magreader,
            R.string.description_mifare,

            R.string.description_paramfile,
            R.string.description_pinpad,
            R.string.description_printer,
            R.string.description_vector_printer,
            R.string.description_rfreader,

            R.string.description_scanner,
            R.string.description_serialport,
            R.string.description_usbdevice,
            R.string.description_signpanel,
            R.string.description_system,
            R.string.description_systemstatictis,
            R.string.description_tms,
            R.string.description_decodeengine,
            R.string.description_onGuard,
            R.string.description_resetfactory,
            R.string.description_update,
            R.string.description_net_work_manager,
            R.string.device_admin_title,
    };
    private final static String[] CLASSES = new String[] {
            ".myemv.StartEMVFirstActivity",
            ".mymobile.MobileMoneyActivity",


            ".view.AlgorithmActivity",
            ".view.BeeperActivity",
            ".view.CashBoxActivity",
            ".view.DeviceManagerActivity",
            ".view.DigledActivity",
            ".view.dock.MenuActivity",
            ".view.emv.MenuActivity",
            ".view.ethernet.EthernetActivity",
            ".view.ExScannerActivity",
            ".view.FelicaReaderActivity",
            ".view.FiscalActivity",
            ".view.icreader.MenuActivity",
            ".view.IndustryMagReaderActivity",
            ".view.InnerScannerActivity",
            ".view.LedActivity",
            ".view.LKIToolActivity",
            ".view.MagReaderActivity",
            ".view.mifare.MenuActivity",
            ".view.ParamFileActivity",
            ".view.pinpad.MenuActivity",
            ".view.PrinterActivity",
            ".view.VectorPrinterActivity",
            ".view.RFReaderActivity",
            ".view.ScannerActivity",
            ".view.SerialPortActivity",
            ".view.UsbDeiceActivity",
             ".view.SignPanelActivity",
            ".view.system.MenuActivity",
            ".view.SystemStatisticsActivity",
            ".view.TMSActivity",
            ".view.DecodeEngineActivity",
            ".view.onguard.OnGuardActivity",
            ".view.ResetFactoryActivity",
            ".view.update.UpdateActivity",
            ".view.networkmanager.MenuActivity",
            ".view.DeviceAdminActivity"
    };

    private TextView tvRegister;
    private RelativeLayout layoutRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_title));

        initListView(TITLES, DESCRIPTION_STRING_IDS, CLASSES);

        setSettingClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SettingActivity.class);
            }
        });

        DeviceHelper.me().setServiceListener(this);

        requestPermission();
    }

    private boolean requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            Log.d("///", "/// permissions.size = " + permissions.size());
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_PERMISSION);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = true;
        if (requestCode == REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    isGranted = false;
                }
            }
        }
        if (!isGranted) {
            DialogUtil.showMessage(this, "Permission not granted", "Some features will be unavailable");
        }
    }

    @Override
    public void onReady(String version) {
        TextView tvJarVer = bindViewById(R.id.tvJarVer);
        TextView tvServiceVer = bindViewById(R.id.tvServiceVer);

        tvJarVer.setText("USD-API JAR: V" + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_DATE);
        tvServiceVer.setText("SERVICE: " + version);

        initRegister();
    }

    public void initRegister() {
        tvRegister = bindViewById(R.id.tvRegister);
        layoutRegister = bindViewById(R.id.layoutRegister);
        layoutRegister.setVisibility(View.VISIBLE);
        layoutRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRegister()) {
                    unregister();
                } else {
                    DialogUtil.showOption(MainActivity.this,
                            "Register Selection",
                            "Whether to use the Epay Module?",
                            "Yes", "No",
                            new OnChooseListener() {
                                @Override
                                public void onConfirm() {
                                    register(true);
                                }

                                @Override
                                public void onCancel() {
                                    register(false);
                                }
                            });
                }
            }
        });

        layoutRegister.callOnClick();
    }

    private void unregister() {
        try {
            DeviceHelper.me().unregister();
            registerEnabled(true);
        } catch (IllegalStateException e) {
            toast("unregister fail: " + e.getMessage());
        }
    }

    private void register(boolean useEpayModule) {
        try {
            DeviceHelper.me().register(useEpayModule);
            registerEnabled(false);
        } catch (IllegalStateException e) {
            toast("register fail: " + e.getMessage());
        }
    }

    private void registerEnabled(boolean enabled) {
        if (enabled) {
            tvRegister.setEnabled(true);
            tvRegister.setText(getString(R.string.register));
        } else {
            tvRegister.setEnabled(false);
            tvRegister.setText(getString(R.string.unregister));
        }
    }

    private boolean isRegister() {
        return !tvRegister.isEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister();
        DeviceHelper.me().setServiceListener(null);
    }
}
