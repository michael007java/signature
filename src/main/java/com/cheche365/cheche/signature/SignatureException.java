package com.cheche365.cheche.signature;

public class SignatureException extends RuntimeException{

    public SignatureException() {
        super();
    }

    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(Throwable cause) {
        super(cause);
    }

    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }

}

