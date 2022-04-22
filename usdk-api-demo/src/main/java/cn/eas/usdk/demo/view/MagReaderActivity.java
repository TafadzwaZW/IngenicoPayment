package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.magreader.IoCtrlCmd;
import com.usdk.apiservice.aidl.magreader.MagData;
import com.usdk.apiservice.aidl.magreader.MagError;
import com.usdk.apiservice.aidl.magreader.OnSwipeListener;
import com.usdk.apiservice.aidl.magreader.TrackID;
import com.usdk.apiservice.aidl.magreader.TrackType;
import com.usdk.apiservice.aidl.magreader.UMagReader;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.BytesUtil;

public class MagReaderActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> trackTypeList = new LinkedList<>();
    static {
        trackTypeList.add(new SpinnerItem(TrackType.BANK_CARD, "Bank card"));
        trackTypeList.add(new SpinnerItem(TrackType.INDUSTRY_CARD, "Industry card"));
    }

    private UMagReader magReader;
    private int trackType = TrackType.BANK_CARD;
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
        setTitle("MagReader Module");
        initCheckBox();
        initSpinner();
        initSwitch();
    }

    protected void initDeviceInstance() {
        magReader = DeviceHelper.me().getMagReader();
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

    private void initSpinner() {
        bindViewById(R.id.trackTypeLayout).setVisibility(View.VISIBLE);
        NiceSpinner trackTypeSpinner = findViewById(R.id.trackTypeSpinner);
        trackTypeSpinner.attachDataSource(trackTypeList);
        trackTypeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                trackType = trackTypeList.get(position).getValue();
            }
        });
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
                // return;
            }
            enableTrack(TrackID.TRK1, trk1Enabled);
            enableTrack(TrackID.TRK2, trk2Enabled);
            enableTrack(TrackID.TRK3, trk3Enabled);
            magReader.setTrackType(trackType);
            magReader.retainControlChar(retainCtlChar);
            magReader.setTrackStatesChecked(trackStateChecked);
            magReader.searchCard(timeout, new OnSwipeListener.Stub() {
                @Override
                public void onSuccess(Bundle track) throws RemoteException {
                    outputText("=> onSuccess");
                    outputText( "SERVICE_CODE = " + track.getString(MagData.SERVICE_CODE));
                    outputText( "EXPIRED_DATE = " + track.getString(MagData.EXPIRED_DATE));
                    outputText( "CardNo = " + track.getString(MagData.PAN));
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
        BytesValue result = new BytesValue();
        int ret = magReader.magIOControl(IoCtrlCmd.SET_TRKDATA_TYPE, new byte[] {(byte)wholeTrkId}, result);
        if (ret != MagError.SUCCESS) {
            outputRedText("=> magIOControl[SET_TRKDATA_TYPE] fail: " + getErrorDetail(ret));
            return false;
        } else {
            outputText("result = " + BytesUtil.bytes2HexString(result.getData()));
        }

//        result = new BytesValue();
//        ret = magReader.magIOControl(IoCtrlCmd.GET_TRKDATA_TYPE, new byte[] {(byte)wholeTrkId}, result);
//        if (ret != MagError.SUCCESS) {
//            outputRedText("=> magIOControl[GET_TRKDATA_TYPE] fail: " + getErrorDetail(ret));
//            return false;
//        } else {
//            outputText("result = " + BytesUtil.bytes2HexString(result.getData()));
//        }

        return true;
    }

    private void enableTrack(int trkId, boolean enabled) throws RemoteException {
        if (enabled) {
            magReader.enableTrack(trkId);
        } else {
            magReader.disableTrack(trkId);
        }
    }

    public void stopSearch(View v) {
        outputBlueText(">>> stopSearch");
        try {
            magReader.stopSearch();
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
            case MagError.CMD_NO_SUPPORT: message = "CMD_NO_SUPPORT"; break;
            case MagError.DLOPEN_FAILED: message = "DLOPEN_FAILED"; break;
            case MagError.DLSYM_FAILED: message = "DLSYM_FAILED"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
