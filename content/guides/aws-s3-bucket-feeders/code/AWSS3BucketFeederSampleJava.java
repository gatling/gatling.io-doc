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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class AWSS3BucketFeederSampleJava extends Simulation {
    Iterator<Map<String, Object>> feeder;
    {
        String bucket = "<s3-bucket-name>";
        String key = "<feeder-file-name>";
        String fileType = "<csv-or-json>"; // "csv" or "json"

        this.feeder = "json".equalsIgnoreCase(fileType) ? readJsonFromS3(bucket, key) : readCsvFromS3(bucket, key);
    }

    // Custom iterator implementation for circular behavior
    private static class CircularIterator<T> implements Iterator<T>, Iterable<T> {
        private final List<T> items;
        private int currentIndex = 0;

        public CircularIterator(List<T> items) {
            if (items == null || items.isEmpty()) {
                throw new IllegalArgumentException("List cannot be null or empty");
            }
            this.items = items;
        }

        @Override
        public boolean hasNext() {
            return !items.isEmpty();
        }

        @Override
        public T next() {
            if (items.isEmpty()) {
                throw new NoSuchElementException("No elements in the iterator");
            }
            T item = items.get(currentIndex);
            currentIndex = (currentIndex + 1) % items.size(); // Circular logic
            return item;
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }

    private Iterator<Map<String, Object>> readJsonFromS3(String bucket, String key) {
        try (
                S3Client s3 = S3Client.create();
                InputStream inputStream = s3.getObject(
                        GetObjectRequest.builder().bucket(bucket).key(key).build(),
                        ResponseTransformer.toInputStream())) {

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> rows = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

            if (rows.isEmpty()) {
                throw new RuntimeException("JSON file is empty: " + key);
            }

            return new CircularIterator<>(rows);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON from S3: " + key, e);
        }
    }

    private Iterator<Map<String, Object>> readCsvFromS3(String bucket, String key) {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (
                S3Client s3 = S3Client.create();
                InputStream inputStream = s3.getObject(
                        GetObjectRequest.builder().bucket(bucket).key(key).build(),
                        ResponseTransformer.toInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new RuntimeException("CSV file is empty: " + key);
            }
            String[] headers = headerLine.split(",");

            reader.lines().forEach(line -> {
                String[] fields = line.split(",");
                if (fields.length < headers.length) {
                    System.err.println("Skipping malformed CSV line: " + line);
                    return;
                }

                Map<String, Object> map = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    map.put(headers[i].trim(), fields[i].trim());
                }
                rows.add(map);
            });

        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV from S3: " + key, e);
        }

        if (rows.isEmpty()) {
            throw new RuntimeException("CSV file contains no data: " + key);
        }

        return new CircularIterator<>(rows);
    }

}
