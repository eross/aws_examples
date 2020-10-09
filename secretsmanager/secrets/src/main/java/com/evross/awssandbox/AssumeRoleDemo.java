package com.evross.awssandbox;

import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;


/**
 * Main entry point.
 */
public class AssumeRoleDemo {
    private static StsClient stsClient;
    private static final String ROLESESSIONNAME = "assumerole_demo";
    private static S3Client s3Client;
    private static StsClient stsAssumedClient;


    public AssumeRoleDemo() {
        // Initialize the SDK client outside of the handler method so that it can be reused for subsequent invocations.
        // It is initialized when the class is loaded.
        //s3Client = DependencyFactory.s3Client();
        // Consider invoking a simple api here to pre-warm up the application, eg: dynamodb#listTables
    }

    public static StsAssumeRoleCredentialsProvider assumeRole(String rolearn, String sessionname) throws StsException {

        AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                .roleArn(rolearn)
                .roleSessionName(sessionname)
                .build();

        AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
        Credentials myCreds = roleResponse.credentials();

        StsAssumeRoleCredentialsProvider provider = StsAssumeRoleCredentialsProvider.builder().stsClient(stsClient).refreshRequest(roleRequest).build();

        // Display the time when the temp creds expire
        Instant exTime = myCreds.expiration();
        String tokenInfo = myCreds.sessionToken();

        // Convert the Instant to readable date
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(Locale.US)
                        .withZone(ZoneId.systemDefault());

        formatter.format(exTime);
        System.out.println("The token " + tokenInfo + "  expires on " + exTime);
        return provider;
    }

    public static void main(String[] args) {

        GetCallerIdentityResponse caller = null;
        final java.lang.String USAGE = "\n" +
                "To run this example, supply the role ARN value   \n" +
                "\n" +
                "Ex: AssumeRole <roleArn>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String roleArn = args[0];

        System.out.println("---Start---");
        // Get access to sts with the current role.
        stsClient = Clients.stsClient();
        // Deconstruct the sts identity and display the results.
        try {
            caller = stsClient.getCallerIdentity();
            System.out.println(String.format("Caller identity is %s", caller.toString()));
        } catch (Exception e) {
            throw e;
        }
        // Now assume a new role.  To make this example useful, this should be a very restrictive role with
        // and without s3 list access to demonstrate it.
        StsAssumeRoleCredentialsProvider provider = null;
        try {
            provider = assumeRole(roleArn, ROLESESSIONNAME);
        } catch (Exception e) {
            throw e;
        }

        // Rather than getting the role credentials, a provider is retrieved.  The provider
        // will automatically refresh the credentials as needed over time.
        s3Client = Clients.s3Client(provider);
        // This access to the s3 service is restricted (or elevated) by the new assumed role.
        System.out.println(String.format("Provider is: %s", provider.toString()));
        // These are the temporary access credentials that can be used with non-AWS tools.
        //  -- commented out for security reasons.
        //System.out.println(String.format("ID: %s,  SECRET: %s",
        //        provider.resolveCredentials().accessKeyId(),
        //        provider.resolveCredentials().secretAccessKey()));
        //
        // Get the list of buckets from the s3 service.
        ListBucketsResponse buckets = s3Client.listBuckets();
        List<Bucket> blist = buckets.buckets();
        for (Bucket b : blist) {
            System.out.println(b.name());
        }
        // just for kicks let's get a new sts client that is now running under the assumed role.
        //  You can use this to chain your assumptions  role1->role2->role3...
        stsAssumedClient = Clients.stsAssumeClient(provider);
        // Demonstrate that we really are running under the new role.
        caller = stsAssumedClient.getCallerIdentity();
        System.out.println(String.format("Caller identity is %s", caller.toString()));
        System.out.println("---END---");
    }

}
