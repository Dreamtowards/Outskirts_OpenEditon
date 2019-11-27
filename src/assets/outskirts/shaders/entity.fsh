#version 330 core

struct Light {
    vec3 color;
    vec3 position;
};

in vec2 textureCoords;
in vec3 surfaceNormal;
in vec3 fragmentPosition;

uniform sampler2D textureSampler;

uniform Light light;

void main() {

    float gamma = 0.1; //ambient

    vec3 toLightDirection = normalize(light.position - fragmentPosition);
    float diffuse = max(dot(toLightDirection, surfaceNormal), 0.0f);



    gl_FragColor = vec4((gamma + diffuse) * light.color, 1.0f) * texture(textureSampler, textureCoords);

}