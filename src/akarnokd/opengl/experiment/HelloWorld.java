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

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author karnokd
 */
public class HelloWorld {
    static long lastFPS;
    static int fps;
    static float rotation;
    static float x = 400;
    static float y = 300;
    static long lastFrame;
    /**
     * @param args the command line arguments
     * @throws Exception ignored
     */
    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        Display.create();
        Display.setResizable(true);
        
        init();
        
        lastFPS = getTime();
        lastFrame = lastFPS;
        
        while(!Display.isCloseRequested()) {
        
            int delta = getDelta();
            update(delta);
            
            render();
            
            if (getTime() - lastFPS > 1000) {
                Display.setTitle("FPS: " + fps);
                fps = 0;
                lastFPS += 1000;
            }
            fps++;
            
            Display.update();
            Display.sync(30);
        }
        Display.destroy();
    }
    static void init() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 800, 0, 600, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }
    static int getDelta() {
        long time = getTime();
        int delta = (int)(time - lastFrame);
        lastFrame = time;
        return delta;
    }
    static void update(int delta) throws Exception {
        /*
        while (Keyboard.next()) {
            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_LEFT:
                case Keyboard.KEY_A:
                    x -= 0.35 * delta;
                    break;
                case Keyboard.KEY_RIGHT:
                case Keyboard.KEY_D:
                    x += 0.35 * delta;
                    break;
                case Keyboard.KEY_UP:
                case Keyboard.KEY_W:
                    y += 0.35 * delta;
                    break;
                case Keyboard.KEY_DOWN:
                case Keyboard.KEY_S:
                    y -= 0.35 * delta;
                    break;
            }
        }
        */
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
            x -= 0.35 * delta;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
            x += 0.35 * delta;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
            y += 0.35 * delta;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
            y -= 0.35 * delta;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            if (Display.isFullscreen()) {
                DisplayMode dm = new DisplayMode(800, 600);
                setDisplayMode(dm.getWidth(), dm.getHeight(), false);
            } else {
                DisplayMode dm = Display.getDesktopDisplayMode();
                setDisplayMode(dm.getWidth(), dm.getHeight(), true);
            }
        }
        
        
        if (x < 0) {
            x = 0;
        }
        if (x > 800) {
            x = 800;
        }
        if (y < 0) {
            y = 0;
        }
        if (y > 600) {
            y = 600;
        }
        rotation += 0.15f * delta;
    }
    static void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glColor3f(0.5f, 0.5f, 1f);

        glPushMatrix();
            glTranslatef(x, y, 0);
            glRotatef(rotation, 0, 0, 1f);
            glTranslatef(-x, -y, 0);
        
            glBegin(GL_QUADS);
                glVertex2f(x - 50, y - 50);
                glVertex2f(x + 50, y - 50);
                glVertex2f(x + 50, y + 50);
                glVertex2f(x - 50, y + 50);
            glEnd();
        glPopMatrix();
        
    }
    /** @return Timer in millisecond accuracy. */
    public static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
    static void setDisplayMode(int width, int height, boolean fullscreen) {
        DisplayMode current = Display.getDisplayMode();
        if (current.getWidth() == width && current.getHeight() == height && Display.isFullscreen() == fullscreen) {
            return;
        }
        try {
            DisplayMode target = null;
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                for (DisplayMode m : modes) {
                    if (m.getWidth() == width && m.getHeight() == height) {
                        if (target == null || m.getFrequency() >= freq) {
                            if (target == null || m.getBitsPerPixel() > target.getBitsPerPixel()) {
                                target = m;
                                freq = m.getFrequency();
                            }
                        }
                        if (m.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()
                                && m.getFrequency() == Display.getDesktopDisplayMode().getFrequency()) {
                            target = m;
                            break;
                        }
                    }
                }
            } else {
                target = new DisplayMode(width, height);
            }
            if (target == null) {
                System.out.println("Unable to find display mode for " + width + " * " + height + (fullscreen ? " Fullscreen" : ""));
                return;
            }
            Display.setDisplayMode(target);
            Display.setFullscreen(fullscreen);
        } catch (Exception ex) {
            
        }
    }
    
}
