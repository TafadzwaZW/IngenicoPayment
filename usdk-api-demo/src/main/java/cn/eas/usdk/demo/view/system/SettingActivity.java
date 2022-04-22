package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.data.StringValue;
import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.setting.IMEInfo;
import com.usdk.apiservice.aidl.system.setting.LanguageInfo;
import com.usdk.apiservice.aidl.system.setting.TimeZone;
import com.usdk.apiservice.aidl.system.setting.USetting;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class SettingActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> hourList = new ArrayList<>();
    private static List<SpinnerItem> minuteList = new ArrayList<>();
    private static List<SpinnerItem> secondList = new ArrayList<>();
    private static List<SpinnerItem> emvAuthInfoList = new ArrayList<>();
    static {
        for (int i = 0; i< 24; i++) {
            hourList.add(new SpinnerItem(i, String.format("%02d", i)));
        }
        for (int i = 0; i< 60; i++) {
            minuteList.add(new SpinnerItem(i, String.format("%02d", i)));
            secondList.add(new SpinnerItem(i, String.format("%02d", i)));
        }
        emvAuthInfoList.add(new SpinnerItem(1, "EMV L1"));
        emvAuthInfoList.add(new SpinnerItem(2, "EMV L2"));
    }

    private USetting setting;
    private String timeZoneID;
    private int hour, minute, second;
    private int screenTimeout;
    private int emvAuthInfoType;

    private List<LanguageInfo> languageInfoList;
    private LanguageInfo languageInfo;

    private List<IMEInfo> imeInfoList;
    private IMEInfo imeInfo;

    @Override
    protected void onCreateView(Bundle savedInstanceState) throws Exception {
        initDeviceInstance();
        setContentView(R.layout.activity_system_setting);
        setTitle("Setting Module");
        initSwitch();
        initSpinner();
    }

    protected void initDeviceInstance() {
        setting = DeviceHelper.me().getSetting();
    }

    private void initSwitch() {
        Switch autoSwitch = findViewById(R.id.autoSwitch);
        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAutoTimeZoneEnabled(isChecked);
            }
        });
    }

    private void setAutoTimeZoneEnabled(boolean enabled) {
        try {
            outputBlueText(">>> setAutoTimeZoneEnabled : " + enabled);
            int ret = setting.setAutoTimeZoneEnabled(enabled);
            if (ret == SystemError.SUCCESS) {
                outputText("setAutoTimeZoneEnabled success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void initSpinner() throws Exception {
        final List<TimeZone> timeZoneList = getAvailableTimeZones();
        List<String> timeZoneIDList = getTimeZoneIDList(timeZoneList);
        timeZoneID = timeZoneList.get(0).getID();
        NiceSpinner timeZoneSpinner = (NiceSpinner) findViewById(R.id.timeZoneSpinner);
        timeZoneSpinner.attachDataSource(timeZoneIDList);
        timeZoneSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                timeZoneID = timeZoneList.get(position).getID();
            }
        });

        hour = hourList.get(0).getValue();
        NiceSpinner hourSpinner = findViewById(R.id.hourSpinner);
        hourSpinner.attachDataSource(hourList);
        hourSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                hour = hourList.get(position).getValue();
            }
        });

        minute = minuteList.get(0).getValue();
        NiceSpinner minuteSpinner = findViewById(R.id.minuteSpinner);
        minuteSpinner.attachDataSource(minuteList);
        minuteSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                minute = minuteList.get(position).getValue();
            }
        });

        second = secondList.get(0).getValue();
        NiceSpinner secondSpinner = findViewById(R.id.secondSpinner);
        secondSpinner.attachDataSource(secondList);
        secondSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                second = secondList.get(position).getValue();
            }
        });

        final List<SpinnerItem> timeoutShowList = turnToSpinnerList(getAvailableScreenTimeouts());
        screenTimeout = timeoutShowList.get(0).getValue();
        NiceSpinner screenTimeoutSpinner = findViewById(R.id.screenTimeoutSpinner);
        screenTimeoutSpinner.attachDataSource(timeoutShowList);
        screenTimeoutSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                screenTimeout = timeoutShowList.get(position).getValue();
            }
        });


        emvAuthInfoType = emvAuthInfoList.get(0).getValue();
        NiceSpinner emvAuthInfoTypeSpinner = findViewById(R.id.emvAuthInfoTypeSpinner);
        emvAuthInfoTypeSpinner.attachDataSource(emvAuthInfoList);
        emvAuthInfoTypeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                emvAuthInfoType = emvAuthInfoList.get(position).getValue();
            }
        });
    }

    private List<TimeZone> getAvailableTimeZones() throws Exception {
        List<TimeZone> timeZoneList = new ArrayList<>();
        int ret = setting.getAvailableTimeZones(timeZoneList);
        if (ret == SystemError.SUCCESS) {
            return timeZoneList;
        }
        throw new Exception(SystemErrorUtil.getErrorMessage(ret));
    }

    private List<String> getTimeZoneIDList(List<TimeZone> timeZoneList) {
        List<String> timeZoneIDList = new LinkedList<>();
        for(TimeZone timeZone : timeZoneList) {
            timeZoneIDList.add(timeZone.getID());
        }
        return timeZoneIDList;
    }

    public void getTimeZone(View v) {
        try {
            outputBlueText(">>> getTimeZone");
            TimeZone timeZone = new TimeZone();
            int ret = setting.getTimeZone(timeZone);
            if (ret == SystemError.SUCCESS) {
                outputText("> ID = " + timeZone.getID());
                outputText("> DisplayName = " + timeZone.getDisplayName());
                outputText("> DSTSavings = " + timeZone.getDSTSavings());
                outputText("> RawOffset = " + timeZone.getRawOffset());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setTimeZone(View v) {
        try {
            outputBlueText(">>> setTimeZone : " + timeZoneID);
            int ret = setting.setTimeZone(timeZoneID);
            if (ret == SystemError.SUCCESS) {
                outputText("setTimeZone success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setSelfExamTime(View v) {
        try {
            outputBlueText(">>> setSelfExamTime");
            int ret = setting.setSelfExamTime(hour, minute, second);
            if (ret == SystemError.SUCCESS) {
                outputText("setSelfExamTime success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getEMVAuthInfo(View v) {
        try {
            outputBlueText(">>> getEMVAuthInfo : " + emvAuthInfoType);
            StringValue authInfo = new StringValue();
            int ret = setting.getEMVAuthInfo(emvAuthInfoType, authInfo);
            if (ret == SystemError.SUCCESS) {
                outputText("getEMVAuthInfo success:" + authInfo);
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getSelfExamTime(View v) {
        try {
            outputBlueText(">>> getSelfExamTime");
            StringValue timeOut = new StringValue();
            int ret = setting.getSelfExamTime(timeOut);
            if (ret == SystemError.SUCCESS) {
                outputText("getSelfExamTime success:" + timeOut.getData());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    // --------------------------- screen timeout ---------------

    private List<SpinnerItem> turnToSpinnerList(List<IntValue> timeoutList) {
        List<SpinnerItem> list = new ArrayList<>();
        for (IntValue item : timeoutList) {
            if (item.getData() == -1) {
                list.add(new SpinnerItem(item.getData(), "Never sleeps"));
            } else if (item.getData() > 60000) {
                list.add(new SpinnerItem(item.getData(), item.getData()/60000 + getString(R.string.minutes)));
            } else if (item.getData() == 60000) {
                list.add(new SpinnerItem(item.getData(), "1" + getString(R.string.minute)));
            } else if (item.getData() > 1000) {
                list.add(new SpinnerItem(item.getData(), item.getData()/1000 + getString(R.string.seconds)));
            } else if (item.getData() == 1000) {
                list.add(new SpinnerItem(item.getData(), "1" + getString(R.string.second)));
            } else {
                list.add(new SpinnerItem(item.getData(), item.getData() + getString(R.string.milliseconds)));
            }
        }
        return list;
    }

    private List<IntValue> getAvailableScreenTimeouts() throws Exception {
        List<IntValue> timeoutList = new ArrayList<>();
        int ret = setting.getAvailableScreenTimeouts(timeoutList);
        if (ret == SystemError.SUCCESS) {
            return timeoutList;
        }
        throw new Exception(SystemErrorUtil.getErrorMessage(ret));
    }

    public void getScreenTimeout(View v) {
        try {
            outputBlueText(">>> getScreenTimeout");
            IntValue timeout = new IntValue();
            int ret = setting.getScreenTimeout(timeout);
            if (ret == SystemError.SUCCESS) {
                outputText("Current ScreenTimeout = " + timeout.getData());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setScreenTimeout(View v) {
        try {
            outputBlueText(">>> setScreenTimeout : " + screenTimeout);
            int ret = setting.setScreenTimeout(screenTimeout);
            if (ret == SystemError.SUCCESS) {
                outputText("setScreenTimeout success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    // --------------------------- System Language ---------------

    public void getLanguageList(View v) {
        try {
            outputBlueText(">>> getLanguageList");
            languageInfoList = new ArrayList<>();
            int ret = setting.getLanguageList(languageInfoList);
            if (ret == SystemError.SUCCESS) {
                outputText("getLanguageList success");

                List<String> list = new ArrayList<>();
                for (LanguageInfo info : languageInfoList) {
                    list.add(info.getLabel());
                }
                languageInfo = languageInfoList.get(0);
                NiceSpinner languageSpinner = findViewById(R.id.languageSpinner);
                languageSpinner.attachDataSource(list);
                languageSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        languageInfo = languageInfoList.get(position);
                    }
                });
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setSystemLanguage(View v) {
        try {
            if (languageInfo == null) {
                return;
            }
            outputBlueText(">>> setSystemLanguage");
            int ret = setting.setSystemLanguage(languageInfo.getLanguageCode(), languageInfo.getCountryCode());
            if (ret == SystemError.SUCCESS) {
                outputText("setSystemLanguage success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    // --------------------------- System IME ---------------

    public void getCurrentIME(View v) {
        try {
            outputBlueText(">>> getCurrentIME");
            IMEInfo ime = new IMEInfo();
            int ret = setting.getCurrentIME(ime);
            if (ret == SystemError.SUCCESS) {
                outputText("getCurrentIME success : " + ime.getLabel());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getInstalledIMEList(View v) {
        try {
            outputBlueText(">>> getInstalledIMEList");
            imeInfoList = new ArrayList<>();
            int ret = setting.getInstalledIMEList(imeInfoList);
            if (ret == SystemError.SUCCESS) {
                outputText("getInstalledIMEList success");

                List<String> list = new ArrayList<>();
                for (IMEInfo info : imeInfoList) {
                    list.add(info.getLabel());
                }
                imeInfo = imeInfoList.get(0);
                NiceSpinner imeSpinner = findViewById(R.id.imeSpinner);
                imeSpinner.attachDataSource(list);
                imeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        imeInfo = imeInfoList.get(position);
                    }
                });
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setCurrentIME(View v) {
        try {
            if (imeInfo == null) {
                return;
            }
            outputBlueText(">>> setCurrentIME");
            int ret = setting.setCurrentIME(imeInfo.getPackageName(), imeInfo.getClassName());
            if (ret == SystemError.SUCCESS) {
                outputText("setCurrentIME success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

}
