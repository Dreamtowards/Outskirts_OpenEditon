#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoords;
layout (location = 2) in vec3 normals;

out vec2 textureCoords;
out vec3 surfaceNormal;
out vec3 fragmentPosition;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {

    textureCoords = texCoords;

    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    surfaceNormal = normalize(normals);
    fragmentPosition = worldPosition.xyz;
}