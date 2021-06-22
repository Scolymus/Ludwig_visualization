#version 450

uniform mat4 viewMatrix, projMatrix;
uniform float Radius;

in vec4 position;
uniform vec3 pos[N];	//WATCH OUT! MAYBE TEXTURE????

vec3 poss;

in float color;

float d;
vec4 complete;
vec4 complete2;

out float Color;
out float AlphaC;

void main()
{
    Color = color;
    AlphaC = 0.05;

	complete = projMatrix * viewMatrix * position ;
	
    for ( int i = 0; i < pos[i].length(); i++ ) {
    	complete2 = projMatrix * viewMatrix * vec4(pos[i],1) ;
        d = distance(complete2,complete);
        if (d<Radius) {
           AlphaC = 1.0;
           break;
        }
    }
	
	gl_Position = complete;

}