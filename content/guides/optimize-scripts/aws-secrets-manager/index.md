---
title: How to use Gatling with AWS Secrets Manager 
menutitle: AWS Secrets Manager
seotitle: Easily Access and Manage AWS Secret Values with Gatling
description: Learn how to retrieve and access your AWS Secrets Manager values directly within your Gatling scripts for improved security and efficiency during load testing.
lead: Integrate AWS Secrets Manager with your Gatling scripts to securely retrieve and manage secret values after the initialization stage of your load generators.
aliases:
  - /guides/aws-secrets-manager
---

## Use Case

Integrating AWS Secrets Manager with Gatling allows secure access and retrieval of secret values directly within your Gatling scripts. This process is performed only once during the spawning of load generators in the initialization block, ensuring your secrets are handled securely before launching your simulation test.


## Prerequisites

- Utilizing Gatling Enterprise's Private Locations feature. For more information, visit: [Gatling Cloud Installation Guide]({{< ref "/reference/deploy/private-locations/introduction/" >}})
- Using Gatling SDK with Java 1.x or 2.x.


## Configuration

To enable secure access to AWS Secrets Manager, assign an IAM instance profile to your load generators. This profile should grant access permissions for retrieving and describing secrets as detailed below. 
For more information, visit: [Gatling AWS Locations Configuration]({{< ref "reference/deploy/private-locations/aws/configuration/" >}}).

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue",
                "secretsmanager:DescribeSecret"
            ],
            "Resource": "arn:aws:secretsmanager:{region}:{account-id}:secret:{secret-name}"
        }
    ]
}
```

### Batch Retrieval Permissions

{{< alert info >}}
To retrieve secrets in batches, ensure you have the `secretsmanager:GetSecretValue` permission for each secret. Additionally, the `secretsmanager:BatchGetSecretValue` permission is required.
{{< /alert >}}

### Pass Role Policy

{{< alert tip >}}
Next policy is required to pass the created role as an iam-instance-profile to AWS private location.

IAM Instance Profile on AWS private location allow to assign that role to all load generator instances spawned for that private location.
{{< /alert >}}

`GatlingIAMPolicy` allows the Control Plane to pass an IAM instance profile role to a deployed a load generator on EC2.
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "iam:PassRole"
            ],
            "Effect": "Allow",
            "Resource": [
                "arn:aws:iam::{Account}:role/{RoleNameWithPath}"
            ]
        }
    ]
}
```
{{< alert info >}}
The resources are the Amazon Resource Names (ARN) of the IAM instance-profile roles you configured on some of your private locations.
{{< /alert >}}

## Installation

Install the AWS SDK into your Java project using either Maven or Gradle:
- [AWS SDK Maven Installation Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html)
- [AWS SDK Gradle Installation Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-gradle.html)


## Suggested Implementation

Utilize the AWS SDK for Java 2.x to implement the `Get a Secret Value` sample from the AWS Secrets Manager examples. For more detailed examples, visit the [AWS SDK for Java Code Examples](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/java_secrets-manager_code_examples.html).

{{< include-code "aws-secrets-manager" java >}}
