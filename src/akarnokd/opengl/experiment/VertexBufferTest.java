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
 *
 */
public class VertexBufferTest {
    public static void main(String[] args) {
        G2D.init(800, 600);
        
        // geometry
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3 * 3 * 2);
        
        buffer.put(400).put(200).put(0);
        buffer.put(1).put(0).put(0);
        buffer.put(400).put(400).put(0);
        buffer.put(0).put(1).put(0);
        buffer.put(200).put(400).put(0);
        buffer.put(0).put(0).put(1);
        
        buffer.flip();

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        G2D.loop(30, () -> {

            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);

            glBindBuffer(GL_ARRAY_BUFFER, vbo);

            glVertexPointer(3, GL_FLOAT, 6 * 4, 0);
            glColorPointer(3, GL_FLOAT, 6 * 4, 3 * 4);

            glDrawArrays(GL_TRIANGLES, 0, 3);

            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glDisableClientState(GL_COLOR_ARRAY);
            glDisableClientState(GL_VERTEX_ARRAY);
            
        }, () -> {
            glDeleteBuffers(vbo);
        });
    }
}
