package com.example.amaroescobar.transuniondemo;

import java.util.ArrayList;
import java.util.List;

public class Cuestionario {

    private int status;
    private String glosa;
    private List<Pregunta> preguntas = new ArrayList<Pregunta>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGlosa() {
        return glosa;
    }

    public void setGlosa(String glosa) {
        this.glosa = glosa;
    }

    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }
}
