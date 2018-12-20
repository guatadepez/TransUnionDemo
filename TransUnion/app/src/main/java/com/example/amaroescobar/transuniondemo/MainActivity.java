package com.example.amaroescobar.transuniondemo;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.amaroescobar.transuniondemo.adapter.ScreenSlidePagerAdapter;
import com.example.amaroescobar.transuniondemo.http.AutentiaWSClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AutentiaWSClient mAutentiaWSClient;
    //private List<questionClass> questionClassList;
    private List<Pregunta> questionClassList;
    private ProgressDialog mProgressDialog;
    private CountDownTimer mCountDownTimer;
    int mMaxAttempts = 2;
    int mIntentoActual;


    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private LinearLayout mLinearLayout;

    Button sendButton;
    String mRut;
    String mDv;
    ArrayList respuestas;

    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAutentiaWSClient = new AutentiaWSClient(this);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setText("siguiente");
        mLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);

        Intent intent = getIntent();

        //Log.e("intent", intent.toString());
        //Log.e("intent.getExtras", intent.getExtras().toString());

        if (intent.getExtras() != null) {
            mRut = String.valueOf(getIntent().getIntExtra("RUT", 0)) + String.valueOf(getIntent().getCharExtra("DV", 'k'));
            mDv = String.valueOf(getIntent().getCharExtra("DV", 'k'));
            if (!Utils.validaRutDV(getIntent().getIntExtra("RUT", 0), getIntent().getCharExtra("DV", 'k'))) {
                try {
                    throw new TransunionException("el rut es invalido", TransunionException.ReturnCode.RUT_INVALIDO);

                } catch (TransunionException e) {
                    e.printStackTrace();
                    finishActivityWithError(e.status,
                            e.returnCode.getCode(),
                            e.returnCode.getDescription());
                }
            }
            Log.e(TAG, "Rut: " + mRut);
            getAuth();
        }else
            finish();
    }

    public void getAuth(){
        try {
            showProgressMessage("Obteniendo cuestionario", "Por favor, espere...", false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        questionClassList = mAutentiaWSClient.getQuestions("{A00002921}", "AUTENTIA", mRut, MainActivity.this);
                        if (questionClassList.size() > 0){ //SI TENEMOS PREGUNTAS
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressMessage();
                                }
                            });
                            refresh();
                        }else{
                            Log.e(TAG,"EMPTY");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finishActivityWithError("ERROR", TransunionException.ReturnCode.ERROR_GENERICO.getCode(), e.toString());
                    }

                }
            });
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"HHHHHHERROR");
        }

        Log.e(TAG,"DONE");
    }

    void refresh(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager = (ViewPager) findViewById(R.id.pager);
                mPager.setAdapter(null);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),questionClassList);
                mPager.setAdapter(mPagerAdapter);
                mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == questionClassList.size()-1) sendButton.setText("Finalizar");
                        else sendButton.setText("siguiente");
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(mPager,true);
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void buttonOnClick(View v){
        if(mPager.getCurrentItem() == questionClassList.size()-2){
            sendButton.setText("Finalizar");
            mPager.setCurrentItem(mPager.getCurrentItem()+1);
        }else{
            if(sendButton.getText().equals("Finalizar")){
                sendResponse();
            }else{
                mPager.setCurrentItem(mPager.getCurrentItem()+1);
            }
        }
    }

    void sendResponse(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                respuestas = new ArrayList();
                HashMap<String,String> hashMap = new HashMap<>();
                ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

                for(int i = 0; i < questionClassList.size();i++){
                    RadioGroup rg = (RadioGroup) viewPager.findViewWithTag(questionClassList.get(i).getId());
                    for (int j = 0; j < rg.getChildCount(); j++) {
                        RadioButton rb = (RadioButton) rg.getChildAt(j);
                        if (rb.isChecked()){
                            respuestas.add(rb.getId());
                            hashMap.put(questionClassList.get(i).getId(), String.valueOf(rb.getId()));
                            Log.e("HASHMAP","ID: "+questionClassList.get(i).getId()+" VALUE: "+hashMap.get(questionClassList.get(i).getId()));
                        }
                    }
                }
                Log.e("respuesta.size",""+respuestas.size());
                Log.e("questionClassList.size",""+questionClassList.size());
                if(respuestas.size() < questionClassList.size()){  //TODO: mensaje que responda todas las preguntas.
                    Log.e("respuestassize","miss some answers");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar snackbar = Snackbar
                                    .make(mLinearLayout, "Debes contestar todas las preguntas antes de continuar", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            mPager.setCurrentItem(0);
                        }
                    });

                }else {
                    String genAuditResult = mAutentiaWSClient.sendResponse(hashMap,mRut);
                    Log.e("genAuditResult",genAuditResult);
                    final Bundle bundle = new Bundle();
                    bundle.putBoolean("match", true);
                    bundle.putString("auditoria", genAuditResult);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showValidationResponse(bundle);
                        }
                    });
                }
            }
        });
        t.start();
    }

    protected void showProgressMessage(final String title, final String message, final boolean cancelable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress(title,message,cancelable);
            }
        });
    }

    public void showProgress(final String title, final String message, final boolean cancelable){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // por si hubo contador lo cancelamos
                dismissProgressMessage();
                Log.v(TAG, String.format("message: %s: %s", title, message));

                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setCancelable(false);
                if (cancelable)
                    mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                            onBackPressed();

                        }
                    });
                mProgressDialog.setTitle(title);
                mProgressDialog.setMessage(message);

                if (!isAppIsInBackground(getApplicationContext())) {
                    mProgressDialog.show();
                }
            }
        });
    }

    protected void dismissProgressMessage() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();

        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        Log.v(TAG, "dialog dismissed");
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    void showValidationResponse(Bundle result) {

        boolean identidadVerificada = true;
        String codigoAuditoria = "COD-EXAMPLE-AUDIT-3434";
        byte[] fingerWSQ = new byte[0];
        int dedo = 10;
        int status;
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(result.get("auditoria")));
            status = jsonObject.getInt("status");
            mIntentoActual++;
            Log.e("mIntentoActual",""+mIntentoActual);
            Log.e("mMaxAttempts",""+mMaxAttempts);

            if (status != 0) { //si falla el servicio
                finishActivityWithError("0001",
                        TransunionException.ReturnCode.ERROR_GENERICO.getCode(),
                        jsonObject.getString("glosa"));
            } else {
                if (jsonObject.getString("decision").equals("ExamenRechazado")){ //si se rechaza.
                    if (mIntentoActual >= mMaxAttempts) {
                        dismissProgressMessage();
                        Log.e("try again","cant");
                        finishActivityWithError("0001",
                                TransunionException.ReturnCode.VERIFICATION_SUPERADO_INTENTOS_VERIFICACION.getCode(),
                                TransunionException.ReturnCode.VERIFICATION_SUPERADO_INTENTOS_VERIFICACION.getDescription());

                    } else {
                        Snackbar snackbar = Snackbar
                                .make(mLinearLayout, "Rechazado", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        Log.e("try again","yes");
                        Log.e("mIntentoActual",""+mIntentoActual);
                        Log.e("mMaxAttempts",""+mMaxAttempts);
                        questionClassList = null;
                        respuestas = null;
                        mPagerAdapter = null;
                        mPager = null;
                        sendButton.setText("SIGUIENTE");
                        dismissProgressMessage();
                        getAuth();
                    }
                }else{
                    dismissProgressMessage();
                    Log.e("END", jsonObject.toString());
                    final String resultfinal = jsonObject.toString();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Extras.Out.RUT, mRut);
                    returnIntent.putExtra(Extras.Out.DV, mDv);
                    returnIntent.putExtra(Extras.Out.ESTADO, "Exito");
                    returnIntent.putExtra(Extras.Out.CODIGO_RESPUESTA, "0000");
                    returnIntent.putExtra(Extras.Out.DESCRIPCION, resultfinal);
                    returnIntent.putExtra(Extras.Out.IDENTIDAD_VERIFICADA, true);
                    returnIntent.putExtra(Extras.Out.NUMERO_SERIE_HUELLERO, "0000-0000-0000-0000");
                    returnIntent.putExtra(Extras.Out.CODIGO_AUDITORIA, codigoAuditoria);
                    returnIntent.putExtra(Extras.Out.TIPO_LECTOR, "");
                    returnIntent.putExtra(Extras.Out.TRANSACTION_ID, "TRANSID");
                    returnIntent.putExtra(Extras.Out.NOMBRE, "ALAN"); //lo obtendremos de la información obtenida en la cédula
                    returnIntent.putExtra(Extras.Out.APELLIDOS, "BRITO"); //lo obtendremos de la información obtenida en la cédula
                    returnIntent.putExtra(Extras.Out.FECHA_NACIMIENTO, "30/02/1990"); //lo obtendremos de la información obtenida en la cédula
                    returnIntent.putExtra(Extras.Out.ENROLADO, true);
                    returnIntent.putExtra(Extras.Out.FECHA_VENCIMIENTO, "");
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void showMessage(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle("Resultado:")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create().show();
    }



    interface Extras {

        interface In extends CommonInExtras {

            String RUT = "RUT";
            String DV = "DV";
            String OFFLINE_MODE = "OFFLINE_MODE";
            String BARCODE = "BARCODE";
            String INTENTOS = "INTENTOS";
            String TIMEOUT = "TIMEOUT";
            String SKIP_TERMS = "SKIP_TERMS"; //******* Revisar si corresponde o no este parámetro
            String PREVIRED = "PREVIRED";
            String URL_DOCUMENT = "URL_DOCUMENT";
            String HIDE_RUT = "HIDE_RUT";
            String COD_DOC = "COD_DOCUMENTO";
            String INSTITUCION_DEC = "INSTITUCION_DEC";
            String ORIENTACION = "ORIENTACION";
        }

        interface Out extends CommonOutExtras {

            String IDENTIDAD_VERIFICADA = "identidadVerificada";
            String RUT = "rut";
            String DV = "dv";
            String NUMERO_SERIE_HUELLERO = "serialNumber";
            String CODIGO_AUDITORIA = "codigoAuditoria";
            String TIPO_LECTOR = "tipoLector";
            String TRANSACTION_ID = "idtx";
            String NOMBRE = "nombre";
            String APELLIDOS = "apellidos";
            String FECHA_NACIMIENTO = "fechaNac";
            String ENROLADO = "enrolado";
            String FECHA_VENCIMIENTO = "fechaVencimiento";
        }
    }

    public interface CommonOutExtras {

        String CODIGO_RESPUESTA = "CODIGO_RESPUESTA";
        String ESTADO = "ESTADO";
        String DESCRIPCION = "DESCRIPCION";
    }

    public interface CommonInExtras {

        String ICON = "ICON";
        String COLOR_PRIMARY = "COLOR_PRIMARY";
        String COLOR_PRIMARY_DARK = "COLOR_PRIMARY_DARK";
        String COLOR_TITLE = "COLOR_TITLE";
        String COLOR_SUBTITLE = "COLOR_SUBTITLE";
        String TITLE = "TITLE";
        String SUBTITLE = "SUBTITLE";
    }

    public void finishActivityWithError(String status, int resultCode, String descripcion) {

        Intent returnIntent = new Intent();
        returnIntent.putExtra(Extras.Out.RUT, mRut);
        returnIntent.putExtra(Extras.Out.IDENTIDAD_VERIFICADA, false);
        returnIntent.putExtra(Extras.Out.CODIGO_RESPUESTA, resultCode);
        returnIntent.putExtra(Extras.Out.ESTADO, status);
        returnIntent.putExtra(Extras.Out.DESCRIPCION, descripcion);
        setResult(RESULT_OK, returnIntent);
        finish();
    }



}
