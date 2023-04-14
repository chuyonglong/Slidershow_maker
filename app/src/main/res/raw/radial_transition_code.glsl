precision highp float;
float smoothness = 1.0; // = 1.0

const highp float PI = 3.141592653589;

vec4 transition(vec2 p) {
    vec2 rp = p*2.-1.;
    return mix(
    getToColor(p),
    getFromColor(p),
    smoothstep(0., smoothness, atan(rp.y,rp.x) - (progress-.5) * PI * 2.5)
    );
}