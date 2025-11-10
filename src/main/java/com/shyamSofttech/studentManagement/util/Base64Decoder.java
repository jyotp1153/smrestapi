package com.shyamSofttech.studentManagement.util;

import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@UtilityClass
public class Base64Decoder {
    public InputStream convertBase64Image(String base64Url){
        byte[] imageBytes = Base64.getDecoder().decode(base64Url);
        return new ByteArrayInputStream(imageBytes);
    }
    public static byte[] getImageByte(String base64Url){
        return Base64.getDecoder().decode(base64Url);
    }
}
