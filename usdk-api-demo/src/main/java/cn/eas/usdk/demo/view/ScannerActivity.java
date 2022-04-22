package cn.eas.usdk.demo.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.usdk.apiservice.aidl.scanner.BackError;
import com.usdk.apiservice.aidl.scanner.CameraId;
import com.usdk.apiservice.aidl.scanner.FrontError;
import com.usdk.apiservice.aidl.scanner.OnScanListener;
import com.usdk.apiservice.aidl.scanner.ScanType;
import com.usdk.apiservice.aidl.scanner.ScannerData;
import com.usdk.apiservice.aidl.scanner.UScanner;

import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.BytesUtil;

public class ScannerActivity extends BaseDeviceActivity {
    private static final List<SpinnerItem> cameraIdList = new LinkedList<>();

    static {
        cameraIdList.add(new SpinnerItem(CameraId.FRONT, "Preposition"));
        cameraIdList.add(new SpinnerItem(CameraId.BACK, "Postposition"));
        cameraIdList.add(new SpinnerItem(CameraId.SIDE, "Side"));
    }

    private UScanner scanner;
    private int cameraId;
    private boolean enableFixFocus = false;
    private boolean showDefaultPreviewUI = false;
    private boolean fillLight = false;
    private boolean indicantLight = false;
    private boolean scanHid = false;
    private boolean customUI = false;
    private boolean enableOutputBroadcast = false;
    private boolean autoAddTABChar = false;
    private boolean autoAddCarriageReturnChar = false;
    private boolean charConvertToKey = false;

    private static List<SpinnerItem> gravityList = new LinkedList<>();

    static {
        gravityList.add(new SpinnerItem(Gravity.NO_GRAVITY, "NO_GRAVITY"));
        gravityList.add(new SpinnerItem(Gravity.RIGHT, "RIGHT"));
        gravityList.add(new SpinnerItem(Gravity.LEFT, "LEFT"));
        gravityList.add(new SpinnerItem(Gravity.CENTER_VERTICAL, "CENTER_VERTICAL"));
        gravityList.add(new SpinnerItem(Gravity.CENTER, "CENTER"));
        gravityList.add(new SpinnerItem(Gravity.CENTER_HORIZONTAL, "CENTER_HORIZONTAL"));
    }

    private int gravity = Gravity.NO_GRAVITY;
    private int layoutGravity = Gravity.NO_GRAVITY;
    private String textColor;
    private String customTextView;

