package com.wekast.wekastandroiddongle.controllers;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;
import com.wekast.wekastandroiddongle.commands.Answer;
import com.wekast.wekastandroiddongle.commands.ConfigCommand;
import com.wekast.wekastandroiddongle.commands.FileAnswer;
import com.wekast.wekastandroiddongle.commands.FileCommand;
import com.wekast.wekastandroiddongle.commands.ICommand;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static com.wekast.wekastandroiddongle.Utils.Utils.PRESENTATION_FILE_PATH;

public class SocketController {

    private static final String TAG = "SocketController";
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
        Socket socket = null;
        try {
            while (true) {
                socket = serverSocket.accept();
                InetAddress clientInetAddress = socket.getInetAddress();
//                logToTextView("Connected client from IP", clientInetAddress.toString());
                Log.e(TAG, "Connected client from IP " + clientInetAddress.toString());
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
//                    logToTextView("Received task", task);
                    Log.e(TAG, "SocketController waitForTask receivedTask=" + task);

                    // TODO: move answer after commands
                    Answer answer = commandController.processTask(task);
                    printWriter.println(answer);
//                    logToTextView("Sended answer", answer.toString());
                    Log.e(TAG, "Sended answer" + answer.toString());

                    ICommand icommand = null;
                    try {
                        icommand = commandController.parseCommand(task);
                    } catch (Exception e) {
//                        logToTextView("ERROR", "Unknown TASK");
                        Log.e(TAG, "Unknown TASK");
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
                            Log.e(TAG, "Exception message: " + e.getMessage());
                            e.printStackTrace();
                            Log.e(TAG, "Closing socket");
                            socket.close();
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
            Log.e(TAG, "Exception message: " + e.getMessage());
            e.printStackTrace();

            try {
                socket.close();
                Log.e(TAG, "Socket closed");
            } catch (IOException e1) {
                Log.e(TAG, "Exception message2: " + e1.getMessage());
                e1.printStackTrace();
            }

            // close presentation if opened
            // close 8888 socket
        }
        commandController.getService().stopPresentation();
    }

    public void close() throws IOException {
        if (!serverSocket.isClosed())
            serverSocket.close();
        if (!serverSocketFile.isClosed())
            serverSocketFile.close();
    }

    public void waitForFile(String fileSize) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket sock = null;
        try {
            sock = serverSocketFile.accept();

            // TODO: remove presentation if exist
            // Ideally this removing not needed, becaulse presentation must be removed before.
            File file = new File(PRESENTATION_FILE_PATH);
            if(file.exists()){
                file.delete();
                Log.e(TAG, "presentation.ezs DELETED");
            }

            commandController.getService().showProgressDialogReceiving();
//            logToTextView("Receiving presentation", PRESENTATION_FILE_PATH);
            Log.e(TAG, "Receiving presentation" + PRESENTATION_FILE_PATH);

            fos = new FileOutputStream(PRESENTATION_FILE_PATH);
            InputStream is = sock.getInputStream();
            int bytesToRead = Integer.valueOf(fileSize);
            int bufferLength;
            byte[] buffer = new byte[1024 * 10];
            while (true) {
                bufferLength = is.read(buffer);
                bytesToRead -= bufferLength;
                fos.write(buffer, 0, bufferLength);
                if (bytesToRead == 0)
                    break;
            }
            commandController.getService().hideProgressDialog();
//            logToTextView("Success: ", "File " + PRESENTATION_FILE_PATH + " downloaded (" + fileSize + " bytes readed)");
            Log.e(TAG, "File " + PRESENTATION_FILE_PATH + " downloaded (" + fileSize + " bytes readed)");

            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            out.println(new FileAnswer());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
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
                Log.e(TAG, "Message3" + e.getMessage());
                e.printStackTrace();
            }
        }

        // TODO: think, maybe this check is not needed
        File file = new File(PRESENTATION_FILE_PATH);
        if(file.exists()){
            commandController.getService().showProgressDialogUnzip();
//        logToTextView("Unzip started", PRESENTATION_FILE_PATH);
            Log.e(TAG, "Unzip started " + PRESENTATION_FILE_PATH);
            Utils.unZipPresentation(PRESENTATION_FILE_PATH);
//        logToTextView("Unzip finished", PRESENTATION_FILE_PATH);
            Log.e(TAG, "Unzip finished " + PRESENTATION_FILE_PATH);
            commandController.getService().hideProgressDialog();

            commandController.getService().showProgressDialogParsing();
            Utils.createWorkArray();
            commandController.getService().hideProgressDialog();
        } else {
            Log.e(TAG, "Presentation doesn't received");
        }

    }

//    private void logToTextView(final String message, final String variable) {
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                loggerView.append(message + ": " + variable + "\n");
//            }
//        });
//    }

}
