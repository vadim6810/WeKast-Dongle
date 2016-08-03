package com.wekast.wekastandroiddongle.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.wekast.wekastandroiddongle.R;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class DongleService extends Service {
    private static final String TAG = "wekastdongle";
    ServerSocket serverSocket;
    String message = "";

    private IBinder myBinder = new MyBinder();

    public DongleService() {

        Thread socketServerThread = new Thread(new SocketDongleServerThread());
//        socketServerThread.setName("socketServerThread");
        socketServerThread.start();
        Log.d(TAG, "socketServerThread.getName(): " + socketServerThread.getName());
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "DongleService.onCreate() called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "DongleService.onBind() done");
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    public class MyBinder extends Binder {
        public DongleService getService() {
            return DongleService.this;
        }
    }

    public void ping() {
        Log.d(TAG, "DongleService.ping() called");
    }

    public void upload() {
        Log.d(TAG, "DongleService.upload() called");
    }

    public String update() {
        return "response from DongleService.update()";
    }

    public void show() {
        Log.d(TAG, "DongleService.show() called");
    }

    private class SocketDongleServerThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(Integer.valueOf(getText(R.string.socketServerPort).toString()));
                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;
                    String ipDongle = socket.getInetAddress().toString();
//                    addIpDongleToSharedPref(ipDongle);
                    message += "#" + count + " from "
                            + ipDongle + ":"
                            + socket.getPort() + "\n";

//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            activity.msg.setText(message);
//                            if (activity.isBound) {
//                                activity.testService.upload();
//                            }
//                        }
//                    });

                    SocketDongleServerReplyThread socketServerReplyThread = new SocketDongleServerReplyThread(
                            socket, count);
//                    socketServerReplyThread.setName("socketServerReplyThread");
                    socketServerReplyThread.run();
                    Log.d(TAG, "socketServerReplyThread.getName(): " + socketServerReplyThread.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        private void addIpDongleToSharedPref(String ipDongle) {
//            SharedPreferences.Editor editor = MainActivity.mySharedPreferences.edit();
//            editor.putString(APP_PREFERENCES_DONGLE_IP, ipDongle);
//            editor.apply();
//            if(MainActivity.mySharedPreferences.contains(APP_PREFERENCES_DONGLE_IP)) {
//                Log.d("sss", "" + APP_PREFERENCES_DONGLE_IP + ": " + MainActivity.mySharedPreferences.getString(APP_PREFERENCES_DONGLE_IP, ""));
//            }
//        }
    }

    private class SocketDongleServerReplyThread extends Thread {
        private Socket hostThreadSocket;
        int cnt;

        SocketDongleServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Dongle, you are #" + cnt;
            msgReply += update();
            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";

//                activity.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        activity.msg.setText(message);
//                    }
//                });

            } catch (IOException e) {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

//            activity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
////                    activity.msg.setText(message);
//                }
//            });
        }
    }
}
