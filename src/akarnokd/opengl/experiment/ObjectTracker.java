/**
 * Copyright 2013 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package akarnokd.opengl.experiment;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public final class ObjectTracker {
    private ObjectTracker() {}
    private static final ConcurrentHashMap<Integer, Object> OBJECTS = new ConcurrentHashMap<>();
    private static final AtomicInteger IDS = new AtomicInteger(0xFF50_4000);
    public static int add(Object o) {
        Objects.requireNonNull(o);
        int id = IDS.getAndAdd(0x20);
        OBJECTS.put(id, o);
        return id;
    }
    @SuppressWarnings("unchecked")
    public static <T> T find(int id) {
        Object o = OBJECTS.get(id);
        return (T)o;
    }
    public static void remove(int id) {
        OBJECTS.remove(id);
    }
}
