package com.wekast.wekastandroiddongle.controllers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;
import com.wekast.wekastandroiddongle.commands.Answer;
import com.wekast.wekastandroiddongle.commands.ConfigCommand;
import com.wekast.wekastandroiddongle.commands.FileCommand;
import com.wekast.wekastandroiddongle.commands.ICommand;
import com.wekast.wekastandroiddongle.commands.SlideCommand;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//import static com.wekast.wekastandroiddongle.Utils.Utils.DONGLE_SOCKET_PORT_FILE_TRANSFER;
//import static com.wekast.wekastandroiddongle.Utils.Utils.unZipPresentation;
import static com.wekast.wekastandroiddongle.Utils.Utils.APP_PATH;

public class SocketController {

    public static final String TAG = "DongleSocket";
    private CommandController commandController;
    private ServerSocket serverSocket;
    private ServerSocket serverSocketFile;

    private Activity mainActivity = FullscreenActivity.getMainActivity();
    private TextView loggerView = (TextView) mainActivity.findViewById(R.id.logger);

    public SocketController(CommandController commandController) throws IOException {
        this.commandController = commandController;
        int port = 8888;
        int portFile = 9999;
        serverSocket = new ServerSocket(port);
        serverSocketFile = new ServerSocket(portFile);
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

                    // TODO: move answer after commands
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

                    // TODO: move to command config like slide
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

                    // TODO: move to command file like slide
                    if (curCommand.equals("file")) {
                        FileCommand fileCommand = (FileCommand) icommand;
                        waitForFile(fileCommand.getFileSize());
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
        if (!serverSocketFile.isClosed())
            serverSocketFile.close();
    }

    public void waitForFile(String fileSize) {
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket sock = null;
        try {
            sock = serverSocketFile.accept();

            // receive file
            byte [] mybytearray  = new byte [Integer.valueOf(fileSize)];
            InputStream is = sock.getInputStream();
            fos = new FileOutputStream(APP_PATH + "presentation.ezs");
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead > 0)
                    current += bytesRead;
            } while(bytesRead > 0);

            bos.write(mybytearray, 0 , current);
            bos.flush();
            System.out.println("File " + APP_PATH + "presentation.ezs"
                    + " downloaded (" + current + " bytes read)");

            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            // TODO: think what to send in response
            out.println("response");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fos != null)
                    fos.close();
                if (bos != null)
                    bos.close();
                if (sock != null)
                    sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // TODO: move path to constants
        Utils.initWorkFolder();
        Utils.clearWorkDirectory();
        Utils.unZipPresentation(APP_PATH + "presentation.ezs");
    }

    private void logToTextView(final String message, final String variable) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loggerView.append(message + ": " + variable + "\n");
            }
        });
    }

}
