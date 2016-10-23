package com.wekast.wekastandroiddongle.controllers;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;
import com.wekast.wekastandroiddongle.commands.Answer;
import com.wekast.wekastandroiddongle.commands.ConfigCommand;
import com.wekast.wekastandroiddongle.commands.ICommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketController {

    public static final String TAG = "DongleSocket";
    private CommandController commandController;
    private ServerSocket serverSocket;

    private Activity mainActivity = FullscreenActivity.getMainActivity();
    private TextView textView = (TextView) mainActivity.findViewById(R.id.logger);

    public SocketController(CommandController commandController) throws IOException {
        this.commandController = commandController;
        int port = 8888;
        serverSocket = new ServerSocket(port);
    }

    public void waitForTask() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                InetAddress clientInetAddress = socket.getInetAddress();
                logToTextView("Connected client from IP", clientInetAddress.toString());
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter printWriter = new PrintWriter(outputStream, true);
//                WelcomeAnswer answer = new WelcomeAnswer();
//                printWriter.println(answer);
//                logToTextView("Sended answer", answer.toString());
//                while (true) {
                    String task = br.readLine();
                    if (task == null || task.equals("")) {
                        socket.close();
                        break;
                    }
                    logToTextView("Received task", task);

                    Answer answer = commandController.processTask(task);
                    printWriter.println(answer);
                    logToTextView("Sended answer", answer.toString());

                    ICommand icommand = commandController.parseCommand(task);
                    String curCommand = icommand.getCommand();
                    if (curCommand.equals("config")) {
                        ConfigCommand configCommand = (ConfigCommand) icommand;
                        String ssid = configCommand.getSsid();
                        String password = configCommand.getPassword();
                        WifiController wifiController = commandController.getService().getWifiController();
                        wifiController.saveWifiConfig(ssid, password);
                        wifiController.startConnection();
                        wifiController.changeState(WifiController.WifiState.WIFI_STATE_CONNECT);
                    }
                    int jj = 0;
                    if (curCommand.equals("file")) {
                        jj = 1;
                    }

                    if (Thread.interrupted()) {
                        return;
                    }

                    socket.close();
//                }
            }
        } catch (Exception e) {
            Log.i(TAG, "Socket closed: interrupting");
        }
    }

    public void close() throws IOException {
        if (!serverSocket.isClosed())
            serverSocket.close();
    }

    public boolean waitForFile() {
        return false;
    }

    private void logToTextView(final String message, final String variable) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(message + ": " + variable + "\n");
            }
        });
    }

}
