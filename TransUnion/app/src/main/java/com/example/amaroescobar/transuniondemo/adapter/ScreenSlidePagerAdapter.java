package com.example.amaroescobar.transuniondemo.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.amaroescobar.transuniondemo.Pregunta;
import com.example.amaroescobar.transuniondemo.questionClass;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 3;
    List<Pregunta> questions;

    public ScreenSlidePagerAdapter(FragmentManager fm, List<Pregunta> questions) {
        super(fm);
        this.questions = questions;
    }

    @Override
    public Fragment getItem(int position) {

        ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putString(ScreenSlidePageFragment.ARG_QUESTION_TITLE,questions.get(position).getPregunta());
        args.putString(ScreenSlidePageFragment.ARG_QUESTION_ID,questions.get(position).getId());
        args.putStringArrayList(ScreenSlidePageFragment.ARG_QUESTION_ANSWER, (ArrayList<String>) questions.get(position).getRespuestas());
        screenSlidePageFragment.setArguments(args);
        updateFragment(screenSlidePageFragment);
        return screenSlidePageFragment;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    public void updateFragment(ScreenSlidePageFragment fgm){
        fgm.onResume();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    /*public void setPagerItems(ArrayList<Pregunta> preguntas) {
        if (Pregunta != null)
            for (int i = 0; i < mPagerItems.size(); i++) {
                mFragmentManager.beginTransaction().remove(Pregunta.get(i).getFragment()).commit();
            }
        questions = preguntas;
    }*/
}