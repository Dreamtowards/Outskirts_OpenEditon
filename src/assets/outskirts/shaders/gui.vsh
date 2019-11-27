#version 330 core
layout (location = 0) in vec3 position; //vec3(not vec2) for compatible to Loader.loadModel()
layout (location = 1) in vec2 texCoords;

out vec2 textureCoords;

uniform vec2 offset;
uniform vec2 scale;

void main() {

    gl_Position = vec4(position.xy * scale + offset, 0, 1.0);

    textureCoords = texCoords;
}