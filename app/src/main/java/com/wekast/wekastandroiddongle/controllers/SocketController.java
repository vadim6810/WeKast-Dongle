package com.wekast.wekastandroiddongle.controllers;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;
import com.wekast.wekastandroiddongle.commands.WelcomeAnswer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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
                WelcomeAnswer answer = new WelcomeAnswer();
                printWriter.println(answer);
                logToTextView("Sended answer", answer.toString());
                while (true) {
                    String task = br.readLine();
                    if (task == null || task.equals("")) {
                        socket.close();
                        break;
                    }
                    logToTextView("Received task", task);
                    commandController.processTask(task);
                    // TODO: if task is reconfig answer2 can't send becouse new connection established
//                    Answer answer2 = commandController.processTask(task);
//                    printWriter.println(answer2);
//                    logToTextView("Sended answer", answer2.toString());
                    if (Thread.interrupted()) {
                        return;
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
