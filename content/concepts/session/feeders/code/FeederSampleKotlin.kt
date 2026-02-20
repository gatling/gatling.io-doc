/*
 * Copyright 2011-2026 GatlingCorp (https://gatling.io)
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

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.jdbc.JdbcDsl.*
import io.gatling.javaapi.redis.*
import io.gatling.javaapi.redis.RedisDsl.*
import org.apache.commons.lang3.RandomStringUtils

class FeederSampleKotlin {

  init {
val feeder = csv("foo")
//#feed-keyword
// called directly
feed(feeder)
// attached to a scenario or an exec
scenario("scn").feed(feeder)
//#feed-keyword

//#feed-multiple
// feed 2 records at once
feed(feeder, 2)
// feed a number of records that's defined as the "numberOfRecords" attribute
// stored in the session of the virtual user
feed(feeder, "#{numberOfRecords}")
// feed a number of records that's computed dynamically from the session
// with a function
feed(feeder) { session -> session.getInt("numberOfRecords") }
//#feed-multiple

//#queue
// default behavior, can be omitted
csv("foo").queue()
//#queue

//#shuffle
csv("foo").shuffle()
//#shuffle

//#random
csv("foo").random()
//#random

//#circular
csv("foo").circular()
//#circular
  }

  init {
//#feeder-in-memory
// using an array
arrayFeeder(arrayOf(
  mapOf("foo" to "foo1"),
  mapOf("foo" to "foo2"),
  mapOf("foo" to "foo3")
))

// using a List
listFeeder(listOf(
  mapOf("foo" to "foo1"),
  mapOf("foo" to "foo2"),
  mapOf("foo" to "foo3")
))
//#feeder-in-memory
  }

  init {
//#sep-values-feeders
// use a comma separator
csv("foo.csv")
// use a tabulation separator
tsv("foo.tsv")
// use a semicolon separator
ssv("foo.ssv")
// use a custom separator
separatedValues("foo.txt", '#')
//#sep-values-feeders
  }

  init {
//#unzip
csv("foo.csv.zip").unzip()
//#unzip
  }

  init {
//#shard
csv("foo.csv").shard()
//#shard
  }

  init {
//#json-feeders
jsonFile("foo.json")
jsonUrl("http://me.com/foo.json")
//#json-feeders
  }

  init {
/*
//#jdbc-imports
// beware: you need to import the jdbc module
import static io.gatling.javaapi.jdbc.JdbcDsl.*;
//#jdbc-imports
*/

//#jdbc-feeder
jdbcFeeder("databaseUrl", "username", "password", "SELECT * FROM users")
//#jdbc-feeder
  }

  init {
/*
//#sitemap-imports
// beware: you need to import the http module
import static io.gatling.javaapi.http.HttpDsl.*;
//#sitemap-imports
*/

//#sitemap-feeder
sitemap("/path/to/sitemap/file")
//#sitemap-feeder
  }

  init {
/*
//#redis-imports
// beware: you need to import the redis module
import io.gatling.javaapi.redis.*
import io.gatling.javaapi.redis.RedisDsl.*
//#redis-imports
*/

//#redis-LPOP
val redisPool =
  RedisClientPool("localhost", 6379)

// use a list, so there's one single value per record, which is here named "foo"
redisFeeder(redisPool, "foo")
// identical to above, LPOP is the default
redisFeeder(redisPool, "foo").LPOP()
//#redis-LPOP

//#redis-SPOP
// read data using SPOP command from a set named "foo"
redisFeeder(redisPool, "foo").SPOP()
//#redis-SPOP

//#redis-SRANDMEMBER
// read data using SRANDMEMBER command from a set named "foo"
redisFeeder(redisPool, "foo").SRANDMEMBER()
//#redis-SRANDMEMBER

//#redis-RPOPLPUSH
// read data using RPOPLPUSH command from a list named "foo" and atomically store in list named "bar"
redisFeeder(redisPool, "foo", "bar").RPOPLPUSH()
// identical to above but we create a circular list by using the same keys
redisFeeder(redisPool, "foo", "foo").RPOPLPUSH()
//#redis-RPOPLPUSH
}

  init {
//#transform
csv("myFile.csv").transform { key, value ->
  if (key.equals("attributeThatShouldBeAnInt")) Integer.valueOf(value) else value
}
//#transform
  }

  init {
//#records
val records = csv("myFile.csv").readRecords()
//#records
  }

  init {
//#recordsCount
val recordsCount = csv("myFile.csv").recordsCount()
//#recordsCount
  }

  init {
/*
//#random-imports
import org.apache.commons.lang3.RandomStringUtils
//#random-imports
*/

//#random-mail-generator
val feeder = generateSequence {
  val email = RandomStringUtils.insecure().nextAlphanumeric(20) + "@foo.com"
  mapOf("email" to email)
}.iterator()
//#random-mail-generator
  }
}
