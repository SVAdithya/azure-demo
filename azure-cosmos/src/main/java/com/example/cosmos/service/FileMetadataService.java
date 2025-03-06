package com.example.cosmos.service;

import com.example.cosmos.repo.FileMetadataRepository;
import com.example.cosmos.repo.dto.Details;
import com.example.cosmos.repo.dto.FileMetadata;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FileMetadataService {
	private FileMetadataRepository fileMetadataRepository;

	public FileMetadata saveFileMetadata(String name, String uuid, long size, String type) {
		FileMetadata fileMetadata = generateFileMetadata(name, uuid, size, type);
		return fileMetadataRepository.save(fileMetadata);
	}

	public FileMetadata getFileMetadataById(String id) {
		Optional<FileMetadata> metadata = fileMetadataRepository.findById(id);
		return metadata.orElse(null);
	}

	public String deleteFileMetadata(String id) {
		fileMetadataRepository.deleteById(id);
		return "Deleted "+ id;
	}

	private static FileMetadata generateFileMetadata(String name, String uuid, long size, String type) {
		return new FileMetadata(
				name,
				uuid,
				size,
				type,
				"",
				new Details(
						ZonedDateTime.now().toString(),
						"",
						ZonedDateTime.now().toString(),
						"",
						null
				),
				"",
				true,
				false,
				false
		);
	}
}
