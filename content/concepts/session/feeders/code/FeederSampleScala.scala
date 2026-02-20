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

import io.gatling.core.Predef._
import io.gatling.core.feeder._

class FeederSampleScala {
  {
val feeder: FeederBuilder = null
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
feed(feeder, session => session("numberOfRecords").as[Int])
//#feed-multiple

//#queue
// default behavior, can be omitted
csv("foo").queue
//#queue

//#shuffle
csv("foo").shuffle
//#shuffle

//#random
csv("foo").random
//#random

//#circular
csv("foo").circular
//#circular
  }

  {
//#feeder-in-memory
// using an array (implicit conversion)
Array(
  Map("foo" -> "foo1", "bar" -> "bar1"),
  Map("foo" -> "foo2", "bar" -> "bar2"),
  Map("foo" -> "foo3", "bar" -> "bar3")
)

// using a IndexedSeq (implicit conversion)
IndexedSeq(
  Map("foo" -> "foo1", "bar" -> "bar1"),
  Map("foo" -> "foo2", "bar" -> "bar2"),
  Map("foo" -> "foo3", "bar" -> "bar3")
)
//#feeder-in-memory
  }

  {
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

  {
//#unzip
csv("foo.csv.zip").unzip
//#unzip
  }

  {
//#shard
csv("foo.csv").shard
//#shard
  }

  {
//#json-feeders
jsonFile("foo.json")
jsonUrl("http://me.com/foo.json")
//#json-feeders
  }

  {
//#jdbc-imports
// beware: you need to import the jdbc module
import io.gatling.jdbc.Predef._
//#jdbc-imports

//#jdbc-feeder
jdbcFeeder("databaseUrl", "username", "password", "SELECT * FROM users")
//#jdbc-feeder
  }

  {
//#sitemap-imports
// beware: you need to import the http module
import io.gatling.http.Predef._
//#sitemap-imports

//#sitemap-feeder
// beware: you need to import the http module
sitemap("/path/to/sitemap/file")
//#sitemap-feeder
  }

  {
//#redis-imports
// beware: you need to import the redis module
import io.gatling.redis.Predef._
import com.redis._
//#redis-imports
//#redis-LPOP
val redisPool =
  new RedisClientPool("localhost", 6379)

// use a list, so there's one single value per record, which is here named "foo"
redisFeeder(redisPool, "foo")
// identical to above, LPOP is the default
redisFeeder(redisPool, "foo").LPOP
//#redis-LPOP

//#redis-SPOP
// read data using SPOP command from a set named "foo"
redisFeeder(redisPool, "foo").SPOP
//#redis-SPOP

//#redis-SRANDMEMBER
// read data using SRANDMEMBER command from a set named "foo"
redisFeeder(redisPool, "foo").SRANDMEMBER
//#redis-SRANDMEMBER

//#redis-RPOPLPUSH
// read data using RPOPLPUSH command from a list named "foo" and atomically store in list named "bar"
redisFeeder(redisPool, "foo", "bar").RPOPLPUSH
// identical to above but we create a circular list by using the same keys
redisFeeder(redisPool, "foo", "foo").RPOPLPUSH
//#redis-RPOPLPUSH
  }

  {
//#transform
csv("myFile.csv").transform {
  case ("attributeThatShouldBeAnInt", string) => string.toInt
}
//#transform
  }

  {
//#records
val records: Seq[Map[String, Any]] = csv("myFile.csv").readRecords
//#records
  }

  {
//#recordsCount
val recordsCount = csv("myFile.csv").recordsCount
//#recordsCount
  }

  {
//#random-imports
import scala.util.Random
//#random-imports
//#random-mail-generator
val feeder = Iterator.continually {
  Map("email" -> s"${Random.alphanumeric.take(20).mkString}@foo.com")
}
//#random-mail-generator
  }
}
