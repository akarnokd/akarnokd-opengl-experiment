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

import java.util.Arrays;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 */
public class SolarSystem {
    static class Planet {
        int handle;
        Planet parent;
        float orbit;
        float angle;
        float rotate;
        float x;
        float y;
        float z;
        public static Planet create(String file, float radius, int smoothness) {
            Planet p = new Planet();
            // GLU sphere requires a mirrored texture ?!
            Texture tex = Texture.fromFile(file, false, true);
        
            Sphere sphere = new Sphere();
            sphere.setDrawStyle(GLU.GLU_FILL);
            sphere.setTextureFlag(true);
            sphere.setNormals(GLU.GLU_SMOOTH);
            sphere.setOrientation(GLU.GLU_OUTSIDE);

            int sphereId = glGenLists(1);

            glNewList(sphereId, GL_COMPILE);
            tex.use();
            sphere.draw(radius, smoothness, smoothness);
            tex.stop();
            glEndList();
            
            p.handle = sphereId;
            
            return p;
        }
        public void position(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        /** Compute the position from the orbital information. */
        public void positionFromOrbit() {
            x = parent.x + orbit * (float)Math.cos(angle);
            y = parent.y + orbit * (float)Math.sin(angle);
            z = parent.z;
        }
        public void draw() {
            glCallList(handle);
        }
        /** Draw with full position and rotation information. */
        public void drawFull() {
            glPushMatrix();
            
            glTranslatef(x, y, z);
            glRotatef(rotate, 0, 0, 1);
            draw();
            
            glPopMatrix();
        }
    }
    static float scale = 0.2f;
    static boolean pause;
    static float speed = 0.01f;
    static float tilt = 135;
    public static void main(String[] args) {
        G3D.init(800, 600);
        Display.setTitle("Solar system example");
        
        Texture.enable();
        
        glClearColor(0.5f, 0.5f, 0.5f, 1f);
        
        Planet sun = Planet.create("res/sunmap.jpg", 20, 50);
        Planet earth = Planet.create("res/earthmap1k.jpg", 8, 50);
        Planet moon = Planet.create("res/moonmap1k.jpg", 4, 50);
        Planet mercury = Planet.create("res/mercurymap.jpg", 3, 30);
        Planet venus = Planet.create("res/venusmap.jpg", 8, 50);
        Planet mars = Planet.create("res/mars_1k_color.jpg", 6, 30);
        Planet jupiter = Planet.create("res/jupitermap.jpg", 15, 50);
        Planet saturn = Planet.create("res/saturnmap.jpg", 12, 50);
        Planet uranus = Planet.create("res/uranusmap.jpg", 10, 50);
        Planet neptune = Planet.create("res/neptunemap.jpg", 10, 50);
        Planet pluto = Planet.create("res/plutomap1k.jpg", 3, 30);
        
        earth.parent = sun;
        moon.parent = earth;
        mercury.parent = sun;
        venus.parent = sun;
        mars.parent = sun;
        jupiter.parent = sun;
        saturn.parent = sun;
        uranus.parent = sun;
        neptune.parent = sun;
        pluto.parent = sun;

        sun.position(0, 0, -10);
        earth.orbit = 50;
        moon.orbit = 20;
        
        float split = 360f / 9;
        
        List<Planet> planets = Arrays.asList(mercury, venus, earth, mars, jupiter, saturn, uranus, neptune, pluto);
        
        AtomicInteger idx = new AtomicInteger();
        
        planets.forEach(p -> {
            p.orbit = 50;
            p.angle = (float)Math.toRadians(split * idx.getAndAdd(1));
        });
        
        G3D.loop(30, () -> {
            int mdw = Mouse.getDWheel();
            if (mdw < 0) {
                scale = (float)Math.max(0.01, scale - 0.01);
            } else
            if (mdw > 0) {
                scale += 0.01;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
                speed += 0.01;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {
                speed = (float)Math.max(0.01, speed - 0.01);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                tilt -= 5;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                tilt += 5;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                planets.forEach(p -> p.angle += speed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                planets.forEach(p -> p.angle -= speed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
                planets.forEach(p -> p.rotate++);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
                planets.forEach(p -> p.rotate--);
            }
            while (Keyboard.next()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                    pause = !pause;
                }
            }
            
            glTranslatef(0, 0, -50);
            glScalef(scale, scale, scale);
            glRotatef(tilt, 1, 0, 0);
            
            sun.drawFull();
            earth.positionFromOrbit();
            earth.drawFull();
            moon.positionFromOrbit();
            moon.drawFull();
            
            mercury.positionFromOrbit();
            mercury.drawFull();

            mercury.positionFromOrbit();
            mercury.drawFull();
            venus.positionFromOrbit();
            venus.drawFull();
            mars.positionFromOrbit();
            mars.drawFull();
            jupiter.positionFromOrbit();
            jupiter.drawFull();
            saturn.positionFromOrbit();
            saturn.drawFull();
            uranus.positionFromOrbit();
            uranus.drawFull();
            neptune.positionFromOrbit();
            neptune.drawFull();
            pluto.positionFromOrbit();
            pluto.drawFull();
            
            if (!pause) {
                sun.rotate++;
                earth.rotate++;
                moon.rotate++;
                moon.angle += speed * 5;

                planets.forEach(p -> p.angle += speed);
            }
        });
    }
}
