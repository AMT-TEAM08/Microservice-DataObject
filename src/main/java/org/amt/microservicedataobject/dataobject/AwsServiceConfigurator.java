package org.amt.microservicedataobject.dataobject;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.util.Objects;

/**
 * Class to set Aws profile details
 *
 * @author De Bleser Dimitri
 * @author Peer Vincent
 * @author Nelson Jeanreneaud
 */
public class AwsServiceConfigurator {
    private static final Region DEFAULT_REGION = Region.EU_WEST_2;
    private final Region region;
    private final AwsCredentialsProvider credentialsProvider;

    private static boolean isRegionInvalid(String region) {
        return Region.regions().stream().noneMatch(r -> r.equals(Region.of(region)));
    }

    private AwsServiceConfigurator(Region region, AwsCredentialsProvider credentialsProvider) {
        if (region == null) {
            this.region = DEFAULT_REGION;
        } else {
            this.region = region;
        }
        if (credentialsProvider == null) {
            this.credentialsProvider = ProfileCredentialsProvider.create();
        } else {
            this.credentialsProvider = credentialsProvider;
        }
    }

    public Region getRegion() {
        return region;
    }

    public AwsCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {
        private Region region;
        private AwsCredentialsProvider credentialsProvider;

        public Builder withRegion(String region) {
            if (isRegionInvalid(region)) {
                throw new IllegalArgumentException("Invalid region: " + region);
            }
            this.region = Region.of(region);
            return this;
        }
        public Builder withRegion(Region region) {
            Objects.requireNonNull(region);
            this.region = region;
            return this;
        }

        public Builder withCredentialsProvider(AwsCredentialsProvider credentialsProvider) {
            Objects.requireNonNull(credentialsProvider);
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        public Builder withProfile(String profileName) {
            Objects.requireNonNull(profileName);
            this.credentialsProvider = ProfileCredentialsProvider.create(profileName);
            return this;
        }

        public Builder withEnvironmentVariables() {
            this.credentialsProvider = EnvironmentVariableCredentialsProvider.create();
            return this;
        }


        public AwsServiceConfigurator build() {
            return new AwsServiceConfigurator(region, credentialsProvider);
        }
    }

}
