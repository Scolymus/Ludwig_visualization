#version 450

//https://github.com/kbinani/glsl-colormap

in float Color;
in float AlphaC;

uniform float phi_max;
uniform float phi_min;
uniform float phi_zero;

out vec4 outColor;

vec3 colormap(float x, float xmin, float xmax, float xzero) {
	if (x>=xzero) {
		return vec3(.655555556,(x-xzero)/(xmax-xzero),1.0);
	} else {
		return vec3(.105555556,(xzero-x)/(xzero-xmin),1.0);
	}
}


//https://stackoverflow.com/questions/15095909/from-rgb-to-hsv-in-opengl-glsl
// All components are in the range [0…1], including hue.
vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}
  
void main()
{
	//WATCH OUT! ONLY 5,7 and 15 are corrected for color max!
	//Otherwise, x should be: 0<=x<=1
    outColor = vec4(hsv2rgb(colormap(Color, phi_min, phi_max, phi_zero)), AlphaC);
    //outColor = vec4(Color, 100, 25, AlphaC);
}
