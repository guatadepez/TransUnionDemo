package com.example.amaroescobar.transuniondemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import com.example.amaroescobar.transuniondemo.http.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ch.boye.httpclientandroidlib.conn.util.InetAddressUtils;

public class Utils {

    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";
    static final String HEXDIGITS = "0123456789ABCDEF";

    /**
     * Retorna la descripción del dedo a partir del idDedo en ISO
     * <p>
     * Nota:
     * La descripcion es en formato Digital Persona Legacy
     *
     * @param idFingerIso
     * @return
     */
    public static String traslateFingerIdToFingerDescription(int idFingerIso) {
        switch (idFingerIso) {
            case 1:
                return "PULGAR DERECHO";
            case 2:
                return "INDICE DERECHO";
            case 3:
                return "MEDIO DERECHO";
            case 4:
                return "ANULAR DERECHO";
            case 5:
                return "MEÑIQUE DERECHO";
            case 6:
                return "PULGAR IZQUIERDO";
            case 7:
                return "INDICE IZQUIERDO";
            case 8:
                return "MEDIO IZQUIERDO";
            case 9:
                return "ANULAR IZQUIERDO";
            case 10:
                return "MEÑIQUE IZQUIERDO";
            default:
                return "DEDO INDETERMINADO";
        }
    }

    /**
     * manda al log un arreglo de bytes grande transformandolo a base64
     * e imprimiendo linea por linea
     *
     * @param tag   - log tag
     * @param value - arreglo de bytes a mandar
     */
    public static void dump(String tag, byte[] value) {
        String encoded = Base64.encodeToString(value, Base64.DEFAULT);
        String lines[] = encoded.split("[\r\n\t ]+");
        long ts = System.currentTimeMillis();
        Log.d(tag, "DUMP start: " + ts);
        for (String line : lines) {
            Log.d(tag, line);
        }
        Log.d(tag, "DUMP end:" + ts);
    }

    public static String coalesce(String... alternatives) {
        for (String alternative : alternatives) {
            if (!TextUtils.isEmpty(alternative)) {
                return alternative;
            }
        }
        return null;
    }

