---
title: Use Postman collections to create Gatling load tests
menutitle: Postman-based load tests
seotitle: How to create Gatling load tests from Postman collections
description: Export Postman collections and use them as Gatling scenarios. 
lead: Learn how to export Postman collections and use them as Gatling scenarios.
date: 2025-02-28T6:30:56+02:00

---

## Prerequisites

- A Gatling Enterprise Edition account [sign up for a free trial here](https://cloud.gatling.io/)
- A Postman collection

## Step 1: Export your Postman Collection

In your Postman workspace navigate to the collections menu and press the ellipsis button. At the bottom of the menu select the export option. Export your collection as a v2.1. This downloads a JSON file to your machine.

<div style="position: relative; overflow: hidden; max-width: 100%; padding-bottom: 56.25%; margin: 0px;"><iframe width="560" height="315" src="https://demo.arcade.software/qT3USG1Adi0NoGUfkKnL?embed&show_copy_link=true" title="Export your Postman Collection" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen="" style="position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; border: none;"></iframe></div>

## Step 2: Setup a Gatling JavaScript Project

If you don't already have a Gatling JavaScript Project, head to the [open-source downloads page](https://hubs.ly/Q02_3LcX0) to download it today.

Unzip the Gating JavaScript project and open it in your favorite IDE. The SDK has JavaScript and TypeScript folders. You can use either, but we will focus on JavaScript for the rest of this post.

Add your Postman collection to the `javascript/resources` folder in the Gatling JavaScript project
Open the `javascript/src` folder and create a file named `postman.gatling.js`.
In the newly created file add the following JavaScript code:

```javascript
import { simulation, constantUsersPerSec } from "@gatling.io/core";
import { http } from "@gatling.io/http"; 
import { postman } from "@gatling.io/postman";

export default simulation((setUp) => {
  const httpProtocol = http;

  const collection = postman
    .fromResource("yourCollectionName.postman_collection.json");


  const scn = collection.scenario("My Scenario", { pauses: 1 });
  
  setUp(
    scn.injectOpen(
        constantUsersPerSec(1).during(60)
    ).protocols(httpProtocol)
 ); 
});
```

- Change your `CollectionName` on line 9 to match the filename for your imported Postman collection.
- Run the installation command to install all of the packages and dependencies:

```console
npm install --save "@gatling.io/postman"
```

<div style="position: relative; overflow: hidden; max-width: 100%; padding-bottom: 56.25%; margin: 0px;"><iframe width="560" height="315" src="https://demo.arcade.software/73pR4BgMxkhNDo2gig4i?embed&show_copy_link=true" title="Setup a Gatling JavaScript Project" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen="" style="position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; border: none;"></iframe></div>

## Step 3: Run your test on Gatling Enterprise Edition

Remove the open-source test limits by running your test on Gatling Enterprise Edition with a free trial account. To do so:

1. Package your test by running the command `npx gatling enterprise-package` in your terminal. The packaged simulation is saved in the `target` folder.
2. Log in to your Gatling Enterprise Edition account.
3. Click on **Simulations** in the left-side menu.
4. Click on **Create a simulation** and follow the prompts to upload your package and create your simulation.
5. Start your simulation and see the live results!

<div style="position: relative; overflow: hidden; max-width: 100%; padding-bottom: 56.25%; margin: 0px;"><iframe width="560" height="315" src="https://demo.arcade.software/Ac2v3b1ldJUxMyuSCESd?embed&show_copy_link=true" title="Run your test on Gatling Enterprise Edition" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen="" style="position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; border: none;"></iframe></div>
