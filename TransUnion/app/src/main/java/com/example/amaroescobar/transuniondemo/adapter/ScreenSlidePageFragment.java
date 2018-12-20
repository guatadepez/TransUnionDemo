package com.example.amaroescobar.transuniondemo.adapter;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.amaroescobar.transuniondemo.R;

import java.util.ArrayList;
import java.util.Objects;

public class ScreenSlidePageFragment extends Fragment{

    public static final String ARG_QUESTION_TITLE = "question_title";
    public static final String ARG_QUESTION_ID = "question_id";
    public static final String ARG_QUESTION_ANSWER = "question_answer";
    Bundle bundle;
    RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragmentcontent, container, false);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.rg_id);
        bundle = getArguments();
        dynRadioButtons();

        TextView id = (TextView) rootView.findViewById(R.id.tv_question);
        id.setText(bundle.getString(ARG_QUESTION_TITLE));

        return rootView;

    }

    void dynRadioButtons(){
        for (String p: Objects.requireNonNull(bundle.getStringArrayList(ARG_QUESTION_ANSWER))){
            RadioButton rbResp = new RadioButton(getContext());
            rbResp.setId(Integer.parseInt(p.split("&")[0]));
            rbResp.setText(p.split("&")[1]);
            radioGroup.addView(rbResp);
        }
        radioGroup.setTag(bundle.getString(ARG_QUESTION_ID));
        //radioGroup.setTag(bundle.getString(ARG_QUESTION_ID).replaceAll("[^0-9]", "")); //si queremos dejar el tag con puros numeros.
        Log.e("TAG",""+radioGroup.getTag());
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
