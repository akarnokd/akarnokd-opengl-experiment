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
import java.util.concurrent.CancellationException;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;

/**
 *
 */
public class ShaderCalcModel {
    public static void main(String[] args) {
        G3D.init(800, 600);
        
        Game game = new Game();
        
        G3D.loop(30, game::tick);
    }
    static class Game {
        Floor floor;
        Box box;
        int mousex = 100;
        int mousey = 100;
        public Game() {
            floor = new Floor();
            box = new Box();
            box.setPos(0, -0.4f, -10);
            box.setHeading(45);
            box.setPitch(-1);
            box.setRoll(0.1f);
            Mouse.setGrabbed(true);
        }
        void tick() {
            pollEvents();
            floor.draw();
            box.draw();
        }
        void pollEvents() {
            Keyboard.poll();
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                throw new CancellationException();
            }
            Mouse.setCursorPosition(mousex, mousey);
        }
    }
    static class Floor {
        void draw() {
            glLoadIdentity();
            glBegin(GL_QUADS);
            glColor3f(.2f, .4f, 0);
            glVertex3f(-100, -1, 100);
            glVertex3f(-100, -1, -100);
            glVertex3f(100, -1, -100);
            glVertex3f(100, -1, 100);
            glEnd();
        }
    }
    static class Box {
        float[] mat = { 1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        };
        FloatBuffer posMat;
        FloatBuffer headingMat;
        FloatBuffer pitchMat;
        FloatBuffer rollMat;
        float posx;
        float posy;
        float posz;
        float pitch;
        float heading;
        float roll;
        public Box() {
            posMat = BufferUtils.createFloatBuffer(mat.length);
            headingMat = BufferUtils.createFloatBuffer(mat.length);
            pitchMat = BufferUtils.createFloatBuffer(mat.length);
            rollMat = BufferUtils.createFloatBuffer(mat.length);
            
            posMat.put(mat);
            headingMat.put(mat);
            pitchMat.put(mat);
            rollMat.put(mat);
        }
        void setPos(float x, float y, float z) {
            posMat.put(12, x);
            posMat.put(13, y);
            posMat.put(14, z);
        }
        void setHeading(float a) {
            headingMat.put(0, (float)cos(a));
            headingMat.put(2, -(float)sin(a));
            headingMat.put(8, (float)sin(a));
            headingMat.put(10, (float)cos(a));
        }
        void setPitch(float p) {
            pitchMat.put(5, (float)cos(p));
            pitchMat.put(6, (float)sin(p));
            pitchMat.put(9, -(float)sin(p));
            pitchMat.put(10, (float)cos(p));
        }
        void setRoll(float r) {
            rollMat.put(0, (float)cos(r));
            rollMat.put(1, (float)sin(r));
            rollMat.put(4, -(float)sin(r));
            rollMat.put(5, (float)cos(r));
        }
        void draw() {
            glLoadIdentity();
            posMat.rewind();
            glMultMatrix(posMat);
            headingMat.rewind();
            glMultMatrix(headingMat);
            pitchMat.rewind();
            glMultMatrix(pitchMat);
            rollMat.rewind();
            glMultMatrix(rollMat);
            
            glColor3f(0.8f, 0.5f, 0.3f);
            glBegin(GL_QUADS);
                // front face
                glNormal3f(0, 0, 1);
                glTexCoord2f(0, 0);
                glVertex3f(-1, -1, 1);
                glTexCoord2f(1, 0);
                glVertex3f(1, -1, 1);
                glTexCoord2f(1, 1);
                glVertex3f(1, 1, 1);
                glTexCoord2f(0, 1);
                glVertex3f(-1, 1, 1);

                // backface
                glNormal3f(0.0f, 0.0f, -1.0f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glTexCoord2f(1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glTexCoord2f(0.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
                glTexCoord2f(0.0f, 0.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);

                // Top Face

                glNormal3f(0.0f, 1.0f, 0.0f);
                glTexCoord2f(0.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glTexCoord2f(0.0f, 0.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
                glTexCoord2f(1.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);

                // Bottom Face

                glNormal3f(0.0f, -1.0f, 0.0f);
                glTexCoord2f(1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glTexCoord2f(0.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);
                glTexCoord2f(0.0f, 0.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);

                // Right face

                glNormal3f(1.0f, 0.0f, 0.0f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);
                glTexCoord2f(1.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
                glTexCoord2f(0.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
                glTexCoord2f(0.0f, 0.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);

                // Left Face

                glNormal3f(-1.0f, 0.0f, 0.0f);
                glTexCoord2f(0.0f, 0.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
                glTexCoord2f(1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glTexCoord2f(0.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
            
            glEnd();
        }
        
    }
}
