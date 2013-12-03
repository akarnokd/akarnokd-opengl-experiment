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
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Load and compile a vertex and fragment shader program.
 */
public class ShaderProgram {
    /** The program identifier. */
    private int program;
    private int vertex;
    private int fragment;
    private ShaderProgram() { }
    /** The cached uniform locations. */
    private final Map<String, Integer> uniformCache = new HashMap<>();
    /**
     * Creates a shader program from the vertex and fragment shaders.
     * @param vertex
     * @param fragment
     * @return 
     */
    public static ShaderProgram create(Supplier<String> vertex, Supplier<String> fragment) {
        
        ShaderProgram sp = new ShaderProgram();
        
        sp.vertex = createShader(ARBVertexShader.GL_VERTEX_SHADER_ARB, vertex);
        sp.fragment = createShader(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB, fragment);
        
        if (sp.vertex != 0 && sp.fragment != 0) {
            sp.program = glCreateProgramObjectARB();
            if (sp.program != 0) {
                
                glAttachObjectARB(sp.program, sp.vertex);
                glAttachObjectARB(sp.program, sp.fragment);
                
                glLinkProgramARB(sp.program);
                if (glGetObjectParameteriARB(sp.program, GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
                    sp.close();
                    throw new RuntimeException("Program failed: " + getLogInfo(sp.program));
                }
                glValidateProgramARB(sp.program);
                if (glGetObjectParameteriARB(sp.program, GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
                    sp.close();
                    throw new RuntimeException("Program failed: " + getLogInfo(sp.program));
                }
            } else {
                sp.close();
                throw new RuntimeException("Unable to create program");
            }
        }
        
        return sp;
    }
    /** 
     * @return check if the program is valid
     */
    public boolean isValid() {
        return program != 0;
    }
    /** Releases the resources. */
    public void close() {
        if (program != 0) {
            glDeleteObjectARB(program);
            program = 0;
        }
        if (vertex != 0) {
            glDeleteObjectARB(vertex);
            vertex = 0;
        }
        if (fragment != 0) {
            glDeleteObjectARB(fragment);
            fragment = 0;
        }
    }
    /** Start using the program. */
    public void use() {
        glUseProgramObjectARB(program);
    }
    /** Stop using the program. */
    public void stop() {
        glUseProgramObjectARB(0);
    }
    /**
     * Create and compile a shader object
     * @param shaderType the shader type: ARBVertexShader.GL_VERTEX_SHADER_ARB or ARBFragmentShader.GL_FRAGMENT_SHADER_ARB
     * @param shaderCode the shader code supplier
     * @return the shader id or null if not supported
     */
    private static int createShader(int shaderType, Supplier<String> shaderCode) {
        int sid = glCreateShaderObjectARB(shaderType);
        if (sid == 0) {
            return 0;
        }
        glShaderSourceARB(sid, shaderCode.get());
        glCompileShaderARB(sid);
        
        if (glGetObjectParameteriARB(sid, GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
            glDeleteObjectARB(sid);
            throw new RuntimeException("Error creating shader: " + getLogInfo(sid));
        }
        return sid;
    }
    /** Return the log info. */
    static String getLogInfo(int obj) {
        return glGetInfoLogARB(obj, glGetObjectParameteriARB(obj, GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
    /**
     * Locate an uniform variable.
     * @param name
     * @return 
     */
    public int findUniform(String name) {
        Objects.requireNonNull(name);
        Integer r = uniformCache.get(name);
        if (r == null) {
            r = glGetUniformLocationARB(program, name);
            if (r < 0) {
                throw new IllegalArgumentException("Uniform not found: " + name);
            }
            uniformCache.put(name, r);
        }
        return r;
    }
    /**
     * Set the value of an uniform shader variable.
     * @param name
     * @param x
     * @param y 
     */
    public void setUniformf(String name, float x, float y) {
         int index = findUniform(name);
         glUniform2fARB(index, x, y);
    }
    /**
     * Set the value of an uniform shader variable.
     * @param name
     * @param x
     * @param y 
     */
    public void setUniformi(String name, int x, int y) {
         int index = findUniform(name);
         glUniform2iARB(index, x, y);
    }
    /**
     * Set the value of an uniform shader variable.
     * @param name
     * @param x
     * @param y
     * @param z
     */
    public void setUniformf(String name, float x, float y, float z) {
         int index = findUniform(name);
         glUniform3fARB(index, x, y, z);
    }
    /**
     * Set the value of an uniform shader variable.
     * @param name
     * @param x
     * @param y
     * @param z
     */
    public void setUniformi(String name, int x, int y, int z) {
         int index = findUniform(name);
         glUniform3iARB(index, x, y, z);
    }
    /**
     * Set the value of an uniform shader variable.
     * @param name
     * @param x
     * @param y
     * @param z
     * @param w
     */
    public void setUniformf(String name, float x, float y, float z, float w) {
         int index = findUniform(name);
         glUniform4fARB(index, x, y, z, w);
    }
    /**
     * Set the value of an uniform shader variable.
     * @param name
     * @param x
     * @param y
     * @param z
     * @param w
     */
    public void setUniformi(String name, int x, int y, int z, int w) {
         int index = findUniform(name);
         glUniform4iARB(index, x, y, z, w);
    }
    /**
     * Set the value of an uniform shader variable.
     * @param name
     * @param x
     */
    public void setUniformf(String name, float x) {
         int index = findUniform(name);
         glUniform1fARB(index, x);
    }
    /**
     * Set the value of an uniform shader variable.
     * @param name
     * @param x
     */
    public void setUniformi(String name, int x) {
         int index = findUniform(name);
         glUniform1iARB(index, x);
    }
    /**
     * Set a Matrix4f
     * @param name
     * @param transposed
     * @param mat 
     */
    public void setUniformMatrix(String name, boolean transposed, Matrix4f mat) {
        int index = findUniform(name);
        FloatBuffer b16 = BufferUtils.createFloatBuffer(16);
        mat.store(b16);
        b16.flip();
        glUniformMatrix4ARB(index, transposed, b16);
    }
    /**
     * Set an array of values.
     * @param name
     * @param array 
     */
    public void setUniformi(String name, int[] array) {
        int index = findUniform(name);
        IntBuffer ib = BufferUtils.createIntBuffer(array.length);
        ib.put(array);
        ib.rewind();
        glUniform1ARB(index, ib);
    }
    /**
     * Set an array of values.
     * @param name
     * @param array 
     */
    public void setUniformf(String name, float[] array) {
        int index = findUniform(name);
        FloatBuffer fb = BufferUtils.createFloatBuffer(array.length);
        fb.put(array);
        fb.rewind();
        glUniform1ARB(index, fb);
    }
}
