package com.example.amaroescobar.transuniondemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class questionClass {

    private String id;
    private String question;
    private ArrayList<String> list;

    public questionClass(String id, String question, ArrayList<String> list){
        this.id = id;
        this.question = question;
        this.list = list;
    }

    public String getId(){return id;}
    public String getQuestion(){return question;}
    public ArrayList <String> getHashMap(){return list;}

}
