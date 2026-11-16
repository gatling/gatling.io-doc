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
import io.gatling.jdbc.Predef._

class JdbcChecksSampleScala {

//#update-count
jdbc("Close orders")
  .statement("UPDATE orders SET status = 'CLOSED' WHERE placed_on < CURRENT_DATE")
  .check(updateCount.gt(0))
jdbc("Insert orders")
  .preparedStatement("INSERT INTO orders (label) VALUES (?)")
  .batch("#{rows}")
  // one update count per row
  .check(updateCount.findAll.saveAs("counts"))
//#update-count

//#row-count
jdbc("Find users")
  .statement("SELECT * FROM users")
  .check(rowCount.gt(0))
//#row-count

//#column
jdbc("Find users")
  .statement("SELECT id, email FROM users")
  .check(
    // the value in the first row
    column("email").find.saveAs("email"),
    // the values of all the rows
    column("id").findAll.saveAs("userIds"),
    // by index, starting at 1 like JDBC
    column(1).count.gt(0),
    // SQL NULLs are extracted as null
    column("email").find(1).notNull
  )
//#column

//#rows
val chain =
  exec(
    jdbc("Find users")
      .statement("SELECT id, email FROM users")
      .check(rows.saveAs("users"))
  ).foreach("#{users}", "user")(
    exec(
      jdbc("Find orders")
        .preparedStatement("SELECT * FROM orders WHERE user_id = ?")
        .params("#{user.id}")
    )
  )
//#rows

//#generated-key
jdbc("Insert order")
  .preparedStatement("INSERT INTO orders (label) VALUES (?)")
  .params("#{label}")
  .returnGeneratedKeys("id")
  .check(generatedKey("id").saveAs("orderId"))
//#generated-key

//#out-value
jdbc("Count orders")
  .callableStatement("{call count_orders(?, ?)}")
  .params("#{userId}", outParam(java.sql.JDBCType.INTEGER))
  .check(outValue(2).saveAs("orderCount"))
//#out-value

//#check-if
jdbc("Find user")
  .preparedStatement("SELECT * FROM users WHERE id = ?")
  .params("#{userId}")
  .checkIf("#{mustExist}")(rowCount.is(1))
//#check-if
}
