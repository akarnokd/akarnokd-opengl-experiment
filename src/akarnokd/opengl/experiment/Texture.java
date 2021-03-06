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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * Manages a texture object.
 * Uses GL_LINEAR and GL_CLAMP_TO_EDGE.
 */
public class Texture {
    private int handle;
    private int originalWidth;
    private int originalHeight;
    private int textureWidth;
    private int textureHeight;
    public int originalWidth() {
        return originalWidth;
    }
    public int originalHeight() {
        return originalHeight;
    }
    public int textureWidth() {
        return textureWidth;
    }
    public int textureHeight() {
        return textureHeight;
    }
    public boolean isValid() {
        return handle != 0;
    }
    /** @return the texture's identifier. */
    public int handle() {
        return handle;
    }
    /**
     * Start using the texture.
     */
    public void use() {
//        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, handle);
    }
    /**
     * Activate the given texture unit and use this texture.
     * Don't forget to reactivate GL_TEXTURE0 once finished.
     * @param textureUnit see GL13.GL_TEXTURE0 .. GL13.GL_TEXTURE_30
     */
    public void useWith(int textureUnit) {
        glActiveTexture(textureUnit);
        use();
    }
    /**
     * Stop using the texture.
     */
    public void stop() {
        glBindTexture(GL_TEXTURE_2D, 0);
//        glDisable(GL_TEXTURE_2D);
    }
    /** Free the texture resource. */
    public void close() {
        if (handle != 0) {
            glDeleteTextures(handle);
            handle = 0;
        }
    }
    /** Find the next power-of-2 value greater or equal to v. */
    static int power2Up(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }
    /**
     * Loads a texture from the given resource name.
     * @param resource
     * @return 
     */
    public static Texture fromResource(String resource) {
        try (InputStream in = Texture.class.getResourceAsStream(resource)) {
            return fromImage(ImageIO.read(in));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Loads a texture from the given file.
     * @param path
     * @return 
     */
    public static Texture fromFile(String path) {
        try {
            return fromImage(ImageIO.read(new File(path)));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Loads a texture from the given file.
     * @param path
     * @param powerOf2
     * @return 
     */
    public static Texture fromFile(String path, boolean powerOf2) {
        try {
            return fromImage(ImageIO.read(new File(path)), powerOf2, false, GL_CLAMP_TO_EDGE, GL_NEAREST);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
        /**
     * Loads a texture from the given file.
     * @param path
     * @param powerOf2
     * @param mirrorX
     * @return 
     */
    public static Texture fromFile(String path, boolean powerOf2, boolean mirrorX) {
        try {
            return fromImage(ImageIO.read(new File(path)), powerOf2, mirrorX, GL_CLAMP_TO_EDGE, GL_NEAREST);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Loads a texture from the given file.
     * @param path
     * @param powerOf2
     * @param mirrorX mirror in the X direction?
     * @param wrapMode GL_CLAMP, GL_REPEAT, GL_CLAMP_TO_EDGE
     * @param filterMode GL_NEAREST, GL_LINEAR
     * @return 
     */
    public static Texture fromFile(String path, boolean powerOf2, boolean mirrorX, int wrapMode, int filterMode) {
        try {
            return fromImage(ImageIO.read(new File(path)), powerOf2, mirrorX, wrapMode, filterMode);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Loads a texture from the given file.
     * @param file
     * @return 
     */
    public static Texture fromFile(File file) {
        try {
            return fromImage(ImageIO.read(file));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Loads a texture from the given buffered image.
     * @param sourceImage
     * @return 
     */
    public static Texture fromImage(BufferedImage sourceImage) {
        return fromImage(sourceImage, true, false, GL_CLAMP_TO_EDGE, GL_NEAREST);
    }
    /**
     * Reverse a part of the array.
     * @param array
     * @param from
     * @param to 
     */
    static void reverse(int[] array, int from, int to) {
        int half = (to - from) / 2;
        for (int i = 0; i < half; i++) {
            int v = array[from + i];
            array[from + i] = array[to - i - 1];
            array[to - i - 1] = v;
        }
    }
    /**
     * Loads a texture from the given buffered image.
     * @param sourceImage
     * @param powerOf2
     * @param mirrorX mirror in the X direction?
     * @param wrapMode GL_CLAMP, GL_REPEAT, GL_CLAMP_TO_EDGE
     * @param filterMode GL_NEAREST, GL_LINEAR
     * @return 
     */
    public static Texture fromImage(BufferedImage sourceImage, boolean powerOf2, boolean mirrorX, int wrapMode, int filterMode) {
        Texture r = new Texture();
        BufferedImage img = sourceImage;
        
        r.originalWidth = sourceImage.getWidth();
        r.originalHeight = sourceImage.getHeight();
        
        int w0 = sourceImage.getWidth();
        int h0 = sourceImage.getHeight();
        if (powerOf2) {
            w0 = power2Up(w0);
            h0 = power2Up(h0);
        }

        r.textureWidth = w0;
        r.textureHeight = h0;
        
        // expand texture to power of 2 size, otherwise it would be stretched
        if (w0 != sourceImage.getWidth() || h0 != img.getHeight()) {
            img = new BufferedImage(w0, h0, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.drawImage(sourceImage, 0, 0, null);
            g2.dispose();
        }
        
        int[] pixels = new int[w0 * h0];
        img.getRGB(0, 0, w0, h0, pixels, 0, w0);
        ByteBuffer buffer = BufferUtils.createByteBuffer(w0 * h0 * 4);

        if (mirrorX) {
            for (int j = 0; j < h0; j++) {
                reverse(pixels, j * w0, (j + 1) * w0);
            }
        }
        
        for (int px : pixels) {
            buffer.put((byte)((px >> 16) & 0xFF));
            buffer.put((byte)((px >> 8) & 0xFF));
            buffer.put((byte)((px) & 0xFF));
            buffer.put((byte)((px >> 24) & 0xFF));
        }
        buffer.flip();
        
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterMode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterMode);
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        glBindTexture(GL_TEXTURE_2D, 0);

        r.handle = id;
        
        return r;
    }
    /**
     * Draw the texture to the specified position.
     * @param x
     * @param y 
     */
    public void draw(int x, int y) {
        use();
        glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2i(x, y);
            glTexCoord2f(1, 0);
            glVertex2i(x + textureWidth, y);
            glTexCoord2f(1, 1);
            glVertex2i(x + textureWidth, y + textureHeight);
            glTexCoord2f(0, 1);
            glVertex2i(x, y + textureHeight);
        glEnd();
        stop();
    }
    /**
     * Draw only a subimage of this texture.
     * @param x
     * @param y
     * @param ix
     * @param iy
     * @param iw
     * @param ih 
     */
    public void drawSubimage(int x, int y, int ix, int iy, int iw, int ih) {
        float fx0 = 1f * ix / textureWidth;
        float fx1 = 1f * (ix + iw) / textureWidth;
        float fy0 = 1f * iy / textureHeight;
        float fy1 = 1f * (iy + ih) / textureHeight;
        use();
        glBegin(GL_QUADS);
            glTexCoord2f(fx0, fy0);
            glVertex2i(x, y);
            glTexCoord2f(fx1, fy0);
            glVertex2i(x + iw, y);
            glTexCoord2f(fx1, fy1);
            glVertex2i(x + iw, y + ih);
            glTexCoord2f(fx0, fy1);
            glVertex2i(x, y + ih);
        glEnd();
        stop();
    }
    /**
     * Enable the usage of textures and blending.
     */
    public static void enable() {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
}
