package cn.eas.usdk.demo.rkis;

import android.content.Context;

import java.io.IOException;
import java.net.SocketTimeoutException;

import cn.eas.usdk.demo.communication.SocketCommunication;
import cn.eas.usdk.demo.rkis.message.Response;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.util.LogUtil;

/**
 * 实现与KMS的通讯及解包
 * 通讯方式：socket 长链接
 *
 * @author linll
 */
public class KmsCommunication extends SocketCommunication {

    private String ip;
    private String port;
    private SocketListener socketListener;
    private ResponseHandler responseHandler;

    public KmsCommunication(Context context) {
        super(context);
    }

    public void setURL(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public void setSocketListener(SocketListener listener) {
        this.socketListener = listener;
    }

    public void communicate(final byte[] sendData, final boolean isLastRequest, final ResponseHandler resultListener) {
        this.responseHandler = resultListener;

        new Thread(new Runnable() {
            @Override
            public void run() {
                doCommunication(sendData, isLastRequest);
            }
        }).start();
    }

    private void doCommunication(byte[] sendData, boolean isLastRequest) {
        // Connect
        if (!isConnected()) {
            try {
                log("URL:" + ip + ":" + port);
                connect(ip, port);
                notifyConnected();
            } catch (IllegalAccessException e) {
                notifyError(SocketListener.NO_NETWORK);
                return;
            } catch (IllegalArgumentException e) {
                notifyError(SocketListener.URL_ERROR);
                return;
            } catch (IOException e) {
                notifyError(SocketListener.CONNECT_FAIL);
                return;
            }
        }

        // Send
        try {
            send(sendData);
            notifySended(sendData);
        } catch (SocketTimeoutException e) {
            notifyError(SocketListener.SEND_TIMEOUT);
            return;
        } catch (IOException e) {
            notifyError(SocketListener.SEND_FAIL);
            return;
        }

        // Receive
        try {
            int sum;
            int curSn;
            do {
                byte[] receData = receive();
                notifyReceviced(receData);

                if (Response.isInValid(receData)) {
                    notifyError(SocketListener.RECEIVE_FAIL);
                    return;
                }

                Response response = new Response(receData);
                if (! responseHandler.handle(response)) {
                    LogUtil.e("!!! handleResponse fail,please check the legality of response data." );
                    disconnect();
                    return;
                }

                sum = response.getMessageHead().getSum();
                curSn = response.getMessageHead().getCurrentSn();
                log("报文组数：" + sum + ", 当前组号：" + curSn);
            } while (curSn < sum);

        } catch (SocketTimeoutException e) {
            notifyError(SocketListener.RECEIVE_TIMEOUT);
            return;
        } catch (IOException e) {
            notifyError(SocketListener.RECEIVE_FAIL);
            return;
        }

        // Disconnect
        if (isLastRequest) {
            log("The transaction is complete, so close socket..." );
            disconnect();
        }
    }

    private void notifyError(final int error) {
        disconnect();

        if (socketListener == null) {
            return;
        }
        socketListener.onError(error, getSocketErrorInfo(error));
    }

    private void notifyConnected() {
        if (socketListener == null) {
            return;
        }
        socketListener.onConnected();
    }

    private void notifySended(final byte[] data) {
        log("send:" + BytesUtil.bytes2HexString(data));
        if (socketListener == null) {
            return;
        }
        socketListener.onSended(data);
    }

    private void notifyReceviced(final byte[] data) {
        log("receive:" + BytesUtil.bytes2HexString(data));
        if (socketListener == null) {
            return;
        }
        socketListener.onReceviced(data);
    }

    private static String getSocketErrorInfo(int errorCode) {
        switch (errorCode) {
            case SocketListener.NO_NETWORK:
                return "Network unconnected";
            case SocketListener.CONNECT_FAIL:
                return "Connect fail";
            case SocketListener.SEND_FAIL:
                return "Send fail";
            case SocketListener.RECEIVE_FAIL:
                return "Receive fail";
            case SocketListener.SEND_TIMEOUT:
                return "Send timeout";
            case SocketListener.RECEIVE_TIMEOUT:
                return "Receive timeout";
            case SocketListener.URL_ERROR:
                return "IP or PORT error";
        }
        return "Unknown";
    }

    private static void log(String message) {
        LogUtil.d("<Message>" + message);
    }

    public interface SocketListener {
        /** 当前没有网络 */
        int NO_NETWORK = 0xFF;
        int CONNECT_FAIL = 1;
        int SEND_FAIL = 3;
        int RECEIVE_FAIL = 4;
        int SEND_TIMEOUT = 5;
        int RECEIVE_TIMEOUT = 6;
        int URL_ERROR = 7;

        /** 网络已连接 */
        void onConnected();
        /**
         * 已发送
         * @param sendData 发送的数据
         */
        void onSended(byte[] sendData);
        /**
         * 已接收
         * @param receData 接收原始数据
         */
        void onReceviced(byte[] receData);
        /**
         * 网络出错
         * @param errCode 错误码
         * @param errInfo 错误信息
         */
        void onError(int errCode, String errInfo);
    }
}
