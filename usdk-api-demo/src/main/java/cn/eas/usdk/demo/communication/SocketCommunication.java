package cn.eas.usdk.demo.communication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import cn.eas.usdk.demo.util.BytesUtil;

public class SocketCommunication {
	public static final int DEFAULT_TIMEOUT = 20000;

	private Context context;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	public SocketCommunication(Context context) {
		this.context = context;
	}
	
	public boolean isConnected() {
		if (socket != null) {
			return socket.isConnected();
		}
		return false;
	}

	public void connect(String ip, String port) throws IllegalArgumentException, IOException, IllegalAccessException {
		if (! isNetworkAvailable(context)) {
			throw new IllegalAccessException();
		}
		if (socket == null) {
			socket = new Socket();
		}

		InetSocketAddress remoteAddr = new InetSocketAddress(ip, Integer.parseInt(port));
		socket.connect(remoteAddr, DEFAULT_TIMEOUT);
		socket.setSoTimeout(DEFAULT_TIMEOUT);
	}
	
	public void send(byte[] sendData) throws IOException {
		if (outputStream == null) {
			outputStream = socket.getOutputStream();
		}

		byte[] data = new byte[sendData.length +2];
		data[0] = (byte) ((byte)(sendData.length /256) & 0xff);
		data[1] = (byte) ((byte)(sendData.length %256) & 0xff);
		System.arraycopy(sendData, 0, data, 2, sendData.length);

		outputStream.write(data);
		outputStream.flush();
	}
	
	public byte[] receive() throws IOException {
		if (inputStream == null) {
			inputStream = socket.getInputStream();
		}

		byte[] lengthBytes = new byte[2];
		inputStream.read(lengthBytes, 0, 2);
		int length = BytesUtil.bytesToInt(lengthBytes);
		byte[] buffer = new byte[length];
		int recLength = 0 ;
		int tempLength = 0;
		while (length - recLength > 0){
			tempLength =  inputStream.read(buffer, recLength, length-recLength);
			if(tempLength <= 0){
				break;
			} else {
				recLength += tempLength;
			}
		}

		return BytesUtil.merage(lengthBytes, buffer);
	}
	
	public boolean disconnect() {
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * 检查当前网络是否可用
	 */
	private static boolean isNetworkAvailable(Context context) {
		// 获取所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
