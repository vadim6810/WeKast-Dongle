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

import static com.wekast.wekastandroiddongle.Utils.Utils.DONGLE_SOCKET_PORT_FILE_TRANSFER;
import static com.wekast.wekastandroiddongle.Utils.Utils.unZipPresentation;

public class SocketController {

    public static final String TAG = "DongleSocket";
    private CommandController commandController;
    private ServerSocket serverSocket;
    private ServerSocket serverSocketFile;

    private Activity mainActivity = FullscreenActivity.getMainActivity();
    private TextView textView = (TextView) mainActivity.findViewById(R.id.logger);

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
                        FileCommand fileCommand = (FileCommand) icommand;
                        waitForFile(fileCommand.getFileSize());
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
//            sock = new Socket(SERVER, SOCKET_PORT);
            sock = serverSocketFile.accept();

            // receive file
            byte [] mybytearray  = new byte [Integer.valueOf(fileSize)];
//            byte [] mybytearray  = new byte [6822921];              // Pass real from response
            InputStream is = sock.getInputStream();
            fos = new FileOutputStream("/sdcard/wekastdongle/presentation.ezs");
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

//            int bytesReadTotal = 0;
//            boolean readedAllBytes = false;
            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
//                if(bytesRead >= 0)
//                bytesReadTotal += bytesRead;
                if(bytesRead > 0)
                    current += bytesRead;
//                if(bytesReadTotal == Integer.valueOf(fileSize))
//                    readedAllBytes = true;
            } while(bytesRead > 0);
//            } while(!readedAllBytes);
//        } while(bytesRead > -1);

            bos.write(mybytearray, 0 , current);
            bos.flush();
            System.out.println("File " + "/sdcard/wekastdongle/presentation.ezs"
                    + " downloaded (" + current + " bytes read)");

            //RESPONSE FROM THE SERVER
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            out.println(99); //REPLY DE NUMBER 99
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
        Utils.unZipPresentation("/sdcard/wekastdongle/presentation.ezs");
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
