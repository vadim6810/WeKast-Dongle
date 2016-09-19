package com.wekast.wekastandroiddongle.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.wekast.wekastandroiddongle.Utils.Loger;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.activity.MainActivity;
import com.wekast.wekastandroiddongle.models.DongleWifi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class DongleService extends Service {

    private static final String TAG = "wekastdongle";
    private Loger log = Loger.getInstance();
    MainActivity activity;
    ServerSocket serverSocket;
    String message = "";

//    private IBinder myBinder = new MyBinder();

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public DongleService() {
        Thread socketServerThread = new Thread(new SocketDongleServerThread());
        socketServerThread.setName("DongleSocketServer");
        socketServerThread.start();
        Log.d(TAG, "DongleService.DongleService() Starting: " + socketServerThread.getName());
        log.createLogger("DongleService.DongleService() Starting: " + socketServerThread.getName());
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "DongleService.onCreate()");
        log.createLogger("DongleService.onCreate()");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DongleService.onDestroy()");
        log.createLogger("DongleService.onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "DongleService.onBind()");
        log.createLogger("DongleService.onBind()");
//        return myBinder;
        return null;
    }

//    @Override
//    public boolean onUnbind(Intent intent) {
//        return false;
//    }
//
//    public class MyBinder extends Binder {
//        public DongleService getService() {
//            return DongleService.this;
//        }
//    }

//    public void ping() {
//        Log.d(TAG, "DongleService.ping() called");
//    }

//    public void upload() {
//        Log.d(TAG, "DongleService.upload() called");
//    }

//    public String update() {
//        return "response from DongleService.update()";
//    }

//    public void show(int slideNumber) {
//        Log.d(TAG, "DongleService.show(" + slideNumber + ") called");
//    }

    public void saveAccessPointConfig(JSONObject jsonObject) throws JSONException {

        final String newSsid = jsonObject.getString("ssid");
        final String newPass = jsonObject.getString("pass");


        // Save received ssid and pass in shared preferences
        Utils.setFieldSP(activity, "ACCESS_POINT_SSID_ON_APP", jsonObject.getString("ssid"));
        Utils.setFieldSP(activity, "ACCESS_POINT_PASS_ON_APP", jsonObject.getString("pass"));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.toastShowBottom(activity, "Received wifi config from application. Connecting to " + newSsid + " " + newPass);
            }
        });

        // Connect to Access Point of application
        DongleWifi dongleWifi = new DongleWifi(activity);
        dongleWifi.connectToAccessPoint();
    }

    private class SocketDongleServerThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            InputStream inputStream = null;
            OutputStream outputStream = null;
//            String socketServerPort = getText(R.string.socketServerPort).toString();
            String socketServerPort = "8888";
//            int socketServerPort = Integer.valueOf(getText(R.string.socketServerPort).toString());
            Log.d(TAG, "DongleService.SocketDongleServerThread.run() socketServerPort: " + socketServerPort);
            log.createLogger("DongleService.SocketDongleServerThread.run() socketServerPort: " + socketServerPort);
            try {
                serverSocket = new ServerSocket(Integer.valueOf(socketServerPort));
//                serverSocket = new ServerSocket(socketServerPort);
                Socket socket = serverSocket.accept();
                while (true) {
                    count++;
                    String ipAPP = socket.getInetAddress().toString();
                    message += "#" + count + " from "
                            + ipAPP + ":"
                            + socket.getPort() + "\n";

                    inputStream = socket.getInputStream();
//                    int isAvailable = inputStream.available();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String task = "";
                    String input = "";
                    while((input = br.readLine())!= null) {
                        task += input;
                    }

                    JSONObject  jsonRootObject = null;
                    JSONArray jsonTask = null;
                    String curDevice = "";
                    try {
                        jsonRootObject = new JSONObject(task);
                        curDevice = jsonRootObject.getString("device");
                        if (curDevice.equals("android")) {
                            jsonTask = jsonRootObject.optJSONArray("task");
                            for(int i=0; i < jsonTask.length(); i++){
                                JSONObject jsonObject = jsonTask.getJSONObject(i);
                                String curCommmand = jsonObject.getString("command").toString();
                                if (curCommmand.equals("show")) {
                                    int slide = jsonObject.getInt("slide");
                                    // show(slide);
                                }
                                if (curCommmand.equals("accessPointConfig")) {
                                    saveAccessPointConfig(jsonObject);
                                }
                            }
                        }
                        if (curDevice.equals("ios")) {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "DongleService.SocketDongleServerThread.run(): ERROR: " + e.getMessage());
                        log.createLogger("DongleService.SocketDongleServerThread.run() ERROR: " + e.getMessage());
                    }

                    SocketDongleServerReplyThread socketServerReplyThread = new SocketDongleServerReplyThread(
                            socket, count);
                    socketServerReplyThread.setName("socketServerReplyThread");
                    socketServerReplyThread.run();

                    Log.d(TAG, "DongleService.SocketDongleServerThread.run() end of: " + socketServerReplyThread.getName());
                    log.createLogger("DongleService.SocketDongleServerThread.run() end of: " + socketServerReplyThread.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "DongleService.SocketDongleServerThread.run(): ERROR: " + e.getMessage());
                log.createLogger("DongleService.SocketDongleServerThread.run() ERROR: " + e.getMessage());
            }
//            finally {
//                if (serverSocket != null) {
//                    try {
//                        serverSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.d(TAG, "DongleService: " + e);
//                    }
//                }
//            }
        }
    } // SocketDongleServerThread

    private class SocketDongleServerReplyThread extends Thread {
        private Socket hostThreadSocket;
        int cnt;

        SocketDongleServerReplyThread(Socket socket, int cnt) {
            hostThreadSocket = socket;
            this.cnt = cnt;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Response from Dongle" + cnt;
            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
        }
    }

}
