package com.initgrupp.error;

public class UnknownTagException extends RuntimeException {

    public UnknownTagException(){
        super("Unknown tag into TLV");
    }
}
