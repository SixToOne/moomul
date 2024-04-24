package com.cheerup.moomul.global.util;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String saveFile(MultipartFile multipartFile) {
		UUID uuid = UUID.randomUUID();
		StringBuilder fileName = new StringBuilder(uuid.toString());
		fileName.append(multipartFile.getOriginalFilename());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		try {
			amazonS3.putObject(bucket, fileName.toString(), multipartFile.getInputStream(), metadata);
		} catch (IOException e) {
			throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return amazonS3.getUrl(bucket, fileName.toString()).toString();
	}

	public void deleteFile(String imagePath) {
		String splitStr = ".com/";
		String fileName = imagePath.substring(imagePath.lastIndexOf(splitStr) + splitStr.length());

		amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}
}
