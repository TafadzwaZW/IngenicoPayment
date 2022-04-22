package cn.eas.usdk.demo.view.mifare;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.mifare.ApduStyleMode;
import com.usdk.apiservice.aidl.mifare.MifareKey;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.List;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BytesUtil;

public class DesFireFileActivity extends BaseDesFireActivity {
    private static final String FILE_TYPE_STANDARD_FILE = "standard";
    private static final String FILE_TYPE_LINEAR_RECORD = "linear";
    private static final String FILE_TYPE_VALUE_FILE = "value";
    private static List<String> fileTypeList = new ArrayList<>();

    static {
        fileTypeList.add(FILE_TYPE_STANDARD_FILE);
        fileTypeList.add(FILE_TYPE_LINEAR_RECORD);
        fileTypeList.add(FILE_TYPE_VALUE_FILE);
    }

    private EditText edtFileId;
    private EditText edtData;
    private LinearLayout layoutStandardFile;
    private LinearLayout layoutLinearRecord;
    private LinearLayout layoutValueFile;
    private Button btnCommit;

    private String fileType = FILE_TYPE_STANDARD_FILE;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_desfire_file);
        setTitle("DesFire File Module");

        edtFileId = findViewById(R.id.edt_file_id);
        edtData = findViewById(R.id.edt_data_written);

        layoutStandardFile = findViewById(R.id.layout_std_file);
        layoutLinearRecord = findViewById(R.id.layout_linear_record);
        layoutValueFile = findViewById(R.id.layout_value_file);
        btnCommit = findViewById(R.id.btn_commit);

        initSpinner();
    }

    private void initSpinner() {
        NiceSpinner deviceSpinner = findViewById(R.id.spinner_file_type);
        deviceSpinner.attachDataSource(fileTypeList);
        deviceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                fileType = fileTypeList.get(position);
                switch (fileType) {
                    case FILE_TYPE_STANDARD_FILE:
                        layoutLinearRecord.setVisibility(View.GONE);
                        layoutValueFile.setVisibility(View.GONE);
                        btnCommit.setVisibility(View.INVISIBLE);
                        layoutStandardFile.setVisibility(View.VISIBLE);
                        break;
                    case FILE_TYPE_LINEAR_RECORD:
                        layoutValueFile.setVisibility(View.GONE);
                        layoutStandardFile.setVisibility(View.GONE);
                        layoutLinearRecord.setVisibility(View.VISIBLE);
                        btnCommit.setVisibility(View.VISIBLE);
                        break;
                    case FILE_TYPE_VALUE_FILE:
                        layoutStandardFile.setVisibility(View.GONE);
                        layoutLinearRecord.setVisibility(View.GONE);
                        layoutValueFile.setVisibility(View.VISIBLE);
                        btnCommit.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void createStdDataFile(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        int isoFileId = -1;
        int comSettings = 0x01;
        int accessRights = 0xeeee;
        int fileSize = 0x01;
        outputText("createStdDataFile | fileId = " + fileId);
        int ret = desFireManager.createStdDataFile(fileId, isoFileId, comSettings, accessRights, fileSize);
        outputRet("createStdDataFile", ret);
    }

    public void writeData(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        String data = edtData.getText().toString();
        if (TextUtils.isEmpty(data)) {
            toast("Input write data");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("writeData | data = " + data);
        int ret = desFireManager.writeData(fileId, 0x00, data.getBytes());
        outputRet("writeData", ret);
    }

    public void writeDataCS(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        String data = edtData.getText().toString();
        if (TextUtils.isEmpty(data)) {
            toast("Input write data");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        int comSettings = 0x00;
        outputText("writeDataCS | data = " + data);
        int ret = desFireManager.writeDataCS(fileId, 0x00, comSettings, data.getBytes());
        outputRet("writeDataCS", ret);
    }

    public void readData(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        BytesValue bytesValue = new BytesValue();
        int ret = desFireManager.readData(fileId, 0x00, 1, bytesValue);
        outputRet("readData", ret);
        if (ret == SUCCESS) {
            outputText("readData | data = " + BytesUtil.fromBytes(bytesValue.getData()));
        }
    }

    public void readDataCS(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int comSettings = 0x00;
        int fileId = Integer.parseInt(edtFileId.getText().toString());
        BytesValue bytesValue = new BytesValue();
        int ret = desFireManager.readDataCS(fileId, 0x00, 1, comSettings, bytesValue);
        outputRet("readDataCS", ret);
        outputText("readDataCS | data = " + BytesUtil.fromBytes(bytesValue.getData()));
    }

    public void deleteFile(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        int ret = desFireManager.deleteFile(fileId);
        outputRet("deleteFile", ret);
    }

    public void createLinearRecordFile(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        int isoFileId = -1;
        int comSettings = 0x01;
        int accessRights = 0x1111;
        int recordSize = 0x05;
        int maxNumberOfRecords = 0x10;

        outputText("createLinearRecordFile | fileId = " + fileId);
        int ret = desFireManager.createLinearRecordFile(fileId, isoFileId, comSettings, accessRights, recordSize, maxNumberOfRecords);
        outputRet("createLinearRecordFile", ret);
    }

    public void writeRecord(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        String data = edtData.getText().toString();
        if (TextUtils.isEmpty(data)) {
            toast("Input write data");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("writeRecord | fileId = " + fileId);
        int ret = desFireManager.writeRecord(fileId, 0x00, data.getBytes());
        outputRet("writeRecord", ret);
    }

    public void readRecords(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("readRecords | fileId = " + fileId);
        BytesValue bytesValue = new BytesValue();
        int ret = desFireManager.readRecords(fileId, 0x00, 0, bytesValue);
        outputRet("readRecords", ret);
        if (ret == SUCCESS) {
            outputText("readRecords | data = " + BytesUtil.fromBytes(bytesValue.getData()));
        }
    }

    public void commitTransaction(View view) throws RemoteException {
        int ret = desFireManager.commitTransaction();
        outputRet("commitTransaction", ret);
    }

    public void writeRecordCS(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        String data = edtData.getText().toString();
        if (TextUtils.isEmpty(data)) {
            toast("Input write data");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int comSettings = 0x01;
        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("writeRecordCS | fileId = " + fileId);
        int ret = desFireManager.writeRecordCS(fileId, 0x00, comSettings, data.getBytes());
        outputRet("writeRecordCS", ret);
    }

    public void readRecordsCS(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int comSettings = 0x01;
        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("readRecordsCS | fileId = " + fileId);
        BytesValue bytesValue = new BytesValue();
        int ret = desFireManager.readRecordsCS(fileId, 0x00, 1, comSettings, bytesValue);
        outputRet("readRecordsCS", ret);
        if (ret == SUCCESS) {
            outputText("readRecordsCS | data = " + BytesUtil.fromBytes(bytesValue.getData()));
        }
    }

    public void createValueFile(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        int comSettings = 0x01;
        int accessRights = 0x1111;
        int lowerLimit = -10;
        int upperLimit = 10;
        int value = 0;
        byte limitedCreditEnabled = 0x03;
        outputText("createValueFile | fileId = " + fileId);
        int ret = desFireManager.createValueFile(fileId, comSettings, accessRights, lowerLimit, upperLimit, value, limitedCreditEnabled);
        outputRet("createValueFile", ret);
    }

    public void credit(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        String data = edtData.getText().toString();
        if (TextUtils.isEmpty(data)) {
            toast("Input write data");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            toast("Input data must be number");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("credit | fileId = " + fileId);
        int ret = desFireManager.credit(fileId, amount);
        outputRet("credit", ret);
    }

    public void debit(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        String data = edtData.getText().toString();
        if (TextUtils.isEmpty(data)) {
            toast("Input write data");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            toast("Input data must be number");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("debit | fileId = " + fileId);
        int ret = desFireManager.debit(fileId, amount);
        outputRet("debit", ret);
    }

    public void getValue(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("getValue | fileId = " + fileId);
        BytesValue bytesValue = new BytesValue();
        int ret = desFireManager.getValue(fileId, bytesValue);
        outputRet("getValue", ret);
        if (ret == SUCCESS) {
            outputText("getValue | data = " + BytesUtil.bytesToInt(bytesValue.getData()));
        }
    }

    public void creditCS(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        String data = edtData.getText().toString();
        if (TextUtils.isEmpty(data)) {
            toast("Input write data");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            toast("Input data must be number");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int comSettings = 0x01;
        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("creditCS | fileId = " + fileId);
        int ret = desFireManager.creditCS(fileId, amount, comSettings);
        outputRet("creditCS", ret);
    }

    public void debitCS(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        String data = edtData.getText().toString();
        if (TextUtils.isEmpty(data)) {
            toast("Input write data");
            return;
        }

        int amount = 0;
        try {
            amount = Integer.parseInt(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            toast("Input data must be number");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int comSettings = 0x01;
        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("debitCS | fileId = " + fileId);
        int ret = desFireManager.debitCS(fileId, amount, comSettings);
        outputRet("debitCS", ret);
    }

    public void getValueCS(View view) throws RemoteException {
        if (TextUtils.isEmpty(edtFileId.getText().toString())) {
            toast("Input file id!");
            return;
        }

        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        if (selectApp1AndAuth() != SUCCESS) {
            return;
        }

        int comSettings = 0x01;
        int fileId = Integer.parseInt(edtFileId.getText().toString());
        outputText("getValueCS | fileId = " + fileId);
        BytesValue bytesValue = new BytesValue();
        int ret = desFireManager.getValueCS(fileId, comSettings, bytesValue);
        outputRet("getValueCS", ret);
        if (ret == SUCCESS) {
            outputText("getValueCS | data = " + BytesUtil.bytesToInt(bytesValue.getData()));
        }
    }
}
