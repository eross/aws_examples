package com.evross.awssandbox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import software.amazon.awssdk.core.exception.SdkException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssumeRoleDemoTest {

    // To test with valid arn, set appropriately and make sure your AWS Profile credentials are set.
    private static boolean isArnValid = false;
    private static String ValidArn = "arn:aws:iam::1234567890:role/mydemorole";
    @Test
    public void handleRequest_shouldThrowException() {
        // use bogus arn--throws sts exception.
        Assertions.assertThrows(SdkException.class, () -> {
            AssumeRoleDemo function = new AssumeRoleDemo();
            String[] args = new String[]{"arn:aws:iam::123456789999:role/somerole"};
            function.main(args);
        });
    }

    @Test
    public void handleRequest_valid() {
        // insert a valid ARN here.
        if (isArnValid) {
            AssumeRoleDemo function = new AssumeRoleDemo();
            String[] args = new String[]{ValidArn};
            function.main(args);
        } else {
            System.out.println("handleRequest_valid():  Invalid -- ARN.  Test disabled.");
        }
    }
}
