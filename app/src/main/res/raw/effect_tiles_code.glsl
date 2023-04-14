precision highp float;
const vec3 edgeColor = vec3(0.7);
const float threshhold = 0.15;
float texelWidth=1.0/1080.0;
float paramIntensity=1.0/1080.0;
vec2 fmod(vec2 a, vec2 b) { return abs(fract(abs(a / b)) * abs(b)); }
vec4 effect() {
    float numTiles = 30.0 + (paramIntensity * texelWidth * 500.0);
    vec2 uv = vTextureCoord;
    float size = 1.0 / numTiles;
    vec2 pBase = uv - fmod(uv, vec2(size));
    vec2 pCenter = pBase + vec2(size / 2.0);
    vec2 st = (uv - pBase) / size;
    vec4 invOff = vec4((1.0 - edgeColor), 1.0);
    float threshholdB = 1.0 - threshhold;
    vec4 c1 = st.x > st.y ? invOff : vec4(0);
    vec4 cBottom = st.x > threshholdB || st.y > threshholdB ? c1 : vec4(0);
    c1 = st.x > st.y ? invOff : vec4(0);
    vec4 cTop = st.x < threshhold || st.y < threshhold ? c1 : vec4(0);
    vec4 tileColor = texture2D(sTexture, pCenter);
    return tileColor + cTop - cBottom;
}