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

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import java.util.*
import java.util.stream.Collectors

class GroupingFeederKotlin {
//#grouping-feeder
val records: List<Map<String, Any>> = csv("file.csv").readRecords()

val recordsGroupedByUsername =
  records
    .stream()
    .collect(Collectors.groupingBy { record: Map<String, Any> -> record["username"] as String })

val groupedRecordsFeeder =
  recordsGroupedByUsername
    .values
    .stream()
    .map { Collections.singletonMap("userRecords", it as Any) }
    .iterator()

val chain =
  feed(groupedRecordsFeeder)
    .foreach("#{userRecords}", "record").on(
      exec(http("request")["#{record.url}"])
    )
//#grouping-feeder
}