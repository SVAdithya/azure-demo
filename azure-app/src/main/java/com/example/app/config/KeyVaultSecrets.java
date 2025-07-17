package com.example.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyVaultSecrets {

    @Value("${azure-cosmos-uri}")
    private String cosmosUri;

    @Value("${azure-cosmos-key}")
    private String cosmosKey;

    @Value("${azure-storage-connection-string}")
    private String storageConnectionString;

    public String getCosmosUri() {
        return cosmosUri;
    }

    public String getCosmosKey() {
        return cosmosKey;
    }

    public String getStorageConnectionString() {
        return storageConnectionString;
    }
}
