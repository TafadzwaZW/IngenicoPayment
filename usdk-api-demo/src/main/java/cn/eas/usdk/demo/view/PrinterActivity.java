package cn.eas.usdk.demo.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.printer.ASCScale;
import com.usdk.apiservice.aidl.printer.ASCSize;
import com.usdk.apiservice.aidl.printer.AlignMode;
import com.usdk.apiservice.aidl.printer.ECLevel;
import com.usdk.apiservice.aidl.printer.FactorMode;
import com.usdk.apiservice.aidl.printer.HZScale;
import com.usdk.apiservice.aidl.printer.HZSize;
import com.usdk.apiservice.aidl.printer.OnPrintListener;
import com.usdk.apiservice.aidl.printer.PrintFormat;
import com.usdk.apiservice.aidl.printer.PrinterData;
import com.usdk.apiservice.aidl.printer.PrinterError;
import com.usdk.apiservice.aidl.printer.UPrinter;

import org.angmarch.views.NiceSpinner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.FileUtil;

public class PrinterActivity extends BaseDeviceActivity {
    private static final List<Integer> sheetNumList = new LinkedList<>(Arrays.asList(1, 2, 3));

    private UPrinter printer;
    private int sheetNum;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_printer);
        setTitle("Printer Module");
        initSpinner();
    }

    protected void initDeviceInstance() {
        printer = DeviceHelper.me().getPrinter();
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
            int status = printer.getStatus();
            if (status != PrinterError.SUCCESS) {
                outputRedText(getErrorDetail(status));
                return;
            }
            outputText("The printer status is normal!");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getValidWidth(View v) {
        outputBlueText(">>> getValidWidth");
        try {
            int validWidth = printer.getValidWidth();
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
        if (curSheetNo > sheetNum) {
            return;
        }
        outputBlueText(">>> start print | sheetNo = " + curSheetNo);

        // 打印文本 print text
        printer.addText(AlignMode.CENTER, "Sheet number " + curSheetNo);

        printer.setAscScale(ASCScale.SC1x1);
        printer.setAscSize(ASCSize.DOT24x12);
        printer.addText(AlignMode.LEFT, "English:Scale1x1,Dot24x12");

        printer.setHzScale(HZScale.SC1x1);
        printer.setHzSize(HZSize.DOT24x24);
        printer.addText(AlignMode.LEFT, "chinese : scaling ratio 1x1, bitmap 24x24");

        printer.setPrintFormat(PrintFormat.FORMAT_MOREDATAPROC, PrintFormat.VALUE_MOREDATAPROC_PRNTOEND);
        printer.addText(AlignMode.LEFT, "Setting Print format for printing all the contents until the end!");
        printer.setPrintFormat(PrintFormat.FORMAT_MOREDATAPROC, PrintFormat.VALUE_MOREDATAPROC_PRNONELINE);
        printer.addText(AlignMode.LEFT, "Setting Print format for printing only one line, more than one line does not print!");

        printer.setPrintFormat(PrintFormat.FORMAT_ZEROSPECSET, PrintFormat.VALUE_ZEROSPECSET_SPECIALZERO);
        printer.addText(AlignMode.LEFT, "Zero print style: 0 ");
        printer.setPrintFormat(PrintFormat.FORMAT_ZEROSPECSET, PrintFormat.VALUE_ZEROSPECSET_DEFAULTZERO);
        printer.addText(AlignMode.LEFT, "Zero print style: 0 ");

        // 打印行混合文本 Print mix text on the same line
        List<Bundle> textBlockList = new ArrayList<Bundle>();
        Bundle block1 = new Bundle();
        block1.putString(PrinterData.TEXT, "Thank ");
        textBlockList.add(block1);
        Bundle block2 = new Bundle();
        block2.putString(PrinterData.TEXT, "you ");
        block2.putInt(PrinterData.ASC_SCALE, ASCScale.SC2x2);
        block2.putInt(PrinterData.ASC_SIZE, ASCSize.DOT24x8);
        textBlockList.add(block2);
        Bundle block3 = new Bundle();
        block3.putString(PrinterData.TEXT, "for using");
        textBlockList.add(block3);
        printer.addMixText(AlignMode.CENTER, textBlockList);

        List<Bundle> textBlockList2 = new ArrayList<Bundle>();
        block1.putInt(PrinterData.ALIGN_MODE, AlignMode.LEFT);
        block2.putInt(PrinterData.ALIGN_MODE, AlignMode.RIGHT);
        textBlockList2.add(block1);
        textBlockList2.add(block2);
        printer.addMixStyleText(textBlockList2);

        printer.addQrCode(AlignMode.CENTER, 240, ECLevel.ECLEVEL_H, "www.landicorp.com");
        printer.addBarCode(AlignMode.CENTER, 2, 48,  "1234567");

        printer.addText(AlignMode.LEFT, ">>>>>>> addBmpImage ");
        byte[] image = readAssetsFile(this, "logo.bmp");
        printer.addBmpImage(0, FactorMode.BMP1X1, image);

        printer.addText(AlignMode.LEFT, ">>>>>>> addBmpPath ");
        FileUtil.copyFileToSD(getBaseContext(), Environment.getExternalStorageDirectory().getPath(), "barcode.bmp");
        printer.addBmpPath(0, FactorMode.BMP1X1, Environment.getExternalStorageDirectory() + "/barcode.bmp");

        printer.addText(AlignMode.LEFT, ">>>>>>> gray0, 5, 10");
        for (int i = 0; i <= 10; i=i+5) {
            printer.setPrnGray(i);
            printer.addText(AlignMode.LEFT, i + "gray浓度测试");
        }
        printer.setPrnGray(3);
        printer.feedLine(5);

        printer.startPrint(new OnPrintListener.Stub() {
            @Override
            public void onFinish() throws RemoteException {
                outputText("=> onFinish | sheetNo = " + curSheetNo);
                startPrinter(curSheetNo + 1);
            }

            @Override
            public void onError(int error) throws RemoteException {
                outputRedText("=> onError: " + getErrorDetail(error));
            }
        });
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case PrinterError.ERROR_NOT_INIT: message = "ERROR_NOT_INIT"; break;
            case PrinterError.ERROR_PARAM: message = "ERROR_PARAM"; break;
            case PrinterError.ERROR_BMBLACK: message = "ERROR_BMBLACK"; break;
            case PrinterError.ERROR_BUFOVERFLOW: message = "ERROR_BUFOVERFLOW"; break;
            case PrinterError.ERROR_BUSY: message = "ERROR_BUSY"; break;
            case PrinterError.ERROR_COMMERR: message = "ERROR_COMMERR"; break;
            case PrinterError.ERROR_CUTPOSITIONERR: message = "ERROR_CUTPOSITIONERR"; break;
            case PrinterError.ERROR_HARDERR: message = "ERROR_HARDERR"; break;
            case PrinterError.ERROR_LIFTHEAD: message = "ERROR_LIFTHEAD"; break;
            case PrinterError.ERROR_LOWTEMP: message = "ERROR_LOWTEMP"; break;
            case PrinterError.ERROR_LOWVOL: message = "ERROR_LOWVOL"; break;
            case PrinterError.ERROR_MOTORERR: message = "ERROR_MOTORERR"; break;
            case PrinterError.ERROR_NOBM: message = "ERROR_NOBM"; break;
            case PrinterError.ERROR_OVERHEAT: message = "ERROR_OVERHEAT"; break;
            case PrinterError.ERROR_PAPERENDED: message = "ERROR_PAPERENDED"; break;
            case PrinterError.ERROR_PAPERENDING: message = "ERROR_PAPERENDING"; break;
            case PrinterError.ERROR_PAPERJAM: message = "ERROR_PAPERJAM"; break;
            case PrinterError.ERROR_PENOFOUND: message = "ERROR_PENOFOUND"; break;
            case PrinterError.ERROR_WORKON: message = "ERROR_WORKON"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }

    private static byte[] readAssetsFile(Context ctx, String fileName) {
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

}
