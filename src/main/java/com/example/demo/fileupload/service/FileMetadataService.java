package com.example.demo.fileupload.service;

import com.example.demo.fileupload.cosmos.dto.Details;
import com.example.demo.fileupload.cosmos.dto.FileMetadata;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FileMetadataService {

	//private FileMetadataRepository fileMetadataRepository;

	public FileMetadata saveFileMetadata(String name, String uuid, long size, String type) {

		FileMetadata fileMetadata = generateFileMetadata(name, uuid, size, type);
		return null; //fileMetadataRepository.save(fileMetadata);
	}

	public FileMetadata getFileMetadataById(String id) {
		//Optional<FileMetadata> metadata = fileMetadataRepository.findById(id);
		//return metadata.orElse(null);
		return null;
	}

	public String deleteFileMetadata(String id) {
		// TODO: is_deleted
		//fileMetadataRepository.deleteById(id);
		return null;
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
