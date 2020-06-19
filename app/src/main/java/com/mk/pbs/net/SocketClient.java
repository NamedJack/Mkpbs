package com.mk.pbs.net;




import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @description: socketClient
 * @author: zyj
 * @create: 2020-03-20 13:28
 **/
public class SocketClient {

    private static final char END_OF_BLOCK = '\u001c';
    private static final char START_OF_BLOCK = '\u000b';
    private static final char CARRIAGE_RETURN = 13; //"\r"
    private static final char NEW_LINE = 10;//"\n"

    private OutputStreamWriter outputStreamWriter = null;
    private BufferedReader bufferedReader = null;
    private Socket mSocket = null;

    private static String TAG = "socketClient";

    private SocketCallBack socketCallBack;

    public void setSocketCallBack(SocketCallBack socketCallBack) {
        this.socketCallBack = socketCallBack;
    }

    public void socketConnect(SocketConnectCallBack connectCallBack, boolean keepAlive) {
        Runnable runnable = () -> {
            try {
                String ip = "192.168.100.44";
                String port = "8001";
                mSocket = new Socket(ip, Integer.parseInt(port));
                if (keepAlive) {
                    mSocket.setKeepAlive(true);
                } else mSocket.setKeepAlive(false);
                outputStreamWriter = new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8");
                bufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));

                if (!mSocket.isConnected()) {
                    connectCallBack.onConnectStatue(false);
                    return;
                }
                connectCallBack.onConnectStatue(true);
                String line = "";
                StringBuilder buffer = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.endsWith(String.valueOf(END_OF_BLOCK))) {
                        if (socketCallBack != null) {
                            socketCallBack.callBackMessage(buffer.toString());
                        }
                    } else {
                        buffer.append(line).append(CARRIAGE_RETURN);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (e.toString().contains("failed to connect to")) {
                    connectCallBack.onConnectStatue(false);
                }
                if (socketCallBack != null) {
                    socketCallBack.callErrorMessage(e.toString());
                }
            } finally {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSocket = null;
            }
        };
        ThreadPoolHelper.getInstance().execute(runnable);
    }

    public void sendMessage(final String msg) {
        if (msg == null) {
            return;
        }
        Runnable runnable = () -> {
            try {
                outputStreamWriter.write(msg);
                outputStreamWriter.flush();
                Log.d("MainActivity", "write Msg" + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        ThreadPoolHelper.getInstance().execute(runnable);
    }

    public void disConnect() {
        if (mSocket != null) {
            try {
                mSocket.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                mSocket = null;
            }
        }

    }

    public interface SocketCallBack {
        void callBackMessage(String message);

        void callErrorMessage(String errorMessage);
    }

    public interface SocketConnectCallBack {
        void onConnectStatue(boolean isSuccess);
    }

}
