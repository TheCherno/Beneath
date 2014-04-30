#version 330 compatibility

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 coords;

out vec2 texCoords;

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * position;
	texCoords = coords * vec2(1.0, -1.0);
}