    public static byte[] inputStreamtoByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public static String join(String joiner, Object... parts) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < parts.length; ++i) {
            builder.append(parts[i]);
            if (i < parts.length - 1) {
                builder.append(joiner);
            }
        }

        return builder.toString();
    }

    public static String convertStreamToString(InputStream is) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("ISO-8859-1")));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } finally {
            is.close();
        }
        return sb.toString();
    }

    /**
     * Recibe un arreglo con los nombres de los archivos contenidos dentro del zip y una lista
     * con el contenido propiamente tal.
     *
     * @param fileName
     * @param input
     * @return
     * @throws Exception
     */
    public static byte[] zipBytes(String[] fileName, List<byte[]> input) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (int i = 0; i < input.size(); i++) {

            ZipEntry entry = new ZipEntry(fileName[i]);
            byte[] content = input.get(i);
            entry.setSize(content.length);
            zos.putNextEntry(entry);
            zos.write(content);
            zos.closeEntry();
        }

        zos.close();
        return baos.toByteArray();
    }

    public static <E> Iterator<List<E>> partition(List<E> list, final int batchSize) {

        assert (batchSize > 0);
        assert (list != null);
        assert (list.size() + batchSize <= Integer.MAX_VALUE);

        int idx = 0;

        List<List<E>> result = new ArrayList<List<E>>();

        for (idx = 0; idx + batchSize <= list.size(); idx += batchSize) {
            result.add(list.subList(idx, idx + batchSize));
        }
        if (idx < list.size()) {
            result.add(list.subList(idx, list.size()));
        }

        return result.iterator();
    }

    public static List<String> listOfString(String... strings) {
        List<String> result = new ArrayList<String>(strings.length);
        for (int n = 0; n < strings.length; n++) {
            result.set(n, strings[n]);
        }
        return result;
    }

    public static byte[] invert(byte[] data) {
        byte[] resp = new byte[data.length];
        int end = data.length - 1;
        for (int i = 0; i < data.length; i++) {
            resp[end] = data[i];
            end--;
        }

        return resp;

    }

    public static String saveByteToFile(byte[] buffer, String filename) {
        File dataFile = new File(filename);
        if (dataFile.exists()) {
            dataFile.delete();
        }
        try {
            // SimpleBitmap bm = renderCroppedGreyscaleBitmap(data, width, height);
            FileOutputStream out = new FileOutputStream(dataFile);
            out.write(buffer);
            out.close();
            return dataFile.getPath();

        } catch (java.io.IOException e) {
            HttpUtil.sendReportUncaughtException(e);
            return null;
        }

    }

    public static byte[] readByteFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }

        try {
            byte[] data = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(data);
            in.close();

//            file.delete();

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean isOutdated(Application application) {

        int currentVersion = 0;
        try {
            currentVersion = application.getPackageManager().getPackageInfo(
                    application.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            HttpUtil.sendReportUncaughtException(e);
//            ACRA.getErrorReporter().handleSilentException(e);
        }

        return currentVersion < 1000;
    }

    public static boolean isNonPrintableData(byte[] bytes) {
        for (byte b : bytes) {
            int c = uint8(b);
            if (!(((c >= 32) && (c <= 255)) ||
                    (c == 9) || (c == 10) ||
                    (c == 11) || (c == 12) ||
                    (c == 13))) return true;
        }
        return false;
    }

    public static String bytesAsHex(byte[] bytes) {
        if (bytes == null)
            return "<NULL>";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(HEXDIGITS.charAt((b & 0xf0) >> 4));
            sb.append(HEXDIGITS.charAt(b & 0x0f));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int n = 0; n < hex.length(); n += 2) {
            baos.write(Integer.parseInt(hex.substring(n, n + 2), 16));
        }
        return baos.toByteArray();
    }

    public static byte[] concatArrays(byte[]... parts) {
        int totalBytes = 0;
        for (byte[] part : parts) {
            totalBytes += part.length;
        }
        ByteBuffer buf = ByteBuffer.allocate(totalBytes);
        for (byte[] part : parts) {
            buf.put(part);
        }
        return buf.array();
    }

    public static byte[] copySlice(byte[] data, int pos, int count) {
        return Arrays.copyOfRange(data, pos, pos + count);
    }

    public static int uint16(byte hb, byte lb) {
        return (uint8(hb) << 8) | uint8(lb);
    }

    public static int uint16(int hb, int lb) {
        return (uint8(hb) << 8) | uint8(lb);
    }

    public static short uint8(long b) {
        return (short) (0xff & b);
    }

    public static short uint8(int b) {
        return (short) (0xff & b);
    }

    public static short uint8(byte b) {
        return (short) (0xff & b);
    }

    public static String asHex(byte[] bytes) {
        final String hexChars = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(hexChars.charAt((b & 0xf0) >> 4));
            sb.append(hexChars.charAt(b & 0x0f));
        }
        return sb.toString();
    }

    public static byte[] fromHex(String hex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int n = 0; n < hex.length(); n += 2) {
            baos.write(Integer.parseInt(hex.substring(n, n + 2), 16));
        }
        return baos.toByteArray();
    }

    @SuppressLint("DefaultLocale")
    public static String normalizeRUT(String rut) {
        String tmp = rut.replaceAll("[^0-9kK]+", "").toUpperCase();
        int leftLen = tmp.length() - 1;
        return tmp.substring(0, leftLen) + "-" + tmp.substring(leftLen, leftLen + 1);
    }

    static public byte[] randomBytes(int count) {
        byte[] result = new byte[count];
        new Random().nextBytes(result);
        return result;
    }

    static public byte[] sha(byte[]... blocks) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            for (byte[] block : blocks) {
                digest.update(block);
            }
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static public byte[] orBytes(byte[] a, byte[] b) {
        byte[] result = new byte[Math.max(a.length, b.length)];
        for (int n = 0; n < result.length; n++) {
            byte ba = 0, bb = 0;
            if (n < a.length) {
                ba = a[n];
            }
            if (n < b.length) {
                bb = b[n];
            }
            result[n] = (byte) (ba | bb);
        }
        return result;
    }

    static public byte[] xorBytes(byte[] a, byte[] b) {
        byte[] result = new byte[Math.max(a.length, b.length)];
        for (int n = 0; n < result.length; n++) {
            byte ba = 0, bb = 0;
            if (n < a.length) {
                ba = a[n];
            }
            if (n < b.length) {
                bb = b[n];
            }
            result[n] = (byte) (ba ^ bb);
        }
        return result;
    }

    public static byte[] andBytes(byte[] a, byte[] b) {
        byte[] result = new byte[Math.max(a.length, b.length)];
        for (int n = 0; n < result.length; n++) {
            byte ba = 0, bb = 0;
            if (n < a.length) {
                ba = a[n];
            }
            if (n < b.length) {
                bb = b[n];
            }
            result[n] = (byte) (ba & bb);
        }
        return result;
    }

    public static String getCallingClassName(int aboveOffset) {
        Throwable t = new Throwable();
        StackTraceElement[] trace = t.getStackTrace();
        return trace[1 + aboveOffset].getClassName();
    }

    public static byte[] getContent(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[16384];
        while (true) {
            int readBytes = stream.read(buf);
            if (readBytes > 0)
                baos.write(buf, 0, readBytes);
            else
                break;
        }
        return baos.toByteArray();
    }

    public static byte[] longToBytes(long curTime) {
        // TODO Auto-generated method stub
        return null;
    }

    public static String stackTrace(Exception e) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream err = new PrintStream(baos, true, "UTF-8");
            e.printStackTrace(err);
            return baos.toString("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return "error while building stacktrace";
        }
    }

    public static void drawFatalError(String msg, final Activity act) {
        HttpUtil.sendReportUncaughtException(new Exception(msg));
//        ACRA.getErrorReporter().handleSilentException(new Exception(msg));
        new AlertDialog.Builder(act)
                .setTitle("Error")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                act.finish();
                            }
                        }).show();
    }

    public static void drawError(String msg, final Activity act) {
        HttpUtil.sendReportUncaughtException(new Exception(msg));
//        ACRA.getErrorReporter().handleSilentException(new Exception(msg));
        new AlertDialog.Builder(act)
                .setTitle("Error")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //
                            }
                        }).show();
    }

    public static boolean validaRutDV(int rut, char dv) {
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return dv == (char) (s != 0 ? s + 47 : 75);
    }

    // private byte[] readFile(String rutaInput) {
    // try {
    // File file = new File(rutaInput);
    //
    // if (file.exists()) {
    // FileInputStream streamIn = new FileInputStream(file);
    // ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    //
    // int nRead;
    // byte[] data = new byte[1024];
    //
    // while ((nRead = streamIn.read(data, 0, data.length)) != -1) {
    // buffer.write(data, 0, nRead);
    // }
    //
    // buffer.flush();
    // streamIn.close();
    // return buffer.toByteArray();
    // } else {
    // Utils.drawFatalError("El archivo no existe en la memoria", this);
    // return null;
    // }
    // } catch (IOException e) {
    // Utils.drawFatalError("Error al leer el archivo", this);
    // return null;
    // }
    // }

    public static boolean isConnectingToInternet(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx
                .getSystemService(ctx.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

//    public static byte[] reverse(byte[] array) {
//        byte[] validData = Arrays.copyOf(array, array.length);
//        for (int i = 0; i < validData.length / 2; i++) {
//            byte temp = validData[i];
//            validData[i] = validData[validData.length - i - 1];
//            validData[validData.length - i - 1] = temp;
//        }
//        return validData;
//    }

    public static int simpleScore(int score) {
        int puntaje = score;
        if (puntaje <= 1) {
            puntaje = 10;
        } else {
            puntaje = (int) Math.ceil(10 - Math.log10(puntaje));
        }
        return puntaje;
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public static byte[] readByteFile(String filename, boolean delete) {


        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }

        try {
            byte[] data = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(data);
            in.close();

            if (delete)
                file.delete();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean storeByteFile(String path, byte[] data) {
        if (path == null || data == null)
            return false;
        try {
            File sdCard = new File(Environment.getExternalStorageDirectory(),
                    path);
            File sdCardParent = sdCard.getParentFile();
            if (!sdCardParent.exists()) {
                sdCardParent.mkdirs();
            }
            // File sdCard = new File(path);
            if (sdCard.exists()) {
                sdCard.delete();
            }

            FileOutputStream out = new FileOutputStream(sdCard);
            out.write(data);
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    public static String stringFromInputStream(InputStream is, String charset)
            throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is,
                Charset.forName(charset)));

        StringBuffer buf = new StringBuffer();
        String str;

        while ((str = in.readLine()) != null) {
            buf.append(str);
        }

        in.close();
        return buf.toString();
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10)
                sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     *
     * @param str
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static String loadFileAsString(String filename)
            throws java.io.IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(
                filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF
                        && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8")
                    : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName))
                        continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null)
                    return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0)
                    buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*
         * try { // this is so Linux hack return
         * loadFileAsString("/sys/class/net/" +interfaceName +
         * "/address").toUpperCase().trim(); } catch (IOException ex) { return
         * null; }
         */
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf
                        .getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port
                                // suffix
                                return delim < 0 ? sAddr : sAddr.substring(0,
                                        delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    public static void fixInmmersive(Dialog dialog, Context context) {
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static byte[] InputStreamToByteArray(InputStream inputStream) {
        byte[] bytes = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte buffer[] = new byte[1024];
            int count;
            while (true) {
                count = inputStream.read(buffer);
                if (count == -1)
                    break;
                bos.write(buffer, 0, count);
            }
            bos.flush();
            bos.close();
            inputStream.close();

            bytes = bos.toByteArray();
        } catch (IOException e) {
            HttpUtil.sendReportUncaughtException(e);
        }
        return bytes;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static JSONObject BundleToJsonObject(Bundle bundle) {
        JSONObject json = new JSONObject();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                try {
                    json.put(key, JSONObject.wrap(bundle.get(key)));
                } catch (JSONException e) {
                    Log.e("JSON transform", e.getMessage());
                }
            }
        }
        return json;
    }

    public static Bundle JsonObjectToBundle(JSONObject json) {
        Bundle bundle = new Bundle();
        if (json != null) {
            Iterator<String> it = json.keys();
            while (it.hasNext()) {
                String key = it.next();
                String value = null;
                try {
                    value = json.get(key).toString();
                } catch (JSONException e) {
                    Log.e("Bundle transform", e.getMessage());
                }
                bundle.putString(key, value);
            }
        }
        return bundle;
    }

    public static Logger getLogger(Class<?> clazz) {

        Logger log = Logger.getLogger(clazz.getName());
        return log;
    }

    public static Set<String> setOf_Strings(String... elements) {
        Set<String> result = new HashSet<String>();
        for (String element : elements) {
            result.add(element);
        }
        return result;
    }

    public static void reverse(byte[] pixels) {
        int head = 0;
        int tail = pixels.length - 1;
        byte tmp;
        while (head < tail) {
            tmp = pixels[head];
            pixels[head] = pixels[tail];
            pixels[tail] = tmp;
            --tail;
            ++head;
        }
    }

    public static byte[] readAllBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[16384];
        int bytesRead = 0;
        while (true) {
            bytesRead = stream.read(buffer);
            if (bytesRead == -1) break;
            result.write(buffer, 0, bytesRead);
        }
        return result.toByteArray();
    }

    public static boolean arrayHeaderMatches(byte[] data, byte[] header) {
        if (header.length >= data.length)
            return false;
        for (int n = 0; n < header.length; n++) {
            if (data[n] != header[n])
                return false;
        }
        return true;
    }

    public static boolean arrayHeaderMatchesAny(byte[] data,
                                                byte[]... headers) {

        for (byte[] header : headers) {
            if (arrayHeaderMatches(data, header))
                return true;
        }
        return false;
    }

    public static String makePath(String basedir, String... parts) {
        StringBuffer buf = new StringBuffer();
        buf.append(basedir);
        for (String part : parts) {
            buf.append(File.separator);
            buf.append(part);
        }
        return buf.toString();
    }

    public static String makePath(File basedir, String... parts) throws IOException {
        return makePath(basedir.getCanonicalPath(), parts);
    }

    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }
        return sb.toString();
    }

    public static String getTrackID() {
        return UUID.randomUUID().toString();
    }

}
