package com.example.demo.fileupload.cosmos.dto;

import com.azure.spring.data.cosmos.core.mapping.Container;
import org.springframework.data.annotation.Id;

@Container(containerName = "files")
public record FileMetadata(
		String fileName,
		@Id
		String fileId,
		Long fileSize,
		String fileType,
		String fileLocation,
		Details details,
		String fileKey,
		Boolean isEncrypted,
		Boolean isArchived,
		Boolean isDeleted
) {
}
