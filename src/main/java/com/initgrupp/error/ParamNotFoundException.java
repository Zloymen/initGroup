package com.initgrupp.error;

public class ParamNotFoundException extends RuntimeException {
    public ParamNotFoundException(){
        super("two parameters are required");
    }
}
