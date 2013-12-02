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

import java.awt.Font;
import java.io.FileInputStream;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.TextureImpl;

/**
 *
 */
public class TextureTest {
    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        Display.create();

        
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);
        
        
        glClearColor(0, 0, 0, 0);
        glClearDepth(1);
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glViewport(0, 0, 800, 600);
        glMatrixMode(GL_MODELVIEW);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 800, 600, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        Texture texture = TextureLoader.getTexture("PNG", new FileInputStream("res/colony_hub.png"), GL_NEAREST);

        TextureImpl.bindNone();
        
        Font awtfont = new Font("Times New Roman", Font.BOLD, 24);
        
        UnicodeFont font = new UnicodeFont(awtfont);
        @SuppressWarnings("unchecked")
        List<Object> eff = (List<Object>)font.getEffects();
        eff.add(new ColorEffect(java.awt.Color.orange));
        font.addAsciiGlyphs();
        font.loadGlyphs();

        int x0 = 30;
        int y0 = 30;
        
        boolean texframe = false;
        int texcorr = 0;
        
        while (!Display.isCloseRequested()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
                x0 -= 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
                x0 += 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
                y0 -= 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
                y0 += 1;
            }
            while (Keyboard.next()) {
                if (Character.toUpperCase(Keyboard.getEventCharacter()) == 'F') {
                    texframe = !texframe;
                }
                if (Character.toUpperCase(Keyboard.getEventCharacter()) == 'C') {
                    texcorr = (texcorr + 1) % 3;
                }
            }
            
            glClear(GL_COLOR_BUFFER_BIT);
            
            float ty = y0 + texture.getImageHeight();
            font.drawString(x0, ty, "Colony hub", Color.orange);
            
            glColor4f(1, 1, 1, 1);

            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            
            float corrx = 0;
            float corry = 0;
            
            if (texcorr == 1) {
                corrx = 1f / texture.getTextureWidth();
                corry = 1f / texture.getTextureHeight();
            } else
            if (texcorr == 2) {
                corrx = 0.5f / texture.getTextureWidth();
                corry = 0.5f / texture.getTextureHeight();
            }
            
            glBegin(GL_QUADS);
                glTexCoord2f(0, 0);
                glVertex2f(x0, y0);
                glTexCoord2f(1 - corrx, 0);
                glVertex2f(x0 + texture.getTextureWidth(), y0);
                glTexCoord2f(1 - corrx, 1 - corry);
                glVertex2f(x0 + texture.getTextureWidth(), y0 + texture.getTextureHeight());
                glTexCoord2f(0, 1 - corry);
                glVertex2f(x0, y0 + texture.getTextureHeight());
            
            glEnd();

            glBindTexture(GL_TEXTURE_2D, 0);

            glColor4f(1, 1, 1, 1);
            glLineWidth(1);
            glBegin(GL_LINE_STRIP);
                glVertex2f(x0, y0);
                glVertex2f(x0 + texture.getImageWidth(), y0);
                glVertex2f(x0 + texture.getImageWidth(), y0 + texture.getImageHeight());
                glVertex2f(x0, y0 + texture.getImageHeight());
                glVertex2f(x0, y0);
            glEnd();
            if (texframe) {
                glColor4f(0.5f, 0.5f, 1, 1);
                glBegin(GL_LINE_STRIP);
                    glVertex2f(x0, y0);
                    glVertex2f(x0 + texture.getTextureWidth(), y0);
                    glVertex2f(x0 + texture.getTextureWidth(), y0 + texture.getTextureHeight());
                    glVertex2f(x0, y0 + texture.getTextureHeight());
                    glVertex2f(x0, y0);
                glEnd();
            }
            
            Display.update();
            Display.sync(30);
        }
        
        texture.release();
        
        Display.destroy();
    }
}
