package com.example.app.service;

import com.example.cosmos.service.FileMetadataService;
import com.example.blob.service.BlobStorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileServiceImpl {
	private FileMetadataService fileMetadataService;
	private BlobStorageService blobStorageService;

	public String uploadFile(MultipartFile file) throws IOException {
		String fileId = UUID.nameUUIDFromBytes(file.getBytes()).toString();

		// Move to file Storage
		blobStorageService.uploadBlob(fileId, file.getInputStream(), file.getSize());
		fileMetadataService.saveFileMetadata(
				file.getOriginalFilename(),
				fileId,
				file.getSize(),
				file.getContentType()
		);
		return file.getOriginalFilename();
		// Generate metadata
	}

	public byte[] downloadFile(String id) {
		return blobStorageService.downloadBlob(id);
	}

	public List<String> getFileMetadata(String id) {
		List<String> s = blobStorageService.listBlobs();
		s.add(fileMetadataService.getFileMetadataById(id).toString());
		return s;
		/* return new FileMetaResponse(
				fileMetadata.fileName(),
				fileMetadata.fileType(),
				fileMetadata.fileSize(),
				fileMetadata.fileId(),
				fileMetadata.isArchived(),
				fileMetadata.isDeleted()
		); */
		//return null;
	}

	public String deleteFile(String id) {
		blobStorageService.deleteBlob(id);
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
