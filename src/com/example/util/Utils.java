/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.AndroidHttpClient;
import com.example.session.Constants;
import com.example.session.Session;
import com.mappn.gfan.R;
import com.gfan.sdk.statistics.Collector;


/**
 * Common Utils for the application
 * 
 * @author andrew.wang
 * @date 2010-9-19
 * @since Version 0.4.0
 */
public class Utils {

    public static boolean sDebug;
    public static String sLogTag;

    private static final String TAG = "Utils";

    // UTF-8 encoding
    private static final String ENCODING_UTF8 = "UTF-8";

    private static WeakReference<Calendar> calendar;

    /**
     * <p>
     * Get UTF8 bytes from a string
     * </p>
     * 
     * @param string
     *            String
     * @return UTF8 byte array, or null if failed to get UTF8 byte array
     */
    public static byte[] getUTF8Bytes(String string) {
        if (string == null)
            return new byte[0];

        try {
            return string.getBytes(ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            /*
             * If system doesn't support UTF-8, use another way
             */
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeUTF(string);
                byte[] jdata = bos.toByteArray();
                bos.close();
                dos.close();
                byte[] buff = new byte[jdata.length - 2];
                System.arraycopy(jdata, 2, buff, 0, buff.length);
                return buff;
            } catch (IOException ex) {
                return new byte[0];
            }
        }
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     * 
     * @param b
     *            byte array
     * @return string in utf-8 encoding, or empty if the byte array is not encoded with UTF-8
     */
    public static String getUTF8String(byte[] b) {
        if (b == null)
            return "";
        return getUTF8String(b, 0, b.length);
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     */
    public static String getUTF8String(byte[] b, int start, int length) {
        if (b == null) {
            return "";
        } else {
            try {
                return new String(b, start, length, ENCODING_UTF8);
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
    }

    /**
     * <p>
     * Parse int value from string
     * </p>
     * 
     * @param value
     *            string
     * @return int value
     */
    public static int getInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }

        try {
            return Integer.parseInt(value.trim(), 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * <p>
     * Parse float value from string
     * </p>
     * 
     * @param value
     *            string
     * @return float value
     */
    public static float getFloat(String value) {
        if (value == null)
            return 0f;

        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    /**
     * <p>
     * Parse long value from string
     * </p>
     * 
     * @param value
     *            string
     * @return long value
     */
    public static long getLong(String value) {
        if (value == null)
            return 0L;

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static void V(String msg) {
        if (sDebug) {
            Log.v(sLogTag, msg);
        }
    }

    public static void V(String msg, Throwable e) {
        if (sDebug) {
            Log.v(sLogTag, msg, e);
        }
    }

    public static void D(String msg) {
        if (sDebug) {
            Log.d(sLogTag, msg);
        }
    }

    public static void D(String msg, Throwable e) {
        if (sDebug) {
            Log.d(sLogTag, msg, e);
        }
    }

    public static void I(String msg) {
        if (sDebug) {
            Log.i(sLogTag, msg);
        }
    }

    public static void I(String msg, Throwable e) {
        if (sDebug) {
            Log.i(sLogTag, msg, e);
        }
    }

    public static void W(String msg) {
        if (sDebug) {
            Log.w(sLogTag, msg);
        }
    }

    public static void W(String msg, Throwable e) {
        if (sDebug) {
            Log.w(sLogTag, msg, e);
        }
    }

    public static void E(String msg) {
        if (sDebug) {
            Log.e(sLogTag, msg);
        }
    }

    public static void E(String msg, Throwable e) {
        if (sDebug) {
            Log.e(sLogTag, msg, e);
        }
    }

    public static String formatDate(long time) {
        if (calendar == null || calendar.get() == null) {
            calendar = new WeakReference<Calendar>(Calendar.getInstance());
        }
        Calendar target = calendar.get();
        target.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(target.getTime());
    }

    public static String getTodayDate() {
        if (calendar == null || calendar.get() == null) {
            calendar = new WeakReference<Calendar>(Calendar.getInstance());
        }
        Calendar today = calendar.get();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(today.getTime());
    }

    /**
     * Returns whether the network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0, length = info.length; i < length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the network is roaming
     */
    public static boolean isNetworkRoaming(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            // Log.w(Constants.TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            } else {
            }
        }
        return false;
    }

    // /**
    // * Get the decrypted HTTP response body<br>
    // *
    // * @return if response is empty or some error occured when decrypt process
    // * will return EMPTY string
    // */
    // public static String getDecryptedResponseBody(HttpEntity entity) {
    // byte[] response = SecurityUtil.decryptHttpEntity(entity);
    // return Utils.getUTF8String(response);
    // }
    //
    // private static GoogleAnalyticsTracker mTracker;
    //
    // public static void trackEvent(Context context, String... paras) {
    // if (paras == null || paras.length != 3) {
    // return;
    // }
    // if (mTracker == null) {
    // mTracker = GoogleAnalyticsTracker.getInstance();
    // mTracker.setProductVersion("GfanMobile",
    // String.valueOf(Session.get(context).getVersionCode()));
    // mTracker.start(com.mappn.gfan.Constants.GOOGLE_UID, context);
    // }
    // mTracker.trackEvent(paras[0], paras[0] + "_" + paras[1] + paras[2], "", 0);
    // Collector.setAppClickCount(String.format(com.mappn.gfan.Constants.STATISTICS_FORMAT,
    // paras[0], paras[1], paras[2]));
    // }

    // /**
    // * Show toast information
    // *
    // * @param context
    // * application context
    // * @param text
    // * the information which you want to show
    // * @return show toast dialog
    // */
    // public static void makeEventToast(Context context, String text, boolean isLongToast) {
    //
    // Toast toast = null;
    // if (isLongToast) {
    // toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
    // } else {
    // toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    // }
    // View v = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
    // TextView textView = (TextView) v.findViewById(R.id.text);
    // textView.setText(text);
    // toast.setView(v);
    // toast.show();
    // }


    public static String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(timeInMillis));
    }

    // /**
    // * 从商家版Zip包中获取加密文件输入�?
    // */
    // public static DecryptStream getDecryptStream(File file, String entryName) {
    // try {
    // ZipFile zipPackage = new ZipFile(file);
    // ZipEntry entry = zipPackage.getEntry(entryName);
    // if (entry == null) {
    // return null;
    // }
    // return new DecryptStream(zipPackage.getInputStream(entry));
    // } catch (IOException e) {
    // }
    // return null;
    // }

    // /**
    // * 从商家版Zip包中获取普�?文件输入�?
    // */
    // public static InputStream getNormalStream(File file, String entryName) {
    // try {
    // ZipFile zipPackage = new ZipFile(file);
    // ZipEntry entry = zipPackage.getEntry(entryName);
    // if (entry == null) {
    // return null;
    // }
    // return zipPackage.getInputStream(entry);
    // } catch (IOException e) {
    // }
    // return null;
    // }

    // /**
    // * 获取商家版加密后的APK文件，并拷贝到SD卡上�?sdcard/gfan/apk�?
    // * @param root 商家版应用包文件
    // * @param entryName APK文件
    // * @return 拷贝后的文件
    // */
    // public static File getEncryptApk(File root, String entryName) {
    //
    // InputStream in = null;
    // FileOutputStream fos = null;
    // File outputFile = null;
    // try {
    // outputFile = new File(Environment.getExternalStorageDirectory() + "/gfan/apk",
    // entryName);
    // fos = new FileOutputStream(outputFile);
    // in = getDecryptStream(root, entryName);
    // if (in == null) {
    // return null;
    // }
    // copyFile(in, fos);
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return outputFile;
    // }

    // /**
    // * 获取商家版APK对应的ICON文件，并拷贝到SD卡上�?sdcard/gfan/.cache�?
    // * @param root 商家版应用包文件
    // * @param entryName ICON文件
    // * @return 拷贝后的文件
    // */
    // public static File getApkIcon(File root, String entryName) {
    // InputStream in = null;
    // FileOutputStream fos = null;
    // File outputFile = null;
    // try {
    // outputFile = new File(Environment.getExternalStorageDirectory() + "/gfan/.cache",
    // entryName);
    // fos = new FileOutputStream(outputFile);
    // in = getNormalStream(root, entryName);
    // if (in == null) {
    // return null;
    // }
    // copyFile(in, fos);
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return outputFile;
    // }

    /**
     * 文件拷贝工具�?
     * 
     * @param src
     *            源文�?
     * @param dst
     *            目标文件
     * @throws IOException
     */
    public static void copyFile(InputStream in, FileOutputStream dst) throws IOException {
        byte[] buffer = new byte[8192];
        int len = 0;
        while ((len = in.read(buffer)) > 0) {
            dst.write(buffer, 0, len);
        }
        in.close();
        dst.close();
    }

    /**
     * 解析HTTP String Entity
     * 
     * @param response
     *            HTTP Response
     * @return 市场API返回的消�?String)
     */
    public static String getStringResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            return entity == null ? null : EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            D("getStringResponse meet ParseException", e);
        } catch (IOException e) {
            D("getStringResponse meet IOException", e);
        }
        return null;
    }

    /**
     * 解析HTTP InputStream Entity
     * 
     * @param response
     *            HTTP Response
     * @return 市场API返回的消�?InputStream)
     */
    public static InputStream getInputStreamResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            if(entity == null) return null;
            return AndroidHttpClient.getUngzippedContent(entity);
        } catch (IllegalStateException e) {
            D("getInputStreamResponse meet IllegalStateException", e);
        } catch (IOException e) {
            D("getInputStreamResponse meet IOException", e);
        }
        return null;
    }

    /**
     * 界面切换动画
     * 
     * @return
     */
    public static LayoutAnimationController getLayoutAnimation() {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(100);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        return controller;
    }

    /**
     * 创建Tab中的只包含TextView的View
     */
