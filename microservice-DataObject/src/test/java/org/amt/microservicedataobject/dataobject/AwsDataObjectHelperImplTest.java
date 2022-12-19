package org.amt.microservicedataobject.dataobject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class AwsDataObjectHelperImplTest {

    final static String TEST_KEY = "test";
    final static String NOT_FOUND_KEY = "notFound";
    final AWSDataObjectHelperImpl helper = new AWSDataObjectHelperImpl(new AwsServiceConfigurator.Builder().build());
    static File testFile;
    static File testFile2;
    static File notFoundFile;

    @BeforeAll
    public static void setUp() {
        testFile = new File("src/test/resources/testImage.jpg");
        testFile2 = new File("src/test/resources/testImage2.jpg");
        notFoundFile = new File("src/test/resources/notFoundFile.jpg");
    }

    @Test
    void testDeleteShouldRemoveFileFromDataObject() throws IDataObjectHelper.DataObjectHelperException {
        // Given
        helper.add(TEST_KEY, testFile);

        // When
        helper.delete(TEST_KEY);

        // Then
        assertFalse(helper.exists(TEST_KEY));
    }

    @Test
    void testDeleteShouldThrowAnExceptionWhenTheFileIsNotFound() {
        assertThrows(IDataObjectHelper.KeyNotFoundException.class, () -> helper.delete(TEST_KEY));
    }

    @Test
    void testDeleteShouldThrowAnExceptionWhenFileIsNull() {
        assertThrows(NullPointerException.class, () -> helper.delete(null));
    }


    @Test
    void testAddShouldAddFileToDataObject() throws IDataObjectHelper.DataObjectHelperException {

        // Given some helper

        // Then
        helper.add(TEST_KEY, testFile);

        // When
        assertTrue(helper.get(TEST_KEY).length > 0);

        // Clean
        helper.delete(TEST_KEY);
    }

    @Test
    void testAddShouldThrowAnExceptionWhenTheFileIsNotFound() {
        assertThrows(NullPointerException.class, () -> helper.add(TEST_KEY, notFoundFile));
    }

    @Test
    void testAddShouldUpdateFileFromDataObject() throws IDataObjectHelper.DataObjectHelperException {
        // Given
        helper.add(TEST_KEY, testFile);
        byte[] original = helper.get(TEST_KEY);

        // When
        helper.add(TEST_KEY, testFile2);

        // Then
        assertNotEquals(original, helper.get(TEST_KEY));

        // Clean
        helper.delete(TEST_KEY);
    }

    @Test
    void testAddShouldThrowAnExceptionWhenFileIsNull() {
        assertThrows(NullPointerException.class, () -> helper.add(TEST_KEY, null));
    }

    @Test
    void testAddShouldThrowAnExceptionWhenKeyIsNull() {
        assertThrows(NullPointerException.class, () -> helper.add(null, testFile));
    }

    @Test
    void testExistsShouldReturnTrueWhenFileExists() throws IDataObjectHelper.DataObjectHelperException {
        // Given
        helper.add(TEST_KEY, testFile);

        // When
        boolean exists = helper.exists(TEST_KEY);


        // Then
        assertTrue(exists);

        // Clean
        helper.delete(TEST_KEY);
    }

    @Test
    void testExistsShouldReturnFalseWhenFileDoesNotExist() throws IDataObjectHelper.DataObjectHelperException {
        // Given an empty DataObject

        // When
        boolean exists = helper.exists(NOT_FOUND_KEY);

        // Then
        assertFalse(exists);
    }

    @Test
    void testExistsShouldThrowAnExceptionWhenKeyIsNull() {
        assertThrows(NullPointerException.class, () -> helper.exists(null));
    }

    @Test
    void testGetUrlShouldReturnUrl() throws IDataObjectHelper.DataObjectHelperException {
        // Given
        helper.add(TEST_KEY, testFile);

        // When
        URL url = helper.getUrl(TEST_KEY);

        // Then
        assertNotNull(url);

        // Clean
        helper.delete(TEST_KEY);
    }

    @Test
    void testGetUrlShouldThrowAnExceptionWhenTheFileIsNotFound() {
        assertThrows(IDataObjectHelper.KeyNotFoundException.class, () -> helper.getUrl(NOT_FOUND_KEY));
    }

    @Test
    void testGetUrlShouldThrowAnExceptionWhenKeyIsNull() {
        assertThrows(NullPointerException.class, () -> helper.getUrl(null));
    }

    @Test
    void testListObjectsShouldReturnListOfKeys() throws IDataObjectHelper.DataObjectHelperException {
        // Given
        helper.add(TEST_KEY, testFile);

        // When
        Vector<String> keys = helper.listObjects();

        // Then
        assertTrue(keys.size() > 0);

        // Clean
        helper.delete(TEST_KEY);
    }

    @Test
    void testListObjectsShouldReturnCorrectKeys() throws IDataObjectHelper.DataObjectHelperException {
        // Given
        helper.add(TEST_KEY, testFile);

        // When
        Vector<String> keys = helper.listObjects();

        // Then
        assertTrue(keys.contains(TEST_KEY));

        // Clean
        helper.delete(TEST_KEY);
    }
}
