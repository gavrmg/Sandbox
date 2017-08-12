#version 450

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;

uniform mat4 transformation;
uniform mat4 ProjectionView;
uniform int id;
out vec3 texCoord0;

void main(){
//	colorPos = vec4(clamp(position,0.0,1.0),1.0);
	float ID2 = max(0, min(2, floor(id)));
	texCoord0 = vec3(texCoord,id);
	gl_Position = ProjectionView*transformation*vec4(position,1);
//	gl_Position = ransformation*vec4(position,1);
//	gl_Position = vec4(position,1);
}

