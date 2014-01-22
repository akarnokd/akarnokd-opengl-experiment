#version 120

uniform sampler2D texture1;
varying vec3 color;

void main() {
    gl_TexCoord[0] = gl_MultiTexCoord0;

    vec3 vertexPosition = (gl_ModelViewMatrix * gl_Vertex).xyz;
    vec3 lightDirection = normalize(gl_LightSource[0].position.xyz - vertexPosition);
    vec3 surfaceNormal = (gl_NormalMatrix * gl_Normal).xyz;
    float diffuseLightIntensity = max(0, dot(surfaceNormal, lightDirection));
    color.rgb = diffuseLightIntensity * vec3(1, 1, 1);
    color += gl_LightModel.ambient.rgb;

    vec3 reflectionDirection = normalize(reflect(-lightDirection, surfaceNormal));
    float specular = max(0, dot(surfaceNormal, reflectionDirection));
    if (diffuseLightIntensity != 0) {
        float fspecular = pow(specular, gl_FrontMaterial.shininess);
        color.rgb += vec3(fspecular, fspecular, fspecular);
    }

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}