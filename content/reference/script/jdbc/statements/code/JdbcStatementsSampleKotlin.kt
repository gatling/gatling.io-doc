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

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.jdbc.JdbcDsl.*
import java.sql.JDBCType

class JdbcStatementsSampleKotlin {

  init {
//#statement
jdbc("Count orders").statement("SELECT COUNT(*) FROM orders")
// the SQL can be dynamic, here the name of the table
jdbc("Count rows").statement("SELECT COUNT(*) FROM #{table}")
jdbc("Count rows").statement { session -> "SELECT COUNT(*) FROM " + session.getString("table") }
//#statement

//#prepared-statement
jdbc("Find user")
  .preparedStatement("SELECT * FROM users WHERE id = ? AND status = ?")
  // Strings are Gatling Expression Language, other values are bound as is
  .params("#{userId}", "ACTIVE")
//#prepared-statement

//#param-function
jdbc("Find next user")
  .preparedStatement("SELECT * FROM users WHERE id = ?")
  // computed from the Session
  .params(param { session -> session.getLong("userId") + 1 })
//#param-function

//#typed-params
jdbc("Insert order")
  .preparedStatement("INSERT INTO orders (label, amount, placed_on, paid) VALUES (?, ?, ?, ?)")
  .params(
    "#{label}",
    // a String converted to a BigDecimal, bound with setBigDecimal, which keeps the scale
    param("#{amount}", JDBCType.DECIMAL),
    // a String converted with java.sql.Date.valueOf, for example 2026-11-04
    param("#{date}", JDBCType.DATE),
    // a null bound with setNull and the given type
    param(null, JDBCType.BOOLEAN)
  )
//#typed-params

//#batch
jdbc("Insert orders")
  .preparedStatement("INSERT INTO orders (label, amount) VALUES (?, ?)")
  // a List of rows, each one being a List of values
  .batch("#{rows}")
jdbc("Insert orders")
  .preparedStatement("INSERT INTO orders (label, amount) VALUES (?, ?)")
  .batch { session -> listOf(listOf<Any>("first", 10), listOf<Any>("second", 20)) }
//#batch

//#generated-keys
jdbc("Insert order")
  .preparedStatement("INSERT INTO orders (label) VALUES (?)")
  .params("#{label}")
  // the driver decides which columns to return
  .returnGeneratedKeys()
  .check(generatedKey().saveAs("orderId"))
jdbc("Insert order")
  .preparedStatement("INSERT INTO orders (label) VALUES (?)")
  .params("#{label}")
  // the given columns
  .returnGeneratedKeys("ID")
  .check(generatedKey("ID").saveAs("orderId"))
//#generated-keys

//#callable-statement
jdbc("Transfer")
  .callableStatement("{call transfer(?, ?, ?)}")
  .params("#{from}", "#{to}", outParam(JDBCType.INTEGER))
  // the value of the OUT parameter at index 3
  .check(outValue(3).shouldBe(0))
jdbc("Increment")
  .callableStatement("{call increment(?)}")
  .params(inOutParam("#{counter}", JDBCType.INTEGER))
  .check(outValue(1).saveAs("counter"))
//#callable-statement
  }
}
