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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.TextureImpl;

/**
 *
 */
public class ClippingTest {
    public static int power2Up(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }
    public static int loadTexture(BufferedImage img0) {
        BufferedImage img = img0;
        
        int w0 = power2Up(img0.getWidth());
        int h0 = power2Up(img0.getHeight());
        
        // expand texture to power of 2 size, otherwise it would be stretched
        if (w0 != img0.getWidth() || h0 != img.getHeight()) {
            img = new BufferedImage(w0, h0, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.drawImage(img0, 0, 0, null);
            g2.dispose();
        }
        
        int[] pixels = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);

        for (int px : pixels) {
            buffer.put((byte)((px >> 16) & 0xFF));
            buffer.put((byte)((px >> 8) & 0xFF));
            buffer.put((byte)((px) & 0xFF));
            buffer.put((byte)((px >> 24) & 0xFF));
        }
        buffer.flip();
        
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        
        return id;
    }
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
        

        BufferedImage img0 = ImageIO.read(new File("res/colony_hub.png"));

        int texture = loadTexture(img0);

        TextureImpl.bindNone();
        
        Font awtfont = new Font("Times New Roman", Font.BOLD, 24);
        
        UnicodeFont font = new UnicodeFont(awtfont);
        @SuppressWarnings("unchecked")
        List<Object> eff = (List<Object>)font.getEffects();
        eff.add(new ColorEffect(java.awt.Color.orange));
        font.addAsciiGlyphs();
        font.loadGlyphs();

        int x0a = 30;
        int y0a = 30;
        
        boolean texframea = false;
        int texcorra = 0;
        
        while (!Display.isCloseRequested()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
                x0a -= 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
                x0a += 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
                y0a -= 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
                y0a += 1;
            }
            while (Keyboard.next()) {
                if (Character.toUpperCase(Keyboard.getEventCharacter()) == 'F') {
                    texframea = !texframea;
                }
                if (Character.toUpperCase(Keyboard.getEventCharacter()) == 'C') {
                    texcorra = (texcorra + 1) % 3;
                }
            }
            
            glClear(GL_COLOR_BUFFER_BIT);

            float ty = y0a + img0.getHeight();
            
            int x0 = x0a;
            int y0 = y0a;
            int texcorr = texcorra;
            boolean texframe = texframea;
            
            G2D.clip(100, 100, 300, 300, () -> {
                font.drawString(x0, ty, "Colony hub", Color.orange);

                glColor4f(1, 1, 1, 1);

                glBindTexture(GL_TEXTURE_2D, texture);

                float corrx = 0;
                float corry = 0;

                int tw = 512;
                int th = 512;
                int iw = img0.getWidth();
                int ih = img0.getHeight();


                if (texcorr == 1) {
                    corrx = 1f / tw;
                    corry = 1f / th;
                } else
                if (texcorr == 2) {
                    corrx = 0.5f / tw;
                    corry = 0.5f / th;
                }

                glBegin(GL_QUADS);
                    glTexCoord2f(0, 0);
                    glVertex2f(x0, y0);
                    glTexCoord2f(1 - corrx, 0);
                    glVertex2f(x0 + tw, y0);
                    glTexCoord2f(1 - corrx, 1 - corry);
                    glVertex2f(x0 + tw, y0 + th);
                    glTexCoord2f(0, 1 - corry);
                    glVertex2f(x0, y0 + th);

                glEnd();

                glBindTexture(GL_TEXTURE_2D, 0);

                glColor4f(1, 1, 1, 1);
                glLineWidth(1);
                glTranslatef(0.5f, 0.5f, 0); // otherwise, the lines don't join cleanly.
                glBegin(GL_LINE_STRIP);
                    glVertex2i(x0, y0);
                    glVertex2i(x0, y0 + ih);
                    glVertex2i(x0 + iw, y0 + ih);
                    glVertex2i(x0 + iw, y0);
                    glVertex2i(x0, y0);
                glEnd();
                glTranslatef(-0.5f, -0.5f, 0);
                if (texframe) {
                    glColor4f(0.5f, 0.5f, 1, 1);
                    glBegin(GL_LINE_STRIP);
                        glVertex2f(x0, y0);
                        glVertex2f(x0 + tw, y0);
                        glVertex2f(x0 + tw, y0 + th);
                        glVertex2f(x0, y0 + th);
                        glVertex2f(x0, y0);
                    glEnd();
                }
            });
            
            Display.update();
            Display.sync(30);
        }

        glDeleteTextures(texture);
        
        Display.destroy();
    }
}
