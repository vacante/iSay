[Bukkit]: http://bukkit.org/
[Maven]: http://maven.apache.org/
[Installation]: https://github.com/psanker/iSay/wiki/Installation

iSay
===========

A better chat plugin for [Bukkit][Bukkit]

General Information
-----------

__Latest version built with Bukkit version 1.3.2-R1.1-SNAPSHOT__

How does one install iSay? Check out [the installation wiki page][Installation].

Building
-----------

I use maven to handle my dependencies. Also, please build with JDK6.

- Install [Maven 3][Maven]
- Check out this repository.
- Run ```mvn clean package```

Tickets
-------------------

How do I create a ticket the right way?

- Don't smash multiple topics together. Make separate tickets.
- Provide as much detail as possible. If you can, provide the steps you took to replicate the issue, if applicable.

### Bug Report

Here is a sample bug report that looks good:

```
Tried to send player a PM and got a message saying "null" where player name was supposed to be

1. Executed command "/m john (message...)"
2. Received message saying "[You -> null] (message...)" 

Version stats:
CraftBukkit 1.3.2-R1.0
iSay 0.1-Re3
```

Java versions are not necessary, but you may include them. iSay is generally built on JDK7.

### Feature Request

If you wish to see something in iSay, go ahead and submit a ticket. As usual, provide as much detail as possible, such as UI requests, channel actions, etc. These tickets do not have priority, but I will get to them eventually, so be sure to make them fun reads!


Pull Requests
-------------

Yes, I do accept pull requests. Despite the fact that this work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License, I do accept creativity and do waive the "no derivative works" part for forks that are intended to be used for pull requests to the main project (aka this one).

I like to see these things:

- A handful of commits per pull request
- A description of what you have done
- Any and all links to existing Issue Tickets in the main project's ticketing system.