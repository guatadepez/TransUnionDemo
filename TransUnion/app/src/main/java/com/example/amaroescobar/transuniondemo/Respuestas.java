package com.example.amaroescobar.transuniondemo;

import java.util.ArrayList;
import java.util.List;

public class Respuestas {

    private String rut;
    private List<Respuesta> respuestas = new ArrayList<Respuesta>();

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }
}
