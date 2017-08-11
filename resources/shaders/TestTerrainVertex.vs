#version 450

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 texCoord;
layout (location = 2) in vec3 normal;


uniform mat4 transformationView;
uniform mat4 Projection;
uniform mat4 MVIT;
uniform float amb;
//uniform float
out vec3 texCoord0;
out vec3 Normal;
out vec3 pos;
out float Amb;
void main(){
	texCoord0 = texCoord;
	Normal = normalize((MVIT*vec4(normal,0)).xyz);
//	Normal = normal;
	Amb = amb;
	vec4 Pos =transformationView*vec4(position,1);
	pos = Pos.xyz;
	//gl_Position = Projection*transformationView*vec4(position,1);
	gl_Position = Projection*Pos;
	
	//pos = gl_Position.xyz;
}