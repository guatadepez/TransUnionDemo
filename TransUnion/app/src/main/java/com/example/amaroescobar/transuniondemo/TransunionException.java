package com.example.amaroescobar.transuniondemo;

public class TransunionException extends Exception {

    public String status;
    public ReturnCode returnCode;

    public TransunionException(ReturnCode returnCode){
        super(returnCode.getDescription());
        this.returnCode = returnCode;
        this.status = null;

    }

    public TransunionException(String status, ReturnCode returnCode) {
        super(returnCode.getDescription());
        this.returnCode = returnCode;
        this.status = status;
    }

    public enum ReturnCode implements ReturnCodeStructure{

        ERROR_GENERICO(201, "%s"),
        VERIFICATION_SUPERADO_INTENTOS_VERIFICACION(903, "Ha superado el máximo de intentos de verificación"),
        RUT_INVALIDO(203, "Rut inválido");


        private int code;
        private String description;

        ReturnCode(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Override
        public int getCode() {
            return this.code;
        }

        @Override
        public String getDescription() {
            return this.description;
        }
    }

    public interface ReturnCodeStructure {

        int getCode();

        String getDescription();

    }
}
