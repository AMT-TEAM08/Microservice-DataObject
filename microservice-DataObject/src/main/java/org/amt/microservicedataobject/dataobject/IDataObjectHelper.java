package org.amt.microservicedataobject.dataobject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

/**
 * Interface to upload/download files on a system
 * @author De Bleser Dimitri
 * @author Peer Vincent
 * @author Nelson Jeanreneaud
 */
public interface IDataObjectHelper {

    /**
     * Upload a file *
     * @param fileName given file name
     * @param file to uplead
     * @throws IOException if an error occurs
     */
    void add(String fileName, File file) throws IOException;

    /**
     * List files in the container *
     * @return file names
     */
    Vector<String> listObjects();

    /**
     * Get file content*
     * @param fileName to get
     * @return File content
     * @throws IOException if the file is not found
     */
    byte[] get(String fileName) throws IOException;

    /**
     * Delete a file in the container *
     * @param fileName to delete
     */
    void delete(String fileName);

    /**
     * Get a public url to pointing to a given file *
     * @param fileName to get
     * @return Url to the file
     */
    URL getUrl(String fileName);

    /**
     * @return The container's name
     */
    String getName();

    /**
     * Chceks if the file exists
     * @param fileName to check
     * @return true if the file exists
     */
    boolean exists(String fileName);


}
