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

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

/**
 *
 */
public class SimpleSphere {
    static int angle;
    static int z = -10;
    static boolean dir;
    public static void main(String[] args) {
        G3D.init(800, 600);
        
        Texture.enable();
        
//        Texture tex = Texture.fromFile("res/16079.jpg", false);
        Texture tex = Texture.fromFile("res/earthmap1k.jpg", false);
        
        Sphere sphere = new Sphere();
        sphere.setDrawStyle(GLU.GLU_FILL);
        sphere.setTextureFlag(true);
        sphere.setNormals(GLU.GLU_SMOOTH);
        
        int sphereId = glGenLists(1);
        
        glNewList(sphereId, GL_COMPILE);
        tex.use();
        sphere.draw(2.3f, 50, 50);
        tex.stop();
        glEndList();

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        G3D.loop(30, () -> {
            glTranslatef(0, 0, z);
            glRotatef(angle++, 0, 1, 0);
            
            glCallList(sphereId);
            
            if (dir) {
                z--;
                if (z < -80) {
                    dir = false;
                }
            } else {
                z++;
                if (z > -10) {
                    dir = true;
                }
            }
        });
    }
}
