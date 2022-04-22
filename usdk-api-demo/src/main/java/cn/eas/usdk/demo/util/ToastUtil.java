package cn.eas.usdk.demo.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.eas.usdk.demo.R;

public class ToastUtil {

    private static Context mContext;

    private static final int LONG_DELAY = Toast.LENGTH_LONG;
    private static final int SHORT_DELAY = Toast.LENGTH_SHORT;

    private static Toast mToast;
    private static TextView tvTip;

    private ToastUtil() {
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static void showToast(String message) {
        showToast(message, SHORT_DELAY);
    }

    public static void showToastLong(String message) {
        showToast(message, LONG_DELAY);
    }

    private static void showToast(String message, int duration) {
        if (mToast != null && tvTip != null) {
            tvTip.setText(message);
            mToast.show();
        } else {
            mToast = new Toast(mContext);
            View view = LayoutInflater.from(mContext).inflate(R.layout.toast_view, null);
            tvTip = (TextView) view.findViewById(R.id.tv_message);
            tvTip.setText(message);
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 420);
            mToast.setView(view);
            mToast.show();
        }
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    public static void toUIToast(final String content) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(content);
            }
        });
    }
}
