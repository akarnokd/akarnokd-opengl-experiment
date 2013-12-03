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

import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * A simple dynamic array of float values.
 */
public class FloatArray {
    private float[] items;
    private int count;
    /** Counstruct a FloatArray with capacity 24. */
    public FloatArray() {
        this(24);
    }
    /**
     * Construct a FloatArray with the given capacity.
     * @param initialCapacity 
     */
    public FloatArray(int initialCapacity) {
        ensureCapacity(Math.max(24, initialCapacity));
    }
    /**
     * Construct a float array from the other instance.
     * @param other 
     */
    public FloatArray(FloatArray other) {
        if (other.items != null) {
            this.items = Arrays.copyOf(other.items, other.items.length);
            this.count = other.count;
        }
    }
    public void add(float value) {
        int c = count;
        ensureCapacity(c + 1);
        float[] v = items;
        v[c] = value;
        count = c + 1;
    }
    public void add(float value1, float value2) {
        int c = count;
        ensureCapacity(c + 2);
        float[] v = items;
        v[c] = value1;
        v[c + 1] = value2;
        count = c + 2;
    }
    public void add(float value1, float value2, float value3) {
        int c = count;
        ensureCapacity(c + 3);
        float[] v = items;
        v[c] = value1;
        v[c + 1] = value2;
        v[c + 2] = value3;
        count = c + 3;
    }
    public void add(float value1, float value2, float value3, float value4) {
        int c = count;
        ensureCapacity(c + 4);
        float[] v = items;
        v[c] = value1;
        v[c + 1] = value2;
        v[c + 2] = value3;
        v[c + 3] = value4;
        count = c + 4;
    }
    public void add(float... values) {
        int c = count;
        ensureCapacity(c + values.length);
        System.arraycopy(values, 0, items, c, values.length);
        count = c + values.length;
    }
    /** Ensure the capacity is available. */
    private void ensureCapacity(int capacity) {
        if (items == null) {
            int newCap = Math.max(24, capacity);
            items = new float[newCap];
            return;
        }
        if (items.length < capacity) {
            int newCap = Math.max(items.length * 2, capacity);
            items = Arrays.copyOf(items, newCap);
        }
    }
    /** @return the size */
    public int size() {
        return count;
    }
    /** Clear the contents. */
    public void clear() {
        count = 0;
        items = null;
    }
    /** @return is this empty. */
    public boolean isEmpty() {
        return count == 0;
    }
    /**
     * Changes the capacity to the current item count, freeing memory.
     */
    public void compact() {
        if (items != null && items.length != count) {
            items = Arrays.copyOf(items, count);
        } 
    }
    /**
     * Put the values into the given float buffer.
     * @param buffer 
     */
    public void into(FloatBuffer buffer) {
        buffer.put(items, 0, count);
    }
    /**
     * Read values from the given float buffer.
     * @param buffer 
     */
    public void from(FloatBuffer buffer) {
        int c = count;
        int r = buffer.remaining();
        ensureCapacity(c + r);
        buffer.get(items, c, r);
        
        count = c + r;
    }
}
