#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoords;

#define FONT_INSTANCE_MAX 64

out vec2 textureCoords;
flat out int unicodePage;
flat out vec4 charColor;

uniform float glyphWidth[FONT_INSTANCE_MAX];
uniform vec2 offset[FONT_INSTANCE_MAX];
uniform int string[FONT_INSTANCE_MAX]; //for page, TEX_COORDS calculation
uniform vec4 color[FONT_INSTANCE_MAX];

uniform float textHeight;    //0.0 - 1.0 screen percent
uniform float textUnitWidth;

float mod(float a, float b) {
    return a - (b * floor(a/b));
}

void main() {

    vec2 pos = position.xy * vec2(textUnitWidth * glyphWidth[gl_InstanceID], textHeight) + offset[gl_InstanceID];

    gl_Position = vec4(pos, 0.0, 1.0);


    int ch = string[gl_InstanceID];

    vec2 textureOffset = vec2(
         mod(ch, 16) / 16,
         1 - (int(mod(ch, 256)) / 16 / 16.0) - 1/16.0
    );
    textureCoords = texCoords * vec2(glyphWidth[gl_InstanceID], 1) / 16 + textureOffset;


    unicodePage = int(ch / 256);

    charColor = color[gl_InstanceID];
}