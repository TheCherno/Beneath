#version 330 compatibility

in vec2 texCoords;
uniform sampler2D texture;

void main() {
	vec4 color = texture2D(texture, texCoords);
	if (color == vec4(1.0, 0.0, 1.0, 1.0)) color = vec4(0.0, 0.0, 0.0, 0.0);
	gl_FragColor = color;
}