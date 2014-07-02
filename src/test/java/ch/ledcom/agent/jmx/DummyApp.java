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
