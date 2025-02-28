---
title: Create a load test with basic authentication
menutitle: Test a Docker-based app
seotitle: How to test a Docker-based application with Gatling
description: Create a Gatling simulation that tests dockerized applications.
lead: tbd
date: 2025-02-28T10:30:56+02:00
---

## Introduction

<!--sync with the new blog post-->
The number of production services that are deployed and running in containerization platforms such as Docker has grown dramatically in recent years. With this radical shift in the deployment architecture, the need for robust load testing of applications and services while running in containers is crucial.

<!--fix header formatting (caps, structure)-->

## Load testing Docker containers in practice

### How do we prepare for load testing of Docker containers?

We prepare for load testing of Docker containers in mostly the same way as we do for traditional performance or load tests, i.e.:

- Defining clear load testing objectives and success criteria, such as the expected response time of transactions or the amount of throughput the system can handle.
- Procuring and establishing a test environment that is representative of the production environment the application will eventually go live in.
- Choosing an appropriate load testing tool that is capable of generating sufficient traffic to test the system and creating realistic user journey scenario scripts.

In addition to the aforementioned, we must ensure that container-specific monitoring and logging tools are in place and configured for real-time insights into the containers’ performance during the load test. Container monitoring can be achieved using Application Performance Monitoring tools.

Gatling is an excellent choice for load testing your application running in a Docker container. You can use Gatling to create user journey scripts through your application and then execute a stress test once the application is running in a containerized fashion.

## Example TypeScript API

Here's an example of a simple API written in TypeScript that has two different API endpoints and its associated Dockerfile:

```typescript
import express, { Request, Response } from 'express';

const app = express();
const port = 3000;

interface Game {
  title: string;
  type: string;
}

const games: Game[] = [
  { title: 'RL', type: 'Sports' },
  { title: 'FIFA', type: 'Sports' },
  { title: 'PES', type: 'Sports' },
  { title: 'Fortnite', type: 'Battle Royale' },
  { title: 'Minecraft', type: 'Sandbox' },
  { title: 'CS2', type: 'Shooter' }
];

app.get('/games', (_, res: Response) => {
  const randomGame = games[Math.floor(Math.random() * games.length)];
  res.json({ game: `${randomGame.title}` });
});

app.get('/game/:title', (req: Request, res: Response) => {
  const gameTitle = req.params.title;
  const game = games.find(g => g.title.toLowerCase() === gameTitle.toLowerCase());
  
  if (game) {
    res.json({ title: game.title, type: game.type });
  } else {
    res.status(404).json({ message: 'Game not found' });
  }
});

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
```

```dockerfile
FROM node:22

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

RUN npm install -g typescript

COPY . .

RUN tsc

EXPOSE 3000

CMD [ "node", "server.js" ]
```
Now we will build our container image and launch it:

```console
% docker build -t myapi .
```

The console output should look like the following:

```console
[+] Building 1.3s (12/12) FINISHED                                                                                         docker:desktop-linux
=> [internal] load build definition from Dockerfile                                                                                       0.0s
=> => transferring dockerfile: 205B                                                                                                       0.0s
...

% docker run -p 3000:3000 myapi

Server is running on http://localhost:3000/
```

Here's an example of a simple user journey Gatling script written in the TypeScript version of Gatling that calls the two API endpoints of our application (save it as myapi.gatling.ts):

```typescript
import {
  simulation,
  scenario,
  exec,
  atOnceUsers,
  jsonPath,
} from "@gatling.io/core";
import { http } from "@gatling.io/http";

export default simulation((setUp) => {


  const httpProtocol = http
    .baseUrl("http://localhost:3000")
    .acceptHeader("application/json");


  const GetGame = exec(
    http("Get random game")
    .get("/games")
    .check(jsonPath("$.game").saveAs("GameName"))
  );

  const GetType = exec(
    http("Get type")
    .get("#/game/{GameName}")
  );

   const GetGameAndType = scenario("Get game and type").exec(GetGame,GetType);

  setUp(
    GetGameAndType.injectOpen(atOnceUsers(1)),
  ).protocols(httpProtocol);
});
```
Now to launch the simulation, I just need to run:

```console
npx gatling run --typescript --simulation myapi
```
At the end of the simulation, Gatling will provide you with a report containing information such as the response time range, number of requests, and more.

When you’re ready to start executing more realistic distributed load tests against your containerized application, Gatling Enterprise is a natural choice. The Enterprise version of Gatling enables you to deploy a distributed test environment in the cloud with multiple load injectors in different availability zones in just a few clicks. You can then execute a load test against your containerized application using the same Gatling user journey scripts created previously.

## What are the best practices for load testing Docker containers?

One of the most important aspects of load testing Docker containers is to ensure that we start load testing early on in the software development lifecycle. The best way to do this is to integrate load testing into your CI/CD process. Gatling’s load testing as code methodology means that it fits perfectly into your CI/CD automation pipeline.

By deploying your application into a Docker container and running load tests early on, you’ll be able to find and resolve any performance-related issues before they become too cumbersome.

As well as closely monitoring the performance and health of the Docker containers during load testing, another important aspect of testing is the failover and recovery of your application. You should introduce failure scenarios into your load testing, such as container crashes, network failures, or other infrastructure disruptions. You need to assess the impact of these events on the application during load testing of Docker containers and whether or not the system can recover sufficiently.

Your approach to load-testing applications running in Docker containers should be based on benchmarking load-test results. Gatling Enterprise enables you to quickly execute repeatable distributed load tests and compare the results of any previous test runs in detail directly in the advanced graphical reporting interface.

## Conclusion

The requirement for performance testing of applications running in Docker containers is more important than ever. With the vast majority of applications now running in containerization platforms, load testing of these containers is crucial to ensure scalability and performance.

Gatling enables you to create load-testing scripts that are capable of simulating high levels of traffic against applications running in containers. Our comprehensive documentation has everything you need to get started right away.

There are many advantages to containerizing applications, but ensuring that your application still performs optimally whilst running inside these platforms is critical to ensure that the demands of your customers and users are met.
