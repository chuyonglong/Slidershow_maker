float texelWidth=0.1;
float texelHeight=0.1;
uniform float _motion;
int orientation=1;
#define cc vec2(0.5, 0.5)
#define SNOW_COL vec4(1.0, 1.0, 1.0, 1.0)
#define SNOW_ALPHA 0.75
float paramIntensity=20.0;
float paramSize=25.0;
float paramSpeed=25.0;
float smoothness;
float smoothCircle(vec2 position, float relativeSize) {
    float d = distance(cc, position)*2./relativeSize;
    return d > 1.0 ? 0.0 : clamp(smoothness/d-smoothness, -1.0, 1.0);
}
float randF(float n) { return fract(sin(n) * 43758.5453123); }
bool rand2d(float i, float j, float probability) { return (randF(i + j*7.8124861) > probability); }
float circleGrid(vec2 position, float spacing, float dotSize) {
    float idx = floor(1./spacing * position.x);
    float yIdx = floor(1./spacing * position.y);
    if (rand2d(idx, yIdx, 0.06)) { return 0.0; }
    float relativeSize = (0.5 + 0.5*randF(yIdx))*dotSize / spacing;
    return smoothCircle(vec2(fract(1./spacing*position.x), fract(1./spacing*position.y + yIdx)), relativeSize);
}

vec4 effect() {
    float layers = 1.0 + (paramIntensity / 100.0)*24.0;
    smoothness = 0.1 + (paramSize / 100.0)*1.2;
    float speed = -(0.1 + (paramSpeed / 100.0)*0.9);
    vec2 uvsq = vec2(vTextureCoord.x * texelHeight / texelWidth, vTextureCoord.y);
    uvsq = orientation > 0 ? uvsq.yx : uvsq;
    float amnt = 0.0;
    float rotX = 0.0;
    float rotY = 0.0;
    for (float i = 0.0; i < layers; i++) {
        float p = 0.5 + ((i+1.) / layers)*0.4;
        vec2 fallPosition = vec2(
        rotX * (1.0-p) + uvsq.x + i + p*sin(_motion/2.+i)/4.*speed,
        rotY * (1.0-p) + i * 3.0 + uvsq.y + _motion*p/1.*speed);
        amnt = amnt + SNOW_ALPHA * circleGrid(fallPosition, 0.06* p, 0.04* p*p);
    }
    return mix(SNOW_COL, texture2D(sTexture, vTextureCoord), 1.0-amnt);
}