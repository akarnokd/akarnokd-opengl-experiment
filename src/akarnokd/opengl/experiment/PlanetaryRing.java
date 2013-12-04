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
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;

/**
 *
 */
public class PlanetaryRing {
    public Planet parent;
    public int id;
    private int handle;
    private int handleSelect;
    public float x;
    public float y;
    public float z;
    public float minRadius;
    public float maxRadius;
    public float rotate;
    public static PlanetaryRing create(String ring, float minRadius, float maxRadius, int slices, int loops) {
        PlanetaryRing r = new PlanetaryRing();
        r.id = ObjectTracker.newId();
        
        Texture texture = Texture.fromFile(ring, false, true, GL_REPEAT, GL_LINEAR);
        
        {
            RingDisk d = new RingDisk();
            d.setDrawStyle(GLU.GLU_FILL);
            d.setTextureFlag(true);
            d.setNormals(GLU.GLU_SMOOTH);
            d.setOrientation(GLU.GLU_OUTSIDE);

            r.handle = glGenLists(1);
            glNewList(r.handle, GL_COMPILE);
            texture.use();
            d.draw(minRadius, maxRadius, slices, loops);
            texture.stop();
            glEndList();
        }
        {
            RingDisk d = new RingDisk();
            d.setDrawStyle(GLU.GLU_FILL);
            d.setNormals(GLU.GLU_SMOOTH);
            d.setOrientation(GLU.GLU_OUTSIDE);

            r.handleSelect = glGenLists(1);
            glNewList(r.handleSelect, GL_COMPILE);
            G2D.color(r.id);
            d.draw(minRadius, maxRadius, slices, loops);
            glEndList();
        }
        
        return r;
    }
    public void position(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Compute the position from the orbital information. */
    public void positionFromParent() {
        x = parent.x;
        y = parent.y;
        z = parent.z;
        rotate = parent.rotate;
    }
    public void draw(boolean select) {
        if (select) {
            glCallList(handleSelect);
        } else {
            glCallList(handle);
        }
    }
    public void drawFull(boolean forSelect) {
        glPushMatrix();
        glTranslatef(x, y, z);
        glRotatef(rotate, 0, 0, 1);
        draw(forSelect);
        glPopMatrix();
    }
}
