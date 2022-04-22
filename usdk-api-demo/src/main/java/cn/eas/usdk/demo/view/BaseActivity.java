package cn.eas.usdk.demo.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.angmarch.views.NiceSpinner;

import java.util.List;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.DialogUtil;

public abstract class BaseActivity extends Activity {

    protected Handler uiHandler = new Handler(Looper.getMainLooper());

    protected <T extends View> T bindViewById(int id) {
        return (T) findViewById(id);
    }

    protected void setTitle(String title) {
        ((TextView)bindViewById(R.id.tvTitle)).setText(title);
    }

    public void setSettingClickListener(View.OnClickListener listener) {
        ImageView ivSetting = bindViewById(R.id.ivSetting);
        ivSetting.setVisibility(View.VISIBLE);
        ivSetting.setOnClickListener(listener);
    }

    protected void setSpinnerDefaultValue(NiceSpinner spinner, List<SpinnerItem> valueList, int value) {
        for (int i = 0; i < valueList.size(); i++) {
            if (value == valueList.get(i).getValue()) {
                spinner.setSelectedIndex(i);
                break;
            }
        }
    }

    protected void setSpinnerDefaultValue(NiceSpinner spinner, List<String> valueList, String value) {
        for (int i = 0; i < valueList.size(); i++) {
            if (TextUtils.equals(value, valueList.get(i))) {
                spinner.setSelectedIndex(i);
                break;
            }
        }
    }

    /** Fold up the soft keyboard */
    protected void hideSoftInput() {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showException(final String message) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                DialogUtil.showMessage(BaseActivity.this, getString(R.string.exception_prompt), message, new DialogUtil.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                    }
                });
            }
        });
    }

    public void finishWithInfo(String info) {
        DialogUtil.showMessage(this, getString(R.string.error_prompt), info, new DialogUtil.OnConfirmListener() {
            @Override
            public void onConfirm() {
                finish();
            }
        });
    }

    public AlertDialog showProgress(String message) {
        return DialogUtil.showProgress(this, "Prompt", message);
    }

    protected void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
        overridePendingTransition(R.anim.dync_in_from_right, R.anim.dync_out_to_left);
    }
}