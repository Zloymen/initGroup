package com.initgrupp.error;

public class BadTLVstructureException extends RuntimeException {

    public BadTLVstructureException(){
        super("Incorrect TLV structure");
    }
}
