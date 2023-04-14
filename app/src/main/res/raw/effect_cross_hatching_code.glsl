precision highp float;
float texelWidth=1.0/1080.0;
float texelHeight=1.0/1080.0;
float paramIntensity=70.0;
const float lum_threshold_1 = 1.0;
const float lum_threshold_2 = 0.7;
const float lum_threshold_3 = 0.5;
const float lum_threshold_4 = 0.3;
vec4 effect() {
    float absHatchOffset = 9.0 - (paramIntensity / 100.0)*16.0;
    vec3 tc = vec3(1.0, 1.0, 1.0);
    vec2 absCoords = vec2(vTextureCoord.x/texelWidth, vTextureCoord.y/texelHeight);
    float absHatchMod = 2.0*absHatchOffset;
    float lum = length(texture2D(sTexture, vTextureCoord).rgb);
    if (lum < lum_threshold_1) {
        if (int(mod(floor(absCoords.x + absCoords.y), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);
    }
    if (lum < lum_threshold_2) {
        if (int(mod(floor(absCoords.x - absCoords.y), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);
    }
    if (lum < lum_threshold_3) {
        if (int(mod(floor(absCoords.x + absCoords.y - absHatchOffset), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);
    }
    if (lum < lum_threshold_4) {
        if (int(mod(floor(absCoords.x - absCoords.y - absHatchOffset), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);
    }
    return vec4(tc.rgb, 1.0);
}