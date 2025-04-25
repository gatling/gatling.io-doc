---
title: Workload models
---

## Open vs closed workload models

When it comes to load model, systems behave in 2 different ways:

* Closed systems, where you control the concurrent number of users
* Open systems, where you control the arrival rate of users

Make sure to use the proper load model that matches the load your live system experiences.

Closed systems are systems where the number of concurrent users is capped.
At full capacity, a new user can effectively enter the system only once another exits.

Typical systems that behave this way are:

* call center where all operators are busy
* ticketing websites where users get placed into a queue when the system is at full capacity

On the contrary, open systems have no control over the number of concurrent users: users keep on arriving even though applications have trouble serving them.
Most websites behave this way.

{{< alert warning >}}
Don't reason in terms of  concurrent users if your system can't push excess traffic into a queue.

If you're using a closed workload model in your load tests while your system actually is an open one, your test is broken, and you're testing some different imaginary behavior.
In such case, when the system under test starts to have some trouble, response times will increase, journey time will become longer, so number of concurrent users will increase
and the virtual users injection will slow down to match the imaginary cap you've set.
{{< /alert >}}

You can read more about open and closed models [here](https://www.usenix.org/legacy/event/nsdi06/tech/full_papers/schroeder/schroeder.pdf).

{{< alert warning >}}
Open and closed workload models are antinomical and you can't mix them in the same injection profile.
{{< /alert >}}