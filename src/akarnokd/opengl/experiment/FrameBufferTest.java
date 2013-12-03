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

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 */
public class FrameBufferTest {
    public static void main(String[] args) {
        G2D.init(800, 600);
        
        int fbo = glGenFramebuffersEXT();
        Texture texture = Texture.fromFile("res/colony_hub.png");
        
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture.handle(), 0);

        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport(0, 0, texture.textureWidth(), texture.textureHeight());
        glClear(GL_COLOR_BUFFER_BIT);
        G2D.fillRect(0, 0, texture.textureWidth(), texture.textureHeight());
        
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        glPopAttrib();
        
        G2D.loop(30, () -> { });
    }
    /** Check the completeness state of the framebuffer. */
    static void checkComplete() {
        int fbo = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
        switch (fbo) {
            case GL_FRAMEBUFFER_COMPLETE_EXT:
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                throw new RuntimeException("Framebuffer: GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT");
            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                throw new RuntimeException("Framebuffer: GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT");
            case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                throw new RuntimeException("Framebuffer: GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT");
            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                throw new RuntimeException("Framebuffer: GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT");
            case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                throw new RuntimeException("Framebuffer: GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT");
            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                throw new RuntimeException("Framebuffer: GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT");
            default:
                throw new RuntimeException("Framebuffer: " + fbo);
        }
    }
}
