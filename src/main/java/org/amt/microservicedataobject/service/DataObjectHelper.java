package org.amt.microservicedataobject.service;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Vector;

/**
 * Interface to upload/download files on a system
 * @author De Bleser Dimitri
 * @author Peer Vincent
 * @author Nelson Jeanreneaud
 */
public interface DataObjectHelper {

    /**
     * Upload a file *
     * @param fileName given file name
     * @param file to uplead
     */
    void add(String fileName, File file) throws NullPointerException, DataObjectHelperException;

    /**
     * List files in the container *
     * @return file names
     */
    Vector<String> listObjects() throws DataObjectHelperException;

    /**
     * Get file content*
     * @param fileName to get
     * @return File content
     */
    byte[] get(String fileName) throws DataObjectHelperException;

    /**
     * Delete a file in the container *
     * @param fileName to delete
     */
    void delete(String fileName) throws DataObjectHelperException;

    /**
     * Get a public url to pointing to a given file *
     *
     * @param fileName     to get
     * @param linkDuration duration of the link
     * @return Url to the file
     */
    URL getUrl(String fileName, Duration linkDuration) throws DataObjectHelperException;

    /**
     * @return The container's name
     */
    String getName();

    /**
     * Chceks if the file exists
     * @param fileName to check
     * @return true if the file exists
     */
    boolean exists(String fileName) throws DataObjectHelperException;


    class DataObjectHelperException extends Exception {
        public DataObjectHelperException(String message) {
            super(message);
        }
    }

    class InvalidParamException extends DataObjectHelperException {
        public InvalidParamException(String message) {
            super(message);
        }
    }

    class ServiceException extends DataObjectHelperException {
        public ServiceException(String message) {
            super(message);
        }
    }

    class ClientException extends DataObjectHelperException {
        public ClientException(String message) {
            super(message);
        }
    }

    class DataObjectException extends DataObjectHelperException {
        public DataObjectException(String message) {
            super(message);
        }
    }

    class DataObjectNotFoundException extends DataObjectException {
        public DataObjectNotFoundException(String message) {
            super(message);
        }
    }

    class KeyNotFoundException extends DataObjectException {
        public KeyNotFoundException(String message) {
            super(message);
        }
    }

    class AccessDeniedException extends DataObjectException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }
}
