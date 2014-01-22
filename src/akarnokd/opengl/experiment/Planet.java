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

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

/**
 *
 */
public class Planet {
    private int handle;
    private int handleSelect;
    private int wireframe;
    public Planet parent;
    public float orbit;
    public float angle;
    public float rotate;
    public float x;
    public float y;
    public float z;
    public int id;
    public float radius;
    public int smoothness;
    public Texture tex;
    public static Planet create(String file, float radius, int smoothness) {
        Planet p = new Planet();
        p.id = ObjectTracker.add(p);
        p.radius = radius;
        p.smoothness = smoothness;
        
        // GLU sphere requires a mirrored texture ?!
        p.tex = Texture.fromFile(file, false, true);

        {
            Sphere sphere = new Sphere();
            sphere.setDrawStyle(GLU.GLU_FILL);
            sphere.setTextureFlag(true);
            sphere.setNormals(GLU.GLU_SMOOTH);
            sphere.setOrientation(GLU.GLU_OUTSIDE);

            p.handle = GL11.glGenLists(1);

            GL11.glNewList(p.handle, GL11.GL_COMPILE);
            p.tex.use();
            GL11.glColor4f(1, 1, 1, 1);
            sphere.draw(radius, smoothness, smoothness);
            p.tex.stop();
            GL11.glEndList();
        }
        {
            Sphere sphere2 = new Sphere();
            sphere2.setDrawStyle(GLU.GLU_FILL);
            sphere2.setNormals(GLU.GLU_SMOOTH);
            sphere2.setOrientation(GLU.GLU_OUTSIDE);

            p.handleSelect = GL11.glGenLists(1);
            GL11.glNewList(p.handleSelect, GL11.GL_COMPILE);
            G2D.color(p.id);
            sphere2.draw(radius, smoothness, smoothness);
            GL11.glEndList();
        }
        {
            Sphere sphere2 = new Sphere();
            sphere2.setDrawStyle(GLU.GLU_LINE);
            sphere2.setNormals(GLU.GLU_NONE);

            p.wireframe = GL11.glGenLists(1);
            GL11.glNewList(p.wireframe, GL11.GL_COMPILE);
            sphere2.draw(radius, smoothness, smoothness);
            GL11.glEndList();
        }
        
        return p;
    }

    public void position(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Compute the position from the orbital information. */
    public void positionFromOrbit() {
        x = parent.x + orbit * (float) Math.cos(angle);
        y = parent.y + orbit * (float) Math.sin(angle);
        z = parent.z;
    }

    public void draw() {
        GL11.glCallList(handle);
    }

    /** Draw with full position and rotation information. */
    public void drawFull() {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(rotate, 0, 0, 1);
        draw();
        GL11.glPopMatrix();
    }
    public void draw(boolean forSelect) {
        if (forSelect) {
            GL11.glCallList(handleSelect);
        } else {
            GL11.glCallList(handle);
        }
    }
    public void drawFull(boolean forSelect) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(rotate, 0, 0, 1);
        draw(forSelect);
        GL11.glPopMatrix();
    }
    public void drawWireframe() {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(rotate, 0, 0, 1);

        GL11.glCallList(wireframe);

        GL11.glPopMatrix();
    }
}
