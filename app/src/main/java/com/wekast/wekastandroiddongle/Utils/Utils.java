package com.wekast.wekastandroiddongle.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.wekast.wekastandroiddongle.activity.FullscreenActivity;
import com.wekast.wekastandroiddongle.entities.Slide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {

    public static final String SERVICE_API_URL = "http://78.153.150.254";
    public static final String SERVICE_API_URL_LIST = SERVICE_API_URL + "/list";
    public static final String SERVICE_API_URL_REGISTER = SERVICE_API_URL + "/register";
    public static final String SERVICE_API_URL_DOWNLOAD = SERVICE_API_URL + "/download/";
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_ERROR = -1;
    public static final String SHAREDPREFERNCE = "WeKastPreference";
//    public static final String DEFAULT_PATH_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static final String DEFAULT_PATH_DIRECTORY = "/sdcard/";
    public static final String WORK_DIRECTORY = "WekastDongle/";
    public static final String APP_PATH = "/sdcard/wekastdongle/";
    public static final String CASH_DIRECTORY = "cash/";
//    public static final String SLIDES_DIRECTORY = slides
    public static final File DIRECTORY = new File(DEFAULT_PATH_DIRECTORY + WORK_DIRECTORY);
    public static final String CASH_ABSOLUTE_PATH = DEFAULT_PATH_DIRECTORY + WORK_DIRECTORY + CASH_DIRECTORY;
    public static final String PREVIEW_DIRECTORY = "Preview/";
    public static final String PREVIEW_ABSOLUTE_PATH = DEFAULT_PATH_DIRECTORY + WORK_DIRECTORY + PREVIEW_DIRECTORY;
    public static final String DONGLE_SOCKET_PORT = "8888";
    public static final String DONGLE_SOCKET_PORT_FILE_TRANSFER = "9999";
    public static final String PRESENTATION_FILE = "presentation.ezs";
    public static final String PRESENTATION_FILE_PATH = APP_PATH + PRESENTATION_FILE;
    public static final String infoXML = CASH_ABSOLUTE_PATH + "/info.xml";

    // SharedPreferences keys
    // WIFI_STATE_BEFORE_LAUNCH_APP             // save state of wifi module
    // ACCESS_POINT_STATE_BEFORE_LAUNCH_APP     // save state of access point
    // ACCESS_POINT_SSID_ON_APP         // loaded value when first time connected application to dongle
    // ACCESS_POINT_PASS_ON_APP         // loaded value when first time connected application to dongle

    public static void initWorkFolder() {
        ArrayList<String> workFolder = new ArrayList<>();
        workFolder.add(DEFAULT_PATH_DIRECTORY + WORK_DIRECTORY);
        workFolder.add(CASH_ABSOLUTE_PATH);
        workFolder.add(PREVIEW_ABSOLUTE_PATH);
        workFolder.add(CASH_ABSOLUTE_PATH + "animations");
        workFolder.add(CASH_ABSOLUTE_PATH + "audio");
        workFolder.add(CASH_ABSOLUTE_PATH + "slides");
        workFolder.add(CASH_ABSOLUTE_PATH + "video");

        createFolder(workFolder);
    }

    private static void createFolder(ArrayList<String> workFolder) {
        for (String str: workFolder) {
            File file = new File(str);
            if (!file.isDirectory()) {
                file.mkdir();
                Log.d("Create directory", str);
            }
        }
    }

    public static void clearWorkDirectory(String absolutePath){
        File[] clearWorkDirectory = (new File(absolutePath)).listFiles();
        if(clearWorkDirectory != null){
            for (File tmp : clearWorkDirectory) {
                clearDirectory(tmp);
            }
        }
    }

//    public static void clearWorkDirectory(String absolutePath, String file){
//        File[] clearWorkDirectory = (new File(absolutePath)).listFiles();
//        if(clearWorkDirectory != null){
//            for (File tmp : clearWorkDirectory) {
//                String res = tmp.getName().toString();
//                if(tmp.getName().toString() == file)
//                    clearDirectory(tmp);
//            }
//        }
//    }

    public static void clearDirectory(File file) {
        if (!file.exists())
            return;
        if(file.isDirectory()){
            for (File tmp2: file.listFiles()) {
                clearDirectory(tmp2);
            }
        } else file.delete();
    }

    public static boolean getContainsSP(Context context, String field) {
        SharedPreferences settingsActivity = context.getSharedPreferences(SHAREDPREFERNCE, context.MODE_PRIVATE);
        return  settingsActivity.contains(field);
    }
    public static String getFieldSP(Context context, String field) {
        SharedPreferences settingsActivity = context.getSharedPreferences(SHAREDPREFERNCE, context.MODE_PRIVATE);
        String login = settingsActivity.getString(field, "");
        return  login;
    }

//    public static SharedPreferences getSharedPreferences(Context context) {
//        return context.getSharedPreferences(SHAREDPREFERNCE, context.MODE_PRIVATE);
//    }

    public static void setFieldSP(Context context, String field1, String field2) {
        SharedPreferences settingsActivity = context.getSharedPreferences(Utils.SHAREDPREFERNCE, context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settingsActivity.edit();
        prefEditor.putString(field1, field2);
        prefEditor.apply();
    }

    public static boolean removeFromSharedPreferences(Context context, String field) {
        SharedPreferences settingsActivity = context.getSharedPreferences(SHAREDPREFERNCE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settingsActivity.edit();
        editor.remove(field);
        editor.apply();
        return true;
    }

    public static void toastShow(Context context, String s) {
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static boolean unZipPresentation(String pathFile) {
        boolean res = false;
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(pathFile));
            ZipEntry zipEntry = null;
            File targetDirectory = new File(CASH_ABSOLUTE_PATH);
            while ((zipEntry = zin.getNextEntry()) != null) {
                String filePath = targetDirectory + File.separator + zipEntry.getName();
                if (!zipEntry.isDirectory()) {
                    extractFile(zin, filePath, (int) zipEntry.getSize());
                }
                zin.closeEntry();
            }
            res = true;
        } catch (Exception e) {
            Log.d("UnzipError = ", e.toString());
        }
        return res;
    }

    private static void extractFile(ZipInputStream zipIn, String filePath, int size) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[size];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private static int slideNumber;
    private static String comments;
    private static String filePath;
    public static ArrayList<Slide> slidesList = new ArrayList<>();
    public static ArrayList<Integer> chID = new ArrayList<>();
    private static Map<Integer, String> mediaType = new HashMap<>();

    public static void createWorkArray() {
        try {
            XmlPullParser parser = prepareXpp();
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (parser.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("slide")) {
                            slideNumber = Integer.parseInt(parser.getAttributeValue(0));
                            filePath = CASH_ABSOLUTE_PATH + parser.getAttributeValue(1);
                            if (parser.getAttributeCount() > 2)
                                comments = parser.getAttributeValue(2);
                        }
                        if (parser.getName().equals("animation")) {
                            chID.add(Integer.parseInt(parser.getAttributeValue(0)));
                            mediaType.put(Integer.parseInt(parser.getAttributeValue(0)), "animation");
                        }
                        if (parser.getName().equals("media")) {
                            chID.add(Integer.parseInt(parser.getAttributeValue(0)));
                            for (int i = 0; i < parser.getAttributeCount(); i++ ) {
                                String attributeName = parser.getAttributeName(i);
                                if (attributeName.equals("type")) {
                                    mediaType.put(Integer.parseInt(parser.getAttributeValue(0)), parser.getAttributeValue(i));
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("slide")) {
                            Slide slide = new Slide("", slideNumber, comments, filePath, chID, mediaType);
                            Log.d("XML parser: ", slide.toString());
                            slidesList.add(slide);
                            chID = new ArrayList<>();
                        }
                        break;
                }
                parser.next();
            }
        } catch (Throwable t) {
            Log.e("Error XML parser: ", t.toString());
            toastShow(FullscreenActivity.getMainActivity(), "Broken XML from EZS.");
        }
    }

    private static XmlPullParser prepareXpp() throws XmlPullParserException {
        // получаем фабрику
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // включаем поддержку namespace (по умолчанию выключена)
        factory.setNamespaceAware(true);
        // создаем парсер
        XmlPullParser xpp = factory.newPullParser();
        // даем парсеру на вход Reader
        try {
            xpp.setInput(new FileInputStream(infoXML), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return xpp;
    }

}
