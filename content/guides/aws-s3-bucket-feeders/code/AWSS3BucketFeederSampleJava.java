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
import static io.gatling.javaapi.core.CoreDsl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import io.gatling.javaapi.core.FeederBuilder;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class AWSS3BucketFeederSampleJava extends Simulation {

    private final String bucketName = "<bucket-name>";
    private final String objectKey = "<feeder-file-object-key>";
    private final Path feederFile = loadFeeder();

    private Path loadFeeder() {
        try (S3Client s3 = S3Client.create()) {
            // Create a request to get the object
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(this.bucketName)
                    .key(this.objectKey)
                    .build();

            Path tempFeederFile = Files.createTempFile("hello", ".file");
            tempFeederFile.toFile().deleteOnExit();

            try (ResponseInputStream<GetObjectResponse> inputStream = s3.getObject(getObjectRequest)) {
                // Write to the temp file
                Files.copy(inputStream, tempFeederFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return tempFeederFile;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * - Since the feeder file is created at runtime, we use absolute file paths in
     * order to be able to load the feeder files AFTER Gatling starts.
     *
     * - Use the feeder method that corresponds to the feeder file type: csv,
     * json..etc
     */

    FeederBuilder<String> feeder = csv(feederFile.toFile().getAbsolutePath()).random();

}
