package com.example.blob.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.example.app.config.KeyVaultSecrets;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private KeyVaultSecrets keyVaultSecrets;
	@Value("${azure.storage.container-name}")
	private String containerName;

	/*private BlobContainerClient getBlobContainerClient() {
	   return new BlobClientBuilder()
			   .connectionString(keyVaultSecrets.getStorageConnectionString())
			   .containerName(containerName)
			   .buildClient().getContainerClient();
   }*/
	private BlobContainerClient getBlobContainerClient( ) {
		// Create a BlobServiceClient to interact with the service
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
				.connectionString(keyVaultSecrets.getStorageConnectionString())
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
