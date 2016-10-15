package com.wekast.wekastandroiddongle.controllers;


import android.util.Log;

import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.models.DongleWifi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketController {
    public boolean waitForTask() {

        int port = 8888;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                while (true) {
                    try {
                        String task = br.readLine();
                        String answer = parseTask(task);
                        bw.write(answer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String parseTask(String task) {
        Log.i("Task", task);
        return "Answer";
    }


    private void saveAccessPointConfig(JSONObject jsonObject) throws JSONException {
        String newSsid = jsonObject.getString("ssid");
        String newPass = jsonObject.getString("pass");

        // Save received ssid and pass in shared preferences
//        Utils.setFieldSP(activity, "ACCESS_POINT_SSID_ON_APP", newSsid);
//        Utils.setFieldSP(activity, "ACCESS_POINT_PASS_ON_APP", newPass);

    }

    public boolean waitForCommand() {
        return false;
    }

    public boolean waitForFile() {
        return false;
    }
}
