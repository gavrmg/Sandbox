#version 450
in vec3 texCoord0;
in vec3 Normal;
in vec3 pos;

uniform sampler2DArray sampler2d;
uniform float amb;
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
in PointLight point_light;
in DiffuseLight diff_light;



vec4 calcPoint(PointLight pointLight, vec3 Pos, vec3 normal){
	vec3 to_Light = pointLight.position - Pos;
	float distance = length(to_Light);
	vec3 lightDir = normalize(to_Light);
	float attenuationFactor = 1f/(pointLight.attenuation.constant+pointLight.attenuation.linear*distance + pointLight.attenuation.exponential*distance*distance);
	float diffusePart = max(dot(lightDir,normal),0)*pointLight.intensity;
	return texture(sampler2d,texCoord0)*pointLight.attenuation.constant;//+diffusePart*vec4(pointLight.color,1)*attenuationFactor;
}



void main(){
	gl_FragColor = calcPoint(point_light,pos,Normal);
//	gl_FragColor = texture(sampler2d,texCoord0)*amb;
}


