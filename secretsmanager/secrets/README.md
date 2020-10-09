# AWS Assume Role
This is an example of how to assume roles and use them using the AWS SDK v2

## Prerequisites
- Java 1.8+
- Apache Maven

## Development

Using access rights defined in the default profile, assume a permitted role and then
list the buckets in S3 using that new role

#### Building the project
```
mvn clean install
```

#### Execute the tests.
```
mvn package
```

The tests run with an invalid ARN.  Modify AssumeRoleDemoTest to add your valid
arn and enable full function execution.

