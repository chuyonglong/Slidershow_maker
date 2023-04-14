precision highp float;
varying vec2 vTextureCoord;
uniform sampler2D sTexture;

float time=1.0;
const float cloudIntensity = 2.0;

float clouds(vec2 uv) {
    return 0.6;
}

vec4 effect() {
    vec2 uv = vTextureCoord;
    float lightIntensity = 0.001 + (1.0 / 100.0)*0.1;
    vec2 center = vec2(0.5, 0.5);
    vec2 light1 = vec2(sin(time*1.2+45.0)*1.0 + cos(time*0.4+32.0)*0.6, sin(time*1.2+99.0)*1.2 + cos(time*0.2-15.0)*-0.4)*0.25+center;
    vec3 lightColor1 = vec3(1.0, 0.25, 0.25);
    vec2 light2 = vec2(sin(time+2.0)*-1.7, cos(time+8.0)*1.0)*0.25+center;
    vec3 lightColor2 = vec3(0.25, 1.0, 0.25);
    vec2 light3 = vec2(sin(time+3.0)*1.6, cos(time+14.0)*-1.2)*0.25+center;
    vec3 lightColor3 = vec3(0.25, 0.25, 1.0);
    float cloudIntensity1 = 1.0 - (cloudIntensity*distance(uv, light1));
    float lightIntensity1 = lightIntensity / max(0.0001, distance(uv, light1));
    float cloudIntensity2 = 1.0 - (cloudIntensity*distance(uv, light2));
    float lightIntensity2 = lightIntensity / max(0.0001, distance(uv, light2));
    float cloudIntensity3 = 1.0 - (cloudIntensity*distance(uv, light3));
    float lightIntensity3 = lightIntensity / max(0.0001, distance(uv, light3));
    vec4 ts = texture2D(sTexture, uv);
    vec3 tl =  vec3(cloudIntensity1*clouds(uv))*lightColor1 + lightIntensity1*lightColor1 + vec3(cloudIntensity2*clouds(uv))*lightColor2 + lightIntensity2*lightColor2 +vec3(cloudIntensity3*clouds(uv))*lightColor3 + lightIntensity3*lightColor3;
    return vec4(ts.rgb*tl.rgb, 1.0);
}

void main() {


    gl_FragColor = effect();
}

