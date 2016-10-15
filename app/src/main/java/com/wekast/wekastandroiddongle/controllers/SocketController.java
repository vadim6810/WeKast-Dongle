package com.wekast.wekastandroiddongle.controllers;


import android.util.Log;

import com.wekast.wekastandroiddongle.commands.ICommand;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketController {

    CommandController commandController;

    public SocketController(CommandController commandController) {
        this.commandController = commandController;
    }

    public boolean waitForTask() {

        int port = 8888;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter printWriter = new PrintWriter(outputStream, true);

                while (true) {
                    try {
                        String task = br.readLine();
                        if (task == null) {
                            break;
                        }
                        printWriter.println(parseTask(task));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String parseTask(String task) {
        ICommand command = null;
        try {
            command = commandController.parseCommand(task);
            return command.execute();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Good JSON Answer
            return "bad command";
        }
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
