package com.wekast.wekastandroiddongle.controllers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;
import com.wekast.wekastandroiddongle.commands.Answer;
import com.wekast.wekastandroiddongle.commands.ConfigCommand;
import com.wekast.wekastandroiddongle.commands.ICommand;
import com.wekast.wekastandroiddongle.commands.SlideCommand;

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

                while (true) {
                    String task = br.readLine();
                    if (task == null || task.equals("")) {
                        socket.close();
                        break;
                    }
                    logToTextView("Received task", task);

                    Answer answer = commandController.processTask(task);
                    printWriter.println(answer);
                    logToTextView("Sended answer", answer.toString());

                    ICommand icommand = null;
                    try {
                        icommand = commandController.parseCommand(task);
                    } catch (Exception e) {
                        logToTextView("ERROR", "Unknown TASK");
                        break;
                    }

                    // TODO: switch
                    String curCommand = icommand.getCommand();
                    if (curCommand.equals("config")) {
                        ConfigCommand configCommand = (ConfigCommand) icommand;
                        String ssid = configCommand.getSsid();
                        String password = configCommand.getPassword();
                        WifiController wifiController = commandController.getService().getWifiController();

                        wifiController.saveWifiConfig(ssid, password);
                        try {
                            wifiController.startConnection();
                        } catch (Exception e) {
                            Log.i(TAG, "Socket closed: interrupting");
                        }
//                        wifiController.changeState(WifiController.WifiState.WIFI_STATE_CONNECT);
                    }

                    if (curCommand.equals("file")) {
                    }

                    if (curCommand.equals("slide")) {
                        SlideCommand slideCommand = (SlideCommand) icommand;
                        String curSlide = slideCommand.getSlide();
                        showSlideOnDongle(curSlide);
                    }

                    if (Thread.interrupted()) {
                        return;
                    }
                }
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

    private void showSlideOnDongle(final String curSlide) {
        // TODO: getPath from variables or get path by device also from variables
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrameLayout logoFrame = (FrameLayout) mainActivity.findViewById(R.id.logoFrame);
                logoFrame.setBackgroundColor(Color.rgb(0, 0, 0));

                // TODO: define device (dongle or test phone)
                Bitmap bmp = null;
//                File f = new File(Environment.getExternalStorageDirectory() + "/sdcard/wekastdongle/cash/slides/");
//                if(f.isDirectory()) {
                    bmp = BitmapFactory.decodeFile("/sdcard/wekastdongle/cash/slides/" + curSlide + ".jpg");
//                }
//                f = new File(Environment.getExternalStorageDirectory() + "/storage/sdcard0/WeKast/cash/slides/");
//                if(f.isDirectory()) {
//                    bmp = BitmapFactory.decodeFile("/storage/sdcard0/WeKast/cash/slides/" + curSlide + ".jpg");
//                }
                ImageView img = (ImageView) mainActivity.findViewById(R.id.slideIMG);
                img.setImageBitmap(bmp);
            }
        });
    }

}
