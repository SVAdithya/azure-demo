package com.example.demo.fileupload.service;

import com.example.demo.fileupload.controller.response.FileMetaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface FileService {
	String uploadFile(MultipartFile file) throws IOException;

	byte[] downloadFile(String id);
	String deleteFile(String id);
	FileMetaResponse getFileMetadata(String id);
}