    private EditText edtKey;
    private EditText edtValue;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scanner);
        setTitle("Scanner Module");
        initViews();
    }

    private void initViews() {
        initSpinner();
        initSwithes();

        edtKey = findViewById(R.id.edt_key);
        edtValue = findViewById(R.id.edt_value);
    }

    private void initSpinner() {
        initDeviceInstance(cameraIdList.get(0).getValue());
        NiceSpinner cameraIdSpinner = (NiceSpinner) findViewById(R.id.cameraIdSpinner);
        cameraIdSpinner.attachDataSource(cameraIdList);
        cameraIdSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                initDeviceInstance(cameraIdList.get(position).getValue());
                if (cameraIdList.get(position).getValue() == CameraId.BACK) {
                    findViewById(R.id.layout_custom_textview).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.layout_custom_textview).setVisibility(View.GONE);
                }

                if (cameraIdList.get(position).getValue() == CameraId.SIDE) {
                    findViewById(R.id.layoutSideParams).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.layoutSideParams).setVisibility(View.GONE);
                }
            }
        });
    }

    private void initSwithes() {
        Switch switchEnableFixFocus = bindViewById(R.id.switchEnableFixFocus);
        switchEnableFixFocus.setChecked(enableFixFocus);
        switchEnableFixFocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableFixFocus = isChecked;
            }
        });
        Switch switchShowDefaultPreviewUI = bindViewById(R.id.switchShowDefaultPreviewUI);
        switchShowDefaultPreviewUI.setChecked(showDefaultPreviewUI);
        switchShowDefaultPreviewUI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showDefaultPreviewUI = isChecked;
            }
        });
        Switch switchFillLight = bindViewById(R.id.switchFillLight);
        switchFillLight.setChecked(fillLight);
        switchFillLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fillLight = isChecked;
            }
        });
        Switch switchIndicantLight = bindViewById(R.id.switchIndicantLight);
        switchIndicantLight.setChecked(indicantLight);
        switchIndicantLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                indicantLight = isChecked;
            }
        });
        Switch switchCustomUI = bindViewById(R.id.switchCustomUI);
        switchCustomUI.setChecked(fillLight);
        switchCustomUI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customUI = isChecked;
            }
        });
        Switch switchHidScan = bindViewById(R.id.switchHidScan);
        switchHidScan.setChecked(scanHid);
        switchHidScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                scanHid = isChecked;
            }
        });

        Switch switchEnableOutputBroadcast = bindViewById(R.id.switchEnableOutputBroadcast);
        switchEnableOutputBroadcast.setChecked(enableOutputBroadcast);
        switchEnableOutputBroadcast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableOutputBroadcast = isChecked;
            }
        });

        Switch switchAutoAddTABChar = bindViewById(R.id.switchAutoAddTABChar);
        switchAutoAddTABChar.setChecked(autoAddTABChar);
        switchAutoAddTABChar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoAddTABChar = isChecked;
            }
        });

        Switch switchAddCarriageReturnChar = bindViewById(R.id.switchAddCarriageReturnChar);
        switchAddCarriageReturnChar.setChecked(autoAddCarriageReturnChar);
        switchAddCarriageReturnChar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoAddCarriageReturnChar = isChecked;
            }
        });

        Switch switchCharConvertToKey = bindViewById(R.id.switchCharConvertToKey);
        switchCharConvertToKey.setChecked(charConvertToKey);
        switchCharConvertToKey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                charConvertToKey = isChecked;
            }
        });
    }

    protected void initDeviceInstance(int cameraId) {
        scanner = DeviceHelper.me().getScanner(cameraId);
    }

    public void startScan(View v) {
        outputBlueText(">>> startScan ");
        startScan();
    }

    public void stopScan(View v) {
        outputBlueText(">>> stopScan after 2 seconds");
        startScan();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, 2000);
    }

    private void startScan() {
        try {
            Bundle param = new Bundle();
            param.putInt(ScannerData.TIMEOUT, 15);
            param.putString(ScannerData.TITLE, "Custom title");
            param.putBoolean(ScannerData.IS_SHOW_HAND_INPUT_BUTTON, false);

            if (!TextUtils.isEmpty(customTextView)) {
                param.putString(ScannerData.CUSTOM_TEXTVIEW, customTextView);
            }

            if(edtKey.getText().toString().length() > 0 && edtValue.getText().toString().length() > 0) {
                String key = edtKey.getText().toString();
                int value = Integer.parseInt(edtValue.getText().toString());

                JSONObject propertys = new JSONObject();
                try {
                    propertys.put(key, value);
                    param.putString(ScannerData.SET_DECODE_PROPERTY, propertys.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            param.putBoolean(ScannerData.ENABLE_FIX_FOCUS, enableFixFocus);
            param.putBoolean(ScannerData.SHOW_FRONT_DEFAULT_PREVIEW_UI, showDefaultPreviewUI);
            param.putBoolean(ScannerData.FILL_LIGHT, fillLight);
            param.putBoolean(ScannerData.INDICANT_LIGHT, indicantLight);
            param.putBoolean(ScannerData.CAMERA_CUSTOM_UI, customUI);
            if (scanHid) {
                param.putString(ScannerData.SCAN_TYPE, ScanType.HID);
                param.putBoolean(ScannerData.ENABLE_OUTPUT_BROADCAST, enableOutputBroadcast);
                param.putBoolean(ScannerData.AUTO_ADD_TAB_CHAR, autoAddTABChar);
                param.putBoolean(ScannerData.AUTO_CARRIAGE_RETURN_CHAR, autoAddCarriageReturnChar);
                param.putBoolean(ScannerData.CHAR_CONVERT_TO_KEY, charConvertToKey);
            }
            scanner.startScan(param, new OnScanListener.Stub() {
                @Override
                public void onSuccess(String barcode) throws RemoteException {
                    outputText("=> " + barcode);
                    outputText("=> bytes data: " + BytesUtil.bytes2HexString(scanner.getRecentScanResult()));
                }

                @Override
                public void onError(int error) throws RemoteException {
                    outputRedText("=> onError | " + getErrorDetail(error));
                }

                @Override
                public void onTimeout() throws RemoteException {
                    outputRedText("=> onTimeout");
                }

                @Override
                public void onCancel() throws RemoteException {
                    outputText("=> onCancel");
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void stopScan() {
        try {
            scanner.stopScan();
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopScan();
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        if (cameraId == CameraId.BACK) {
            switch (error) {
                case BackError.ERROR_INIT_FAIL:
                    message = "ERROR_INIT_FAIL";
                    break;
                case BackError.ERROR_ALREADY_INIT:
                    message = "ERROR_ALREADY_INIT";
                    break;
                case BackError.ERROR_AUTH_LICENSE:
                    message = "ERROR_AUTH_LICENSE";
                    break;
                case BackError.ERROR_OPEN_CAMERA:
                    message = "ERROR_OPEN_CAMERA";
                    break;
                default:
                    message = super.getErrorMessage(error);
            }
        } else {
            switch (error) {
                case FrontError.ERROR_INIT_FAIL:
                    message = "ERROR_INIT_FAIL";
                    break;
                case FrontError.ERROR_ALREADY_INIT:
                    message = "ERROR_ALREADY_INIT";
                    break;
                case FrontError.ERROR_INIT_ENGINE:
                    message = "ERROR_INIT_ENGINE";
                    break;
                case FrontError.ERROR_AUTH_LICENSE:
                    message = "ERROR_AUTH_LICENSE";
                    break;
                case FrontError.ERROR_NOT_INIT:
                    message = "ERROR_NOT_INIT";
                    break;
                case FrontError.ERROR_START_SCANNER:
                    message = "ERROR_START_SCANNER";
                    break;
                default:
                    message = super.getErrorMessage(error);
            }
        }
        return message;
    }

    public void addCustomTextView(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_scanner_add_textview, null);
        Button btnOK = view.findViewById(R.id.ok_btn);
        Button btnCancel = view.findViewById(R.id.cancel_btn);
        final EditText edtText = view.findViewById(R.id.edt_text);
        final NiceSpinner spinnerGravity = view.findViewById(R.id.spinner_gravity);
        final NiceSpinner spinnerLayoutGravity = view.findViewById(R.id.spinner_layoutgravity);
        initCustomSpinner(view);

        final EditText edtTextSize = view.findViewById(R.id.edt_textsize);
        ImageButton btnTextSizeMinus = view.findViewById(R.id.btn_textsize_minus);
        ImageButton btnTextSizePlus = view.findViewById(R.id.btn_textsize_plus);
        initCustomMinusPlus(edtTextSize, btnTextSizeMinus, btnTextSizePlus, 0, 30);

        final EditText edtMarginTop = view.findViewById(R.id.edt_margintop);
        ImageButton btnMarginTopMinus = view.findViewById(R.id.btn_top_minus);
        ImageButton btnMarginTopPlus = view.findViewById(R.id.btn_top_plus);
        initCustomMinusPlus(edtMarginTop, btnMarginTopMinus, btnMarginTopPlus, 0, 50);

        final EditText edtMarginBottom = view.findViewById(R.id.edt_marginbottom);
        ImageButton btnMarginBottomMinus = view.findViewById(R.id.btn_bottom_minus);
        ImageButton btnMarginBottomPlus = view.findViewById(R.id.btn_bottom_plus);
        initCustomMinusPlus(edtMarginBottom, btnMarginBottomMinus, btnMarginBottomPlus, 0, 50);

        final EditText edtMarginLeft = view.findViewById(R.id.edt_marginleft);
        ImageButton btnMarginLeftMinus = view.findViewById(R.id.btn_left_minus);
        ImageButton btnMarginLeftPlus = view.findViewById(R.id.btn_left_plus);
        initCustomMinusPlus(edtMarginLeft, btnMarginLeftMinus, btnMarginLeftPlus, 0, 50);

        final EditText edtMarginRight = view.findViewById(R.id.edt_marginright);
        ImageButton btnMarginRightMinus = view.findViewById(R.id.btn_right_minus);
        ImageButton btnMarginRightPlus = view.findViewById(R.id.btn_right_plus);
        initCustomMinusPlus(edtMarginRight, btnMarginRightMinus, btnMarginRightPlus, 0, 50);

        final Button btnColorPicker = view.findViewById(R.id.btn_color_picker);

        if (!TextUtils.isEmpty(customTextView)) {
            try {
                JSONObject custom = new JSONObject(customTextView);
                edtText.setText(custom.getString("text"));
                edtTextSize.setText(String.valueOf(custom.getInt("textSize")));
                textColor = custom.getString("textColor");
                btnColorPicker.setBackgroundColor(Color.parseColor(textColor));
                btnColorPicker.setText(textColor);

                for (int i = 0; i < gravityList.size(); ++i) {
                    SpinnerItem item = gravityList.get(i);
                    if (item.getValue() == custom.getInt("gravity")) {
                        spinnerGravity.setSelectedIndex(i);
                    }
                    if (item.getValue() == custom.getInt("layoutGravity")) {
                        spinnerLayoutGravity.setSelectedIndex(i);
                    }
                }

                edtMarginTop.setText(String.valueOf(custom.getInt("marginTop")));
                edtMarginBottom.setText(String.valueOf(custom.getInt("marginBottom")));
                edtMarginLeft.setText(String.valueOf(custom.getInt("marginLeft")));
                edtMarginRight.setText(String.valueOf(custom.getInt("marginRight")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    JSONObject json = new JSONObject();
                    json.put("text", edtText.getText().toString());
                    json.put("textSize", Integer.parseInt(edtTextSize.getText().toString()));
                    json.put("textColor", textColor);
                    json.put("gravity", gravity);
                    json.put("layoutGravity", layoutGravity);
                    json.put("marginTop", Integer.parseInt(edtMarginTop.getText().toString()));
                    json.put("marginLeft", Integer.parseInt(edtMarginLeft.getText().toString()));
                    json.put("marginBottom", Integer.parseInt(edtMarginBottom.getText().toString()));
                    json.put("marginRight", Integer.parseInt(edtMarginRight.getText().toString()));
                    customTextView = json.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.Builder builder = ColorPickerDialog.newBuilder();
                try {
                    int color = Color.parseColor(textColor);
                    builder.setColor(color);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ColorPickerDialog colorPickerDialog = builder.setDialogTitle(R.string.color_picker)
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setShowAlphaSlider(true)
                        .setDialogId(0)
                        .setAllowPresets(false)
                        .create();
                colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        btnColorPicker.setBackgroundColor(color);
                        textColor = "#" + Integer.toHexString(color);
                        btnColorPicker.setText(textColor);
                    }

                    @Override
                    public void onDialogDismissed(int dialogId) {

                    }
                });
                colorPickerDialog.show(ScannerActivity.this.getFragmentManager(), "color-picker-dialog");
            }
        });
    }

    private void initCustomSpinner(View view) {
        NiceSpinner spinnerGravity = view.findViewById(R.id.spinner_gravity);
        NiceSpinner spinnerLayoutGravity = view.findViewById(R.id.spinner_layoutgravity);

        spinnerGravity.attachDataSource(gravityList);
        spinnerGravity.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                gravity = gravityList.get(position).getValue();
            }
        });

        spinnerLayoutGravity.attachDataSource(gravityList);
        spinnerLayoutGravity.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                layoutGravity = gravityList.get(position).getValue();
            }
        });
    }

    private void initCustomMinusPlus(final EditText editText, ImageButton minus, ImageButton plus, final int min, final int max) {
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(editText.getText().toString());
                if (number <= min) {
                    return;
                }
                editText.setText(String.valueOf(--number));
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(editText.getText().toString());
                if (number >= max) {
                    return;
                }
                editText.setText(String.valueOf(++number));
            }
        });
    }
}
