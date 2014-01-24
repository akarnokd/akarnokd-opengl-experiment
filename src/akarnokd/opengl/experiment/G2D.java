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
import java.util.Objects;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
/**
 * Utility lambdas for rendering with LWJGL.
 */
public final class G2D {
    /** Utility class. */
    private G2D() { throw new IllegalStateException("No instances!"); }
   /**
     * Initialize a windowed display with the given dimensions and default field-of-view.
     * @param w
     * @param h 
     */
    public static void init(int w, int h) {
        try {
            Display.setDisplayMode(new DisplayMode(w, h));
            Display.create();
        } catch (LWJGLException ex) {
            throw new RuntimeException(ex);
        }
        setOrthogonal();
    }    
    public static void setOrthogonal() {
        int w = Display.getWidth();
        int h = Display.getHeight();
        
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);
        glDisable(GL_CULL_FACE);
        
        
        glClearColor(0, 0, 0, 0);
        glClearDepth(1);
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glViewport(0, 0, w, h);
        glMatrixMode(GL_MODELVIEW);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, w, h, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }
    /**
     * Run a rendering loop.
     * @param fps the frames per second
     * @param body the body to execute
     */
    public static void loop(int fps, Runnable body) {
        loop(fps, body, () -> { });
    }
    /**
     * Run a rendering loop with the cleanup afterwards.
     * @param fps the frames per second
     * @param body the body to execute
     * @param cleanup the cleanup to execute
     */
    public static void loop(int fps, Runnable body, Runnable cleanup) {
        Objects.requireNonNull(body);
        Objects.requireNonNull(cleanup);
        try {
            while (!Display.isCloseRequested()) {
                glClear(GL_COLOR_BUFFER_BIT);

                body.run();

                Display.update();
                Display.sync(fps);
            }
        } finally {
            try {
                cleanup.run();
            } finally {
                Display.destroy();
            }
        }
    }
    /**
     * Execute the code while the given clipping region is defined.
     * Assumes top-down viewport.
     * @param x the left coordinate
     * @param y the *top* coordinate
     * @param width the width of the region
     * @param height the height of the region
     * @param run the action to run while clipped
     */
    public static void clip(int x, int y, int width, int height, Runnable run) {
        glEnable(GL_SCISSOR_TEST);
        try {
            int h = Display.getDisplayMode().getHeight();
            
            glScissor(x, h - y - height, width, height);
            
            run.run();
            
        } finally {
            glDisable(GL_SCISSOR_TEST);
        }
    }
    /**
     * Draw an unfilled rectangle with the current color settings.
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public static void drawRect(int x, int y, int width, int height) {
        glTranslatef(0.5f, 0.5f, 0); // otherwise, the lines don't join cleanly.
        glBegin(GL_LINE_STRIP);
            glVertex2i(x, y);
            glVertex2i(x + width - 1, y);
            glVertex2i(x + width - 1, y + height - 1);
            glVertex2i(x, y + height - 1);
            glVertex2i(x, y);
        glEnd();
        glTranslatef(-0.5f, -0.5f, 0);
    }
    /**
     * Draw a filled rectangle with the current color and texture settings.
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public static void fillRect(int x, int y, int width, int height) {
        glBegin(GL_QUADS);
            glVertex2i(x, y);
            glVertex2i(x + width - 1, y);
            glVertex2i(x + width - 1, y + height - 1);
            glVertex2i(x, y + height - 1);
        glEnd();
    }
    /**
     * Set the current color from an ARGB integer.
     * @param argb 
     */
    public static void color(int argb) {
        glColor4ub((byte)((argb >> 16) & 0xFF),
                (byte)((argb >> 8) & 0xFF),
                (byte)(argb & 0xFF),
                (byte)((argb >> 24) & 0xFF));
    }
    /**
     * Create a float buffer from varargs floats.
     * @param floats
     * @return 
     */
    public static FloatBuffer floats(float... floats) {
        FloatBuffer b = BufferUtils.createFloatBuffer(floats.length);
        b.put(floats);
        b.flip();
        return b;
    }
}
