package com.example.amaroescobar.transuniondemo.http;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.example.amaroescobar.transuniondemo.Pregunta;
import com.example.amaroescobar.transuniondemo.Cuestionario;
import com.example.amaroescobar.transuniondemo.Respuesta;
import com.example.amaroescobar.transuniondemo.Respuestas;
import com.example.amaroescobar.transuniondemo.questionClass;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.message.BasicHeader;

public class AutentiaWSClient {

    private static final String TAG = "AutentiaWSClient";

    private HttpManager mHttpManager;
    private Context mContext;
    private String mUrlBase;


    public AutentiaWSClient(Context context) {
        mHttpManager = new HttpManager(HttpManager.PORT_HTTP, HttpManager.PORT_HTTPS);
        mContext = context;
    }

    public Bundle getAuth(String nroSerie, String institucion, Context context) {

        String url = "http://192.168.1.113:8081/getToken";

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String jsonString = AUTH(url,android_id,institucion,nroSerie);
        Log.e(TAG,jsonString);

        Bundle data = new Bundle();

        return data;

    }

    public String sendResponse(HashMap<String,String> hashMap, String rut) {

        String url = "http://192.168.1.114:8081/enviarRespuestas";
        //String url = "http://172.16.14.41/autentiaws/enviarRespuestas";

        String result = null;
        Respuesta resp;
        List<Respuesta> respList = new ArrayList<>();

        for(Map.Entry<String, String> entry : hashMap.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            resp = new Respuesta(key,value);
            respList.add(resp);
        }

        Respuestas respuestas = new Respuestas();
        respuestas.setRut(rut);
        respuestas.setRespuestas(respList);
        Log.e("GSOOON",new Gson().toJson(respuestas));
        result = SENDRESPONSE(new Gson().toJson(respuestas),url);

        if (result != null){
            Log.e("result",result);
            return result;
        }else{
            Log.e("result",result);
            return "";
        }
    }


    public List<Pregunta> getQuestions(String nroSerie,String institucion,String rut,Context context) throws Exception {

        String url = "http://192.168.1.114:8081/obtenerPreguntas";
        //String url = "http://172.16.14.41/autentiaws/obtenerPreguntas";
        List<questionClass> questionClassList = new LinkedList<>();

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String bearer = "asdfasdfasdfasdfasdfasdf";
        String jsonString = GETQUESTIONS(url,android_id,institucion,nroSerie,rut,bearer);
        Cuestionario gsonPreguntas = new Gson().fromJson(jsonString,Cuestionario.class);

        if (gsonPreguntas.getStatus()==1){
            throw new Exception(gsonPreguntas.getGlosa());
        }

        return gsonPreguntas.getPreguntas();

    }

    private String AUTH(String URL, String android_id, String institucion, String nroSerie){
        InputStream inputStream;
        String result = "";
        Log.i("AUTH","INIT");
        try {

            String json;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("institucion", institucion);
            jsonObject.put("androidId", android_id);
            jsonObject.put("nroSerie", nroSerie);

            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            List<BasicHeader> basicHeaders = new ArrayList<>();
//            basicHeaders.add(new BasicHeader("Content-Type", "application/json"));
            Log.e(TAG,jsonObject.toString());
            Log.e(TAG,URL);
            HttpResponse httpResponse = mHttpManager.sendDataPostMethodStringEntity(URL,se,basicHeaders);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.d("RESULT", result);
            }else {
                result = "Did not work!";
                Log.d("RESULT", result);
            }

        } catch (Exception e) {
            Log.e("InputStream", e.toString());
        }

