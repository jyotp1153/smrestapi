package com.shyamSofttech.studentManagement.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.shyamSofttech.studentManagement.constant.ApiErrorCodes;
import com.shyamSofttech.studentManagement.exception.NoSuchElementFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Service
public class ImageUploader {
    @Value("${aws.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;
    public final String baseUrl = "https://jwellery-bucket.s3.eu-north-1.amazonaws.com/";

    @Autowired
    public ImageUploader(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }



    public String uploadFile(String base64Data, String extensionType){
        extensionType = extensionType.replace(".", ""); // Sanitize
        byte[] fileBytes = Base64Decoder.getImageByte(base64Data);
        InputStream fileStream = Base64Decoder.convertBase64Image(base64Data);
        String key = System.currentTimeMillis() + "." + extensionType;
        String contentType = getMimeType(extensionType);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileBytes.length);
        metadata.setContentType(contentType);

        amazonS3.putObject(bucketName, key, fileStream, metadata);
        return baseUrl + key;
    }


    private String getMimeType(String extension) {
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "ppt", "pptx" -> "application/vnd.ms-powerpoint";
            case "doc", "docx" -> "application/msword";
            case "xls", "xlsx" -> "application/vnd.ms-excel";
            case "txt" -> "text/plain";
            case "csv" -> "text/csv";
            case "zip" -> "application/zip";
            case "mp4" -> "video/mp4";
            case "mp3" -> "audio/mpeg";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            default -> "application/octet-stream"; // Default for unknown types
        };
    }
    public void deleteImage(String base64Url){
        try {
            if (!Objects.equals(base64Url, "")) {
                URL url = new URL(base64Url);
                String key = url.getPath().substring(1);
                amazonS3.deleteObject(bucketName, key);
            }
        }catch (MalformedURLException malformedURLException){
            throw new NoSuchElementFoundException(ApiErrorCodes.INVALID_INPUT.getErrorCode(), ApiErrorCodes.INVALID_INPUT.getErrorMessage());
     }
    }
}