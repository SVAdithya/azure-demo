package com.example.demo.fileupload.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ServiceClient-> ContainerClient -> Blob files
 */
@Service
public class BlobStorageService {

	@Value("${azure.storage.connection-string}")
	private String connectionString;
	@Value("${azure.storage.container-name}")
	private String containerName;

	private BlobContainerClient getBlobContainerClient( ) {
		// Create a BlobServiceClient to interact with the service
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
				.connectionString(connectionString)
				.buildClient();

		// Get the container client for the specified container name
		return blobServiceClient.getBlobContainerClient(containerName);
	}


	public void uploadBlob(String filename, InputStream content, long size) {
		getBlobContainerClient()
				.getBlobClient(filename)
				.upload(content, size, false);
	}

	public byte[] downloadBlob(String filename) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getBlobContainerClient().getBlobClient(filename).downloadStream(outputStream);
		return outputStream.toByteArray();
	}

	public void deleteBlob(String filename) {
		getBlobContainerClient().getBlobClient(filename).delete();
	}

	public List<String> listBlobs( ) {
		List<String> filenames = new ArrayList<>();
		for (BlobItem blobItem : getBlobContainerClient().listBlobs()) {
			filenames.add(blobItem.getName());
		}
		return filenames;
	}
}
