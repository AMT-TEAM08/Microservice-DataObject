package org.amt.microservicedataobject.service.aws;

import org.amt.microservicedataobject.service.DataObjectHelper;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Vector;


/**
 * Implementation of {@link DataObjectHelper} using AWS services
 * @author De Bleser Dimitri
 * @author Peer Vincent
 * @author Nelson Jeanreneaud
 */
public class AwsDataObjectHelperImpl implements DataObjectHelper {

    private final static String BUCKET = "amt.team08.diduno.education";
    private final S3Client s3;

    /**
     * Constructor
     */
    public AwsDataObjectHelperImpl(AwsServiceConfigurator awsServiceConfigurator) {
        s3 = S3Client.builder()
                .region(awsServiceConfigurator.getRegion())
                .credentialsProvider(awsServiceConfigurator.getCredentialsProvider())
                .build();
    }

    /**
     * Bucket name getter
     * @return String containing the bucket name
     */
    public String getName() {
        return BUCKET;
    }

    /**
     * Close the S3Client
     */
    public void close() {
        s3.close();
    }

    /**
     * List files contained in the bucket
     * @return Objects names
     */
    public Vector<String> listObjects() throws DataObjectHelperException {
        Vector<String> keys = new Vector<>();
        ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(BUCKET)
                .build();

        try {
            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects)
                keys.add(myValue.key());

            return keys;
        } catch (NoSuchBucketException e) {
            throw new DataObjectNotFoundException("Bucket not found" + e.getMessage());
        } catch (S3Exception e) {
            throw new DataObjectHelperException("Error listing objects" + e.getMessage());
        }
    }

    /**
     * Upload a file to the bucket
     * @param targetFileName uploaded file name
     * @param file to be uploaded
     */
    public void add(String targetFileName, File file) throws NullPointerException, DataObjectHelperException {
        Objects.requireNonNull(targetFileName, "targetFileName must not be null");
        Objects.requireNonNull(file, "file must not be null");

        if (!file.exists()) {
            throw new NullPointerException("File does not exist");
        }

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(targetFileName)
                .build();

        try {
            // Upload file to bucket
            s3.putObject(objectRequest, RequestBody.fromFile(file));
        } catch (S3Exception e) {
            throw new DataObjectException("Error adding object" + e.getMessage());
        } catch (AwsServiceException e) {
            throw new ServiceException("Error adding object" + e.getMessage());
        } catch (SdkClientException e) {
            throw new ClientException("Error adding object" + e.getMessage());
        }
    }

    /**
     * Get the file content
     * @param fileName to be downloaded
     * @return file content in byte array
     */
    public byte[] get(String fileName) throws DataObjectHelperException {
        Objects.requireNonNull(fileName, "fileName must not be null");

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .build();
        byte[] imageInByte;

        try {
            InputStream stream = s3.getObject(getObjectRequest);

            imageInByte = IoUtils.toByteArray(stream);

            return imageInByte;
        } catch (NoSuchKeyException e) {
            throw new KeyNotFoundException("Object not found" + e.getMessage());
        } catch (InvalidObjectStateException e) {
            throw new AccessDeniedException("Access denied" + e.getMessage());
        } catch (S3Exception e) {
            throw new DataObjectException("Error getting object" + e.getMessage());
        } catch (AwsServiceException e) {
            throw new ServiceException("Error getting object" + e.getMessage());
        } catch (SdkClientException e) {
            throw new ClientException("Error getting object" + e.getMessage());
        } catch (IOException e) {
            throw new DataObjectException("Error while reading object" + e.getMessage());
        }
    }

    /**
     * Delete a file in the bucket*
     * @param fileName to delete
     */
    public void delete(String fileName) throws DataObjectHelperException {
        Objects.requireNonNull(fileName, "fileName must not be null");

        try {
            if (exists(fileName)) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(fileName)
                        .build();

                s3.deleteObject(deleteObjectRequest);
            } else {
                throw new KeyNotFoundException("Object not found");
            }
        } catch (S3Exception e) {
            throw new DataObjectException("Error deleting object" + e.getMessage());
        } catch (AwsServiceException e) {
            throw new ServiceException("Error deleting object" + e.getMessage());
        } catch (SdkClientException e) {
            throw new ClientException("Error deleting object" + e.getMessage());
        }
    }

    /**
     * Checks if a file exists in the bucket
     * @param fileName to check
     * @return true if the file exists, false otherwise
     */
    public boolean exists(String fileName) throws DataObjectHelperException {
        Objects.requireNonNull(fileName, "fileName must not be null");
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .build();
        try {
            s3.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            throw new DataObjectException("Error checking object" + e.getMessage());
        } catch (AwsServiceException e) {
            throw new ServiceException("Error checking object" + e.getMessage());
        } catch (SdkClientException e) {
            throw new ClientException("Error checking object" + e.getMessage());
        }
    }

    /**
     * Request a publicly accessible url to a file *
     *
     * @param fileName     of the requested file
     * @param linkDuration duration of the link validity
     * @return Url to linking to a file
     */
    public URL getUrl(String fileName, Duration linkDuration) throws DataObjectHelperException {
        Objects.requireNonNull(fileName, "fileName must not be null");
        Objects.requireNonNull(linkDuration, "linkDuration must not be null");

        if (linkDuration.isNegative() || linkDuration.isZero()) {
            throw new InvalidParamException("linkDuration must be positive");
        }

        if(!exists(fileName))
            throw new KeyNotFoundException("Object not found");

        // Create an S3Presigner using the default region and credentials.
        // This is usually done at application startup, because creating a presigner can be expensive.
        S3Presigner presigner = S3Presigner.create();

        // Create a GetObjectRequest to be pre-signed
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .build();

        // Create a GetObjectPresignRequest to specify the signature duration
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(linkDuration)
                .getObjectRequest(getObjectRequest)
                .build();

        // Generate the presigned request
        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);

        // It is recommended to close the S3Presigner when it is done being used, because some credential
        // providers (e.g. if your AWS profile is configured to assume an STS role) require system resources
        // that need to be freed. If you are using one S3Presigner per application (as recommended), this
        // usually is not needed.
        presigner.close();

        // Return URL.
        return presignedGetObjectRequest.url();
    }
}
