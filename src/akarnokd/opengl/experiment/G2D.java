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

import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
/**
 * Utility lambdas for rendering with LWJGL.
 */
public final class G2D {
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
}
