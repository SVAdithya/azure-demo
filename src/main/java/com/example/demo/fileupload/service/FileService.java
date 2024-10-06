package com.example.demo.fileupload.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface FileService {
	String uploadFile(MultipartFile file) throws IOException;

	byte[] downloadFile(String id);
	String deleteFile(String id);
	List<String> getFileMetadata(String id);
}
