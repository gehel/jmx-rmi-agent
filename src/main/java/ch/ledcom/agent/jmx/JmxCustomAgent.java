/**
 * Copyright (C) 2012 LedCom (guillaume.lederrey@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ledcom.agent.jmx;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

/**
 * This CustomAgent will start an RMI Connector Server using only port
 * "ch.ledcom.agent.jmx.port". When used with the javaagent option, it allows a
 * remote connection using JConsole or VisualVM through an SSH tunnel.
 * <p/>
 * The purpose of this agent is to open a JMX server that will use only one
 * fixed port for all communication instead of two dynamically set ports.
 * <p/>
 * Code adapted from:
 * https://blogs.oracle.com/jmxetc/entry/connecting_through_firewall_using_jmx
 *
 * @author gehel
 */
public final class JmxCustomAgent {

    /**
     * Base name for all parameter names.
     */
    private static final String BASE = "ch.ledcom.agent.jmx.";

    /** Parameter name to set the JMX port. */
    public static final String PORT_KEY = BASE + "port";

    /** Parameter name to force localhost. */
    public static final String FORCE_LOCALHOST_KEY = BASE + "forceLocalhost";

    /** Parameter name to activate authentication. */
    public static final String AUTHENTICATE = BASE + "authenticate";

    /** Parameter name to set password file used for authentication. */
    public static final String PASSWORD_FILE = BASE + "password.file";

    /** Parameter name to set access file used for authentication. */
    public static final String ACCESS_FILE = BASE + "access.file";

    /** Default JMX port. */
    private static final int DEFAULT_PORT = 3000;

    /** Private constructor to make sure nobody tries to instantiate this class. */
    private JmxCustomAgent() {
    }

    /**
     * Entry point into a JavaAgent.
     *
     * @param agentArgs arguments given to the agent (not used, we only use
     *                  system properties)
     * @throws IOException in case of problems setting up the agent
     */
    public static void premain(final String agentArgs) throws IOException {
        // get all parameters from environment
        final int port = parseInt(System.getProperty(PORT_KEY), DEFAULT_PORT);
        final boolean forceLocalhost = Boolean.getBoolean(FORCE_LOCALHOST_KEY);
        final String accessFile = System.getProperty(ACCESS_FILE);
        final String passwordFile = System.getProperty(PASSWORD_FILE);
        final boolean authenticate = Boolean.getBoolean(AUTHENTICATE);

        System.out.println("RMI / JMX Custom Agent");
        System.out.println("======================");
        System.out.println(PORT_KEY + " = [" + port + "]");
        System.out.println(FORCE_LOCALHOST_KEY + " = [" + forceLocalhost + "]");
        System.out.println(AUTHENTICATE + " = [" + authenticate + "]");
        System.out.println(ACCESS_FILE + " = [" + accessFile + "]");
        System.out.println(PASSWORD_FILE + " = [" + passwordFile + "]");

        // Ensure cryptographically strong random number generator used
        // to choose the object number - see java.rmi.server.ObjID
        System.setProperty("java.rmi.server.randomIDs", "true");

        System.out.println("Create RMI registry on port " + port);
        LocateRegistry.createRegistry(port);

        // Retrieve the PlatformMBeanServer.
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Environment map.
        System.out.println("Initialize the environment map");
        HashMap<String, Object> env = new HashMap<String, Object>();

        // Provide the password file used by the connector server to
        // perform user authentication. The password file is a properties
        // based text file specifying username/password pairs.
        env.put("jmx.remote.x.password.file", passwordFile);

        // Provide the access level file used by the connector server to
        // perform user authorization. The access level file is a properties
        // based text file specifying username/access level pairs where
        // access level is either "readonly" or "readwrite" access to the
        // MBeanServer operations.
        env.put("jmx.remote.x.access.file", accessFile);
        env.put("jmx.remote.x.authenticate", authenticate);

        // Create an RMI connector server.
        //
        // As specified in the JMXServiceURL the RMIServer stub will be
        // registered in the RMI registry running in the local host on
        // port 3000 with the name "jmxrmi". This is the same name the
        // out-of-the-box management agent uses to register the RMIServer
        // stub too.
        //
        // The port specified in "service:jmx:rmi://"+hostname+":"+port
        // is the second port, where RMI connection objects will be exported.
        // Here we use the same port as that we choose for the RMI registry.
        // The port for the RMI registry is specified in the second part
        // of the URL, in "rmi://"+hostname+":"+port
        String hostname;
        if (forceLocalhost) {
            System.out
                    .println("Using localhost as hostname "
                            + "as defined by system property: "
                            + FORCE_LOCALHOST_KEY);
            hostname = "localhost";
        } else {
            hostname = InetAddress.getLocalHost().getHostName();
        }

        System.out.println("Create an RMI/JMX connector server, hostname is: ["
                + hostname + "]");
        String serviceUrl = "service:jmx:rmi://" + hostname + ":" + port
                + "/jndi/rmi://" + hostname + ":" + port + "/jmxrmi";
        System.out.println("and url is: [" + serviceUrl + "]");
        JMXServiceURL url = new JMXServiceURL(serviceUrl);

        // Now create the server from the JMXServiceURL
        JMXConnectorServer cs = JMXConnectorServerFactory
                .newJMXConnectorServer(url, env, mbs);

        // Start the RMI connector server.
        System.out.println("Start the RMI/JMX connector server on port ["
                + port + "]");
        cs.start();
    }

    /**
     * Utility method to parse integer.
     *
     * @param s            String to parse as an int
     * @param defaultValue default value to return in case the String is empty
     * @return the integer represented by the String or the default value if
     * the String is empty
     */
    private static int parseInt(final String s, final int defaultValue) {
        if (s == null || s.trim().equals("")) {
            return defaultValue;
        }
        return Integer.parseInt(s);
    }
}
