package com.example.amaroescobar.transuniondemo;

public class Respuesta {

    private String idPregunta;
    private String idRespuesta;

    public Respuesta(String idPregunta, String idRespuesta) {
        this.idPregunta = idPregunta;
        this.idRespuesta = idRespuesta;
    }

    public String getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(String idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(String idRespuesta) {
        this.idRespuesta = idRespuesta;
    }
}
