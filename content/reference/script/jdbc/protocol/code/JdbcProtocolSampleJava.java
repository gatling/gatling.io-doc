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

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.jdbc.JdbcProtocolBuilder;
import java.time.Duration;
import javax.sql.DataSource;

import static io.gatling.javaapi.core.CoreDsl.*;
//#imports
import static io.gatling.javaapi.jdbc.JdbcDsl.*;
//#imports

class JdbcProtocolSampleJava extends Simulation {

  DataSource someDataSource() {
    return null;
  }

  {
//#protocol-sample
JdbcProtocolBuilder jdbcProtocol = jdbc
  .url("jdbc:postgresql://localhost:5432/shop")
  .credentials("gatling", "secret");

setUp(
  scenario("JDBC").exec(jdbc("Count orders").statement("SELECT COUNT(*) FROM orders")).injectOpen(atOnceUsers(1))
).protocols(jdbcProtocol);
//#protocol-sample

//#properties
jdbc
  .url("jdbc:postgresql://localhost:5432/shop")
  .credentials("gatling", "secret")
  // passed to the driver when opening connections
  .property("ssl", "true")
  .property("sslmode", "verify-full");
//#properties

//#data-source
// a DataSource configured with driver specific settings, not a pooling one
DataSource dataSource = someDataSource();
jdbc
  .dataSource(dataSource)
  // optional, passed to dataSource.getConnection(username, password)
  .credentials("gatling", "secret");
//#data-source

//#pool
jdbc
  .url("jdbc:postgresql://localhost:5432/shop")
  // the number of connections, and the maximum number of statements running concurrently (default: 10)
  .maxConnections(50);
//#pool

//#timeouts
jdbc
  .url("jdbc:postgresql://localhost:5432/shop")
  // how long opening a connection may take, see DriverManager.setLoginTimeout
  .loginTimeout(Duration.ofSeconds(5))
  // how long a statement may run, see Statement.setQueryTimeout
  .queryTimeout(Duration.ofSeconds(10));
//#timeouts

//#fetch-size
jdbc
  .url("jdbc:postgresql://localhost:5432/shop")
  // see Statement.setFetchSize
  .fetchSize(1000);
//#fetch-size
  }
}
