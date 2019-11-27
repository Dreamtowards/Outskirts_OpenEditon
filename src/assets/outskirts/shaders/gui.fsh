#version 330 core

in vec2 textureCoords;

uniform sampler2D textureSampler;
uniform vec4 colorMultiply;

void main() {


    gl_FragColor = texture(textureSampler, textureCoords) * colorMultiply;

}