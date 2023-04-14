precision mediump float;

uniform float _motion;
float texelWidth=1.0/1080.0;
float texelHeight=1.0/1080.0;
float paramIntensity=60.0;
float paramSize=30.0;
float paramColor=50.0;
float color;
float hash(vec2 v) { return fract(9831.0 * cos(289.0*v.x + 0.2*v.y) * abs(sin(2.0*v.x + 17.0*v.y))); }
void compare(vec3 prepA, vec3 b, inout vec3 c0, inout vec3 c1, inout float minDist0, inout float minDist1) {
    vec3 orgB = b;
    b = max(vec3(0.0), b - min(b.r, min(b.g, b.b)) * color);
    float dist = distance(prepA, b*b*b);
    if (dist < minDist0) {
        minDist1 = minDist0;
        minDist0 = dist;
        c1 = c0;
        c0 = orgB;
    }
}
vec4 effect() {
    float intensity = (paramIntensity / 100.0)*1.5;
    float size = 1.0 + (paramSize / 100.0)*8.0;
    color = 0.05 + (paramColor / 100.0)*0.4;
    vec2 R = vec2(texelWidth, texelHeight);
    vec2 UV = vTextureCoord / R;
    vec2 coord = floor(UV / size);
    vec3 ts = texture2D(sTexture, coord * size * R).rgb;
    vec3 c0 = vec3(0);
    vec3 c1 = c0;
    float minDist0 = 1000.0;
    float minDist1 = minDist0;
    vec3 prepTs = max(vec3(0.0), ts - min(ts.r, min(ts.g, ts.b)) * color);
    prepTs = prepTs*prepTs*prepTs;
    # define cmp(R,G,B) compare(prepTs, vec3(R,G,B), c0, c1, minDist0, minDist1);
    cmp(0.078431, 0.047059, 0.109804);
    cmp(0.266667, 0.141176, 0.203922);
    cmp(0.188235, 0.203922, 0.427451);
    cmp(0.305882, 0.290196, 0.305882);
    cmp(0.521569, 0.298039, 0.188235);
    cmp(0.203922, 0.396078, 0.141176);
    cmp(0.815686, 0.274510, 0.282353);
    cmp(0.458824, 0.443137, 0.380392);
    cmp(0.349020, 0.490196, 0.807843);
    cmp(0.823529, 0.490196, 0.172549);
    cmp(0.521569, 0.584314, 0.631373);
    cmp(0.427451, 0.666667, 0.172549);
    cmp(0.823529, 0.666667, 0.600000);
    cmp(0.427451, 0.760784, 0.792157);
    cmp(0.854902, 0.831373, 0.368627);
    cmp(0.870588, 0.933333, 0.839216);
    minDist0 = sqrt(minDist0);
    minDist1 = sqrt(minDist1);
    float h = (hash(3.0*coord + fract(cos(vec2(floor(1.7*_motion))))) * 0.75) + (minDist1 / (minDist0 + minDist1));
    return vec4(mix(ts, mod(coord.x + coord.y, 2.0) > h  ? c1 : c0, intensity), 1.0);
}