package ch.ledcom.agent.jmx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;

import com.google.common.io.ByteStreams;

public abstract class AbstractJMXConnectionTest {

	private static final int PAUSE = 500;
	
	private Process process;
	
	public void startDummyApplication(String... additionalArguments) throws IOException,
			InterruptedException {
		List<String> args = new ArrayList<String>();
		args.add("java");
		args.add("-javaagent:target/jmx-rmi-agent-0.1-SNAPSHOT.jar");
		args.add("-cp");
		args.add("target/classes/");
		
		args.addAll(Arrays.asList(additionalArguments));
		
		args.add(DummyApp.class.getName());
		ProcessBuilder pBuilder = new ProcessBuilder(args);
		pBuilder.redirectErrorStream(true);

		process = pBuilder.start();
		redirectProcessIO(process);
		Thread.sleep(PAUSE);
	}

	@After
	public void stopDummyApplication() throws InterruptedException {
		process.destroy();
		Thread.sleep(PAUSE);
	}

	private void redirectProcessIO(final Process process) {
		new Thread(new Runnable() {
			public void run() {
				try {
					ByteStreams.copy(process.getInputStream(), System.out);
				} catch (IOException e) {
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					ByteStreams.copy(process.getErrorStream(), System.err);
				} catch (IOException e) {
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					ByteStreams.copy(System.in, process.getOutputStream());
				} catch (IOException e) {
				}
			}
		}).start();
	}

}