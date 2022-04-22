package cn.eas.usdk.demo.util;

import com.usdk.apiservice.aidl.system.SystemError;

public class SystemErrorUtil {

    public static String getErrorMessage(int error) {
        String message;
        switch (error) {
            case SystemError.ERR_PARAM: message = "Error parameter"; break;
            case SystemError.METHOD_NOT_SUPPORTED: message = "Method not supported"; break;
            case SystemError.ALIAS_NOT_EXIST: message = "Certificate alias does not exist"; break;
            case SystemError.ERR_SIM_STATE: message = "Sim state error"; break;
            case SystemError.SET_PREFERRED_NETWORK_TYPE_FAIL: message = "Set preference network type fail"; break;
            case SystemError.UNKNOWN_PREFERRED_NETWORK_TYPE: message = "Unknown preference network type"; break;
            case SystemError.APN_EXIST: message = "APN exist"; break;
            case SystemError.APN_NOT_EXIST: message = "APN do not exist";break;
            default:
                message = "Unknown error"; break;
        }
        return message + String.format("[0x%02X]", error);
    }
}
