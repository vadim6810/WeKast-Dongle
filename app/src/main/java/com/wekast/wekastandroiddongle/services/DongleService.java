package com.wekast.wekastandroiddongle.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Explanations
 * For keeping activity created class DongleServiceBinder
 *
 * Created by YEHUDA on 8/1/2016.
 */
public class DongleService extends Service {

    private static final String TAG = "wekastdongle";
    private static JSONObject jsonResponse;
    private Loger log = Loger.getInstance();
    MainActivity activity;
    ServerSocket serverSocket;

    // code for keeping activity in service
    private final IBinder mBinder = new DongleServiceBinder();

    public class DongleServiceBinder extends Binder {
        public DongleService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DongleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "DongleService.onBind()");
        log.createLogger("DongleService.onBind()");
        return mBinder;
    }
    // end code for keeping activity in service

//    private IBinder myBinder = new MyBinder();

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public DongleService() {
        jsonResponse = Utils.createJsonResponse("jsonResponse", "ok");
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
//        return "jsonResponse from DongleService.update()";
//    }

//    public void show(int slideNumber) {
//        Log.d(TAG, "DongleService.show(" + slideNumber + ") called");
//    }



    private class SocketDongleServerThread extends Thread {

        @Override
        public void run() {
            InputStream inputStream = null;
//            OutputStream outputStream = null;
//            String socketServerPort = getText(R.string.socketServerPort).toString();
            String socketServerPort = "8888";
//            int socketServerPort = Integer.valueOf(getText(R.string.socketServerPort).toString());
            Log.d(TAG, "DongleService.SocketDongleServerThread.run() socketServerPort: " + socketServerPort);
            log.createLogger("DongleService.SocketDongleServerThread.run() socketServerPort: " + socketServerPort);
            try {
                serverSocket = new ServerSocket(Integer.valueOf(socketServerPort));

                while (true) {
                    Socket socket = serverSocket.accept();

                    // TODO: comment after debug
                    printMessageOnUi("Client connected");

                    Log.d(TAG, "DongleService.SocketDongleServerThread.run(): socket is connected? " + socket.isConnected());
                    log.createLogger("DongleService.SocketDongleServerThread.run() socket is connected? " + socket.isConnected());

                    while (true) {
                        try {
                            inputStream = socket.getInputStream();
                        } catch (IOException e) {
                            break;
                        }

//                    int isAvailable = inputStream.available();
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        String task = null;
                        task = br.readLine();

                        if (task == null)
                            break;

                        // TODO: comment after debug
                        printMessageOnUi("RECEIVED:" + task);

                        JSONObject jsonRootObject = null;
                        JSONArray jsonTask = null;
                        String curDevice = "";
                        try {
                            jsonRootObject = new JSONObject(task);
                            curDevice = jsonRootObject.getString("device");
                            if (curDevice.equals("android")) {
                                jsonTask = jsonRootObject.optJSONArray("task");
                                for (int i = 0; i < jsonTask.length(); i++) {
                                    JSONObject jsonObject = jsonTask.getJSONObject(i);
                                    String curCommmand = jsonObject.getString("command").toString();
                                    if (curCommmand.equals("show")) {
                                        int slide = jsonObject.getInt("slide");
                                        // show(slide);
                                    }
                                    if (curCommmand.equals("accessPointConfig")) {
                                        //sendResponse(socket);
                                        saveAccessPointConfig(jsonObject);
                                    }
                                    if (curCommmand.equals("uploadFile")) {
                                        //sendResponse(socket);
                                        task = br.readLine();
                                        printMessageOnUi("RECEIVED:" + task);
                                        int j = 0;
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

                        sendResponse(socket);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "DongleService.SocketDongleServerThread.run(): ERROR: " + e.getMessage());
                log.createLogger("DongleService.SocketDongleServerThread.run() ERROR: " + e.getMessage());
            }
            finally {

                    try {
                        if (inputStream != null) inputStream.close();
//                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "DongleService: " + e);
                    }

            }
        }
    } // SocketDongleServerThread


    private void sendResponse(Socket socket) {
        SocketDongleServerReplyThread socketServerReplyThread = new SocketDongleServerReplyThread(
                socket, jsonResponse);
        socketServerReplyThread.setName("socketServerReplyThread");
        socketServerReplyThread.run();
        Log.d(TAG, "DongleService.SocketDongleServerThread.run() end of: " + socketServerReplyThread.getName());
        log.createLogger("DongleService.SocketDongleServerThread.run() end of: " + socketServerReplyThread.getName());
    }

    private class SocketDongleServerReplyThread extends Thread {
        private Socket hostThreadSocket;
        JSONObject response;

        SocketDongleServerReplyThread(Socket socket, JSONObject response) {
            hostThreadSocket = socket;
            this.response = response;
        }

        @Override
        public void run() {
            OutputStream outputStream = null;
            try {
                outputStream = hostThreadSocket.getOutputStream();
//                PrintStream printStream = new PrintStream(outputStream);
//                printStream.print(response);
//                printStream.flush();
//                printStream.close();
//                outputStream.close();
//                PrintWriter printWriter = new PrintWriter(outputStream, true);
//                printWriter.print(response);
//                printWriter.close();
//                outputStream.close();

                PrintWriter printWriter = new PrintWriter(outputStream, true);
                printWriter.println(response);

                Utils.setFieldSP(activity, "DONGLE_SERVICE_RESPONSE_SENDED", "1");

                // TODO: comment after debug
                printMessageOnUi("SENDED:" + response);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "DongleService.SocketDongleServerReplyThread.run() IOException " + e.getMessage());
                log.createLogger("DongleService.SocketDongleServerReplyThread.run() IOException " + e.getMessage());
            }
            finally {
                try {
                    if (outputStream != null) outputStream.close();
//                    if (hostThreadSocket != null) {
//                        hostThreadSocket.close();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveAccessPointConfig(JSONObject jsonObject) throws JSONException {
        String newSsid = jsonObject.getString("ssid");
        String newPass = jsonObject.getString("pass");
//        final String newSsid = jsonObject.getString("ssid");
//        final String newPass = jsonObject.getString("pass");

        // Save received ssid and pass in shared preferences
        Utils.setFieldSP(activity, "ACCESS_POINT_SSID_ON_APP", newSsid);
        Utils.setFieldSP(activity, "ACCESS_POINT_PASS_ON_APP", newPass);
//        Utils.setFieldSP(activity, "ACCESS_POINT_SSID_ON_APP", jsonObject.getString("ssid"));
//        Utils.setFieldSP(activity, "ACCESS_POINT_PASS_ON_APP", jsonObject.getString("pass"));


//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Utils.toastShowBottom(activity, "Received wifi config from application. Connecting to " + newSsid + " " + newPass);
//            }
//        });

        waitWhileDongleServiceResponseSended();

        // Connect to Access Point of application
        printMessageOnUi("Connecting to " + newSsid + " " + newPass);
        DongleWifi dongleWifi = new DongleWifi(activity);
        dongleWifi.connectToAccessPoint();
    }

    private void waitWhileDongleServiceResponseSended() {
        String isResponseSended = "";
        boolean sended = false;
        while(!sended) {
            isResponseSended = Utils.getFieldSP(activity, "DONGLE_SERVICE_RESPONSE_SENDED");
            if (isResponseSended.equals("1")) {
                sended = true;
                Utils.setFieldSP(activity, "DONGLE_SERVICE_RESPONSE_SENDED", "0");
            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d(TAG, "DongleService.waitWhileDongleServiceResponseSended() " + e.getMessage());
                }
            }
        }

    }

    public void printMessageOnUi(String message) {
        final String curMessage = message;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.toastShowBottom(activity, curMessage);
            }
        });
    }

}
