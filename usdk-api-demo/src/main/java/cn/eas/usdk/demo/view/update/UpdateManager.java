package cn.eas.usdk.demo.view.update;

import android.os.Bundle;
import android.os.RemoteException;

import com.usdk.apiservice.aidl.update.OnProcessListener;
import com.usdk.apiservice.aidl.update.UUpdate;
import com.usdk.apiservice.aidl.update.UpdateResult;
import java.util.List;

public class UpdateManager {

    private UUpdate update;
    private static UpdateManager updateManager;

    private UpdateManager() {}
    public static UpdateManager getInstance() {
        if (updateManager == null) {
            synchronized (UpdateManager.class) {
                if (updateManager == null) {
                    updateManager = new UpdateManager();
                }
            }
        }
        return updateManager;
    }

    public void init(UUpdate update) {
        this.update = update;
    }

    public int getUpdateStatus() throws RemoteException {
        return update.getUpdateStatus();
    }

    public String getTermInfo() throws RemoteException {
        return update.getTermInfo();
    }

    public long getUpdateId() throws RemoteException {
        return update.getUpdateId();
    }

    public int update(Bundle params, OnProcessListener listener) throws RemoteException {
        return update.update(params, listener);
    }

    public void reboot(boolean isShowConfirmDialog, int rebootType) throws RemoteException {
        update.reboot(isShowConfirmDialog, rebootType);
    }

    public List<UpdateResult> getUpdateResultInfo(long updateId) throws RemoteException {
        return update.getUpdateResultInfo(updateId);
    }

    private UpdateListener updateListener;
    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }
    public UpdateListener getUpdateListener() {
        return this.updateListener;
    }
    public interface UpdateListener {
        void onFinish(long updateId, List<UpdateResult> resultsList);
    }
}
