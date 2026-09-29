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

import scala.concurrent.duration._

import io.gatling.core.Predef._

class SharedQueueSampleScala {
  {
//#shared-queue-put
exec(
  sharedQueue("myQueue").put("#{value}"),
  sharedQueue("myQueue").put(1),
  sharedQueue("myQueue").put(session => session("value").as[String])
)
//#shared-queue-put

//#shared-queue-take
exec(sharedQueue("myQueue").take("value"))
//#shared-queue-take

//#shared-queue-take-timeout
exec(sharedQueue("myQueue").take("value").timeout(10.seconds))
//#shared-queue-take-timeout

//#shared-queue-poll
exec(sharedQueue("myQueue").poll("value"))
//#shared-queue-poll

//#shared-queue-size
exec(sharedQueue("myQueue").size("queueSize"))
//#shared-queue-size
  }
}
