package com.example.demo.fileupload.service.impl;

import com.example.demo.fileupload.controller.response.FileMetaResponse;
import com.example.demo.fileupload.cosmos.dto.FileMetadata;
import com.example.demo.fileupload.service.BlobStorageService;
import com.example.demo.fileupload.service.FileMetadataService;
import com.example.demo.fileupload.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
	private FileMetadataService fileMetadataService;
	private BlobStorageService blobStorageService;

	public String uploadFile(MultipartFile file) throws IOException {
		String fileId = UUID.nameUUIDFromBytes(file.getBytes()).toString();

				// Move to file Storage
		blobStorageService.uploadBlob(fileId,file.toString());

		// Generate metadata
		FileMetadata fileMetadata = fileMetadataService.saveFileMetadata(
				file.getOriginalFilename(),
				fileId,
				file.getSize(),
				file.getContentType()
		);
		return fileMetadata.fileName();
	}

	public byte[] downloadFile(String id) {
		//TODO:
		return null;
	}

	public FileMetaResponse getFileMetadata(String id) {
		FileMetadata fileMetadata = fileMetadataService.getFileMetadataById(id);
		return new FileMetaResponse(
				fileMetadata.fileName(),
				fileMetadata.fileType(),
				fileMetadata.fileSize(),
				fileMetadata.fileId(),
				fileMetadata.isArchived(),
				fileMetadata.isDeleted()
		);
	}

	public String deleteFile(String id) {
		//TODO:
		return fileMetadataService.deleteFileMetadata(id);
	}

	public String moveToArchive(String id) {
		// TODO: move to Archival Storage

		// update with new key, call Encryption class

		// update new key, isArchived Flag in FileMeta data
		return null;
	}

	public String moveToStorage(String id) {
		// TODO: move to Storage

		// update with new key, call Encryption class

		// update new key, isArchived Flag in FileMeta data
		return null;
	}
}
