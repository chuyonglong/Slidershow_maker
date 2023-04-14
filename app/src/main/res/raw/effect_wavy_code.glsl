precision mediump float;
uniform float _motion;
float paramIntensity=20.0;
float paramSize=20.0;
float paramSpeed=25.0;
vec4 effect() {
    float intensity = 0.005 + (paramIntensity / 100.0)*0.24;
    float size = 1.0 + (paramSize / 100.0)*30.0;
    float speed = 3.0 + (paramSpeed / 100.0)*27.0;
    vec2 uv = vTextureCoord + vec2(sin(speed*_motion + vTextureCoord.y * size) * intensity, 0.0);
    return texture2D(sTexture, uv);
}