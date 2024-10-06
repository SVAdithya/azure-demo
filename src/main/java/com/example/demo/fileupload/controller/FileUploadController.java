package com.example.demo.fileupload.controller;

import com.example.demo.fileupload.controller.response.FileMetaResponse;
import com.example.demo.fileupload.service.impl.FileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/files")
@Tag(name = "File API", description = "Endpoints for file management")
public class FileUploadController {

	private FileServiceImpl fileService;

	@Operation(summary = "Upload a file")
	@ApiResponse(responseCode = "200", description = "File uploaded successfully")
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(
			@Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {
		String fileName;
		try {
			fileName = fileService.uploadFile(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return ResponseEntity.ok(fileName + " File uploaded successfully.");
	}

	@Operation(summary = "Download a file")
	@ApiResponse(responseCode = "200", description = "File downloaded successfully")
	@GetMapping("/download/{id}")
	public ResponseEntity<byte[]> downloadFile(
			@Parameter(description = "ID of the file to download") @PathVariable String id) {
		byte[] fileData = fileService.downloadFile(id);
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(fileData);
	}

	@Operation(summary = "Get file metadata")
	@ApiResponse(responseCode = "200", description = "File metadata retrieved successfully")
	@GetMapping("/{id}/metadata")
	public ResponseEntity<Object> getFileMetadata(
			@Parameter(description = "ID of the file to retrieve metadata for") @PathVariable String id) {
		// FileMetaResponse metadata = fileService.getFileMetadata(id);
		List<String> metadata = fileService.getFileMetadata(id);
		return ResponseEntity.ok(metadata);
	}

	@Operation(summary = "Delete a file")
	@ApiResponse(responseCode = "200", description = "File deleted successfully")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteFile(
			@Parameter(description = "ID of the file to delete") @PathVariable String id) {
		String fileName = fileService.deleteFile(id);
		return ResponseEntity.ok(fileName + "File deleted successfully.");
	}
}

