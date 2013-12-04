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

import java.util.concurrent.CancellationException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 */
public class ShaderCalcModel2 {
    public static void main(String[] args) {
        G3D.init(800, 600);
        
        System.out.println("OpenGL version " + glGetString(GL_VERSION));
        
        String vert = "#version 120\r\n"
                + "uniform vec3 pos;"
                + "uniform vec3 rot;"
                + "varying vec4 vColor;"
                + "void main() {"
                + "mat4x4 position = mat4x4(1.0);"
                + "position[3].x = pos.x;"
                + "position[3].y = pos.y;"
                + "position[3].z = pos.z;"
                + ""
                + "mat4x4 heading = mat4x4(1.0);"
                + "heading[0][0] = cos(rot.y);"
                + "heading[0][2] = -(sin(rot.y));"
                + "heading[2][0] = sin(rot.y);"
                + "heading[2][2] = cos(rot.y);"
                + ""
                + "mat4x4 pitch = mat4x4(1.0);"
                + "pitch[1][1] = cos(rot.x);"
                + "pitch[1][2] = sin(rot.x);"
                + "pitch[2][1] = -(sin(rot.x));"
                + "pitch[2][2] = cos(rot.x);"
                + ""
                + "mat4x4 roll = mat4x4(1.0);"
                + "roll[0][0] = cos(rot.z);"
                + "roll[0][1] = sin(rot.z);"
                + "roll[1][0] = -(sin(rot.z));"
                + "roll[1][1] = cos(rot.z);"
                + ""
                + "gl_Position = gl_ModelViewProjectionMatrix * position * heading * pitch * roll * gl_Vertex;"
                + "vColor = vec4(0.6, 0.5, 0.3, 1.0);"
                + "}";
        
        String frag = "varying vec4 vColor;"
                + "void main() {"
                + "gl_FragColor = vColor;"
                + "}";
        
        ShaderProgram sp = ShaderProgram.create(vert::toString, frag::toString);
        
        Game game = new Game(sp);
        
        G3D.loop(30, game::tick);
    }
    static class Game {
        Floor floor;
        Box box;
        int mousex = 100;
        int mousey = 100;
        ShaderProgram sp;
        public Game(ShaderProgram sp) {
            this.sp = sp;
            floor = new Floor();
            box = new Box(sp);
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
        float posx;
        float posy;
        float posz;
        float pitch;
        float heading;
        float roll;
        ShaderProgram sp;
        public Box(ShaderProgram sp) {
            this.sp = sp;
        }
        void setPos(float x, float y, float z) {
            this.posx = x;
            this.posy = y;
            this.posz = z;
        }
        void setHeading(float a) {
            heading = a;
        }
        void setPitch(float p) {
            pitch = p;
        }
        void setRoll(float r) {
            roll = r;
        }
        void draw() {
            glLoadIdentity();

            sp.use();
            sp.setUniformf("pos", posx, posy, posz);
            sp.setUniformf("rot", pitch, heading, roll);
            
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
            
            sp.stop();
        }
        
    }
}
