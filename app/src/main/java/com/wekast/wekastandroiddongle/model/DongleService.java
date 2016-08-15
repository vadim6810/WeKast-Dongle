package com.wekast.wekastandroiddongle.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.activity.MainActivity;

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
    MainActivity activity;
    ServerSocket serverSocket;
    String message = "";

    private IBinder myBinder = new MyBinder();

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public DongleService() {
        Thread socketServerThread = new Thread(new SocketDongleServerThread());
        socketServerThread.setName("DongleSocketServier");
        socketServerThread.start();
        Log.d(TAG, "DongleService() - socketServerThread.getName(): " + socketServerThread.getName());
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

    public void saveAccessPointConfig(final String ssid, final String pass) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.toastShow(activity, "DongleService test message");
            }
        });

        Utils.setFieldSP(activity, "accessPointSSID", ssid);
        Utils.setFieldSP(activity, "accessPointPASS", pass);

        Intent serviceIntent = new Intent(activity, AccessPointService.class);
        activity.startService(serviceIntent);
    }

    private class SocketDongleServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                String socketServerPort = getText(R.string.socketServerPort).toString();
                serverSocket = new ServerSocket(Integer.valueOf(socketServerPort));
                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;
                    String ipAPP = socket.getInetAddress().toString();
                    message += "#" + count + " from "
                            + ipAPP + ":"
                            + socket.getPort() + "\n";

                    inputStream = socket.getInputStream();
                    int isAvailable = inputStream.available();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String task = "";
                    String input = "";
                    while((input = br.readLine())!= null) {
                        task += input;
                    }

                    JSONObject  jsonRootObject = null;
                    JSONArray jsonTask = null;
                    try {
                        jsonRootObject = new JSONObject(task);
                        jsonTask = jsonRootObject.optJSONArray("task");
                        for(int i=0; i < jsonTask.length(); i++){
                            JSONObject jsonObject = jsonTask.getJSONObject(i);
                            String curCommmand = jsonObject.getString("command").toString();
                            if (curCommmand.equals("show")) {
                                int slide = jsonObject.getInt("slide");
//                                show(slide);
                            }
                            if (curCommmand.equals("accessPointConfig")) {
                                String ssid = jsonObject.getString("ssid");
                                String pass = jsonObject.getString("pass");
                                saveAccessPointConfig(ssid, pass);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SocketDongleServerReplyThread socketServerReplyThread = new SocketDongleServerReplyThread(
                            socket, count);
                    socketServerReplyThread.setName("socketServerReplyThread");
                    socketServerReplyThread.run();

                    Log.d(TAG, "socketServerReplyThread.getName(): " + socketServerReplyThread.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "DongleService: " + e);
                    }
                }
            }
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
