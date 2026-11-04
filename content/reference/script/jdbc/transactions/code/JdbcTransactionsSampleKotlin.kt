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
import java.sql.Connection

class JdbcTransactionsSampleKotlin {

  init {
//#transaction
val transfer =
  exec(
    jdbc("Begin").begin(),
    jdbc("Debit")
      .preparedStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?")
      .params("#{amount}", "#{from}"),
    jdbc("Credit")
      .preparedStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?")
      .params("#{amount}", "#{to}"),
    jdbc("Commit").commit()
  )
//#transaction

//#rollback
jdbc("Rollback").rollback()
//#rollback

//#options
jdbc("Begin")
  .begin()
  // see Connection.setTransactionIsolation
  .transactionIsolation(Connection.TRANSACTION_SERIALIZABLE)
  // see Connection.setReadOnly
  .readOnly()
//#options

//#group
val timedTransfer = group("Transfer").on(transfer)
//#group
  }
}
