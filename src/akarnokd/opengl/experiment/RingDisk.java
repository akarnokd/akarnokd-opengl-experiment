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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import org.lwjgl.util.glu.Quadric;

/**
 * Generates a disk similar to glu.Disk, but the texture is
 * radially painted to a slice.
 */
public class RingDisk extends Quadric {
    static final float PI = (float)Math.PI;
            
    public void draw(float innerRadius, float outerRadius, int slices, int loops) {
        float da, dr;
        
        /* Normal vectors */
        if (super.normals != GLU_NONE) {
            if (super.orientation == GLU_OUTSIDE) {
                glNormal3f(0.0f, 0.0f, +1.0f);
            }
            else {
                glNormal3f(0.0f, 0.0f, -1.0f);
            }
        }
        
        da = 2.0f * PI / slices;
        dr = (outerRadius - innerRadius) /  loops;
        
        switch (super.drawStyle) {
            case GLU_FILL:
            {
                /* texture of a gluDisk is a cut out of the texture unit square
                * x, y in [-outerRadius, +outerRadius]; s, t in [0, 1]
                * (linear mapping)
                */
                float radialTexDiv = 1f / loops;
                float sa, ca;
                float r1 = innerRadius;
                int l;
                for (l = 0; l < loops; l++) {
                    float r2 = r1 + dr;
                    if (super.orientation == GLU_OUTSIDE) {
                        int s;
                        glBegin(GL_QUAD_STRIP);
                        for (s = 0; s <= slices; s++) {
                            float a;
                            if (s == slices)
                                a = 0.0f;
                            else
                                a = s * da;
                            sa = sin(a);
                            ca = cos(a);
                            TXTR_COORD(l * radialTexDiv, s);
                            glVertex2f(r2 * sa, r2 * ca);
                            TXTR_COORD((l + 1) * radialTexDiv, s);
                            glVertex2f(r1 * sa, r1 * ca);
                        }
                        glEnd();
                    }
                    else {
                        int s;
                        glBegin(GL_QUAD_STRIP);
                        for (s = slices; s >= 0; s--) {
                            float a;
                            if (s == slices)
                                a = 0.0f;
                            else
                                a = s * da;
                            sa = sin(a);
                            ca = cos(a);
                            TXTR_COORD(s, l * radialTexDiv);
                            glVertex2f(r2 * sa, r2 * ca);
                            TXTR_COORD(s, (l + 1) * radialTexDiv);
                            glVertex2f(r1 * sa, r1 * ca);
                        }
                        glEnd();
                    }
                    r1 = r2;
                }
                break;
            }
            case GLU_LINE:
            {
                int l, s;
                /* draw loops */
                for (l = 0; l <= loops; l++) {
                    float r = innerRadius + l * dr;
                    glBegin(GL_LINE_LOOP);
                    for (s = 0; s < slices; s++) {
                        float a = s * da;
                        glVertex2f(r * sin(a), r * cos(a));
                    }
                    glEnd();
                }
                /* draw spokes */
                for (s = 0; s < slices; s++) {
                    float a = s * da;
                    float x = sin(a);
                    float y = cos(a);
                    glBegin(GL_LINE_STRIP);
                    for (l = 0; l <= loops; l++) {
                        float r = innerRadius + l * dr;
                        glVertex2f(r * x, r * y);
                    }
                    glEnd();
                }
                break;
            }
            case GLU_POINT:
            {
                int s;
                glBegin(GL_POINTS);
                for (s = 0; s < slices; s++) {
                    float a = s * da;
                    float x = sin(a);
                    float y = cos(a);
                    int l;
                    for (l = 0; l <= loops; l++) {
                        float r = innerRadius * l * dr;
                        glVertex2f(r * x, r * y);
                    }
                }
                glEnd();
                break;
            }
            case GLU_SILHOUETTE:
            {
                if (innerRadius != 0.0) {
                    float a;
                    glBegin(GL_LINE_LOOP);
                    for (a = 0.0f; a < 2.0 * PI; a += da) {
                        float x = innerRadius * sin(a);
                        float y = innerRadius * cos(a);
                        glVertex2f(x, y);
                    }
                    glEnd();
                }
                {
                    float a;
                    glBegin(GL_LINE_LOOP);
                    for (a = 0; a < 2.0f * PI; a += da) {
                        float x = outerRadius * sin(a);
                        float y = outerRadius * cos(a);
                        glVertex2f(x, y);
                    }
                    glEnd();
                }
                break;
            }
            default:
                return;
        }
    }
}
