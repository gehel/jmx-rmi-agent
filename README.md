jmx-rmi-agent
=============

jmx-rmi-agent is a simple Java Agent used to configure JMX registry. It is based
on multiple blog articles and documentation readily found on the web. This agent
barely deserve to be a project on its own, but I found myself in need of this
simple project more than once, and maybe somebody else will need it as well.

There are quite a few projects implementing an agent that will fix the RMI port
issue, but I could not find one that would also configure a few other things,
like authentication.

Goal
----

JMX is awfully useful to monitor your production servers. But usually production
servers are protected by firewalls (at least we hope they are). JMX uses RMI as
its communication layer and RMI is by default using dynamic TCP ports. When
firewalls are part of the equation static ports are much easier to work with.

This project helps you configure JMX through a single TCP port. It also help you
configure a few other things, like authentication.

If you need more functionalities, please open a ticket on [our issue
tracker][issues].

Configuration
-------------

To load the agent, use the standard syntax, add the following to your command
line:

    -javaagent:</path/to/jmx-rmi-agent.jar>

The following additional parameters are supported:

    -Dch.ledcom.agent.jmx.port=<JMX port> the port on which JMX will be available (default = 3000)
    -Dch.ledcom.agent.forceLocalhost=<true|false> is JMX available only from localhost
    -Dch.ledcom.agent.authenticate=<true|false> is authentication required
    -Dch.ledcom.agent.password.file=</path/to/password/file>
    -Dch.ledcom.agent.access.file=</path/to/access/file>

Password and access file are the standard files as described in the [official
documentation][JMXauth].

Maven repositories
------------------

You can download this project from Maven repositories :

* [Snapshots repository][snapshots]
* [Releases repository][releases]
* Or even in [Maven Central][maven-central]

[issues]: https://github.com/gehel/jmx-rmi-agent/issues
[JMXauth]: http://docs.oracle.com/javase/1.5.0/docs/guide/management/agent.html#auth
[snapshots]: https://oss.sonatype.org/content/repositories/snapshots/
[snapshots-project]: https://oss.sonatype.org/content/repositories/snapshots/ch/ledcom/agent/jmx/jmx-rmi-agent/
[releases]: https://oss.sonatype.org/content/repositories/releases/
[releases-project]: https://oss.sonatype.org/content/repositories/releases/ch/ledcom/agent/jmx/jmx-rmi-agent/
[maven-central]: http://repo1.maven.org/maven2/ch/ledcom/agent/jmx/jmx-rmi-agent/
