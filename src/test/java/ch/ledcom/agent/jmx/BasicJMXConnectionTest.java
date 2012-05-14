package ch.ledcom.agent.jmx;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.Assert;
import org.junit.Test;

public class BasicJMXConnectionTest extends AbstractJMXConnectionTest {

	@Test
	public void testConnectingNoPassword() throws InterruptedException,
			IOException {
		startDummyApplication("-D" + JmxCustomAgent.PORT_KEY + "=9999");

		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		Assert.assertNotNull(mbsc);
	}

	@Test(expected = SecurityException.class)
	public void testConnectingWrongPassword() throws InterruptedException,
			IOException {
		startDummyApplication("-D" + JmxCustomAgent.PORT_KEY + "=9999", "-D"
				+ JmxCustomAgent.PASSWORD_FILE
				+ "=src/test/resources/jmxremote.password");

		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		jmxc.getMBeanServerConnection();
	}

}
