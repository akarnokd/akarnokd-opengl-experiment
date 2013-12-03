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
import static org.lwjgl.opengl.GL13.*;
/**
 *
 */
public class ShaderTexturing {
    public static void main(String[] args) {
        G3D.init(800, 600);
        
        String vert = "void main() {"
                + "gl_Position = ftransform();"
                + "gl_TexCoord[0] = gl_MultiTexCoord0;"
                + "}";
        
        String frag = "uniform sampler2D texture1;"
                + "uniform sampler2D texture2;"
                + "uniform float translucency;"
                + "void main() {"
                + "vec4 color1 = texture2D(texture1, gl_TexCoord[0].st);"
                + "vec4 color2 = texture2D(texture2, gl_TexCoord[0].st);"
                + "float tl = float(1) - translucency;"
                + "color1 *= tl;"
                + "color2 *= translucency;"
                + "gl_FragColor = color1 + color2;"
                + "}";

        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        
        Texture tex1 = Texture.fromFile("res/colony_hub.png");
        
        Texture tex2 = Texture.fromFile("res/bar.png");
        
        ShaderProgram sp = ShaderProgram.create(vert::toString, frag::toString);
     
        sp.use();
        sp.setUniformi("texture1", 0);
        sp.setUniformi("texture2", 1);
        sp.setUniformf("translucency", 0.5f);
        sp.stop();
        
        G3D.loop(30, () -> {
            
            glLoadIdentity();
            glTranslatef(0, 0, -10);
            glColor3f(1, 1, 1);
            
            glActiveTexture(GL_TEXTURE0);
            tex1.use();
            glActiveTexture(GL_TEXTURE1);
            tex2.use();
            glActiveTexture(GL_TEXTURE0);
            sp.use();
            glBegin(GL_QUADS);
                glTexCoord2f(0, 0);
                glVertex3f(-1, 1, 0);
                glTexCoord2f(1, 0);
                glVertex3f(1, 1, 0);
                glTexCoord2f(1, 1);
                glVertex3f(1, -1, 0);
                glTexCoord2f(0, 1);
                glVertex3f(-1, -1, 0);
            glEnd(); 
            sp.stop();
            tex2.stop();
            tex1.stop();
            glActiveTexture(GL_TEXTURE0);
        });
        
    }
}
