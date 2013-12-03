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

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import org.lwjgl.util.glu.GLU;

/**
 *
 */
public class BasicShader {
    public static void main(String[] args) throws Exception {
        init();
        
        ShaderProgram sp = ShaderProgram.create(
                IO.fromResource("basicshader.vert"), 
                IO.fromResource("basicshader.frag"));
        
        while (!Display.isCloseRequested()) {
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            
            // --------------------------
            sp.use();
            
            glLoadIdentity();
            glTranslatef(0, 0, -10f);
            glColor3f(1, 1, 1);
            glBegin(GL_QUADS);
                glVertex3f(-1, 1, 0);
                glVertex3f(1, 1, 0);
                glVertex3f(1, -1, 0);
                glVertex3f(-1, -1, 0);
            glEnd();
            
            sp.stop();
            
            // --------------------------
            
            Display.update();
            Display.sync(30);
        }
        
        Display.destroy();
    }
    
    static void init() {
        int w = 1024;
        int h = 768;
        try {
            Display.setDisplayMode(new DisplayMode(w, h));
            Display.setTitle("Basic shader example");
            Display.create();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
        glViewport(0, 0, w, h);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(45, (1f * w / h), 0.1f, 100f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glShadeModel(GL_SMOOTH);
        glClearColor(0, 0, 0, 0);
        glClearDepth(1);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        
    }
}
