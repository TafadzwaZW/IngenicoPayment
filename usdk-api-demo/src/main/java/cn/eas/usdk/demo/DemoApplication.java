package cn.eas.usdk.demo;

import android.app.Application;
import android.os.Build;

import com.usdk.apiservice.aidl.constants.RFDeviceName;
import com.usdk.apiservice.aidl.pinpad.DeviceName;

import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.LogUtil;

public class DemoApplication extends Application {
	
	@Override
	public void onCreate() {
		LogUtil.d("-------------------onCreate-------------------");
		super.onCreate();

		initDefaultConfig();
		DeviceHelper.me().init(this);
		DeviceHelper.me().bindService();
	}

	private void initDefaultConfig() {
		if (Build.MODEL.startsWith("AECR")) {
			DemoConfig.PINPAD_DEVICE_NAME = DeviceName.COM_EPP;
			DemoConfig.RF_DEVICE_NAME = RFDeviceName.EXTERNAL;
		} else {
			DemoConfig.PINPAD_DEVICE_NAME = DeviceName.IPP;
			DemoConfig.RF_DEVICE_NAME = RFDeviceName.INNER;
		}
	}

	@Override
	public void onTerminate() {
		LogUtil.d("-------------------onTerminate-------------------");
		super.onTerminate();
		DeviceHelper.me().unbindService();
	}
}
