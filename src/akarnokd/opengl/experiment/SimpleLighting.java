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
import static akarnokd.opengl.experiment.G2D.floats;
import org.lwjgl.opengl.GL13;

/**
 *
 */
public class SimpleLighting {
    public static void main(String[] args) {
        // <editor-fold desc="Init." defaultstate="collapsed">
        G3D.init(800, 600);
        
        Texture.enable();
        
        glClearColor(0.5f, 0.5f, 0.5f, 1f);

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, floats(0.25f, 0.25f, 0.25f, 1f));
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_DIFFUSE);

        
        glMaterialf(GL_FRONT, GL_SHININESS, 32f);
        Planet planet = Planet.create("res/earthmap1k.jpg", 10, 100);
        planet.position(0, 0, 0);
        
        // </editor-fold>
        
        ShaderProgram sp = ShaderProgram.createFromResource("specularlight.vert", "specularlight.frag");
        
        G3D.loop(30, () -> {

            

            planet.tex.useWith(GL13.GL_TEXTURE0);
            sp.setUniformi("texture1", 0);
            sp.use();
            
            // <editor-fold desc="Place Earth." defaultstate="collapsed">
            glLoadIdentity();
            glTranslatef(0, 0, -50);
            glRotatef(90f, 1, 0, 0);

            glLight(GL_LIGHT0, GL_POSITION, floats(-20, 30, 0, 1));
            
            planet.drawFull();
            
            planet.rotate += 0.5;
            // </editor-fold>
            
            sp.stop();
            
        });
    }
}
