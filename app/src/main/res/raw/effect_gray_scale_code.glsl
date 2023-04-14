precision mediump float;
const highp vec3 weight = vec3(0.2125, 0.7154, 0.0721);
vec4 effect() {
    float luminance = dot(texture2D(sTexture, vTextureCoord).rgb, weight);
    return vec4(vec3(luminance), 1.0);
}