#version 450
in vec3 texCoord0;
//in int ID;
uniform sampler2DArray sampler2d;
//uniform sampler2D sampler1;

out vec4 fragColor;

void main(){
	//float id = ID;
	//vec3 texcoord = vec3(texCoord0.x,texCoord0.y,id);
	//gl_FragColor = texture2D(sampler1,texCoord0.xy);
	gl_FragColor = texture(sampler2d,texCoord0);
	//fragColor = vec4(1.0,0.0,0.0,1.0);
}

