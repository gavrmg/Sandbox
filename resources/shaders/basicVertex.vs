#version 330

layout (location = 0) in vec3 position;
layout (location = 0) in vec2 texCoord;

uniform mat4 transformation;
uniform mat4 ProjectionView;

out vec2 texCoord0;


void main(){
//	colorPos = vec4(clamp(position,0.0,1.0),1.0);
	texCoord0 = texCoord;
	gl_Position = ProjectionView*transformation*vec4(position,1);
//	gl_Position = ransformation*vec4(position,1);
//	gl_Position = vec4(position,1);
}

