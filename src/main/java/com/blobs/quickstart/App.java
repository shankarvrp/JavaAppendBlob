package com.blobs.quickstart;

/**
 * Hello world!
 *
 */
import com.azure.identity.*;
import com.azure.storage.*;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.*;
import com.azure.storage.blob.specialized.*;
import com.azure.storage.blob.models.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");

        /*
         * The default credential first checks environment variables for configuration
         * If environment configuration is incomplete, it will try managed identity
         */
        DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();

        // Azure SDK client builders accept the credential as a parameter
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://sanarayashellstorageacc.blob.core.windows.net/")
                .credential(defaultCredential)
                .buildClient();

        // Create a unique name for the container
        String containerName = "articles";// + java.util.UUID.randomUUID();

        // Create the container and return a container client object
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

        System.out.println("\nListing blobs...");

        // List the blob(s) in the container.
        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            System.out.println("\t" + blobItem.getName());
        }

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        AppendBlobClient appendBlob = containerClient.getBlobClient("abcd.txt").getAppendBlobClient();

        //appendBlob.create(true);
        // java.util.Random random = new java.util.Random();
        // File tempFile1 = DataGenerator.createTempLocalFile("appendblob-", ".tmp",
        // (128 * 1024) + random.nextInt(128 * 1024));

        // appendBlob.getBlobOutputStream().write("asdfdsaf".getBytes());

        String wd = "test12321";
        //
        // AppendBlobClient appendBlobOrig = containerClient.getBlobClient("Article
        // 1.txt").getAppendBlobClient();
        try (InputStream inputStream = new ByteArrayInputStream(wd.getBytes(StandardCharsets.UTF_8))) {
            // Assuming blob is some object that has the appendFromStream method
            appendBlob.appendBlock(inputStream, wd.length());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // FileChannel fileChannel = FileChannel.open(tempFile1.toPath());
        // ByteBuffer fileByteBuffer =
        // ByteBuffer.allocate(Long.valueOf(fileChannel.size()).intValue());
        // fileChannel.read(fileByteBuffer);
        // fileChannel.close();
        // appendBlob.getBlobOutputStream().write(fileByteBuffer.array());
        // fileByteBuffer.clear();

    }
}
