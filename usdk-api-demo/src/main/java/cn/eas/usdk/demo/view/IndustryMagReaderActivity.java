package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.magreader.IoCtrlCmd;
import com.usdk.apiservice.aidl.magreader.MagData;
import com.usdk.apiservice.aidl.magreader.MagError;
import com.usdk.apiservice.aidl.magreader.OnSwipeListener;
import com.usdk.apiservice.aidl.magreader.TrackID;
import com.usdk.apiservice.aidl.magreader.industry.UIndustryMagReader;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class IndustryMagReaderActivity extends BaseDeviceActivity {

    private UIndustryMagReader industryMagReader;
    private boolean trk1Enabled = false;
    private boolean trk2Enabled = true;
    private boolean trk3Enabled = true;
    private boolean retainCtlChar = false;
    private boolean trackStateChecked = true;
    private int wholeTrkId = 0;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_magreader);
        setTitle("Industry MagReader Module");
        initCheckBox();
        initSwitch();
    }

    protected void initDeviceInstance() {
        industryMagReader = DeviceHelper.me().getIndustryMagReader();
    }

    protected void initCheckBox() {
        CheckBox wholeTrack1CBox = bindViewById(R.id.wholeTrack1CBox);
        wholeTrack1CBox.setChecked(false);
        wholeTrack1CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
                setTrkIdWithWholeData(isSlted, TrackID.TRK1);
            }
        });
        CheckBox wholeTrack2CBox = bindViewById(R.id.wholeTrack2CBox);
        wholeTrack2CBox.setChecked(false);
        wholeTrack2CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
                setTrkIdWithWholeData(isSlted, TrackID.TRK2);
            }
        });
        CheckBox wholeTrack3CBox = bindViewById(R.id.wholeTrack3CBox);
        wholeTrack3CBox.setChecked(false);
        wholeTrack3CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
                setTrkIdWithWholeData(isSlted, TrackID.TRK3);
            }
        });

        CheckBox track1CBox = bindViewById(R.id.track1CBox);
        track1CBox.setChecked(trk1Enabled);
        track1CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
                trk1Enabled = isSlted;
            }
        });
        CheckBox track2CBox = bindViewById(R.id.track2CBox);
        track2CBox.setChecked(trk2Enabled);
        track2CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
                trk2Enabled = isSlted;
            }
        });
        CheckBox track3CBox = bindViewById(R.id.track3CBox);
        track3CBox.setChecked(trk3Enabled);
        track3CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
                trk3Enabled = isSlted;
            }
        });
    }

    private void setTrkIdWithWholeData(boolean isSlted, int trkId) {
        if (isSlted) {
            wholeTrkId |= trkId;
        } else {
            wholeTrkId &= ~trkId;
        }
    }

    private void initSwitch() {
        Switch ctrlCharSwitch = bindViewById(R.id.ctrlCharSwitch);
        ctrlCharSwitch.setChecked(retainCtlChar);
        ctrlCharSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                retainCtlChar = isChecked;
            }
        });
        Switch checkStateSwitch = bindViewById(R.id.checkStateSwitch);
        checkStateSwitch.setChecked(trackStateChecked);
        checkStateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                trackStateChecked = isChecked;
            }
        });
    }

    public void searchCard(View v) {
        int timeout = 30;
        outputBlueText(">>> searchCard | timeout: " + timeout);
        try {
            if (!setTRKDataType()) {
                return;
            }
            enableTrack(TrackID.TRK1, trk1Enabled);
            enableTrack(TrackID.TRK2, trk2Enabled);
            enableTrack(TrackID.TRK3, trk3Enabled);
            industryMagReader.retainControlChar(retainCtlChar);
            industryMagReader.setTrackStatesChecked(trackStateChecked);
            industryMagReader.searchCard(timeout, new OnSwipeListener.Stub() {
                @Override
                public void onSuccess(Bundle track) throws RemoteException {
                    outputText("=> onSuccess");
                    outputText( "TRACK1 = " + track.getString(MagData.TRACK1));
                    outputText( "TRACK2 = " + track.getString(MagData.TRACK2));
                    outputText( "TRACK3 = " + track.getString(MagData.TRACK3));

                    int[] trackStates = track.getIntArray(MagData.TRACK_STATES);
                    for(int i = 0; i < trackStates.length; i++) {
                        outputText(String.format("Track%s states = %d", i+1, trackStates[i]));
                    }
                }

                @Override
                public void onError(int i) throws RemoteException {
                    outputRedText("=> onFail: " + getErrorDetail(i));
                }

                @Override
                public void onTimeout() throws RemoteException {
                    outputRedText("=> onTimeout");
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private boolean setTRKDataType() throws RemoteException {
        int ret = industryMagReader.magIOControl(IoCtrlCmd.SET_TRKDATA_TYPE, new byte[] {(byte)wholeTrkId}, new BytesValue());
        if (ret != MagError.SUCCESS) {
            outputRedText("=> magIOControl fail: " + getErrorDetail(ret));
            return false;
        }
        return true;
    }

    private void enableTrack(int trkId, boolean enabled) throws RemoteException {
        if (enabled) {
            industryMagReader.enableTrack(trkId);
        } else {
            industryMagReader.disableTrack(trkId);
        }
    }

    public void stopSearch(View v) {
        outputBlueText(">>> stopSearch");
        try {
            industryMagReader.stopSearch();
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case MagError.ERROR_NODATA: message = "ERROR_NODATA"; break;
            case MagError.ERROR_NEEDSTART: message = "ERROR_NEEDSTART"; break;
            case MagError.ERROR_INVALID: message = "ERROR_INVALID"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
