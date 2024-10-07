package com.example.demo.fileupload.cosmos.repo;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.example.demo.fileupload.cosmos.dto.FileMetadata;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends CosmosRepository<FileMetadata, String> {
}
