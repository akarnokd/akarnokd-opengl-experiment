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
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
/**
 * Simple static triangle-based vertex buffer object manager.
 */
public class StaticTriangleVBO {
    private int handle;
    private int handleCount;
    private final FloatArray vertices;
    private final FloatArray colors;
    /** The number of vertices. */
    private int count;
    /** Create an empty object. */
    public StaticTriangleVBO() {
        vertices = new FloatArray();
        colors = new FloatArray();
    }
    /**
     * Create a duplicate of the other VBO.
     * @param other 
     */
    public StaticTriangleVBO(StaticTriangleVBO other) {
        vertices = new FloatArray(other.vertices);
        colors = new FloatArray(other.colors);
        count = other.count;
    }
    /**
     * Add a vertex.
     * @param x
     * @param y
     * @param z 
     */
    public void addVertex3f(float x, float y, float z) {
        vertices.add(x, y, z);
        count++;
    }
    /**
     * Add a color.
     * @param r
     * @param g
     * @param b 
     */
    public void addColor3f(float r, float g, float b) {
        colors.add(r, g, b);
        
    }
    /**
     * Clears the prepared vertice and color data, but does not
     * delete any built VBO, i.e., you can still call draw on this.
     * May be used to clear the construction data to free memory.
     */
    public void clear() {
        vertices.clear();
        colors.clear();
        count = 0;
    }
    /**
     * Given the vertices and colors, create a new buffer and upload the data.
     */
    public void build() {
        if (vertices.size() != colors.size()) {
            throw new IllegalStateException("Vertice count (" + vertices.size() + ") != Color count (" + colors.size() + ")");
        }
        
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.size() + colors.size());
        
        vertices.into(buffer);
        colors.into(buffer);
        
        buffer.flip();
        
        handleCount = count;
        handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, handle);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);    
    }
    /**
     * Draw the buffer.
     */
    public void draw() {
        draw(0, handleCount);
    }
    /**
     * Draw part of the buffer the buffer.
     * @param start the start index
     * @param count the number of elements to draw
     */
    public void draw(int start, int count) {
        if (start + count > this.handleCount) {
            throw new IndexOutOfBoundsException("Index: " + start + count + ", Limit: " + this.handleCount);
        }
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        
        glBindBuffer(GL_ARRAY_BUFFER, handle);

        glVertexPointer(3, GL_FLOAT, 4 * 3, 0);
        glColorPointer(3, GL_FLOAT, 4 * 3, handleCount * 4 * 3);

        glDrawArrays(GL_TRIANGLES, start, count);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
    }
    /** Release the buffer resource. */
    public void close() {
        if (handle != 0) {
            glDeleteBuffers(handle);
            handle = 0;
            handleCount = 0;
        }
    }
}
