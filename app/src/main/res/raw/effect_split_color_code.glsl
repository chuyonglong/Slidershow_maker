precision mediump float;
uniform float _motion;
float paramIntensity=50.0;
float paramSpeed=20.0;
vec4 effect() {
    float intensity = (paramIntensity / 100.0)*8.0;
    float speed = 0.25 + (paramSpeed / 100.0)*4.0;
    vec2 deltaR = intensity*0.01*vec2(sin(speed*_motion)+sin(speed*0.3*_motion)+sin(speed*0.06*_motion)*0.25+sin(speed*0.9*_motion), 0);
    vec2 deltaB = intensity*0.01*vec2(sin(speed*1.2*_motion)+sin(speed*0.15*_motion)+sin(speed*0.02*_motion)*0.3+sin(speed*0.8*_motion), 0);
    vec4 tc = texture2D(sTexture, vTextureCoord);
    tc.r = texture2D(sTexture, vTextureCoord + deltaR).r;
    tc.b = texture2D(sTexture, vTextureCoord - deltaB).b;
    return vec4(tc.rgb, 1.0);
}