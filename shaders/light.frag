#version 330 compatibility

uniform vec2 lightPosition;
uniform vec3 lightColor;
uniform float lightIntensity;

void main() {
	float distance = length(lightPosition - gl_FragCoord.xy);
	float attenuation = 1.0 / distance;
	vec4 color = vec4(attenuation, attenuation, attenuation, pow(attenuation, 3)) * vec4(lightColor * lightIntensity, 1.0);
	color *= 0.5;
	gl_FragColor = color; 
}