package cn.eas.usdk.demo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.usdk.apiservice.aidl.printer.PrinterError;
import com.usdk.apiservice.aidl.vectorprinter.Alignment;
import com.usdk.apiservice.aidl.vectorprinter.OnPrintListener;
import com.usdk.apiservice.aidl.vectorprinter.TextSize;
import com.usdk.apiservice.aidl.vectorprinter.UVectorPrinter;
import com.usdk.apiservice.aidl.vectorprinter.VectorPrinterData;

import org.angmarch.views.NiceSpinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class VectorPrinterActivity extends BaseDeviceActivity {
    private static final List<Integer> sheetNumList = new LinkedList<>(Arrays.asList(1, 2, 3));

    private UVectorPrinter vectorPrinter;
    private int sheetNum;
    private ImageView previewImage;

    private byte[] readAssetsFile(Context ctx, String fileName) {
        InputStream input = null;
        try {
            input = ctx.getAssets().open(fileName);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveBitmap(Bitmap bitmap, String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            return;
        }
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            return;
        }
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_vector_printer);
        setTitle("Vector Printer Module");
        initSpinner();
        previewImage = findViewById(R.id.previewImg);
    }

    protected void initDeviceInstance() {
        vectorPrinter = DeviceHelper.me().getVectorPrinter();
    }

    private void initSpinner() {
        sheetNum = sheetNumList.get(0);
        NiceSpinner paperNumSpinner = (NiceSpinner) findViewById(R.id.paperNumSpinner);
        paperNumSpinner.attachDataSource(sheetNumList);
        paperNumSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                sheetNum = sheetNumList.get(position);
            }
        });
    }

    public void getStatus(View v) {
        outputBlueText(">>> getStatus");
        try {
            int status = vectorPrinter.getStatus();
            if (status != PrinterError.SUCCESS) {
                outputRedText(getErrorDetail(status));
                return;
            }
            outputText("The vectorPrinter status is normal!");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getValidWidth(View v) {
        outputBlueText(">>> getValidWidth");
        try {
            int validWidth = vectorPrinter.getValidWidth();
            outputText("validWidth =" + validWidth);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void startPrint(View v) {
        try {
            startPrinter(1);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void startPrinter(final int curSheetNo) throws RemoteException {
        final long startTime = System.currentTimeMillis();
        if (curSheetNo > sheetNum) {
            return;
        }
        outputBlueText(">>> start print | sheetNo = " + curSheetNo);
        Bundle initFormat = new Bundle();
//        initFormat.putString(VectorPrinterData.INIT_CUSTOM_TYPEFACE_PATH, "/sdcard/chinese.ttf");
//        initFormat.putInt(VectorPrinterData.LINE_SPACING, 10);
        initFormat.putFloat(VectorPrinterData.LETTER_SPACING, 0);
        initFormat.putBoolean(VectorPrinterData.AUTO_CUT_PAPER, true);
        vectorPrinter.init(initFormat);

        Bundle textFormat = new Bundle();
        textFormat.putInt(VectorPrinterData.ALIGNMENT, Alignment.NORMAL);
//        textFormat.putInt(VectorPrinterData.TEXT_SIZE, TextSize.LARGE);
        textFormat.putBoolean(VectorPrinterData.BLACK_BACKGROUND, true);
        try {
            printUnionPay();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("logo.bmp"));
            vectorPrinter.addImage(null, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Bitmap bitmap = vectorPrinter.getPreviewBmp();
        saveBitmap(bitmap, "/sdcard/Pictures/print.png");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewImage.setImageBitmap(bitmap);
            }
        });

        vectorPrinter.startPrint(new OnPrintListener.Stub() {
            @Override
            public void onFinish() throws RemoteException {
                outputText("=> onFinish | sheetNo = " + curSheetNo);
                System.out.println("time cost = + " + (System.currentTimeMillis() - startTime));
                startPrinter(curSheetNo + 1);
            }

            @Override
            public void onStart() throws RemoteException {
                outputText("=> onStart | sheetNo = " + curSheetNo);
            }

            @Override
            public void onError(int error, String errorMsg) throws RemoteException {
                outputRedText("=> onError: " + errorMsg);
            }
        });
    }

    private void printUnionPayWithQRCode(int curSheetNo) throws RemoteException {

        vectorPrinter.addText(null, "银联POS签购单" + curSheetNo + "\n");
        vectorPrinter.addText(null, "商户存根(MERCHANT COPY)" + "\n");
        vectorPrinter.addText(null, "----------------------------------------------\n");
        vectorPrinter.addText(null, "商户名(MERCHANT NAME):" + "\n");
        vectorPrinter.addText(null, "矢量打印测试商户" + "\n");
        vectorPrinter.addText(null, "商户编号(MERCHANT CODE):12345678901234" + "\n");
        vectorPrinter.addText(null, "终端号(TERMINAL NO):12345678" + "\n");
        vectorPrinter.addText(null, "操作员号(OPERATOR NO):01" + "\n");
        vectorPrinter.addText(null, "收单行(ACQUIRER):Testsystem" + "\n");
        vectorPrinter.addText(null, "发卡行(ISSUER):Testsystem" + "\n");
        vectorPrinter.addText(null, "卡号(CARD NO)" + "\n");
        vectorPrinter.addText(null, "622576******6691 /C" + "\n");
        vectorPrinter.addText(null, "有效期(EXP DATE):2024/08" + "\n");
        vectorPrinter.addText(null, "消费类别(TRANS TYPE)" + "\n");
        vectorPrinter.addText(null, "消费(SALE)" + "\n");
        vectorPrinter.addText(null, "批次号(BATCH NO):000003" + "\n");
        vectorPrinter.addText(null, "凭证号(VOUCHER NO):000003" + "\n");
        vectorPrinter.addText(null, "授权码(AUTH NO):000003" + "\n");
        vectorPrinter.addText(null, "参考号(REFER NO):123212000003" + "\n");
        vectorPrinter.addText(null, "日期/时间(DATE/TIME):2020/03/02 10:04:07" + "\n");
        vectorPrinter.addText(null, "交易金额(AMOUNT)" + "\n");
        vectorPrinter.addText(null, "RMB 0.01" + "\n");
        vectorPrinter.addText(null, "\n");
        vectorPrinter.addText(null, "----------------------------------------------\n");
        vectorPrinter.addText(null, "\n");
        vectorPrinter.addText(null, "备注(REFERENCE):\n");
        vectorPrinter.addText(null, "折扣活动名额剩余：10%\n");
        vectorPrinter.addText(null, "卡信息：借记卡\n");
        vectorPrinter.addText(null, "\n");

        vectorPrinter.addQrCode(null, "www.landicorp.com\n", null);
//        vectorPrinter.setTextFormat(textFormat, false);

        vectorPrinter.feedPix(50);
    }

    private void printUnionPay() throws RemoteException, IOException {
        Bundle boldFormat = new Bundle();
        Bundle textFormat = new Bundle();
        textFormat.putInt(VectorPrinterData.ALIGNMENT, Alignment.NORMAL);
        textFormat.putInt(VectorPrinterData.TEXT_SIZE, TextSize.NORMAL);
        boldFormat.putInt(VectorPrinterData.ALIGNMENT, Alignment.CENTER);
        boldFormat.putInt(VectorPrinterData.TEXT_SIZE, TextSize.LARGE);
        boldFormat.putBoolean(VectorPrinterData.BOLD, true);

        vectorPrinter.addText(boldFormat, "银联POS签购单" + "\n");
        vectorPrinter.addText(textFormat, "商户存根(MERCHANT COPY)" + "\n");
        vectorPrinter.addText(textFormat, "----------------------------------------------\n");
        vectorPrinter.addText(textFormat, "商户名(MERCHANT NAME):" + "\n");
        boldFormat.putInt(VectorPrinterData.TEXT_SIZE, TextSize.NORMAL);
        vectorPrinter.addText(boldFormat, "矢量打印测试商户" + "\n");
        vectorPrinter.addText(textFormat, "商户编号(MERCHANT CODE):12345678901234" + "\n");
        vectorPrinter.addText(textFormat, "终端号(TERMINAL NO):12345678" + "\n");
        vectorPrinter.addText(textFormat, "操作员号(OPERATOR NO):01" + "\n");
        vectorPrinter.addText(textFormat, "收单行(ACQUIRER):Testsystem" + "\n");
        vectorPrinter.addText(textFormat, "发卡行(ISSUER):Testsystem" + "\n");
        vectorPrinter.addText(textFormat, "卡号(CARD NO)" + "\n");
        boldFormat.putInt(VectorPrinterData.ALIGNMENT, TextSize.NORMAL);
        vectorPrinter.addText(boldFormat, "622576******6691 /C" + "\n");
        vectorPrinter.addText(textFormat, "有效期(EXP DATE):2024/08" + "\n");
        vectorPrinter.addText(textFormat, "消费类别(TRANS TYPE)" + "\n");
        vectorPrinter.addText(boldFormat, "消费(SALE)" + "\n");
        int[] weights = {3, 3, 3, 3};
        int[] aligns = {Alignment.NORMAL, Alignment.OPPOSITE, Alignment.NORMAL, Alignment.OPPOSITE};
        vectorPrinter.addTextColumns(null,
                new String[]{"凭证号:", "002134", "授权码", "118525"}, weights, aligns);
        vectorPrinter.addText(textFormat, "批次号(BATCH NO):000003" + "\n");
        vectorPrinter.addText(textFormat, "参考号(REFER NO):123212000003" + "\n");
        vectorPrinter.addText(textFormat, "日期/时间(DATE/TIME):2020/03/02 10:04:07" + "\n");
        vectorPrinter.addText(textFormat, "交易金额(AMOUNT)" + "\n");
        vectorPrinter.addText(boldFormat, "RMB 0.01" + "\n");
        vectorPrinter.addText(textFormat, "\n");
        vectorPrinter.addText(textFormat, "----------------------------------------------\n");
        vectorPrinter.addText(textFormat, "\n");
        vectorPrinter.addText(textFormat, "备注(REFERENCE):\n");
        vectorPrinter.addText(textFormat, "折扣活动名额剩余：10%\n");
        vectorPrinter.addText(textFormat, "卡信息：借记卡\n");
        vectorPrinter.addText(textFormat, "\n");
        textFormat.putInt(VectorPrinterData.TEXT_SIZE, TextSize.SMALL);
        vectorPrinter.addText(textFormat, "ARQC:F51234512039812\n");
        vectorPrinter.addText(textFormat, "APP LABEL:PROC CREDIT\n");
        vectorPrinter.addText(textFormat, "APM:\n\n");
        vectorPrinter.addText(textFormat, "UMPR NUM:200000000\n");
        vectorPrinter.addText(textFormat, "AIP:7c00  CVMR:\n");
        vectorPrinter.addText(textFormat, "1AD:012093281092839018293819203809123\n");
        vectorPrinter.addText(textFormat, "Term Capa:E0F0CB\n");
        vectorPrinter.addText(textFormat, "持卡人签名(CARDHOLDER SIGNATURE):\n");
        vectorPrinter.addText(textFormat, "\n");
        vectorPrinter.addText(textFormat, "\n");
        vectorPrinter.addText(textFormat, "\n");
        vectorPrinter.addText(textFormat, "----------------------------------------------\n");
        vectorPrinter.addText(textFormat, "本人确认以上交易\n");
        vectorPrinter.addText(textFormat, "同意将其计入本卡账户\n");
        vectorPrinter.addText(textFormat, "I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICE\n");
        vectorPrinter.addText(textFormat, "服务热线：1212312312\n");
        vectorPrinter.addText(textFormat, "LANDI_APOS A8\n");
        vectorPrinter.addQrCode(null, "www.landicorp.com\n", null);
//        vectorPrinter.addBarCode(Alignment.CENTER, 320, 48, "1234567123");
        vectorPrinter.feedPix(50);
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case PrinterError.ERROR_NOT_INIT:
                message = "ERROR_NOT_INIT";
                break;
            case PrinterError.ERROR_PARAM:
                message = "ERROR_PARAM";
                break;
            case PrinterError.ERROR_BMBLACK:
                message = "ERROR_BMBLACK";
                break;
            case PrinterError.ERROR_BUFOVERFLOW:
                message = "ERROR_BUFOVERFLOW";
                break;
            case PrinterError.ERROR_BUSY:
                message = "ERROR_BUSY";
                break;
            case PrinterError.ERROR_COMMERR:
                message = "ERROR_COMMERR";
                break;
            case PrinterError.ERROR_CUTPOSITIONERR:
                message = "ERROR_CUTPOSITIONERR";
                break;
            case PrinterError.ERROR_HARDERR:
                message = "ERROR_HARDERR";
                break;
            case PrinterError.ERROR_LIFTHEAD:
                message = "ERROR_LIFTHEAD";
                break;
            case PrinterError.ERROR_LOWTEMP:
                message = "ERROR_LOWTEMP";
                break;
            case PrinterError.ERROR_LOWVOL:
                message = "ERROR_LOWVOL";
                break;
            case PrinterError.ERROR_MOTORERR:
                message = "ERROR_MOTORERR";
                break;
            case PrinterError.ERROR_NOBM:
                message = "ERROR_NOBM";
                break;
            case PrinterError.ERROR_OVERHEAT:
                message = "ERROR_OVERHEAT";
                break;
            case PrinterError.ERROR_PAPERENDED:
                message = "ERROR_PAPERENDED";
                break;
            case PrinterError.ERROR_PAPERENDING:
                message = "ERROR_PAPERENDING";
                break;
            case PrinterError.ERROR_PAPERJAM:
                message = "ERROR_PAPERJAM";
                break;
            case PrinterError.ERROR_PENOFOUND:
                message = "ERROR_PENOFOUND";
                break;
            case PrinterError.ERROR_WORKON:
                message = "ERROR_WORKON";
                break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
