package com.example.demo.fileupload.service;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlobStorageService {

//	@Value("${azure.storage.connection-string}")
//	private String connectionString;
//	@Value("${azure.storage.container-name}")
//	private String containerName;
//
//	private BlobContainerClient getBlobContainerClient() {
//		return new BlobClientBuilder()
//				.connectionString(connectionString)
//				.containerName(containerName)
//				.buildClient().getContainerClient();
//	}

	public void uploadBlob(String blobName, String content) {
		InputStream dataStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		//getBlobContainerClient().getBlobClient(blobName).upload(dataStream, content.length(), true);
	}

	public String downloadBlob(String blobName) {
		//return getBlobContainerClient().getBlobClient(blobName).downloadContent().toString();
		return null;
	}

	public void deleteBlob(String blobName) {
		//getBlobContainerClient().getBlobClient(blobName).delete();
	}

	public List<String> listBlobs() {
		List<String> blobNames = new ArrayList<>();
//		for (BlobItem blobItem : getBlobContainerClient().listBlobs()) {
//			blobNames.add(blobItem.getName());
//		}
		return blobNames;
	}
}
