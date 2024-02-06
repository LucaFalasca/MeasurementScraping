package it.lucafalasca.util;

import java.util.Base64;

public class Decode {

    private Decode(){

    }
    public static String decodeBase64(String s){
        byte[] decodedBytes = Base64.getMimeDecoder().decode(s);
        return new String(decodedBytes);
    }
}
