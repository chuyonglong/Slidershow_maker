precision highp float;
#define PI 3.14159265359
uniform float _motion;
float texelWidth=1.0/1080.0;
float texelHeight=1.0/1080.0;
 float paramIntensity=30.0;
 float paramSpeed=30.0;
vec2 random2( vec2 p ) { return fract(sin(vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))))*43758.5453); }
vec4 effect() {
    float intensity = 0.5 + (paramIntensity / 100.0)*3.0;
    float speed = 0.25 + (paramSpeed / 100.0)*6.0;
    float screenRatio = texelHeight/texelWidth;
    float largerScreenDimSize = max(1.0/texelWidth, 1.0/texelHeight);
    vec2 st = vTextureCoord * vec2(screenRatio, 1.0);
    float pSize = 5.0;
    float scale = intensity*20.0;
    st *= scale;
    vec2 stInt = floor(st);
    vec2 stFract = fract(st);
    float minDist = 100.0;
    vec2 quad;
    for (int j=-1; j <= 1; j++ ) {
        for (int i=-1; i<=1; i++ ) {
            vec2 neighbor = vec2(float(i),float(j));
            vec2 point = 0.5 + 0.5*sin(speed*_motion + 2.0*PI*random2(stInt + neighbor));
            float dist = length(neighbor + point - stFract);
            quad = dist <= minDist ? neighbor : quad;
            minDist = dist <= minDist ? dist : minDist;
        }
    }
    vec2 midPoint = (stInt + 0.5 + quad) / (vec2(screenRatio, 1.0)*scale);
    vec2 m = mod(vec2(vTextureCoord.x / texelWidth, vTextureCoord.y / texelHeight), pSize) / vec2(largerScreenDimSize);
    vec3 tc1 = texture2D(sTexture, midPoint + m).rgb;
    vec3 tc2 = texture2D(sTexture, midPoint + pSize/largerScreenDimSize - m).rgb;
    return vec4(mix(tc1, tc2, 0.5), 1.0);
}