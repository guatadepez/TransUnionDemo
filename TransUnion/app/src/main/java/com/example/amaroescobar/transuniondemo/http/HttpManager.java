package com.example.amaroescobar.transuniondemo.http;

import com.example.amaroescobar.transuniondemo.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.client.CookieStore;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import ch.boye.httpclientandroidlib.message.BasicHeader;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import static java.net.Proxy.Type.HTTP;

public class HttpManager {

    public static final int PORT_HTTP = 80;
    public static final int PORT_HTTPS = 443;
    private HttpParams mHttpParams;
    public HttpContext mHttpContext;
    public HttpClient mHttpClient;

    public HttpManager(int portHTTP, int portHTTPS) {

        mHttpParams = new BasicHttpParams();
        HttpProtocolParams.setVersion(mHttpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(mHttpParams, String.valueOf(StandardCharsets.UTF_8));

        int timeoutConnection = 1000 * 60;
        HttpConnectionParams.setConnectionTimeout(mHttpParams,
                timeoutConnection);
        int timeoutSocket = 1000 * 60;
        HttpConnectionParams
                .setSoTimeout(mHttpParams, timeoutSocket);

        // Registra los protocolos HTTP y HTTPS, en caso de HTTPS genera un socket por default
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), portHTTP));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), portHTTPS));

        CookieStore cookieStore = new BasicCookieStore();

        mHttpContext = new BasicHttpContext();
        mHttpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        ClientConnectionManager mConnectionManager = new ThreadSafeClientConnManager(mHttpParams, registry);
        mHttpClient = new DefaultHttpClient(mConnectionManager, mHttpParams);
    }

    private String getUrlDataMap(Map<String, String> params) throws UnsupportedEncodingException {

        if (params == null)
            return "";
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return String.format("?%s", result.toString());
    }

    /***
     * GET
     *
     * @param connexion
     * @param data
     * @param basicHeaders "Content-Type", "text/plain; charset=utf-8"
     * @return
     * @throws Exception
     */
    public String sendDataGetMethod(String connexion, Map<String, String> data, List<BasicHeader> basicHeaders) throws Exception {
        URL url = new URL(String.format("%s%s", connexion, getUrlDataMap(data)));
        HttpGet httpGet = new HttpGet(url.toURI());

        for (BasicHeader header : basicHeaders) {
            httpGet.setHeader(header);
        }

        HttpResponse response = mHttpClient.execute(httpGet, mHttpContext);
        return EntityUtils.toString(response.getEntity());
    }

    /***
     * POST MULTIPART
     * @param connexion
     * @param entity
     * @param basicHeaders "Content-Type", "multipart/form-data"
     * @return
     * @throws Exception
     */
    public String sendDataPostMethod(String connexion, MultipartEntity entity, List<BasicHeader> basicHeaders) throws Exception {
        URL url = new URL(connexion);
        HttpPost httpPost = new HttpPost(url.toURI());
        httpPost.setEntity(entity);

        for (BasicHeader header : basicHeaders) {
            httpPost.setHeader(header);
        }

        HttpResponse response = mHttpClient.execute(httpPost, mHttpContext);
        return EntityUtils.toString(response.getEntity());
    }


    /**
     * @param connexion
     * @param entity
     * @param basicHeaders
     * @return
     * @throws Exception
     */
    public HttpResponse sendDataPostMethodHttpResponse(String connexion, MultipartEntity entity, List<BasicHeader> basicHeaders) throws Exception {
        URL url = new URL(connexion);
        HttpPost httpPost = new HttpPost(url.toURI());
        httpPost.setEntity(entity);

        for (BasicHeader header : basicHeaders) {
            httpPost.setHeader(header);
        }

        return mHttpClient.execute(httpPost, mHttpContext);
    }

    public HttpResponse sendDataPostMethodStringEntity(String connexion, StringEntity  stringEntity, List<BasicHeader> basicHeaders) throws Exception {
        URL url = new URL(connexion);
        HttpPost httpPost = new HttpPost(url.toURI());
        httpPost.setEntity(stringEntity);

        for (BasicHeader header : basicHeaders) {
            httpPost.setHeader(header);
        }

        return mHttpClient.execute(httpPost, mHttpContext);
    }




    /**
     * @param connexion
     * @param entity
     * @param basicHeaders
     * @return
     * @throws Exception
     */
    public HttpResponse sendDataPostMethodHttpResponse(String connexion, HttpEntity entity, List<BasicHeader> basicHeaders) throws Exception {
        URL url = new URL(connexion);
        HttpPost httpPost = new HttpPost(url.toURI());
        httpPost.setEntity(entity);

        for (BasicHeader header : basicHeaders) {
            httpPost.setHeader(header);
        }

        return mHttpClient.execute(httpPost, mHttpContext);
    }

    /***
     * POST
     *
     * @param connexion
     * @param request
     * @param basicHeaders "Content-Type", "application/json"
     * @return
     * @throws Exception
     */
    public String sendDataPostMethod(String connexion, String request, List<BasicHeader> basicHeaders) throws Exception {
        URL url = new URL(connexion);
        HttpPost httpPost = new HttpPost(url.toURI());
        httpPost.setEntity(new StringEntity(request, "UTF-8"));

        for (BasicHeader header : basicHeaders) {
            httpPost.setHeader(header);
        }

        HttpResponse response = mHttpClient.execute(httpPost, mHttpContext);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * @param connexion
     * @param data
     * @return
     * @throws Exception
     */
    public String downloadTextData(String connexion, Map<String, String> data) throws Exception {

        URL url = new URL(connexion);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(60 * 1000);
        connection.setConnectTimeout(60 * 1000);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.connect();

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getUrlDataMap(data));

        writer.flush();
        writer.close();
        os.close();
        int responseCode = connection.getResponseCode();

        String response = "";
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        }
        return response;
    }

    /**
     * @param connexion
     * @return
     * @throws Exception
     */
    public byte[] downloadData(String connexion) throws Exception {

        URL url = new URL(connexion);
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            return Utils.inputStreamtoByteArray(inputStream);

        } finally {
            urlConnection.disconnect();
        }
    }





}