/*    public static View createTabView(Context context, String text) {
        TextView view = (TextView) LayoutInflater.from(context).inflate(R.layout.common_tab_view,
                null);
        view.setText(text);
        return view;
    }*/
    
    /**
     * 创建Search Tab中的只包含TextView的View
     */
/*    public static View createSearchTabView(Context context, String text) {
        TextView view = (TextView) LayoutInflater.from(context).inflate(R.layout.common_tab_view,
                null);
        view.setBackgroundResource(R.drawable.search_tab_selector);
        view.setTextAppearance(context, R.string.search_tab_text);
        view.setText(text);
        return view;
    }
*/
    /**
     * 获取用户安装的应用列�?
     */
    public static List<PackageInfo> getInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        final String ourPackageName =  Session.get(context).getPackageName();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        for (PackageInfo info : packages) {
            // 只返回非系统级应�?
            if (((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                    && !ourPackageName.equals(info.packageName)) {
                apps.add(info);
            }
        }
        
        return apps;
    }

    /**
     * 获取用户本机�?��应用程序
     */
    public static List<PackageInfo> getAllInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        ArrayList<String> installed = new ArrayList<String>();
        for (PackageInfo info : packages) {
            apps.add(info);
            installed.add(info.packageName);
        }
        Session.get(context).setInstalledApps(installed);
        return apps;
    }

    /**
     * 比较两个文件的签名是否一�?
     */
    public static boolean compareFileWithSignature(String path1, String path2) {

        long start = System.currentTimeMillis();
        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            return false;
        }

        String signature1 = getFileSignatureMd5(path1);
        String signature2 = getFileSignatureMd5(path2);

        V("compareFileWithSignature total time is " + (System.currentTimeMillis() - start));
        if (!TextUtils.isEmpty(signature1) && signature1.equals(signature2)) {
            return true;
        }
        return false;
    }

    /**
     * 获取应用签名MD5
     */
    public static String getFileSignatureMd5(String targetFile) {

        try {
            JarFile jarFile = new JarFile(targetFile);
            // 取RSA公钥
            JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");

            if (jarEntry != null) {
                InputStream is = jarFile.getInputStream(jarEntry);
                byte[] buffer = new byte[8192];
                while (is.read(buffer) > 0) {
                    // do nothing
                }
                is.close();
                Certificate[] certs = jarEntry == null ? null : jarEntry.getCertificates();
                if (certs != null && certs.length > 0) {
                    String rsaPublicKey = String.valueOf(certs[0].getPublicKey());
                    return getMD5(rsaPublicKey);
                }
            }
        } catch (IOException e) {
            W("occur IOException when get file signature", e);
        }
        return "";
    }

    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Convert byte array to Hex string
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Check whether the SD card is readable
     */
    public static boolean isSdcardReadable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
                || Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the SD card is writable
     */
    public static boolean isSdcardWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Show toast information
     * 
     * @param context
     *            application context
     * @param text
     *            the information which you want to show
     * @return show toast dialog
     */
   public static void makeEventToast(Context context, String text, boolean isLongToast) {

        Toast toast = null;
        if (isLongToast) {
            toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        View v = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
        TextView textView = (TextView) v.findViewById(R.id.text);
        textView.setText(text);
        toast.setView(v);
        toast.show();
    }
    
    /**
     * 解析二维码地�?
     */
    public static HashMap<String, String> parserUri(Uri uri) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        String paras[] = uri.getQuery().split("&");
        for (String s : paras) {
            if (s.indexOf("=") != -1) {
                String[] item = s.split("=");
                parameters.put(item[0], item[1]);
            } else {
                return null;
            }
        }
        return parameters;
    }
    
    /**
     * �?��默认Proxy
     */
    public static HttpHost detectProxy(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null 
                && ni.isAvailable() 
                && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int port = android.net.Proxy.getDefaultPort();
            if (proxyHost != null) {
                return new HttpHost(proxyHost, port, "http");
            }
        }
        return null;
    }
    
    /**
     * Android 安装应用
     * 
     * @param context Application Context
     * @param filePath APK文件路径
     */
    public static void installApk(Context context, File file) {
        if (file.exists()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            ((ContextWrapper) context).startActivity(i);
        } else {
            //makeEventToast(context, context.getString(R.string.install_fail_file_not_exist), false);
        }
    }
    
    /**
     * 卸载应用
     * 
     * @param context
     *            应用上下�?
     * @param pkgName
     *            包名
     */
    public static void uninstallApk(Context context, String pkgName) {
        Uri packageURI = Uri.parse("package:" + pkgName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }
    
    /**
     * 获取机锋市场下载的应用文�?
     */
   public static ArrayList<HashMap<String, Object>> getLocalApks(Context context) {
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File root = new File(Environment.getExternalStorageDirectory(), Constants.ROOT_DIR);
            ArrayList<HashMap<String, Object>> apks = new ArrayList<HashMap<String, Object>>();
            getApkList(context, root, apks);
            return apks;
        }
        return null;
    }
    
    /*
     * 遍历Gfan APK文件
     */
    private static void getApkList(Context context, File root,
            ArrayList<HashMap<String, Object>> apkList) {

        int index = 0;
        File marketRoot = new File(root, "market");
        boolean hasMarket = false;
        if (marketRoot.exists()) {
            File[] children = marketRoot.listFiles();
            if (children.length > 0) {

                for (File child : children) {
                    if (!child.isDirectory()) {
                        if (child.getName().endsWith(".apk")) {
                            HashMap<String, Object> item = getApkInfo(context, child);
                            if (item != null) {
                                index++;
                                apkList.add(item);
                            }
                        }
                    }
                }
                if (index > 0) {
                    hasMarket = true;
                    HashMap<String, Object> group = new HashMap<String, Object>();
                    group.put(
                            Constants.KEY_PRODUCT_NAME,
                            context.getString(R.string.apk_title_market) + "("
                                    + marketRoot.getAbsolutePath() + ")");
                    group.put(Constants.KEY_PLACEHOLDER, true);
                    apkList.add(0, group);
                }
            }
        }

        File bbsRoot = new File(root, "bbs");
        boolean hasBbs = false;
        if (bbsRoot.exists()) {
            File[] children = bbsRoot.listFiles();
            if (children.length > 0) {

                int startPos = index;
                for (File child : children) {
                    if (!child.isDirectory()) {
                        if (child.getName().endsWith(".apk")) {
                            HashMap<String, Object> item = getApkInfo(context, child);
                            if (item != null) {
                                index++;
                                apkList.add(item);
                            }
                        }
                    }
                }

                if (index > startPos) {
                    hasBbs = true;
                    HashMap<String, Object> group = new HashMap<String, Object>();
                    group.put(Constants.KEY_PRODUCT_NAME, context.getString(R.string.apk_title_bbs)
                            + "(" + bbsRoot.getAbsolutePath() + ")");
                    group.put(Constants.KEY_PLACEHOLDER, true);
                    if (hasMarket) {
                        apkList.add(startPos + 1, group);
                    } else {
                        apkList.add(startPos, group);
                    }
                }
            }
        }
        
        File cloudRoot = new File(root, "cloud");
        if (cloudRoot.exists()) {
            File[] children = cloudRoot.listFiles();
            if (children.length > 0) {

                int startPos = index;
                for (File child : children) {
                    if (!child.isDirectory()) {
                        if (child.getName().endsWith(".apk")) {
                            HashMap<String, Object> item = getApkInfo(context, child);
                            if (item != null) {
                                index++;
                                apkList.add(item);
                            }
                        }
                    }
                }

                if (index > startPos) {
                    HashMap<String, Object> group = new HashMap<String, Object>();
                    group.put(Constants.KEY_PRODUCT_NAME, context.getString(R.string.apk_title_cloud)
                            + "(" + cloudRoot.getAbsolutePath() + ")");
                    group.put(Constants.KEY_PLACEHOLDER, true);
                    if (hasMarket && hasBbs) {
                        apkList.add(startPos + 2, group);
                    } else if (hasMarket | hasBbs) {
                        apkList.add(startPos + 1, group);
                    } else {
                        apkList.add(startPos, group);
                    }
                }
            }
        }
    }
    
    /*
     * 获取APK信息
     */
    public static HashMap<String, Object> getApkInfo(Context context, File file) {
        PackageManager pm = context.getPackageManager();
        String filePath = file.getAbsolutePath();
        PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info == null) {
            return null;
        }
        ApplicationInfo appInfo = info.applicationInfo;
        info.applicationInfo.sourceDir = filePath;
        info.applicationInfo.publicSourceDir = filePath;
        Drawable icon = pm.getApplicationIcon(appInfo);
        
        HashMap<String, Object> apk = new HashMap<String, Object>();
        apk.put(Constants.KEY_PRODUCT_ICON, icon);
        apk.put(Constants.KEY_PRODUCT_NAME, file.getName());
        apk.put(Constants.KEY_PRODUCT_INFO, filePath);
        apk.put(Constants.KEY_PRODUCT_DESCRIPTION, file.getAbsolutePath());
        apk.put(Constants.KEY_PRODUCT_PAY_TYPE, Constants.PAY_TYPE_FREE);
        apk.put(Constants.KEY_PLACEHOLDER, false);
        return apk;
    }
    
    /**
     * 删除安装�?
     */
    public static boolean deleteFile(String file) {
        File realFile = new File(file);
        return realFile.delete();
    }
    
    /**
     * 计算下载进度字符�?
     */
    public static String calculateRemainBytes(Context ctx, float current, float total) {

        float remain = total - current;
        remain = remain > 0 ? remain : 0;
        String text = "";
        final String megaBytes = "M";
        final String kiloBytes = "K";
        final String bytes = "B";
        if (remain > 1000000) {
            text = ctx.getString(R.string.download_remain_bytes,
                    String.format("%.02f", (remain / 1000000)), megaBytes);
        } else if (remain > 1000) {
            text = ctx.getString(R.string.download_remain_bytes,
                    String.format("%.02f", (remain / 1000)), kiloBytes);
        } else {
            text = ctx.getString(R.string.download_remain_bytes, (int) remain, bytes);
        }
        return text;
    }
    
    /**
     * �?��是否应该进行更新
     */
    public static boolean isNeedCheckUpgrade(Context context) {
        long currentTime = System.currentTimeMillis();
        long lastCheckTime = Session.get(context).getUpdataCheckTime();
        if (currentTime - lastCheckTime > 86400000) {
             // we only check update every 24 hours
            return true;
        }
        return false;
    }
    
    /**
     * 统计工具方法
     */
    public static void trackEvent(Context context, String... paras) {
        if (paras == null || paras.length != 2) {
            return;
        }
        String content = String.format(Constants.STATISTICS_FORMAT,
                paras[0], paras[1]);
//        makeEventToast(context, content, false);
        Collector.setAppClickCount(content);
        Session.get(context).getTracker().trackEvent(paras[0], content, "", 0);
    }
    
    public static String submitLogs() {
        Process mLogcatProc = null;
        BufferedReader reader = null;
        try {
            mLogcatProc = Runtime.getRuntime().exec(
                    new String[] { "logcat", "-d" , "机锋市场:v"});

            reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

            String line;
            final StringBuilder log = new StringBuilder();
            String separator = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }
            return log.toString();

            // do whatever you want with the log. I'd recommend using Intents to
            // create an email
        } catch (IOException e) {
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                }
        }
        return "";
    }
    
    public static void clearCache(Context context) {
        File file = Environment.getDownloadCacheDirectory();
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
        file = context.getCacheDir();
        files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }
}