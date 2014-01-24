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

import akarnokd.opengl.experiment.oskar.Model;
import akarnokd.opengl.experiment.oskar.OBJLoader;
import java.io.File;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;
import static akarnokd.opengl.experiment.G2D.*;
import akarnokd.opengl.experiment.oskar.EulerCamera;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/**
 *
 */
public class VBOLightingSimple {
    public static void main(String[] args) throws Exception {
        G3D.init(800, 600);
        
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, floats(0.05f, 0.05f, 0.05f, 1f));
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glMaterialf(GL_FRONT, GL_SHININESS, 120);
        glMaterial(GL_FRONT, GL_DIFFUSE, floats(0.4f, 0.27f, 0.17f, 0f));
        
        Model m = OBJLoader.loadModel(new File("res/bunny.obj"));
        
        int[] vbos = OBJLoader.createVBO(m);
        
        int vboVertexHandle = vbos[0];
        int vboNormalHandle = vbos[1];
        
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
        glNormalPointer(GL_FLOAT, 0, 0);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        
        EulerCamera cam = new EulerCamera((float)Display.getWidth() / Display.getHeight(), -2.19f, 1.36f, 11.45f);
        cam.setFieldOfView(70);
        cam.invertY(true);
        cam.applyPerspectiveMatrix();
        
        ShaderProgram sp = ShaderProgram.createFromResource("vbospecular.vert", "vbospecular.frag");
        
        G3D.loop(30, () -> {
            if (Mouse.isGrabbed()) {
                cam.processMouse(1, 80, -80);
            }
            cam.processKeyboard(16, 1, 1, 1);
            if (Mouse.isButtonDown(0)) {
                Mouse.setGrabbed(true);
            } else if (Mouse.isButtonDown(1)) {
                Mouse.setGrabbed(false);
            }
            
            cam.applyTranslations();
            
            glLight(GL_LIGHT0, GL_POSITION, floats(cam.x(), cam.y(), cam.z(), 1));
            sp.use();
            glDrawArrays(GL_TRIANGLES, 0, m.getFaces().size() * 3);
            sp.stop();
        });
    }
    static FloatBuffer reserveDate(int size) {
        FloatBuffer data = BufferUtils.createFloatBuffer(size);
        return data;
    }
}
