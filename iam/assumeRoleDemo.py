import boto3
import pprint
import os
import sys
import json

DEBUG = True
KEYID = 'AccessKeyId'
SECKEY = 'SecretAccessKey'
SESSION = 'SessionToken'

SECRET="arn:aws:secretsmanager:us-east-1:875670104278:secret:erictest-Ono6wP"

if(DEBUG):
    pp = pprint.PrettyPrinter()


def assumeRole(role):
    """
    Assume an IAM role
    :param role: arn of role that will be assumed.
    :return: credentials
    """
    client = boto3.client('sts')
    response = client.assume_role(
        RoleArn=role,
        RoleSessionName='ea-service'
    )

    return response['Credentials']


def getSecret(secret, creds=None):
    """
    Retrieve secret from AWS secrets manager
    :param secret: Secret name.  Recommend full ARN but partial and friendly names may be acceptable
    :param creds: creds returned from assumed role.  If None, uses default role from profile.
    :return:
    """
    if(creds is None):
        client = boto3.client('secretsmanager', region_name='us-east-1')
    else:
        client = boto3.client('secretsmanager',
                               region_name='us-east-1',
                               aws_access_key_id=creds[KEYID],
                               aws_secret_access_key=creds[SECKEY],
                               aws_session_token = creds[SESSION])

    return client.get_secret_value(
        SecretId=secret
    )

    return json.loads(bsecret['SecretString'])

def callerIdentity(creds=None):
    """
    Returns caller identity for creds.
    :param creds: credentials or None(default profile)
    :return: caller identity
    """

    # print assumed role
    if(creds is None):
        client = boto3.client('sts',
                              region_name='us-east-1')
    else:
        client = boto3.client('sts',
                              region_name='us-east-1',
                              aws_access_key_id=creds[KEYID],
                              aws_secret_access_key=creds[SECKEY],
                              aws_session_token=creds[SESSION])

    return client.get_caller_identity()

#AWS_ROLE = os.getenv('AWS_ROLE')
AWS_ROLE="arn:aws:iam::558046099615:role/ericrole"
if (AWS_ROLE is None):
    print("echo Must set AWS_ROLE environment variable.")
    sys.exit(1)

creds = assumeRole(AWS_ROLE)

if (DEBUG):
    print("Assumed Role")
    pp.pprint(json.dumps(callerIdentity(creds)))
    print("Default role")
    pp.pprint(json.dumps(callerIdentity()))

secret = getSecret(SECRET, creds)

if (DEBUG):
    print("Secret Value")
    pp.pprint(json.loads(secret['SecretString']))