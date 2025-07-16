package com.example.cosmos.repo;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import com.example.cosmos.repo.dto.FileMetadata;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends CosmosRepository<FileMetadata, String> {
}
