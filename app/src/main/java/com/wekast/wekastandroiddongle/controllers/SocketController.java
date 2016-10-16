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
import java.net.SocketException;

public class SocketController {

    public static final String TAG = "DongleSocket";
    private CommandController commandController;
    private ServerSocket serverSocket;

    public SocketController(CommandController commandController) throws IOException {
        this.commandController = commandController;
        int port = 8888;
        serverSocket = new ServerSocket(port);
    }

    public void waitForTask() {
        try {
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
                        if (Thread.interrupted()) {
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SocketException e) {
            Log.i(TAG, "Socket closed: interrupting");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        if (!serverSocket.isClosed())
            serverSocket.close();
    }

    private String parseTask(String task) {
        ICommand command;
        try {
            command = commandController.parseCommand(task);
            return command.execute();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Good JSON Answer
            return "bad command";
        }
    }

    public boolean waitForFile() {
        return false;
    }
}
