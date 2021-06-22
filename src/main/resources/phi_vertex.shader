#version 450

uniform mat4 viewMatrix, projMatrix;
in float color;
in vec4 position;
uniform float Radius;
uniform vec3 pos;

out float Color;
out float AlphaC;

void main()
{
    Color = color;
    AlphaC = 1.0;

    gl_Position = projMatrix * viewMatrix * position ;

}