package com.github.ep2p.eleuth.util;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {

    public static String encode(byte[] input){
        return new Base64().encodeToString(input);
    }

    public static byte[] decode(String input){
        return new Base64().decode(input);
    }

}
