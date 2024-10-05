package com.example.demo.fileupload.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileMetaResponse {
	private String fileName;
	private String fileType;
	private Long fileSize;
	private String fileId;
	private Boolean isArchive;
	private Boolean isDeleted;
}
