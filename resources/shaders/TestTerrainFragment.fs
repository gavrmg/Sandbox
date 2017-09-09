#version 450
in vec3 texCoord0;
in vec3 Normal;
in vec3 pos;
in float Amb;
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
	float color;
	float coeff;
};
uniform PointLight testlight;
uniform DiffuseLight difflight;


vec4 calcPointLight(PointLight pointLight, vec3 Pos, vec3 normal){
	vec3 to_Light = pointLight.position - Pos;
	float distance = length(to_Light);
	vec3 lightDir = normalize(to_Light);
	float attenuationFactor = 1f/(pointLight.attenuation.constant+pointLight.attenuation.linear*distance + pointLight.attenuation.exponential*distance*distance);
//	float diffusePart = max(dot(lightDir,normal),dot(lightDir,-normal))*pointLight.intensity;
	float diffusePart = clamp(dot(lightDir,normal),0,1)*pointLight.intensity;
	return diffusePart*vec4(pointLight.color,1)*attenuationFactor;
}



void main(){
//	gl_FragColor = calcPoint(testlight,pos,Normal);
	vec4 texColor = texture(sampler2d,texCoord0);
//	gl_FragColor = texColor*calcPointLight(point_light,pos,Normal);
	gl_FragColor = clamp(texColor*(vec4(difflight.coeff)+calcPointLight(testlight,pos,Normal)),vec4(0),vec4(1));
}

