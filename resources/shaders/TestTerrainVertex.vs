#version 450

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 texCoord;
layout (location = 2) in vec3 normal;

struct Attenuation{
	float constant;
	float linear;
	float exponential;
};

struct PointLight{
	vec3 position;
	vec3 color;
	float intensity;
	Attenuation attenuation;
};
struct DiffuseLight{
	vec3 color;
	float coeff;
};
uniform mat4 transformationView;
uniform mat4 Projection;
uniform mat4 MVIT;
uniform PointLight testlight;
uniform DiffuseLight difflight;

//uniform float
out vec3 texCoord0;
out vec3 Normal;
out vec3 pos;
out PointLight point_light;
out DiffuseLight diff_light;
void main(){
	point_light = testlight;
	diff_light = difflight;
	texCoord0 = texCoord;
	Normal = vec3((MVIT*vec4(normal,0)));
	gl_Position = Projection*transformationView*vec4(position,1);
	pos = gl_Position.xyz;
}