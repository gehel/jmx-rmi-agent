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

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ITBasicJMXConnection extends AbstractJMXConnectionTest {

    @After
    public void afterTest() throws InterruptedException {
        stopDummyApplication();
    }

    @Test
    public void testConnectingNoPassword() throws InterruptedException,
            IOException {
        startDummyApplication("-D" + JmxCustomAgent.PORT_KEY + "=9997");

        checkAndReturnConnection(9997);
    }

    @Test
    public void testConnectingValidPassword() throws InterruptedException,
            IOException {
        startDummyApplication("-D" + JmxCustomAgent.PORT_KEY + "=9998",
                "-D" + JmxCustomAgent.AUTHENTICATE + "=true",
                "-D" + JmxCustomAgent.PASSWORD_FILE + "=target/test-classes/jmxremote.password",
                "-D" + JmxCustomAgent.ACCESS_FILE + "=target/test-classes/jmxremote.access");

        checkAndReturnConnection(9998, "testUser", "1234");
    }

    @Test(expected = SecurityException.class)
    public void testConnectingWrongPassword() throws InterruptedException,
            IOException {
        startDummyApplication("-D" + JmxCustomAgent.PORT_KEY + "=9999", "-D"
                + JmxCustomAgent.PASSWORD_FILE
                + "=src/test/resources/jmxremote.password");
        checkAndReturnConnection(9999);
    }

    private MBeanServerConnection checkAndReturnConnection(final int port) throws IOException {
        return checkAndReturnConnection(port, (String) null, (String) null);
    }

    private MBeanServerConnection checkAndReturnConnection(final int port, final String username,
                                                           final String password) throws IOException {
        String urlString = "service:jmx:rmi://" + HOST + ":" + port + "/jndi/rmi://" + HOST + ":" + port + "/jmxrmi";
        System.out.println("Checking url [" + urlString + "]");
        JMXServiceURL url = new JMXServiceURL(
                urlString);
        Map<String, Object> env = new HashMap<String, Object>();
        if (username != null && password != null) {
            String[] creds = new String[2];
            creds[0] = username;
            creds[1] = password;
            env.put(JMXConnector.CREDENTIALS, creds);
        }
        JMXConnector jmxc = JMXConnectorFactory.connect(url, env);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        Assert.assertNotNull(mbsc);
        return mbsc;
    }

}
