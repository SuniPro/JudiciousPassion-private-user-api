package com.suni.api.jpprivateuserapi.service.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.suni.api.jpprivateuserapi.response.ErrorCode;
import com.suni.api.jpprivateuserapi.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@PropertySource("classpath:application-private.properties")
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile image) throws ErrorResponse {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new ErrorResponse(ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) {
        this.validateImageFileExtention(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            try {
                throw new ErrorResponse(ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
            } catch (ErrorResponse ex) {
                throw new RuntimeException(ex);
            }
        } catch (ErrorResponse e) {
            throw new RuntimeException(e);
        }
    }


    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            try {
                throw new ErrorResponse(ErrorCode.NO_FILE_EXCEPTION);
            } catch (ErrorResponse e) {
                throw new RuntimeException(e);
            }
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            try {
                throw new ErrorResponse(ErrorCode.INVALID_FILE_EXCEPTION);
            } catch (ErrorResponse e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String uploadImageToS3(MultipartFile image) throws ErrorResponse, IOException {

        InputStream is = null;
        ByteArrayInputStream byteArrayInputStream = null;

        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; //변경된 파일 명

        try {

            is = image.getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + extention);
            metadata.setContentLength(bytes.length);
            byteArrayInputStream = new ByteArrayInputStream(bytes);

            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, "profile/" + s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest); // put image to S3
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (AmazonS3Exception e) {
            // AWS S3 관련 에러에 대한 자세한 정보 로깅
            log.error("S3 업로드 실패. AWS 오류 코드: {}, 오류 메시지: {}", e.getErrorCode(), e.getMessage());
            log.error("AWS 요청 ID: {}, S3 버킷: {}, 객체 키: {}", e.getRequestId(), bucketName, s3FileName);
            throw new ErrorResponse(ErrorCode.PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    public String getThumbnailPath(String path) {
        return amazonS3.getUrl(bucketName, path).toString();
    }

    public byte[] downloadFile(String file) {

        S3Object s3Object = amazonS3.getObject(bucketName, file);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            // e.printStackTrace();
            throw new IllegalStateException("aws s3 다운로드 error");
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    private static String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }

    public String deleteFile(String fileName) {
        amazonS3.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }

    public void deleteImageFromS3(String imageAddress) throws ErrorResponse {
        String key = getKeyFromImageAddress(imageAddress);
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            throw new ErrorResponse(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    private String getKeyFromImageAddress(String imageAddress) throws ErrorResponse {
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        }catch (MalformedURLException e){
            throw new ErrorResponse(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }
}
