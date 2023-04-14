precision highp float;
float texelWidth=1.0/1080.0;
float texelHeight=1.0/1080.0;
const int invert = 1;
uniform float paramIntensity;
vec4 effect() {
    float intensity = 3.5 - (paramIntensity / 100.0)*2.75;
    vec2 uv = vTextureCoord;
    float stitching_size = 9.0 * sqrt(pow(1.0/texelWidth/100.0, 2.0) + pow(1.0/texelHeight/100.0, 2.0)) / 16.0;
    stitching_size = max(3.0, floor(intensity * stitching_size));
    vec2 cPos = vec2(uv.x / texelWidth, uv.y / texelHeight);
    vec2 tlPos = floor(cPos / vec2(stitching_size, stitching_size)) * stitching_size;
    int remX = int(mod(cPos.x, stitching_size));
    int remY = int(mod(cPos.y, stitching_size));
    if (remX == 0 && remY == 0) tlPos = cPos;
    vec2 blPos = tlPos;
    blPos.y += (stitching_size - 1.0);
    vec4 c;
    if ((remX == remY) || (((int(cPos.x) - int(blPos.x)) == (int(blPos.y) - int(cPos.y))))) {
        c = invert == 1 ? vec4(0.2, 0.15, 0.05, 1.0) : 1.4*texture2D(sTexture, tlPos * vec2(texelWidth, texelHeight));
    } else {
        c = invert == 1 ? 1.4*texture2D(sTexture, tlPos * vec2(texelWidth, texelHeight)) : vec4(0.0, 0.0, 0.0, 1.0);
    }
    return c;
}