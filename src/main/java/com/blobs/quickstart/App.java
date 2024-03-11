package com.blobs.quickstart;

import com.azure.identity.*;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("App started!");

        /**
         * Read Config values
         */
        String TENANT_ID = System.getenv("TENANT_ID");
        String CLIENT_ID = System.getenv("CLIENT_ID");
        String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
        String KEY_VAULT = System.getenv("KEY_VAULT");
        String KEY_VAULT_SECRET_NAME = System.getenv("KEY_VAULT_SECRET_NAME");

        String STORAGE_ACC_ENDPOINT = System.getenv("STORAGE_ACC_ENDPOINT");
        String CONTAINER_NAME = System.getenv("CONTAINER_NAME");
        String BLOB_NAME = System.getenv("BLOB_NAME");

        System.err.println("---------- Config Values ----------------");
        System.err.println("TENANT_ID :: " + TENANT_ID);
        System.err.println("CLIENT_ID :: " + CLIENT_ID);
        System.err.println("CLIENT_SECRET :: " + CLIENT_SECRET);
        System.err.println("KEY_VAULT :: " + KEY_VAULT);
        System.err.println("KEY_VAULT_SECRET_NAME :: " + KEY_VAULT_SECRET_NAME);

        System.err.println("STORAGE_ACC_ENDPOINT :: " + STORAGE_ACC_ENDPOINT);
        System.err.println("CONTAINER_NAME :: " + CONTAINER_NAME);
        System.err.println("BLOB_NAME :: " + BLOB_NAME);
        System.err.println("---------- Config Values ----------------");

        /**
         * Get secret from KeyVault to access Storage account
         */
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .tenantId(TENANT_ID)
                .build();

        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(KEY_VAULT)
                .credential(clientSecretCredential)
                .buildClient();

        KeyVaultSecret retrievedSecret = secretClient.getSecret(KEY_VAULT_SECRET_NAME);
        System.out.println("Your secret's value is '" + retrievedSecret.getValue() + "'.");

        /**
         * Get a file in Blob storage and update some data using the SAS Key obtained
         * above
         */

        // Azure SDK client builders accept the credential as a parameter
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACC_ENDPOINT)
                .sasToken(retrievedSecret.getValue())
                .buildClient();

        // Get the container and append to an existing blob
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        AppendBlobClient appendBlob = containerClient.getBlobClient(BLOB_NAME).getAppendBlobClient();

        // Append some dummy data
        String wd = "\n" + "Entry" + System.currentTimeMillis();
        try (InputStream inputStream = new ByteArrayInputStream(
                wd.getBytes(StandardCharsets.UTF_8))) {
            // Assuming blob is some object that has the appendFromStream method
            appendBlob.appendBlock(inputStream, wd.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
