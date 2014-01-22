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

import java.util.Objects;
import java.util.concurrent.CancellationException;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

/**
 * Utility class to run a 3D OpenGL routine.
 */
public final class G3D {
    /** Utility class. */
    private G3D() { throw new IllegalStateException("No instances!"); }
    /**
     * Initialize a windowed display with the given dimensions and default field-of-view.
     * @param w
     * @param h 
     */
    public static void init(int w, int h) {
        init(w, h, 45);
    }
    /**
     * Initialize a windowed display with the given dimensions and field-of-view.
     * @param w
     * @param h
     * @param fow 
     */
    public static void init(int w, int h, float fow) {
        init(w, h, fow, 0.1f, 100f);
    }
    /**
     * Initialize the windowed display with the given dimensions, field of view
     * and near+far plane distances
     * @param w
     * @param h
     * @param fow
     * @param near
     * @param far 
     */
    public static void init(int w, int h, float fow, float near, float far) {
        try {
            Display.setDisplayMode(new DisplayMode(w, h));
            Display.create();
            
        } catch (LWJGLException ex) {
            throw new RuntimeException(ex);
        }
        
        glViewport(0, 0, w, h);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(45, (1f * w / h), near, far);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glShadeModel(GL_SMOOTH);
        glClearColor(0, 0, 0, 0);
        glClearDepth(1);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
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
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();

                body.run();

                Display.update();
                Display.sync(fps);
            }
        } catch (CancellationException ex) {
            // ignored
        } finally {
            try {
                cleanup.run();
            } finally {
                Display.destroy();
            }
        }
    }
}
