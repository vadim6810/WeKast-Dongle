package com.wekast.wekastandroiddongle.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Meztiros on 01.08.2016.
 */
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

    public static final String DONGLE_SOCKET_PORT = "8888";
    public static final String DONGLE_SOCKET_PORT_FILE_TRANSFER = "9999";

    // SharedPreferences keys
    // WIFI_STATE_BEFORE_LAUNCH_APP             // save state of wifi module
    // ACCESS_POINT_STATE_BEFORE_LAUNCH_APP     // save state of access point
    // ACCESS_POINT_SSID_ON_APP         // loaded value when first time connected application to dongle
    // ACCESS_POINT_PASS_ON_APP         // loaded value when first time connected application to dongle

    public static void initWorkFolder() {
        ArrayList<String> workFolder = new ArrayList<>();
        workFolder.add(DEFAULT_PATH_DIRECTORY + WORK_DIRECTORY);
        workFolder.add(CASH_ABSOLUTE_PATH);
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

    public static void clearWorkDirectory(){
        File[] clearWorkDirectory = (new File(DEFAULT_PATH_DIRECTORY + WORK_DIRECTORY + CASH_DIRECTORY)).listFiles();
        for (File tmp : clearWorkDirectory) {
            clearDirectory(tmp);
        }
    }

    private static void clearDirectory(File file) {
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

    public static void setFieldSP(Context context, String field1, String field2) {
        SharedPreferences settingsActivity = context.getSharedPreferences(Utils.SHAREDPREFERNCE, context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settingsActivity.edit();
        prefEditor.putString(field1, field2);
        prefEditor.apply();
    }

    public static void toastShow(Context context, String s) {
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void toastShowBottom(Context context, String s) {
//        Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void writeFile(byte[] content, String FILENAME, String LOG_TAG) {
        Log.d(LOG_TAG, "writeToFile");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(Utils.DIRECTORY, FILENAME));
            fos.write(content);
            fos.flush();
            fos.close();
            Log.d(LOG_TAG, "finish write!!!");
        } catch (IOException e) {
            Log.d(LOG_TAG, "error write!!!");
        }
    }

    public static HashMap<String, String> parseJSONArrayMap(Context context, String answer) {
        HashMap<String, String> mapList = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(answer);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject index = jsonArray.getJSONObject(i);
                mapList.put(index.getString("id"), index.getString("name"));
            }
        } catch (JSONException e) {
            toastShow(context, e.toString());
        }
        return mapList;
    }

    public static ArrayList<String> parseJSONArray(Context context, String answer) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(answer);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject index = jsonArray.getJSONObject(i);
                arrayList.add(index.getString("name"));
            }
        } catch (JSONException e) {
            toastShow(context, e.toString());
        }
        return arrayList;
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

    public static JSONObject createJsonResponse(String task, String status) {
        // TODO: create rundom ssid and pass
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonTask = new JSONArray();
        JSONObject jsonCommand = new JSONObject();
        try {
            jsonCommand.put("command", task);
            jsonCommand.put("status", status);
            jsonTask.put(jsonCommand);
            jsonObject.put("device", "dongle");
            jsonObject.put("task", jsonTask);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
