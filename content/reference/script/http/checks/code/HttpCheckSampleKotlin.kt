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

import io.gatling.javaapi.http.HttpDsl.*

class HttpCheckSampleKotlin {

  init {
http("").get("")
//#status
.check(
  status().saveAs("status")
) //#status
//#currentLocation
.check(
  currentLocation().saveAs("url")
)
//#currentLocation
//#currentLocationRegex
.check(
  // single capture group
  currentLocationRegex("https://(.*)/.*")
    .saveAs("domain"),
  // multiple capture groups with "captureGroups"
  currentLocationRegex("http://foo.com/bar?(.*)=(.*)")
    .captureGroups(2)
    .saveAs("queryParamKeyAndValue")
)
//#currentLocationRegex
//#header
.check(
  header("Content-Encoding").shouldBe("gzip")
)
//#header
//#headerRegex
.check(
  headerRegex("FOO", "foo(.*)bar(.*)baz")
    .captureGroups(2)
    .saveAs("data")
)
//#headerRegex
  }
}
