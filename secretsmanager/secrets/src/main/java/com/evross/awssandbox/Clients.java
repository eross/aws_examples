package com.evross.awssandbox;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

public class Clients {

    public static StsClient stsClient(){
        return StsClient.builder().credentialsProvider(ProfileCredentialsProvider.create()).region(Region.US_EAST_1).build();
    }

    public static S3Client s3Client(StsAssumeRoleCredentialsProvider provider){
        return S3Client.builder().credentialsProvider(provider).region(Region.US_EAST_1).build();
    }

    public static StsClient stsAssumeClient(StsAssumeRoleCredentialsProvider provider){
        return StsClient.builder().credentialsProvider(provider).region(Region.US_EAST_1).build();
    }


}
