#version 330 compatibility

layout(location = 0) in vec4 position;

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * position;
}