        return result;
    }

    private String SENDRESPONSE(String gson, String URL){
        InputStream inputStream = null;
        String result = "";
        Log.i("SENDRESPONSE", "INIT");
        try {

            StringEntity se = new StringEntity(gson);
            List<BasicHeader> basicHeaders = new ArrayList<>();
            //basicHeaders.add(new BasicHeader("institucion", institucion));
            //basicHeaders.add(new BasicHeader("androidId", android_id));
            //basicHeaders.add(new BasicHeader("nroSerie", nroSerie));
            //basicHeaders.add(new BasicHeader("Authorization", "Bearer "+Bearer));
            HttpResponse httpResponse = mHttpManager.sendDataPostMethodStringEntity(URL,se,basicHeaders);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.d("RESULT", result);
            }else {
                result = "Did not work!";
                Log.d("RESULT", result);
            }

        } catch (Exception e) {
            Log.e("InputStream", e.toString());
        }
        return result;
    }


    private String GETQUESTIONS(String URL, String android_id, String institucion, String nroSerie,String rut, String Bearer){
            //InputStream inputStream;
            InputStream inputStream = null;
            String result = "";
            Log.i("GETQUESTIONS", "INIT");
            try {

                String json;

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("rut", rut);
                jsonObject.put("nroSerie", nroSerie);
                jsonObject.put("inquirySource", "IDV-WEBSERVICE");
                jsonObject.put("tipoDocumento", "C");

                json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                List<BasicHeader> basicHeaders = new ArrayList<>();
                Log.e(TAG,jsonObject.toString());
                Log.e(TAG,URL);
                HttpResponse httpResponse = mHttpManager.sendDataPostMethodStringEntity(URL,se,basicHeaders);
                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    Log.d("RESULT", result);
                }else {
                    result = "Did not work!";
                    Log.d("RESULT", result);
                }

            } catch (Exception e) {
                Log.e("InputStream", e.toString());
            }

        /*try {
            result = String.valueOf(new JSONObject("{\n" +
                    "    \"preguntas\": [\n" +
                    "        {\n" +
                    "            \"id\": \"93b81bec-bf87-4ad4-af74-e910c2ae55fc\",\n" +
                    "            \"pregunta\": \"Q062 - ¿Cuál es el segundo nombre de su hermano(a)?\",\n" +
                    "            \"respuestas\": [\n" +
                    "                \"0&PAULINA\",\n" +
                    "                \"1&PILAR\",\n" +
                    "                \"2&PAZ\",\n" +
                    "                \"3&GONZALO\"\n" +
                    "            ],\n" +
                    "            \"tipo\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"2e2317a4-5353-4254-aeb7-ae782ab10fe7\",\n" +
                    "            \"pregunta\": \"Q007 - ¿Nos podría indicar en qué año celebró un matrimonio civil?\",\n" +
                    "            \"respuestas\": [\n" +
                    "                \"0&1979\",\n" +
                    "                \"1&1978\",\n" +
                    "                \"2&1976\",\n" +
                    "                \"3&Ninguna de las Anteriores\"\n" +
                    "            ],\n" +
                    "            \"tipo\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"2ad8e44f-767b-4ac6-b480-e998684c0044\",\n" +
                    "            \"pregunta\": \"Q002 - ¿De los siguientes teléfonos, cuál reconoce o está relacionado con usted?\",\n" +
                    "            \"respuestas\": [\n" +
                    "                \"0&2-26432833\",\n" +
                    "                \"1&2-25416726\",\n" +
                    "                \"2&2-22431263\",\n" +
                    "                \"3&Ninguna de las Anteriores\"\n" +
                    "            ],\n" +
                    "            \"tipo\": null\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return result;
    }

    public String genAudit(byte[] muestra, String formatoMuestra, String run, String dedo,
                           String tipoLector, String nroSerieLector, byte[] evidencia,
                           String resultado, String nombre, String descripcion,
                           String rutEmpresa, String versionApp, String ubicacion, String porFirmar,
                           String textoAdjunto, String valorAdjunto, String institucion) throws Exception {

        String url = "GenAudit";

        MultipartEntity multipartEntity = new MultipartEntity();

        multipartEntity.addPart("muestra", new ByteArrayBody(muestra, "empty.wsq"));
        multipartEntity.addPart("formatoMuestra", new StringBody("nada"));
        multipartEntity.addPart("run", new StringBody(run));
        multipartEntity.addPart("dedo", new StringBody("-1"));
        multipartEntity.addPart("tipoLector", new StringBody("reader.eikon"));
        multipartEntity.addPart("nroSerieLector", new StringBody("asdsd-asdasd-adsad-adad"));
        multipartEntity.addPart("evidencia", new ByteArrayBody(evidencia, "empty"));
        multipartEntity.addPart("resultado", new StringBody("0000")); //0000-0001
        multipartEntity.addPart("nombre", new StringBody(""));
        multipartEntity.addPart("descripcion", new StringBody(descripcion));
        multipartEntity.addPart("rutOperador", new StringBody(""));
        multipartEntity.addPart("versionApp", new StringBody("000"));
        multipartEntity.addPart("ubicacion", new StringBody("00"));
        multipartEntity.addPart("porFirmar", new StringBody("0"));
        multipartEntity.addPart("textoAdjunto", new StringBody(""));
        multipartEntity.addPart("valorAdjunto", new StringBody(""));
        multipartEntity.addPart("institucion", new StringBody("AUTENTIA"));

        HttpPost postRequest = new HttpPost(url);
        postRequest.setEntity(multipartEntity);

        List<BasicHeader> basicHeaders = new ArrayList<>();
        String auth = HttpUtil.getAuth(mContext, mHttpManager.mHttpContext, mHttpManager.mHttpClient);
        basicHeaders.add(new BasicHeader("Authorization", auth));

        HttpResponse response = mHttpManager.sendDataPostMethodHttpResponse(url, multipartEntity, basicHeaders);

        org.json.simple.JSONObject jsonResponse = HttpUtil.readEntityAsJSONObject(response.getEntity());

        if (jsonResponse.containsKey("message")) {
            throw new Exception(String.valueOf(jsonResponse.get("cause")));
            //throw new AutentiaMovilException(ReturnCode.HUELLERO_NO_REGISTRADO);
        }
        return (String) jsonResponse.get("result");

    }



    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }





}
