package org.amt.microservicedataobject.dataobject;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import static org.junit.jupiter.api.Assertions.*;

public class AwsServiceConfiguratorTest {

    private static final Region DEFAULT_REGION = Region.EU_WEST_2;
    private static final Region TEST_REGION = Region.EU_NORTH_1;
    private static final String TEST_REGION_STRING = "eu-north-1";

    @Test
    public void testBuilderWithRegionShouldSetRegion() {
        // Given
        AwsServiceConfigurator.Builder builder = new AwsServiceConfigurator.Builder();
        AwsServiceConfigurator awsServiceConfigurator;

        // When
        awsServiceConfigurator = builder.withRegion(TEST_REGION).build();

        // Then
        assertEquals(TEST_REGION, awsServiceConfigurator.getRegion());
    }

    @Test
    public void testBuilderWithRegionStringShouldSetRegion() {
        // Given
        AwsServiceConfigurator.Builder builder = new AwsServiceConfigurator.Builder();
        AwsServiceConfigurator awsServiceConfigurator;

        // When
        awsServiceConfigurator = builder.withRegion(TEST_REGION_STRING).build();

        // Then
        assertEquals(TEST_REGION, awsServiceConfigurator.getRegion());
    }

    @Test
    public void testBuilderWithRegionShouldSetRegionToDefault() {
        // Given
        AwsServiceConfigurator.Builder builder = new AwsServiceConfigurator.Builder();
        AwsServiceConfigurator awsServiceConfigurator;

        // When
        awsServiceConfigurator = builder.build();

        // Then
        assertEquals(DEFAULT_REGION, awsServiceConfigurator.getRegion());
    }

    @Test
    public void testBuilderWithRegionInvalidStringShouldThrowIllegalArgumentException() {
        // Given
        AwsServiceConfigurator.Builder builder = new AwsServiceConfigurator.Builder();

        // When
        try {
            builder.withRegion("invalid-region");
            // Then
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid region: invalid-region", e.getMessage());
        }
    }

    @Test
    public void testBuilderWithProfileShouldSetCredentialProvider() {
        // Given
        AwsServiceConfigurator.Builder builder = new AwsServiceConfigurator.Builder();
        AwsServiceConfigurator awsServiceConfigurator;

        // When
        awsServiceConfigurator = builder.withProfile("test").build();

        // Then
        assertNotNull(awsServiceConfigurator.getCredentialsProvider());
    }

    @Test
    public void testBuilderWithCredentialProviderShouldSetCredentialProvider() {
        // Given
        AwsServiceConfigurator.Builder builder = new AwsServiceConfigurator.Builder();
        AwsServiceConfigurator awsServiceConfigurator;
        ProfileCredentialsProvider profileCredentialsProvider = ProfileCredentialsProvider.create();

        // When
        awsServiceConfigurator = builder.withCredentialsProvider(profileCredentialsProvider).build();

        // Then
        assertEquals(profileCredentialsProvider, awsServiceConfigurator.getCredentialsProvider());
    }

    @Test
    public void testBuilderWithEnvironmentVariableCredentialProviderShouldSetCredentialProvider() {
        // Given
        AwsServiceConfigurator.Builder builder = new AwsServiceConfigurator.Builder();
        AwsServiceConfigurator awsServiceConfigurator;

        // When
        awsServiceConfigurator = builder.withEnvironmentVariables().build();

        // Then
        assertNotNull(awsServiceConfigurator.getCredentialsProvider());
    }
}
