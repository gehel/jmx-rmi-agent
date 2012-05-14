package ch.ledcom.agent.jmx;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DummyApp {

	public static void main(String[] args) throws InterruptedException {

		SortedMap<String, String> properties = new TreeMap(
				System.getProperties());
		for (Map.Entry<String, String> property : properties.entrySet()) {
			System.out.println(property.getKey() + " = " + property.getValue());
		}
		
		while (true) {
			System.out.println("hello, it is: " + new Date());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
