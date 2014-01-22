#version 120

uniform sampler2D texture1;
varying vec3 color;

void main() {
    vec4 texColor = texture2D(texture1, gl_TexCoord[0].st);

    texColor.x = texColor.x * (color.x);
    texColor.y = texColor.y * (color.y);
    texColor.z = texColor.z * (color.z);

    gl_FragColor = texColor;
}