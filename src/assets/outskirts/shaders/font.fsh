#version 330 core

in vec2 textureCoords;
flat in int unicodePage;
flat in vec4 charColor;

uniform sampler2DArray unicodeTextureArray;

void main() {


    gl_FragColor = texture(unicodeTextureArray, vec3(textureCoords, unicodePage)) * charColor;

}