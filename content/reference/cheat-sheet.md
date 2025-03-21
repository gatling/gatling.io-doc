---
title: "Cheat-Sheet"
description: "Quick reference guides and sheet in one page"
lead: "Quick reference guides and sheet in one page"
date: 2021-04-20T18:30:56+02:00
type: cheat-sheet
draft: true
content:

  - title: Scenario definition
    description: Describe your users behaviour
    sections:

      - title: Scenario
        functions:

          - keyword: scenario
            description: Declares a new scenario
            syntax:
              - signature: (name)
                description: <i>name</i> is the scenario name in the reports

      - title: Base structures
        functions:

          - keyword: exec
            description: Execution Step
            syntax:
              - signature: (action)
                description: The <i>action</i> that will be executed at this step.
              - signature: (chain...)
                description: Inserts the <i>chain(s)</i> at this point of the scenario
              - signature: (Session => Session)
                description: Perform an operation upon the session at this step

          - keyword: group
            description: Group related requests together
            syntax:
              - signature: (groupName){chain}
                description: Actions from <i>chain</i> are part of the group named <i>groupName</i>

          - keyword: pause
            description: Declares a fixed or random pause in the scenario
            syntax:
              - signature: (dur unit)
                description: The pause will last <i>duration</i>
              - signature: (duration, timeunit)
                description: Declares a pause <i>duration</i> with a specific <i>timeunit</i>
              - signature: (dur unit, dur unit)
                description: Declares a pause duration randomly selected between the two durations
              - signature: (duration, duration, timeunit)
                description: Declares a pause randomly selected between the two durations with a specific time <i>timeunit</i>

          - keyword: pace
            description: Pause which adjusts its wait time depending on how long the chained action took
            syntax:
              - signature: (dur unit)
                description: The pause will last <i>duration</i>
              - signature: (duration, timeunit)
                description: Declares a pause <i>duration</i> with a specific <i>timeunit</i>
              - signature: (dur unit, dur unit)
                description: Declares a pause duration randomly selected between the two durations
              - signature: (duration, duration, timeunit)
                description: Declares a pause randomly selected between the two durations with a specific time <i>timeunit</i>

          - keyword: rendezVous
            description: Pauses users until all other users have reached the rendez-vous point
            syntax:
              - signature: (userNumber)
                description: Number of users to wait for

      - title: Loops
        functions:

          - keyword: repeat
            description: Repeats a part of the scenario for a given number of times
            syntax:
              - signature: (times){chain}
                description: The <i>chain</i> is repeated the specified number of <i>times</i>
              - signature: (times, counterName){chain}
                description: The <i>chain</i> is repeated the specified number of <i>times</i> with a forced counter name

          - keyword: during
            description: Repeats a part of the scenario for a given duration
            syntax:
              - signature: (dur unit){chain}
                description: The <i>chain</i> is repeated for at least <i>dur unit</i>
              - signature: (dur unit, counterName){chain}
                description: Same as above with forced counter name

          - keyword: asLongAs
            description: Repeats a part of the scenario as long as a condition holds
            syntax:
              - signature: (condition){chain}
                description: The <i>chain</i> is repeated as long as the condition is satisfied
              - signature: (condition, counterName){chain}
                description: Same as above with forced counter name

          - keyword: foreach
            description: Repeats a part of the scenario for each element of a sequence
            syntax:
              - signature: (sequence, elementName){chain}
                description: The <i>chain</i> is repeated for every element in the <i>sequence</i> and the current element is stored in the Session under the <i>elementName</i> key
              - signature: (sequence, elementName, counterName){chain}
                description: Same as above with forced counter name

          - keyword: doWhile
            description: Similar to asLongAs but the condition is evaluated after the loop
            syntax:
              - signature: (condition){chain}
                description: The <i>chain</i> is repeated as long as the condition is satisfied
              - signature: (condition, counterName){chain}
                description: Same as above with forced counter name

          - keyword: asLongAsDuring
            description: Similar to <code>asLongAs</code> but with an extra duration condition
            syntax:
              - signature: (condition, duration, counterName){chain}
                description: The <i>chain</i> is repeated as long as the condition is satisfied and the duration is not reached

          - keyword: doWhileDuring
            description: Similar to <code>doWhile</code> but with an extra duration condition
            syntax:
              - signature: (condition, duration, counterName){chain}
                description: The <i>chain</i> is repeated as long as the condition is satisfied and the duration is not reached

          - keyword: forever
            description: Repeats a part of the scenario forever
            syntax:
              - signature: (counterName){chain}
                description: The <i>chain</i> is repeated forever. Forcing the counter name with <i>counterName</i> is optional

      - title: Conditions
        functions:

          - keyword: doIf
            description: Executes a chain when a condition is satisfied
            syntax:
              - signature: (condition){chain}
                description: If the result of the condition is true, then <i>chain</i> is executed.
              - signature: (Session => Boolean){chain}
                description: If the result of the function <i>Session => Boolean</i> is true, then <i>chain</i> is executed.

          - keyword: doIfEquals
            description: Executes a chain when two values are equal
            syntax:
              - signature: (expected, actual){chain}
                description: If expected equals actual, then <i>chain</i> is executed

          - keyword: doIfOrElse
            description: Executes a chain when a condition is satisfied, another if not
            syntax:
              - signature: (condition){chain}{otherChain}
                description: If the result of the condition is true, then <i>chain</i> is executed, else <i>otherChain</i> is executed.
              - signature: (Session => Boolean){chain}{otherChain}
                description: If the result of the function <i>Session => Boolean</i> is true, then <i>chain</i> is executed, else <i>otherChain</i> is executed.

          - keyword: doIfEqualsOrElse
            description: Executes a chain when two values are equal, another if not
            syntax:
              - signature: (expected, actual){chain}{otherChain}
                description: If expected equals actual, then <i>chain</i> is executed, else <i>otherChain</i> is executed.

          - keyword: doSwitch
            description: Executes a chain based on a key
            syntax:
              - signature: (key){switch}
                description: |
                  Switch is selected through the matching of a key with the evaluation of the passed expression.
                  If no switch is selected, switch is bypassed.

          - keyword: doSwitchOrElse
            description: Executes a chain based on a key or a fallback chain
            syntax:
              - signature: (key){switch}{chain}
                description: |
                  Switch is selected through the matching of a key with the evaluation of the passed expression.
                  If no switch is selected, <i>chain</i> is executed

          - keyword: randomSwitch
            description: >
              Current user will continue in one of the specified weighted chains.
              Can be used to simulate simple Markov chains
            syntax:
              - signature: (Map[_,Chain])
                description: The <i>Map</i> contains the distribution of the randomSwitch. Each chain is weighted with a percentage. Make sure the sum of weights does not exceed 100.

          - keyword: randomSwitchOrElse
            description: >
              Current user will continue in one of the specified weighted chains.
              Can be used to simulate simple Markov chains.
              Fallback available if no switch is selected
            syntax:
              - signature: (Map[_,Chain]){chain}
                description: The <i>Map</i> contains the distribution of the randomSwitch. Each chain is weighted with a percentage. Make sure the sum of weights does not exceed 100. If no switch is selected then <i>chain</i> is executed.

          - keyword: uniformRandomSwitch
            description: Current user will continue in one of the uniform distributed chains. Can be used to simulate simple Markov chains
            syntax:
              - signature: (chains...)
                description: The list of <i>chains</i>.

          - keyword: roundRobinSwitch
            description: A round-robin strategy is used to determine the chain to be executed.
            syntax:
              - signature: (chains...)
                description: The list of <i>chains</i>.

      - title: Errors handling
        functions:

          - keyword: tryMax
            description: Retries a chain until it succeeds
            syntax:
              - signature: (times){chain}
                description: <i>chain</i> will be repeated until either all checks in it are OK; or the chain has been repeated <i>times</i> times.

          - keyword: exitBlockOnFail
            description: Stops block execution if a check fails
            syntax:
              - signature: "{chain}"
                description: If a check in <i>chain</i> fails, its execution is stopped and the scenario continues.

          - keyword: exitHere
            description: Exit the scenario at this point.

          - keyword: exitHereIf
            description: Exit the scenario at this point if the condition holds.
            syntax:
              - signature: "(condition: Expression[Boolean])"
                description: Exit if the condition evaluates to true

          - keyword: exitHereIfFailed
            description: Exit the scenario at this point if this virtual user encountered an error during execution.

  - title: Simulation configuration
    description: Tune your simulation
    sections:

      - title: Time
        functions:

          - keyword: pauses
            description: Allows to configure pauses. Can also be defined at the scenario level
            syntax:
              - signature: (disablePauses)
                description: Disable the pauses for the simulation
              - signature: (constantPauses)
                description: Default value. Durations are precisely those filled in the <i>pause(duration)</i> elements.
              - signature: (exponentialPauses)
                description: Pause durations are on average those filled in the <i>pause(duration)</i> elements and follow an exponential distribution.
              - signature: (customPauses(pauseDur))
                description: Pauses durations are computed by the provided <i>pauseDur</i>. In this case the filled duration is bypassed
              - signature: (uniformPauses(plusOrMinus))
                description: Pause durations are on average those filled in the <i>pause(duration)</i> elements and follow an uniform distribution.

          - keyword: maxDuration
            description: Set a duration limit on your simulation
            syntax:
              - signature: (maxDuration)
                description: The maximum duration of the simulation

      - title: Throttling
        functions:

          - keyword: throttle
            description: >
              Allows to reason in terms of request per second and not in terms of users.
              Can also be defined at the scenario level.
              Throttle can take one to many building blocks described below.
            syntax:
              - signature: (reachRps(target) in (time unit))
                description: Target a throughput with a ramp in a given <i>time</i>
              - signature: (jumpToRps(target))
                description: Jump immediately to a given targeted throughput
              - signature: (holdFor(duration))
                description: Hold the current throughput for a given duration
            example: |
              setUp(...)
                .throttle(
                  reachRps(100) in (10 seconds),
                  holdFor(10 minute)
                )

  - title: Feeder definition
    description: Inject data in your scenario
    sections:

      - title: Feeder types
        functions:

          - keyword: csv
            description: Declares a feeder from a CSV file
            syntax:
              - signature: (fileName)
                description: <i>fileName</i> is the name of the file under the <i>resources</i> folder containing the comma separated values to be injected

          - keyword: tsv
            description: Declares a feeder from a TSV file
            syntax:
              - signature: (fileName)
                description: <i>fileName</i> is the name of the file under the <i>resources</i> folder containing the tabulation separated values to be injected

          - keyword: ssv
            description: Declares a feeder from a SSV file
            syntax:
              - signature: (fileName)
                description: <i>fileName</i> is the name of the file under the <i>resources</i> folder containing the semi-colon separated values to be injected

          - keyword: jsonFile
            description: Declares a feeder from a JSON file
            syntax:
              - signature: (fileName)
                description: <i>fileName</i> is the name of the file under the <i>resources</i> folder containing JSON file to be injected

          - keyword: jsonUrl
            description: Declares a feeder from a JSON url
            syntax:
              - signature: (url)
                description: <i>url</i> is the url returning the JSON array to be injected

          - keyword: jdbcFeeder
            description: Declares a feeder from database records
            syntax:
              - signature: (dbURL,user,pass,sql)
                description: The feeder will load its values thanks to the <i>sql</i> request made on the database located at the JDBC URL <i>dbURL</i> as <i>user</i>/<i>pass</i>.<br>Note that you need to include the right JDBC4 driver in the classpath (lib folder if using the bundle)

          - keyword: redisFeeder
            syntax:
              - signature: (pool, key)
                description: This feeder loads its values from a Redis <i>pool</i> and stores them under <i>key</i> in the session.

      - title: Feeder options
        functions:

          - keyword: batch
            syntax:
              - description: Load data by chunks of 200 records instead of loading all the data in memory (local file based feeders only)
              - signature: (bufferSize)
                description: Load data by chunks of <i>bufferSize</i> records instead of loading all the data in memory (local file based feeders only)

          - keyword: unzip
            description: Decompress feeder file that is compressed with zip or gzip

          - keyword: shard
            frontline: true
            description: Distribute data amongst nodes when running a cluster of load generators

          - keyword: readRecords
            description: Return the feeder content as a <code>Seq[Map[String, Any]]</code>

      - title: Feeder strategies
        functions:

          - keyword: queue
            description: This is the default strategy (you don't have to specify it). It takes the values in the feeder as in a queue. If the queue is not long enough, you'll get an error at execution.

          - keyword: random
            description: This strategy chooses values randomly in the feeder. In contrast to a <i>shuffle</i> strategy the same values can be chosen several times.

          - keyword: circular
            description: This strategy takes the values of the feeder in their order, and when it reaches the last, it returns to the first one.

          - keyword: shuffle
            description: This strategy shuffles the values in the feeder but then works like the <i>queue</i> strategy.

  - title: Injection profile
    description: Control how users are injected in your scenario
    sections:

      - title: Open injection steps
        description: I.e. you control virtual users arrival rate
        functions:

          - keyword: atOnceUsers
            description: Injects a specific number of users at the same time
            syntax:
              - signature: (nbUsers)
                description: Injects <i>nbUsers</i>

          - keyword: rampUsers
            description: Injects a given number of users with a linear ramp during a given duration
            syntax:
              - signature: (nbUsers) during(dur unit)
                description: Injects <i>nbUsers</i> during a <i>duration</i>.
                example: rampUsers(10) during(5 seconds)

          - keyword: constantUsersPerSec
            description: Injects users at a constant rate, defined in users per second, during a given duration
            syntax:
              - signature: (nbUsers) during(dur unit)
                description: Injects <i>nbUsers</i> each second for <i>duration</i>.
                example: constantUsersPerSec(10) during(5 seconds)

          - keyword: rampUsersPerSec
            description: Injects users from starting rate to target rate, defined in users per second, during a given duration
            syntax:
              - signature: (rate1) to (rate2) during(dur unit)
                description: Injects users from starting <i>rate1</i> to target <i>rate2</i>, defined in users per second, during a given <i>duration</i>.
                example: rampUsersPerSec(10) to(20) during(10 minutes)

          - keyword: stressPeakUsers
            description: Injects a given number of users following a smooth approximation stretched to a duration
            syntax:
              - signature: (nbUsers) during(dur unit)
                description: Injects a given number of users following a smooth approximation of the Heaviside step function stretched to a given <i>duration</i>

          - keyword: nothingFor
            description: Pauses for a specific duration
            syntax:
              - signature: (dur unit)
                description: Pause for a given <i>duration</i>

          - keyword: incrementUsersPerSec
            description: Meta DSL to write open increment tests (succession of several increasing levels)
            new: true
            syntax:
              - signature: |
                  (incrementUsersPerSec)
                    .times(numberOfSteps)
                    .eachLevelLasting(levelDuration)
                    .separatedByRampsLasting(rampDuration)
                    .startingFrom(initialUsersPerSec)
                description: >
                  Inject a succession of <i>numberOfSteps</i> levels each one during <i>levelDuration</i> and increasing the number of users per sec by <i>incrementUsersPerSec</i> starting from zero
                  or the optional <i>initialUsersPerSec</i> and separated by optional ramps lasting <i>rampDuration</i>

      - title: Closed injection steps
        description: I.e. you control number of concurrent virtual users)
        functions:

          - keyword: constantConcurrentUsers
            description: Maintain a constant number of concurrent users
            syntax:
              - signature: (nbUsers) during(duration)
                description: Maintain <i>nbUsers</i> for <i>duration</i>

          - keyword: rampConcurrentUsers
            description: Scale number of concurrent users
            syntax:
              - signature: (fromNbUsers) to(toNbUsers) during(duration)
                description: Scale from <i>fromNbUsers</i> to <i>toNbUsers</i> during <i>duration</i>

          - keyword: incrementConcurrentUsers
            description: Meta DSL to write closed increment tests (succession of several increasing levels)
            new: true
            syntax:
              - signature: |
                  (incrementConcurrentUsers)
                    .times(numberOfSteps)
                    .eachLevelLasting(levelDuration)
                    .separatedByRampsLasting(rampDuration)
                    .startingFrom(initialConcurrentUsers)
                description: >
                  Inject a succession of <i>numberOfSteps</i> levels each one during <i>levelDuration</i> and increasing the number of users per sec by <i>incrementConcurrentUsers</i> starting from zero
                  or the optional <i>initialConcurrentUsers</i> and separated by optional ramps lasting <i>rampDuration</i>

  - title: Assertions
    description: Check that your results match your expectations
    sections:

      - title: Assertions
        functions:

          - keyword: assertions
            description: Declare the assertions on your simulation
            syntax:
              - signature: (assertions)
                description: The assertions your simulation will be checked against

      - title: Scopes
        functions:

          - keyword: global
            description: Use statistics calculated from all requests

          - keyword: forAll
            description: Use statistics calculated for each individual request.

          - keyword: details
            description: Use statistics calculated from a group or a request
            syntax:
              - signature: (path)
                description: The request or group path

      - title: Statistics
        functions:

          - keyword: responseTime
            description: Target the response time in milliseconds

          - keyword: allRequests
            description: Target the number of requests

          - keyword: failedRequests
            description: Target the number of failed requests

          - keyword: successfulRequests
            description: Target the number of successful requests

          - keyword: requestsPerSec
            description: Target the rate of requests per second

      - title: Response time selectors
        functions:

          - keyword: min
            description: Perform the assertion on the minimum of the stat

          - keyword: max
            description: Perform the assertion on the maximum of the stat

          - keyword: mean
            description: Perform the assertion on the mean of the stat

          - keyword: stdDev
            description: Perform the assertion on the standard deviation of the stat

          - keyword: percentile1
            description: Perform the assertion on the first percentile of the stat

          - keyword: percentile2
            description: Perform the assertion on the second percentile of the stat

          - keyword: percentile3
            description: Perform the assertion on the third percentile of the stat

          - keyword: percentile4
            description: Perform the assertion on the fourth percentile of the stat

          - keyword: percentile
            description: Perform the assertion on the given percentile of the stat
            syntax:
              - signature: (value)
                description: The double value of percentile rank, between 0 and 100

      - title: Count selectors
        functions:

          - keyword: percent
            description: Use the value as a percentage between 0 and 100

          - keyword: count
            description: Perform the assertion on the count of requests

      - title: Assertions conditions
        functions:

          - keyword: lt
            description: Check that the value of the statistic is less than the threshold
            syntax:
              - signature: (threshold)
                description: the statistic's higher bound

          - keyword: lte
            description: Check that the value of the statistic is less than or equal to the threshold
            syntax:
              - signature: (threshold)
                description: the statistic's higher bound

          - keyword: gt
            description: Check that the value of the statistic is greater than the threshold
            syntax:
              - signature: (threshold)
                description: the statistic's lower bound

          - keyword: gte
            description: Check that the value of the statistic is greater than or equal to the threshold
            syntax:
              - signature: (threshold)
                description: the statistic's lower bound

          - keyword: between
            description: Check that the value of the statistic is between two thresholds
            syntax:
              - signature: (thresholdMin, thresholdMax)
                description: the statistic's bounds
              - signature: (thresholdMin,thresholdMax, inclusive = false)
                description: the statistic's bounds, excluded

          - keyword: around
            description: Check that the value of the metric is around a target value plus or minus a given margin.
            syntax:
              - signature: (value, margin)
                description: the statistic's target and bounds margin
              - signature: (value, margin, inclusive = false)
                description: the statistic's bounds, excluded

          - keyword: deviatesAround
            description: Check that metric is around a target value plus or minus a given relative margin
            syntax:
              - signature: (value, relativeMargin)
                description: the statistic's target and bounds margin
              - signature: (thresholdMin, thresholdMax, inclusive = false)
                description: the statistic's bounds, excluded

          - keyword: is
            description: Check that the value of the statistic is equal to the given value
            syntax:
              - signature: (value)
                description: the <i>value</i> the statistic must be equal to

          - keyword: in
            description: Check that the value of statistic is in a sequence
            syntax:
              - signature: (sequence)
                description: the <i>sequence</i> the statistic has to be in

  - title: HTTP Action
    description: Define the HTTP requests sent in your scenario
    sections:

      - title: HTTP
        functions:

          - keyword: http
            description: Declares an HTTP request
            syntax:
              - signature: (name)
                description: <i>name</i> is the request name in the reports

      - title: HTTP verb
        functions:

          - keyword: get
            description: Sets the HTTP method to GET
            syntax:
              - signature: (url)
                description: Sets the <i>url</i> of the HTTP request

          - keyword: post
            description: Sets the HTTP method to POST
            syntax:
              - signature: (url)
                description: Sets the <i>url</i> of the HTTP request

          - keyword: put
            description: Sets the HTTP method to PUT
            syntax:
              - signature: (url)
                description: Sets the <i>url</i> of the HTTP request

          - keyword: delete
            description: Sets the HTTP method to DELETE
            syntax:
              - signature: (url)
                description: Sets the <i>url</i> of the HTTP request

          - keyword: head
            description: Sets the HTTP method to HEAD
            syntax:
              - signature: (url)
                description: Sets the <i>url</i> of the HTTP request

          - keyword: patch
            description: Sets the HTTP method to PATCH
            syntax:
              - signature: (url)
                description: Sets the <i>url</i> of the HTTP request

          - keyword: options
            description: Sets the HTTP method to OPTIONS
            syntax:
              - signature: (url)
                description: Sets the <i>url</i> of the HTTP request

          - keyword: httpRequest
            description: Support for custom HTTP methods
            syntax:
              - signature: (method, url)
                description: Executes <i>method</i> on <i>url</i>

      - title: HTTP options
        functions:

          - keyword: queryParam
            description: Adds a query parameter to the URL
            syntax:
              - signature: (key, value)
                description: http://example.tld?key=value

          - keyword: multivaluedQueryParam
            description: Adds a query parameter to the URL with multiple values
            syntax:
              - signature: (key, value...)
                description: http://example.tld?key=value1&key=value2&key=value3...

          - keyword: queryParamSeq
            description: Adds multiple query parameters to the URL
            syntax:
              - signature: ((key, value)...)
                description: http://example.tld?key1=value1&key2=value2&key3=value3...

          - keyword: queryParamMap
            description: Adds multiple query parameters to the URL
            syntax:
              - signature: (Map[key -> value])
                description: http://example.tld?key1=value1&key2=value2&key3=value3...

          - keyword: header
            description: Adds a header to the request.
            syntax:
              - signature: (key, value)
                description: "Adds <i>key: value</i> as header"

          - keyword: headers
            description: Adds several headers to the request.
            syntax:
              - signature: (Map[key, value])
                description: "Adds every tuple <i>key: value</i> of the map as header"

          - keyword: ignoreProtocolHeaders
            description: Ignore default headers that were defined on the HttpProtocol

          - keyword: requestTimeout
            description: Override the requestTimeout defined in the global configuration
            syntax:
              - signature: (timeout)
                description: A FiniteDuration, eg 3 minutes

          - keyword: sign
            description: Pass a `SignatureCalculator` to sign the request.
            syntax:
              - signature: (signatureCalculator)
                description: Sign the request, typically add a new Authorization header based on request data

          - keyword: signWithOAuth1
            description: Sign the request with OAuth1.
            syntax:
              - signature: (consumerKey, clientSharedSecret, token, tokenSecret)
                description: Sign the request with OAuth1

          - keyword: basicAuth
            description: Sets the credentials for BASIC authentication
            syntax:
              - signature: (user, password)
                description: Will authenticate as <i>user</i> with <i>password</i>

          - keyword: digestAuth
            description: Sets the credentials for DIGEST authentication
            syntax:
              - signature: (user, password)
                description: Will authenticate as <i>user</i> with <i>password</i>

          - keyword: ntlmAuth
            description: Sets the credentials for NTLM authentication
            syntax:
              - signature: (user, password)
                description: Will authenticate as <i>user</i> with <i>password</i>

          - keyword: authRealm
            description: Generic method to add authentication
            syntax:
              - signature: (Realm)
                description: Takes a <i>Realm</i> instance

          - keyword: resources
            description: Allows to fetch resources in parallel in order to emulate the behaviour of a real web browser
            syntax:
              - signature: (request...)
                description: Multiple requests to get resources

          - keyword: disableUrlEncoding
            description: Disable URLEncoding if you're sure the urls you feed to Gatling are already properly encoded.

          - keyword: silent
            description: |
              Forces the request to be silent
              A silent request is issued but not logged nor reported

          - keyword: notSilent
            description: |
              Forces the request to NOT be silent, whatever's defined at protocol level.
              A silent request is issued but not logged nor reported

      - title: HTTP body
        functions:

          - keyword: body
            description: Adds a body from a resource to the request
            syntax:
              - signature: (RawFileBody(path))
                description: Uploads a file as is from the <i>path</i>
              - signature: (ELFileBody(path))
                description: Parses, resolves and then uploads a file containing Gatling EL from the <i>path</i>
              - signature: (StringBody(string))
                description: Sets a string
              - signature: (ByteArrayBody(bytes))
                description: Sets the body from a bytes array
              - signature: (InputStreamBody(stream))
                description: Sets the body from an input stream
              - signature: (PebbleBody(path))
                description: Parses, resolves and then uploads a Pebble template from the <i>path</i>

          - keyword: bodyPart
            description: Add a multipart body to the request
            syntax:
              - signature: (RawFileBodyPart(path))
                description: Uploads a file as is from the <i>path</i>
              - signature: (ELFileBodyPart(path))
                description: Parses, resolves and then uploads a file containing Gatling EL from the <i>path</i>
              - signature: (StringBodyPart(string))
                description: Sets a string
              - signature: (ByteArrayBodyPart(bytes))
                description: Sets the body from a bytes array
              - signature: (PebbleStringBodyPart(path))
                description: Parses, resolves and then uploads a Pebble template from the <i>string</i>
              - signature: (PebbleBodyFilePart(string))
                description: Parses, resolves and then uploads a Pebble template from the <i>path</i>

          - keyword: bodyParts
            description: Add several multipart bodies to the request
            syntax:
              - signature: (parts...)
                description: Where <i>parts</i> is a varag of <i>BodyPart</i>s

          - keyword: formParam
            description: Adds a form parameter to the request body
            syntax:
              - signature: (key, value)
                description: Where <i>key</i> is the name and <i>value</i> is a string
              - signature: (HttpParam)
                description: Where HttpParam is an instance of HttpParam

          - keyword: multivaluedFormParam
            description: Adds a form parameter to the request body with multiple values
            syntax:
              - signature: (key, value...)
                description: Where <i>key</i> is the name of one parameter and <i>value</i> can be a list

          - keyword: formParamSeq
            description: Adds multiple form parameters to the request body
            syntax:
              - signature: ((key, value)...)
                description: Where you can have multiple tuples of <i>key</i> and <i>value</i>

          - keyword: formParamMap
            description: Adds multiple form parameters to the request body
            syntax:
              - signature: (Map[key -> value])
                description: Where you can have a map of multiple <i>key</i> and <i>value</i>

          - keyword: form
            description: Adds a form to the request body
            syntax:
              - signature: (Map[key -> Seq[value]])
                description: Typically saved by a form check

          - keyword: processRequestBody
            description: Allows to process the request body before it is sent to the wire
            syntax:
              - signature: (Body => Body)
                description: Takes a function to transform the request body

          - keyword: transformResponse
            description: Allows to process the response before it is sent to the checks pipeline.
            syntax:
              - signature: ((Response, Session) => Validation[Response]))
                description: Takes a function to transform the Response, possibly based on data from the Session

          - keyword: formUpload
            description: Provides a multi-part encoded request when you need to upload a file corresponding to a form text value.
            example: Email attachment
            syntax:
              - signature: (key, filePath)
                description: Where <i>key</i> is the name of the field and <i>filePath</i> a path to a file located in <i>resources</i>


  - title: Checks
    description: Verifying server responses
    sections:

      - title: Check
        functions:

          - keyword: check
            description: Declare every check that should apply on the action
            syntax:
              - signature: (check...)
                description: Every check you need to make on the response from the server

          - keyword: ignoreProtocolChecks
            description: Ignore default checks that were defined on the HttpProtocol

          - keyword: name
            syntax:
              - signature: (name)
                description: Use <i>name</i> to compute error message when check fails

      - title: CheckIf
        functions:

          - keyword: checkIf
            description: Enslave check execution to a condition
            syntax:
              - signature: "(condition: Expression[Boolean])(thenCheck: Check)"
                description: Only perform <i>thenCheck</i> if <i>condition</i> evaluates to <i>true</i>
              - signature: "(condition: (Response, Session) => Validation[Boolean])(thenCheck: Check)"
                description: Only perform <i>thenCheck</i> if <i>condition</i> return <i>true</i>

      - title: Check extractors
        functions:

          - keyword: find
            description: Specifies that only one value matching the check definition must be extracted
            syntax:
              - description: If used as <i>find</i> check will be applied on the first value found. Note that this is the default behaviour, you don't have to specify it.
              - signature: (occurrence)
                description: Gets the <i>occurrence</i>-th value that matches the check definition.

          - keyword: findAll
            description: Specifies that every value matching the check definition should be extracted for verification. The resulting type is a list of extracted values.

          - keyword: findRandom
            description: Fetches random matches.
            syntax:
              - description: If used as <i>findRandom</i> fetches one single match, returns a single value.
              - signature: "(num: Int)"
                description: Fetches a given number of occurrences, returns a Seq.
              - signature: "(num: Int, failIfLess: Boolean)"
                description: Same as above, but fails if the number of actual matches is less than num.

          - keyword: count
            description: The check will be applied on the size of the list returned by findall

      - title: HTTP checks
        functions:

          - keyword: status
            description: Check the HTTP status code of the response.

          - keyword: currentLocation
            description: Check the url of the landing response.

          - keyword: header
            description: Check the value of a given HTTP header of the response.
            syntax:
              - signature: (headerName)
                description: The check will be applied on the header corresponding to <i>headerName</i>
              - signature: (Session => String)
                description: Same as above, except that the header name is given by the <i>Session => String</i> function.

          - keyword: headerRegex
            description: Apply a regular expression on value of a given HTTP header of the response.
            syntax:
              - signature: (headerName, pattern)
                description: The check will be applied on the headers <i>headerName</i> whose value match the provided <i>pattern</i>.
              - signature: (Session => String)
                description: Same as above, except that the header name is given by the <i>Session => String</i> function.

          - keyword: responseTimeInMillis
            description: Return the response time of this request in milliseconds.

          - keyword: bodyString
            description: Return the full response body String.

          - keyword: bodyBytes
            description: Return the full response body byte array.

          - keyword: bodyLength
            description: Return the length of the response body in bytes (without the overhead of computing the bytes array).

          - keyword: bodyStream
            description: Return an InputStream of the full response body bytes.

          - keyword: substring
            description: Look for occurrences of a given String in the response body String
            syntax:
              - signature: (expression)
                description: Extraction will look for <i>expression</i> indices.

          - keyword: regex
            description: Apply a regular expression, possibly with capture groups, on the response body String.
            syntax:
              - signature: (expression)
                description: Extraction will use a regular expression defined by <i>expression</i>.

          - keyword: xpath
            description: Apply an XPath expression on the response body String.
            syntax:
              - signature: (expression)
                description: Extraction will use an XPath expression defined by <i>expression</i>
              - signature: (expression, namespaces)
                description: If you want to use namespaces in your XPATH expression, you should specify them. The <i>namespaces</i> are specified thanks to a Map[prefix,uri]:<br>Map('c' -> 'http://core', 'f' -> 'http://format').
              - signature: (Session => String)
                description: The expression can be expressed as a <i>Session => String</i> function (with namespaces also)

          - keyword: jsonPath
            description: Apply a JsonPath expression on the response body String.
            syntax:
              - signature: (expression)
                description: jsonPath is similar to XPATH, but for JSON encoded data.

          - keyword: jsonpJsonPath
            description: Apply a JsonPath expression on the JSONP value of the response body String.
            syntax:
              - signature: (expression)
                description: jsonpJsonPath is similar to jsonPath, but for JSONP

          - keyword: jmesPath
            description: Apply a JMESPath expression on the response body String.
            syntax:
              - signature: (expression)
                description: JMESPath is similar to XPATH, but for JSON encoded data.

          - keyword: jsonpJmesPath
            description: Apply a JMESPath expression on the JSONP value of the response body String.
            syntax:
              - signature: (expression)
                description: jsonpJmesPath is similar to jmesPath, but for JSONP

          - keyword: css
            description: Apply a CSS selector on the response body String.
            syntax:
              - signature: (expression)
                description: Uses the <i>css</i> selector to extract the value to be checked.
              - signature: (expression, attr)
                description: Same as above, except that the extracted value is the <i>attr</i> of the selected node.

          - keyword: form
            description: Capture an HTML form in the response body String.
            syntax:
              - signature: (expression)
                description: Uses the <i>css</i> selector to extract the all the field values of an HTML form.

          - keyword: md5
            description: Compute the md5 checksum of the response body.

          - keyword: sha1
            description: Compute the sha1 checksum of the response body.

      - title: Check transformer
        functions:

          - keyword: transform
            description: Transform extracted data before verification.
            syntax:
              - signature: (A => B)
                description: This function is optional. It can be useful sometimes to apply a transformation on the extracted values before validating them. In this function, type <i>A</i> is the type of the find operation. Type B is whatever you want it to be (could be a case class for example)

          - keyword: transformWithSession
            description: Transform extracted data before verification.
            syntax:
              - signature: ((A, Session) => B)
                description: Same as <i>transform</i>, but with read access to the current Session.

          - keyword: transformOption
            description: Transform extracted data before verification.
            syntax:
              - signature: (Option[A] => Validation[Option[B]])
                description: Gives full control over the extracted result, even providing a default value

          - keyword: transformOptionWithSession
            description: Transform extracted data before verification.
            syntax:
              - signature: ((Option[A], Session) => Validation[Option[B]])
                description: Same as <i>transformOption</i>, but with read access to the current Session.

          - keyword: withDefault
            description: Transform extracted data before verification.
            syntax:
              - signature: (expression)
                description: provide default one in case of extracted value absence 

      - title: Check verifiers
        functions:

          - keyword: is
            description: Verify extracted value is equal to expected one.
            syntax:
              - signature: (expected)
                description: if the captured (or transformed) value is equal to <i>expected</i> the check will succeed; it will fail otherwise

          - keyword: not
            description: Verify extracted value is different from to expected one.
            syntax:
              - signature: (expected)
                description: If the captured (or transformed) value is different than <i>expected</i> the check will succeed; it will fail otherwise

          - keyword: exists
            description: Verify extracted value exists. This is the default verification (it does not need to be specified)

          - keyword: notExists
            description: Verify extracted value does not exists.

          - keyword: in
            description: Verify extracted value belongs to a specific set of values.
            syntax:
              - signature: (sequence)
                description: If the captured (or transformed) value is included in the <i>sequence</i> the check will succeed; it will fail otherwise

          - keyword: optional
            description: Noop, never fail.

          - keyword: isNull
            description: Verify extracted value is null (typically JSON null).

          - keyword: notNull
            description: Verify extracted value is not null (typically JSON null).

      - title: Check saver
        functions:

          - keyword: saveAs
            description: If the check succeeds, store in the Session the extracted value(s).
            syntax:
              - signature: (key)
                description: Extracted values get stored in the Session with the key <i>key</i>.


  - title: HTTP Protocol
    description: Mutualize your scenario's code and tune the behaviour of Gatling's HTTP client
    sections:

      - title: Protocol
        functions:

          - keyword: http
            description: Entry point of your HTTP protocol

      - title: Urls
        functions:

          - keyword: baseUrl
            description: Sets the base URL of all relative URLs of the scenario on which the configuration is applied
            syntax:
              - signature: (url)
                description: "The <i>url</i> that will be appended to all relative URLs (ie: not starting with http)"

          - keyword: baseUrls
            description: Same as <i>baseUrl</i> with client-based load balancing.
            syntax:
              - signature: (url...)
                description: Each <i>url</i> will be used randomly to implement client load-balancing.

          - keyword: virtualHost
            description: Overrides the default computed virtual host
            syntax:
              - signature: (name)
                description: "Eg: GET https://mobile.github.com/gatling/gatling instead of GET https://www.github.com/gatling/gatling"

      - title: Proxy
        functions:

          - keyword: proxy
            description: Declares a proxy for all HTTP requetes of the scenario on which the configuration is applied
            syntax:
              - signature: (Proxy)
                description: The requests will pass throught the proxy located within the <i>Proxy</i> object

          - keyword: noProxyFor
            description: Disables the proxy for a certain list of hosts
            syntax:
              - signature: (hosts)
                description: The <i>hosts</i> ignoring the proxy

          - keyword: httpsPort
            description: Uses this if Gatling should use the proxy for HTTPS also
            syntax:
              - signature: (port)
                description: The HTTPS <i>port</i> of the proxy

          - keyword: credentials
            description: If your proxy requires BASIC authentication you can set the credentials with this method
            syntax:
              - signature: (username,password)
                description: The requests will be sent as the user <i>username/password</i>

          - keyword: socks4
            description: If this proxy is a SOCKS4 one

          - keyword: socks5
            description: If this proxy is a SOCKS5 one

      - title: Headers
        functions:

          - keyword: acceptHeader
            description: Sets the Accept header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header.

          - keyword: acceptCharsetHeader
            description: Sets the Accept-Charset header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header

          - keyword: acceptEncodingHeader
            description: Sets the Accept-Encoding header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header

          - keyword: acceptLanguageHeader
            description: Sets the Accept-Language header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header

          - keyword: authorizationHeader
            description: Sets the Authorization header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header

          - keyword: connectionHeader
            description: Sets the Connection header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header

          - keyword: contentTypeHeader
            description: Sets the Content-Type header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header

          - keyword: doNotTrackHeader
            description: Sets the DNT header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header.

          - keyword: userAgentHeader
            description: Sets the User-Agent header for all requests
            syntax:
              - signature: (value)
                description: The <i>value</i> of the header

      - title: Options
        functions:

          - keyword: disableFollowRedirect
            description: Forces the HTTP engine not to follow the redirects. You can also disable it globally on the HttpProtocol

          - keyword: maxRedirects
            description: Avoids infinite redirection loops by specifying a number max of redirects
            syntax:
              - signature: (max)
                description: Where <i>max</i> is the number of max redirects

          - keyword: redirectNamingStrategy
            description: Specify a custom naming strategy for redirected requests
            syntax:
              - signature: (redirectedUri, originalRequestName, redirectCount) => redirectedRequestName
                description: Where <i>redirectedUri</i> is the new Location, <i>originalRequestName</i> is the name of the original request and <i>redirectCount</i> is the number of redirects.

          - keyword: disableAutomaticReferer
            description: Forces Gatling not to set the referer of the requests

          - keyword: disableWarmUp
            description: Disables the warm up request

          - keyword: warmUp
            description: Sets the URL used by Gatling to warm up the HTTP engine
            syntax:
              - signature: (url)
                description: A valid and accessible <i>url</i>

          - keyword: inferHtmlResources
            description: Allows to fetch resources in parallel in order to emulate the behaviour of a real web browser
            syntax:
              - signature: (AllowList)
                description: Fetch all resources matching a pattern in the allow list
              - signature: (AllowList, DenyList)
                description: Fetch all resources matching a pattern in the allow list excepting those in the deny list
              - signature: (Filters)
                description: More generic version taking any kind of filters

          - keyword: nameInferredHtmlResourcesAfterUrlTail
            description: Name requests after the resource's url tail (after last `/`) (default)
            syntax:
              - signature: (AllowList)
                description: Fetch all resources matching a pattern in the allow list
              - signature: (AllowList, DenyList)
                description: Fetch all resources matching a pattern in the allow list excepting those in the deny list
              - signature: (Filters)
                description: More generic version taking any kind of filters

          - keyword: nameInferredHtmlResourcesAfterPath
            description: Name requests after the resource's path

          - keyword: nameInferredHtmlResourcesAfterAbsoluteUrl
            description: Name requests after the resource's absolute url

          - keyword: nameInferredHtmlResourcesAfterRelativeUrl
            description: Name requests after the resource's relative url

          - keyword: nameInferredHtmlResourcesAfterLastPathElement
            description: Name requests after the resource's last path element

          - keyword: nameInferredHtmlResources
            description: Name requests with a custom strategy
            syntax:
              - signature: (Uri => String)
                description: A function to generate the name out of the Uri

          - keyword: maxConnectionsPerHost
            description: Sets the maximum concurrent connections per host per virtual user.
            syntax:
              - signature: (max)
                description: Where <i>max</i> is the number of maximum connections

          - keyword: shareConnections
            description: Allows to share connections among users

          - keyword: enableHttp2
            description: Enable HTTP/2 experimental support

          - keyword: http2PriorKnowledge
            syntax:
              - signature: (Map[String, Boolean])
                description: If virtual users already know if some hosts support HTTP/2 or not

          - keyword: asyncNameResolution
            description: Use async name resolver instead Java's blocking one.

          - keyword: perUserNameResolution
            description: Use per virtual user name resolver instead of a global one. Only enabled if <code>asyncNameResolution</code> is enabled.

          - keyword: hostNameAliases
            description: Configure IP aliases to bypass DNS resolution
            syntax:
              - signature: (Map[String, List[String]])
                description: Map of IP adresses by hostname

          - keyword: localAddress
            description: Binds the sockets from a specific local address instead of the default one
            syntax:
              - signature: (InetAddress)
                description: Address String that will be turned ino a InetAddress

          - keyword: localAddresses
            description: Binds the sockets from a list of specific local addresses instead of the default one
            syntax:
              - signature: (InetAddress...)
                description: Address Strings that will be turned into InetAddresses

          - keyword: useAllLocalAddresses
            description: Binds the sockets from all available local addresses

          - keyword: useAllLocalAddressesMatching
            description: Binds the sockets from all available local addresses matching one of the pattern parameters
            syntax:
              - signature: (String...)
                description: Pattern Strings that will be compiled into Java patterns

          - keyword: perUserKeyManagerFactory
            description: Configure a specific javax.net.ssl.KeyManagerFactory per virtual user
            syntax:
              - signature: (Long => javax.net.ssl.KeyManagerFactory)
                description: Input is the virtual user's id

          - keyword: disableCaching
            description: Disables caching features for some http headers and ETag

          - keyword: disableUrlEncoding
            description: Disable URLEncoding if you're sure the urls you feed to Gatling are already properly encoded.

          - keyword: silentUri
            description: |
              Silence requests that match the pattern
              A silent request is issued but not logged nor reported
            syntax:
              - signature: (String)
                description: A regular expression pattern to match request urls

          - keyword: silentResources
            description: |
              Silence resource requests
              A silent request is issued but not logged nor reported

          - keyword: basicAuth
            description: Sets the credentials for BASIC authentication
            syntax:
              - signature: (user, password)
                description: Will authenticate as <i>user</i> with <i>password</i>

          - keyword: digestAuth
            description: Sets the credentials for DIGEST authentication
            syntax:
              - signature: (user, password)
                description: Will authenticate as <i>user</i> with <i>password</i>

          - keyword: ntlmAuth
            description: Sets the credentials for NTLM authentication
            syntax:
              - signature: (user, password)
                description: Will authenticate as <i>user</i> with <i>password</i>

          - keyword: authRealm
            description: Generic method to add authentication
            syntax:
              - signature: (Realm)
                description: Takes a <i>Realm</i> instance

  - title: WebSockets
    description: Define the WebSocket requests sent in your scenario
    sections:

      - title: WebSocket
        functions:

          - keyword: ws
            description: Declares an WebSocket request
            syntax:
              - signature: (requestName)
                description: Name of the request

      - title: Commons
        functions:

          - keyword: wsName
            description: Allows to deal with several WebSockets per virtual user
            syntax:
              - signature: (requestName)
                description: Gives the requests a name and pass this name on each ws operation

          - keyword: connect
            description: Connects a WebSocket
            syntax:
              - signature: (url)
                description: Enables communication to the given <i>url</i>

          - keyword: onConnected
            description: Pass a chain of actions to be performed on (re-)connecting.
            syntax:
              - signature: (chain)
                description: A chain of actions

          - keyword: close
            description: Closes a WebSocket

          - keyword: sendText
            description: Sends a String message
            syntax:
              - signature: (stringMessage)
                description: Send <i>stringMessage</i> as a string

          - keyword: sendBytes
            description: Sends a byte[] message
            syntax:
              - signature: (bytesMessage)
                description: Send <i>bytesMessage</i> as a bytes array

      - title: Checks
        functions:

          - keyword: await
            description: Creates a blocking check
            syntax:
              - signature: (timeout)
                description: The <i>timeout</i> if check doesn't completes in due time
              - signature: (checks)
                description: The <i>checks</i> applied on expected server messages

          - keyword: checkTextMessage
            description: Expect a WebSocket Text frame that would be validated against some criteria
            syntax:
              - signature: (name)
                description: The <i>name</i> of the expected message to be displayed in response times and failures

          - keyword: checkBinaryMessage
            description: Expect a WebSocket Binary frame that would be validated against some criteria
            syntax:
              - signature: (name)
                description: The <i>name</i> of the expected message to be displayed in response times and failures

          - keyword: silent
            description: Make a check silent so that it has no influence on stats

      - title: Configuration
        functions:

          - keyword: wsBaseUrl
            description: Serves as root that will be prepended to all relative WebSocket urls
            syntax:
              - signature: (url)
                description: Where <i>url</i> is the root url

          - keyword: wsBaseUrls
            description: Serves as round-robin roots that will be prepended to all relative WebSocket urls
            syntax:
              - signature: (url...)
                description: Where <i>url</i> are the root urls

          - keyword: wsReconnect
            description: Automatically reconnect a WebSocket that would have been closed by someone else than the client

          - keyword: wsMaxReconnects
            description: Sets a limit on the number of times a WebSocket will be automatically reconnected
            syntax:
              - signature: (count)
                description: Where <i>count</i> is the number of maximum reconnections url

          - keyword: wsAutoReplyTextFrame
            description: Configure auto reply for specific WebSocket text messages
            syntax:
              - signature: (PartialFunction[message, response])
                description: Where <i>PartialFunction</i> can be a case statement like <i>{ case "ping" => "pong" }</i>

          - keyword: wsAutoReplySocketIo4
            description: Enable partial support for Engine.IO v4 - Gatling will automatically respond to server ping messages (<i>2</i>) with pong (<i>3</i>).

  - title: SSE (Server Sent Events)
    description: Define the SSE requests sent in your scenario
    sections:

      - title: SSE
        functions:

          - keyword: sse
            description: Declares an SSE request
            syntax:
              - signature: (requestName)
                description: Name of the request

      - title: Commons
        functions:

          - keyword: sseName
            description: Allows to deal with several SSE streams per virtual user
            syntax:
              - signature: (requestName)
                description: Gives the requests a name and pass this name on each SSE operation

          - keyword: connect
            description: Connect a SSE stream
            syntax:
              - signature: (url)
                description: Enables communication to the given <i>url</i>

          - keyword: close
            description: Close a SSE stream

  - title: JMS
    description: Define the JMS requests sent in your scenario
    sections:

      - title: Start
        functions:

          - keyword: jms
            description: Declares an JMS request
            syntax:
              - signature: (requestName)
                description: Name of the request

      - title: Commons
        functions:

          - keyword: requestReply
            description: Sets the messaging implementation to request/reply

          - keyword: send
            description: Sets the messaging implementation to send

          - keyword: queue
            description: Defines a target destination
            syntax:
              - signature: "(name: Expression[String])"
                description: Where <i>name</i> is the name of the queue

          - keyword: destination
            description: Defines a target destination
            syntax:
              - signature: (destination)
                description: Where <i>destination</i> is an instance of JmsDestination

          - keyword: replyQueue
            description: Defines a reply destination
            syntax:
              - signature: "(name: Expression[String])"
                description: Where <i>name</i> is the name of the queue

          - keyword: replyDestination
            description: Defines a reply destination
            syntax:
              - signature: (destination)
                description: Where <i>destination</i> is an instance of JmsDestination

          - keyword: trackerQueue
            description: |
              On default gatling tracks the replies on the <code>replyQueue</code>.
              Optionally you can overwrite this destination with the parameter <code>trackerQueue</code>
            syntax:
              - signature: (destination)
                description: Where <i>destination</i> is an instance of JmsDestination

          - keyword: trackerDestination
            description: |
              On default gatling tracks the replies on the <code>replyDestination</code>.
              Optionally you can overwrite this destination with the parameter <code>trackerDestination</code>
            syntax:
              - signature: (destination)
                description: Where <i>destination</i> is an instance of JmsDestination

          - keyword: noJmsReplyTo
            description: Don't populate JMSReplyTo message field

          - keyword: selector
            description: Defines a JMS message selector
            syntax:
              - signature: (selector)
                description: Where <i>selector</i> is the selector

          - keyword: textMessage
            description: Sends a text message
            syntax:
              - signature: (textMessage)
                description: Where <i>textMessage</i> is a string

          - keyword: bytesMessage
            description: Sends a byte message
            syntax:
              - signature: (bytesMessage)
                description: Where <i>bytesMessage</i> is a byte array

          - keyword: mapMessage
            description: Sends a map message
            syntax:
              - signature: (mapMessage)
                description: Where <i>mapMessage</i> is a map

          - keyword: objectMessage
            description: Sends an object message
            syntax:
              - signature: (objectMessage)
                description: Where <i>objectMessage</i> is an object implementing JSerializable

          - keyword: property
            description: Sends additional property
            syntax:
              - signature: (key, value)
                description: Sets <i>value</i> as an object property <i>key</i>

          - keyword: jmsType
            description: Set message's JMS type
            syntax:
              - signature: (value)
                description: Sets <i>value</i> as a <a href="https://docs.oracle.com/javaee/6/api/javax/jms/Message.html#setJMSType(java.lang.String)">JMSType</a>

      - title: Checks
        functions:

          - keyword: simpleCheck
            description: Allows to check a message
            syntax:
              - signature: (function)
                description: Where <i>function</i> takes a Message and returns a Boolean

          - keyword: xpath
            description: Allows to check a TextMessage with XPath
            syntax:
              - signature: (expression)
                description: Where <i>expression</i> is an XPath expression

      - title: Protocol Configuration
        functions:

          - keyword: jms
            description: Entry point of your JMS configuration

        subsections:

          - title: Connecting
            functions:

              - keyword: connectionFactoryName
                required: true
                description: Set the name of the ConnectionFactory to use
                syntax:
                  - signature: (name)
                    description: where <em>name</em> is a string

              - keyword: url
                required: true
                description: Set the URL of the queue to connect to.
                syntax:
                  - signature: (url)
                    description: where <em>url</em> is a string

              - keyword: contextFactory
                required: true
                description: Set the name of the JNDI ContextFactory to use.
                syntax:
                  - signature: (contextFactory)
                    description: where <em>contextFactory</em> is a string

              - keyword: credentials
                description: Set the credentials used for the queues JNDI lookup
                syntax:
                  - signature: (username, password)
                    description: the username and password to use as credentials

              - keyword: disableAnonymousConnect
                description: Use credentials for opening connections too

              - keyword: listenerThreadCount
                description: Number of consumers that will listen to incoming messages on the tracker/reply queue
                syntax:
                  - signature: (listenerThreadCount)
                    contract: must be > 0
                    description: The number of consumers

              - keyword: replyTimeout
                syntax:
                  - signature: (duration)
                    description: the reply timeout in millis

          - title: Delivery modes
            functions:

              - keyword: useNonPersistentDeliveryMode
                description: Use JMS' non-persistent delivery mode (active by default)

              - keyword: usePersistentDeliveryMode
                description: Use JMS' persistent delivery mode

          - title: Message matching
            functions:

              - keyword: matchByMessageId
                description: Match request and response using JMS's MessageID

              - keyword: matchByCorrelationId
                description: Match request and response using JMS's CorrelationID

              - keyword: messageMatcher
                description: Match request and response using a custom strategy
                syntax:
                  - signature: (messageMatcher)
                    description: an implementation of <em>JmsMessageMatcher</em>, specifying how the <em>requestId</em> and <em>responseId</em> are retrieved from the JMS <em>Message</em>

  - title: MQTT
    description: Define the MQTT requests sent in your scenario
    frontline: true
    sections:

      - title: Start
        functions:

          - keyword: mqtt
            description: Declares an MQTT request
            syntax:
              - signature: (requestName)
                description: Name of the request

      - title: Commons
        functions:

          - keyword: subscribe
            syntax:
              - signature: (topic)
                description: Subscribe to a topic

          - keyword: publish
            syntax:
              - signature: (topic).message(body)
                description: Publish   a message with regular <code>Body</code> APIto a <i>topic</i>

          - keyword: expect
            description: Set a non-blocking check
            syntax:
              - signature: (duration)
                description: Expect a matching reply message with <i>duration</i>
              - signature: (duration, topic)
                description: Expect a matching reply message with <i>duration</i> from <i>topic</i> topic
              - signature: (duration, topic).check(check)
                description: Validate received message against <i>check</i> using regulare <code>Check</code> API.

          - keyword: await
            description: Set a blocking check
            syntax:
              - signature: (duration)
                description: Expect a matching reply message with <i>duration</i>
              - signature: (duration, topic)
                description: Expect a matching reply message with <i>duration</i> from <i>topic</i> topic
              - signature: (duration, topic).check(check)
                description: Validate received message against <i>check</i> using regulare <code>Check</code> API.

          - keyword: waitForMessages
            syntax:
              - signature: .timeout(duration)
                description: Wait for all pending check to complete

      - title: Protocol Configuration
        functions:

          - keyword: mqtt
            description: Entry point of your MQTT configuration

          - keyword: mqttVersion_3_1
            description: Use protocol version 3.1

          - keyword: mqttVersion_3_1_1
            description: Use protocol version 3.1.1

          - keyword: broker
            syntax:
              - signature: (hostname, port)
                description: Use <i>hostname</i> and<i>port</i> for broker address

          - keyword: useTls
            syntax:
              - signature: (boolean)
                description: Enable TLS

          - keyword: clientId
            syntax:
              - signature: (id)
                description: Set <i>id</i> as MQTT clientId

          - keyword: cleanSession
            syntax:
              - signature: (boolean)
                description: Clean session when connecting

          - keyword: credentials
            syntax:
              - signature: (userName, password)
                description: Use (<i>userName</i>, <i>password</i>) credentials for connecting

          - keyword: keepAlive
            syntax:
              - signature: (duration)
                description: Set connection keep-alive as <i>duration</i> seconds

          - keyword: qosAtMostOnce
            description: Use at-most-once QoS

          - keyword: qosAtLeastOnce
            description: Use at-least-once QoS

          - keyword: qosExactlyOnce
            description: Use exactly-once QoS

          - keyword: retain
            syntax:
              - signature: (boolean)
                description: Enable or disable retain

          - keyword: lastWill
            syntax:
              - signature: (LastWilltopic, willMessage).qosAtLeastOnce.retain(boolean)
                description: Define last will

          - keyword: reconnectAttemptsMax
            syntax:
              - signature: (number)
                description: Try to reconnect <i>number</i> times max

          - keyword: reconnectDelay
            syntax:
              - signature: (delay)
                description: Wait <i>delay</i> millis before reconnecting

          - keyword: reconnectBackoffMultiplier
            syntax:
              - signature: (multiplier)
                description: Use <i>multiplier</i> to compure actual reconnect delay

          - keyword: resendDelay
            syntax:
              - signature: (delay)
                description: Wait <i>delay</i> millis before resending message

          - keyword: resendBackoffMultiplier
            syntax:
              - signature: (multiplier)
                description: Use <i>multiplier</i> to compure actual resend delay

          - keyword: timeoutCheckInterval
            syntax:
              - signature: (duration)
                description: Check every <i>duration</i> for timed out checks

          - keyword: correlateBy
            syntax:
              - signature: (check)
                description: Use <i>check</i> for extracting correlationId from sent and received messages
---
