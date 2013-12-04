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

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

/**
 *
 */
public class SelectionViaColorpicking {
    static Planet p0;
    static Planet p1;
    static IntBuffer colorBuffer;
    static int selectedId;
    public static void main(String[] args) {
        int w = 800;
        int h = 600;
        G3D.init(w, h);

        colorBuffer = BufferUtils.createIntBuffer(1);
        
        Texture.enable();
        
        p0 = Planet.create("res/earthmap1k.jpg", 2, 50);
        p1 = Planet.create("res/sunmap.jpg", 5, 50);
        
        p0.position(0, 0, 0);
        p1.position(10, 0, 0);

        glClearColor(0.5f, 0.5f, 0.5f, 1f);
        
        G3D.loop(30, () -> {
            Mouse.poll();
            if (Mouse.next()) {
                int x = Mouse.getEventX();
                int y = Mouse.getEventY();
                
                if (Mouse.isButtonDown(0)) {
                    render(true);
                    glReadPixels(x, y, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, colorBuffer);
                    selectedId = toId(colorBuffer.get(0));
                    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                }
            }
            render(false);
            
            p0.rotate += 0.5;
            p1.rotate += 0.5;
        });
    }
    static int toId(int id) {
        return (id & 0xFF00FF00) | ((id & 0xFF) << 16) | ((id & 0xFF0000) >> 16);
    }
    public static void render(boolean named) {
        glPushMatrix();
        glTranslatef(-5, -2, -30);
        glRotatef(135, 1, 0, 0);
        
        p0.drawFull(named);
        p1.drawFull(named);
        
        if (!named) {
            if (selectedId == p0.id) {
                p0.drawWireframe();
            } else
            if (selectedId == p1.id) {
                p1.drawWireframe();
            }
        }
        
        glPopMatrix();
    }
}
