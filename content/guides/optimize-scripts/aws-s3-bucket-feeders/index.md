---
title: Use S3 Bucket Feeders for large scale data
seotitle: Easily Fetch Data from S3 Feeders with Gatling
menutitle: AWS S3 Feeders
description: Store your feeder files in S3 and access them from your Gatling scripts.
lead: Learn how to use AWS S3 buckets to store and fetch simulation feeder data.
aliases:
  - /guides/aws-s3-bucket-feeders
---

## Introduction

When working with large feeder files or files containing sensitive data, storing them locally in your repository is not ideal. Instead, you can store them in an S3 bucket and access the feeder data directly from your Gatling script. This guide explains how to correctly retrieve data from feeders stored in an S3 bucket.

## Prerequisites

This guide requires load generators using:

- [Private locations on Gatling Enterprise](https://docs.gatling.io/reference/install/cloud/private-locations/introduction/)
- [Local test execution](https://docs.gatling.io/tutorials/scripting-intro/#run-the-simulation-locally-for-debugging)
- Gatling SDK with Java 11, 17, 21, or 22.

{{< alert info >}}
This setup does not work with managed locations on Gatling Enterprise.
{{< /alert >}}

## Configuration

### Local configuration

If you are running your scripts locally, make sure to have AWS credentials set up on your local machine.

### Private Locations

To enable secure access to AWS S3, you need to:

- Assign an IAM instance profile to your load generators. This profile should grant access permissions for retrieving objects from the designated S3 bucket. For more information, see [Gatling AWS Locations Configuration](https://docs.gatling.io/reference/install/cloud/private-locations/aws/configuration/).
  ```json
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Sid": "VisualEditor0",
        "Effect": "Allow",
        "Action": "s3:GetObject",
        "Resource": "arn:aws:s3:::<feeders-bucket-name>/*"
      }
    ]
  }
  ```
- Attach a new policy to the control plane role that allows it to pass the previously created IAM instance profile, to the load generators.
  - Create the following policy:
    ```json
    {
      "Version": "2012-10-17",
      "Statement": [
        {
          "Action": ["iam:PassRole"],
          "Effect": "Allow",
          "Resource": ["arn:aws:iam::{Account}:role/{RoleNameWithPath}"]
        }
      ]
    }
    ```
  - Attach it to the control plane role.

## Installation

Install the AWS SDK into your Java project using either Maven or Gradle:

- [AWS SDK Maven Installation Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html)
- [AWS SDK Gradle Installation Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-gradle.html)

## Suggested implementation

**Notes:**

- The example below retrieves the feeder file from the S3 bucket, temporarily stores it in the project's root directory, and deletes it once the simulation is completed.

{{< include-code "aws-s3-bucket-feeders" java >}}

## Test your S3 feeder

Pass the `feeder` object to your scenario and ensure that the feeder data is retrieved correctly, following the required behavior.
