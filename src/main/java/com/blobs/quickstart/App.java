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

        System.err.println("---------- Config Values ----------------");
        System.err.println("TENANT_ID :: " + TENANT_ID);
        System.err.println("CLIENT_ID :: " + CLIENT_ID);
        System.err.println("CLIENT_SECRET :: " + CLIENT_SECRET);
        System.err.println("KEY_VAULT :: " + KEY_VAULT);
        System.err.println("KEY_VAULT_SECRET_NAME :: " + KEY_VAULT_SECRET_NAME);

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
                .endpoint("https://sanarayashellstorageacc.blob.core.windows.net/")
                .sasToken(retrievedSecret.getValue())
                .buildClient();

        // Get the container and append to an existing blob
        String containerName = "articles";
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        AppendBlobClient appendBlob = containerClient.getBlobClient("abcd1.txt").getAppendBlobClient();

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
