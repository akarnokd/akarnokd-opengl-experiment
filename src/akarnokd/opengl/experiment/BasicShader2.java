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
/**
 *
 */
public class BasicShader2 {
    public static void main(String[] args) {
        G3D.init(800, 600);
        
        String vert = "varying vec4 vertColor; "
                + "void main() { "
                + "gl_Position = ftransform(); "
                + "vertColor = vec4(0.6, 0.3, 0.4, 1.0); "
                + "}";
        
        String frag = "varying vec4 vertColor;"
                + "void main() {"
                + "gl_FragColor = vertColor;"
                + "}";
        
        ShaderProgram sp = ShaderProgram.create(vert::toString, frag::toString);
        
        G3D.loop(30, () -> {
            sp.use();
            
            glLoadIdentity();
            glTranslatef(0, 0, -10);
            glColor3f(1, 1, 1);
            
            glBegin(GL_QUADS);
                glVertex3f(-1, 1, 0);
                glVertex3f(1, 1, 0);
                glVertex3f(1, -1, 0);
                glVertex3f(-1, -1, 0);
            glEnd();
            
            sp.stop();
            
        }, () -> {
            sp.close();
        });
    }
}
