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

import com.google.common.io.ByteStreams;
import org.junit.After;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public abstract class AbstractJMXConnectionTest {

    protected static final String HOST = "localhost";
    private static final int PAUSE = 2000;

    private Process process;

    public void startDummyApplication(String... additionalArguments) throws IOException,
            InterruptedException {
        List<String> args = new ArrayList<String>();
        args.add("java");
        args.add("-javaagent:target/jmx-rmi-agent-" + getVersion() + ".jar");
        args.add("-cp");
        args.add("target/test-classes/");

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

    private String getVersion() throws IOException {
        Properties props = new Properties();
        props.load(
                AbstractJMXConnectionTest.class.getClassLoader().getResourceAsStream("jmx-rmi-agent.properties"));
        return props.getProperty("jmx-rmi-agent.version");
    }
}
