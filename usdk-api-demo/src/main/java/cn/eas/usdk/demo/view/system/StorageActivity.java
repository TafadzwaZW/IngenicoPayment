package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.storage.MountPoint;
import com.usdk.apiservice.aidl.system.storage.StorageEventListener;
import com.usdk.apiservice.aidl.system.storage.UStorage;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class StorageActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> storageTypeList = new LinkedList<>();
    static {
        storageTypeList.add(new SpinnerItem(MountPoint.USB_DISK, "USB flash disk"));
        storageTypeList.add(new SpinnerItem(MountPoint.SD_CARD, "SD card"));
        storageTypeList.add(new SpinnerItem("9999", "error param"));
    }

    private UStorage storage;
    private String point;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_storage);
        setTitle("Storage Module");
        initSpinner();
    }

    protected void initDeviceInstance() {
        storage = DeviceHelper.me().getStorage();
    }

    private void initSpinner() {
        point = storageTypeList.get(0).getStringValue();
        NiceSpinner storageTypeSpinner = (NiceSpinner) findViewById(R.id.storageTypeSpinner);
        storageTypeSpinner.attachDataSource(storageTypeList);
        storageTypeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                point = storageTypeList.get(position).getStringValue();
            }
        });
    }

    public void registerListener(View v) {
        try {
            outputBlueText(">>> registerListener ");
            int ret = storage.registerListener(new StorageEventListener.Stub() {
                @Override
                public void onUsbMassStorageConnectionChanged(boolean connected) throws RemoteException {
                    outputText("=> onUsbMassStorageConnectionChanged | connected = " + connected);
                }

                @Override
                public void onStorageStateChanged(String path, String oldState, String newState) throws RemoteException {
                    outputText("=> onStorageStateChanged");
                    outputText("path = " + path);
                    outputText("oldState = " + oldState);
                    outputText("newState = " + newState);
                }
            });

            if (ret == SystemError.SUCCESS) {
                outputText("registerListener success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void unregisterListener(View v) {
        try {
            outputBlueText(">>> unregisterListener ");
            storage.unregisterListener();
            outputText("unregisterListener success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void mount(View v) {
        try {
            outputBlueText(">>> mount | point = " + point);
            int ret = storage.mount(point);
            if (ret == SystemError.SUCCESS) {
                outputText("mount success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void unmount(View v) {
        try {
            outputBlueText(">>> unmount | point = " + point);
            int ret = storage.unmount(point);
            if (ret == SystemError.SUCCESS) {
                outputText("unmount success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }
}
