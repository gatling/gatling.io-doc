/*
 * Copyright 2011-2025 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//#aws-s3-bucket-feeders
import io.gatling.javaapi.core.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class AWSS3BucketFeederSampleJava extends Simulation {

    private final String bucketName = "<bucket-name>";
    private final String objectKey = "<feeder-file-object-key>";
    private final String tempFileName = "<temp-feeder-file-name>"; // ex: feederFile.csv, feederFile.json
    private final String currentAbsolutePath = Paths.get("").toAbsolutePath().toString();
    private final String tempFileAbsolutePath = String.format("%s/%s", this.currentAbsolutePath, tempFileName);

    {
        S3Client s3 = S3Client.create();

        try {
            // Create a request to get the object
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(this.bucketName)
                    .key(this.objectKey)
                    .build();

            // Fetch the object bytes
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getObjectRequest);

            File tempFile = Paths.get(this.tempFileName).toFile();
            tempFile.deleteOnExit();

            // Write to the temp file
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(objectBytes.asByteArray());
            }

            System.out.println("File saved to: " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            s3.close();
        }
    }

    /*
     * - Since the feeder file is created at runtime, we use absolute file paths in
     * order to be able to load the feeder files AFTER Gatling starts.
     *
     * - Use the feeder method that corresponds to the feeder file type: csv,
     * json..etc
     */

    private final FeederBuilder<String> feeder = csv(this.tempFileAbsolutePath).circular();

}
