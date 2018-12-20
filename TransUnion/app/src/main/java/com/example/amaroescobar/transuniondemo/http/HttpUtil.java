package com.example.amaroescobar.transuniondemo.http;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.message.BasicHeader;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HttpUtil {

    final protected static char[] hexArray = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'};

    private static JSONParser parser = new JSONParser();

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }

    public static byte[] readEntityAsByteArray(HttpEntity entity)
            throws IllegalStateException, IOException {
        InputStream is = entity.getContent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = -1;
        read = is.read(buffer);
        while (read > -1) {
            baos.write(buffer, 0, read);
            read = is.read(buffer);
        }
        return baos.toByteArray();
    }

    public static byte[] readEntityAsByteArray(HttpResponse response,
                                               HttpUtilCallback callback) throws IllegalStateException,
            IOException {
        HttpEntity entity = response.getEntity();
        Header[] clHeaders = response.getHeaders("Content-Length");
        Header header = (clHeaders != null && clHeaders.length > 0) ? clHeaders[0]
                : null;
        int contentLength = (header != null) ? Integer.parseInt(header
                .getValue()) : -1;
        boolean informProgress = contentLength != -1;
        int downloadedSize = 0;
        InputStream is = entity.getContent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        read = is.read(buffer);
        while (read > -1 && !callback.isCancelled()) {
            baos.write(buffer, 0, read);
            read = is.read(buffer);
            downloadedSize += read;
            if (informProgress)
                callback.progressUpdate(contentLength, downloadedSize);
        }
        is.close();
        if (!informProgress)
            callback.progressUpdate(downloadedSize, downloadedSize);
        return baos.toByteArray();
    }

    public static String readEntityAsString(HttpEntity entity)
            throws IllegalStateException, IOException {
        Header contentEncodingHeader = entity.getContentEncoding();

        byte[] buffer = readEntityAsByteArray(entity);
        String charsetName;
        if (contentEncodingHeader != null) {
            charsetName = contentEncodingHeader.getValue();
        } else {
            charsetName = "UTF-8";
        }
        String content = new String(buffer, charsetName);
        return content;
    }

    public static String readEntityAsString(HttpResponse response,
                                            HttpUtilCallback callback) throws IllegalStateException,
            IOException {
        HttpEntity entity = response.getEntity();
        Header contentEncodingHeader = entity.getContentEncoding();

        byte[] buffer = readEntityAsByteArray(response, callback);
        String charsetName;
        if (contentEncodingHeader != null) {
            charsetName = contentEncodingHeader.getValue();
        } else {
            charsetName = "UTF-8";
        }
        String content = new String(buffer, charsetName);
        return content;
    }

    public static JSONObject readEntityAsJSONObject(HttpEntity entity)
            throws IllegalStateException, IOException, ParseException {
        String content = readEntityAsString(entity);
        JSONObject jsonObject = (JSONObject) parser.parse(content);
        return jsonObject;
    }

    public static JSONObject readEntityAsJSONObject(HttpResponse response,
                                                    HttpUtilCallback callback) throws IllegalStateException,
            IOException, ParseException {
        String content = readEntityAsString(response, callback);
        JSONObject jsonObject = (JSONObject) parser.parse(content);
        return jsonObject;
    }

    public static final String secret = "i_9012hk11LZ2Ir7sZ:8856zKb052+1SlK6L14k09s0f4.Yk.~E+3c5ZW24710pR*1363;gL000d5-91S8:3klh482-7H428HMGluY+8gSP143v34gC7S84230h=**71g9G332b072-3X8;99:xVbiRn2d630Kt16D3f06U-47Ld1523l4143;1PYNO2*05306a9z6w83fifZ9b7593;KouixJU9402v94Fn56Ym804D98630a4^=998rJ~Ulh4ksCcserllGzWOmuqRFRkxEmliFedzeEnmcDuupYOHlxGqjxDWBqDrXjerdTYzLcTTwYjgGLljSstXIYKiWsRUbzsuSwGlBtDOCtQkTJFkYDiGSLVqYZUWOvTWPqYaJNveuXrHgMzLRpKszStrolMTPnlGQOoOsIdgJTEADKrQsXuDqftMNbPaybQGbHlfQKSwzcFuJHLAyfSrtsDggDxZQjjDBRNAXwKPuTPtKjKbbRouTJPZpebweVVzVCnqAHkJ";

    @SuppressLint("MissingPermission")
    public static String getAuth(Context context,
                                 HttpContext httpContext, HttpClient httpclient)
            throws IOException, NoSuchAlgorithmException {

        String url = String.format("%s%s", "http://192.168.1.113:8080/", "getToken");

        HttpPost postRequest = new HttpPost(url);

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        String userName = "";
        try {

            userName = HttpUtil.hash(android_id + tm.getDeviceId(), "SHA-256");
            //Security exception skips deviceid
//            userName = HttpUtil.hash(android_id, "SHA-256");

        } catch (NoSuchAlgorithmException e1) {
            sendReportUncaughtException(e1);
        }
        HttpResponse response;

        try {
            response = httpclient.execute(postRequest, httpContext);

            String challege = response.getFirstHeader("WWW-Authenticate")
                    .getValue().replaceAll("BASIC realm=", "");
            String password = HttpUtil.hash(secret + challege + userName, "SHA-256");

            postRequest.releaseConnection();

            return "Basic "
                    + Base64.encodeToString(
                    (userName + ":" + password).getBytes(),
                    Base64.NO_WRAP);

        } catch (Exception e) {
            sendReportUncaughtException(e);
//            ACRA.getErrorReporter().handleSilentException(e);
            throw e;
        }
    }

    public static String hash(String input, String metod)
            throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(metod);
        messageDigest.update(input.getBytes());
        byte[] digest = messageDigest.digest();
        return HttpUtil.byteArrayToHexString(digest);
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;

        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    public static void sendReportUncaughtException(Exception exception) {
//        try {
//            Answers.getInstance().logCustom(new CustomEvent("Excepcion no controlada")
//                    .putCustomAttribute("Error", exception.getMessage()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static interface HttpUtilCallback {
        public boolean isCancelled();

        public void progressUpdate(int contentLength, int downloadedSize);
    }

}
