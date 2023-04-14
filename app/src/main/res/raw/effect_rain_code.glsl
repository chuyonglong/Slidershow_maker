uniform float _motion;
float texelWidth=1.0/1080.0;
float texelHeight=1.0/1080.0;
float paramIntensity=70.;
float paramSpeed=20.;
float N11(float t) { return fract(sin(t*12445.56)*7668.76); }
vec3 N13(float p) {
    vec3 p3 = fract(vec3(p) * vec3(.1152,.9378,.12586));
    p3 += dot(p3, p3.yzx + 19.16);
    return fract(vec3((p3.x + p3.y)*p3.z, (p3.x+p3.z)*p3.y, (p3.y+p3.z)*p3.x));
}
vec2 DropLayer2(vec2 uv, float t) {
    vec2 UV = uv;
    uv.y += t*0.74;
    vec2 a = vec2(6.1, 1.);
    vec2 grid = a*2.;
    vec2 id = floor(uv*grid);
    float colShift = N11(id.x);
    uv.y += colShift;
    id = floor(uv*grid);
    vec3 n = N13(id.x*35.8+id.y*2323.2);
    vec2 st = fract(uv*grid)-vec2(.5, 0);
    float x = n.x-.5;
    float y = UV.y*20.1;
    float wiggle = sin(y+sin(y));
    x += wiggle*(.5-abs(x))*(n.z-.5)*0.72;
    float ti = fract(t+n.z);
    y = ((smoothstep(0., .84, ti) * smoothstep(1., 0.84, ti))-.5)*.9+.5;
    vec2 p = vec2(x, y);
    float d = length((st-p)*a.yx);
    float mainDrop = smoothstep(.4, .0, d);
    float r = sqrt(smoothstep(1., y, st.y));
    float cd = abs(st.x-x);
    float trail = smoothstep(.22*r, .16*r*r, cd);
    float trailFront = smoothstep(-.021, .021, st.y-y);
    trail *= trailFront*r*r;
    y = UV.y;
    float trail2 = smoothstep(.2*r, .0, cd);
    float droplets = max(0., (sin(y*(1.-y)*110.)-st.y))*trail2*trailFront*n.z;
    y = fract(y*10.)+(st.y-.5);
    float dd = length(st-vec2(x, y));
    droplets = smoothstep(.3, 0., dd);
    float m = mainDrop+droplets*r*trailFront;
    return vec2(m, trail);
}
float StaticDrops(vec2 uv, float t) {
    uv *= 40.;
    vec2 id = floor(uv);
    uv = fract(uv)-.5;
    vec3 n = N13(id.x*106.34+id.y*3421.456);
    vec2 p = (n.xy-.5)*.71;
    float d = length(uv-p);
    float fade = smoothstep(0., .025, fract(t+n.z)) * smoothstep(1., 0.025, fract(t+n.z));
    float c = smoothstep(.3, 0., d)*fract(n.z*10.)*fade;
    return c;
}
vec2 Drops(vec2 uv, float t, float l0, float l1, float l2) {
    float s = StaticDrops(uv, t)*l0;
    vec2 m1 = DropLayer2(uv, -t)*l1;
    vec2 m2 = DropLayer2(uv*1.8, -t)*l2;
    float c = s+m1.x+m2.x;
    c = smoothstep(.3, 1., c);
    return vec2(c, max(m1.y*l0, m2.y*l1));
}

vec4 effect() {
    float intensity = paramIntensity / 100.0;
    float speed = 0.5 + (paramSpeed / 100.0)*5.5;
    vec2 uv = (vTextureCoord - 0.5);
    uv.x = uv.x / texelWidth * texelHeight;
    vec2 UV = vTextureCoord;
    vec3 M = vec3(0.5, 0.5, 1.0);
    float T = speed*_motion+M.x*2.;
    float t = T*.2;
    float rainAmount = sin(T*.05)*0.3 + intensity;
    float staticDrops = smoothstep(-.5, 1., rainAmount)*2.;
    float layer1 = smoothstep(.25, .75, rainAmount);
    float layer2 = smoothstep(.0, .5, rainAmount);
    vec2 c = Drops(uv, t, staticDrops, layer1, layer2);
    vec2 e = vec2(.001, 0.);
    float cx = Drops(uv+e, t, staticDrops, layer1, layer2).x;
    float cy = Drops(uv+e.yx, t, staticDrops, layer1, layer2).x;
    vec2 n = vec2(cx-c.x, cy-c.x);
    return vec4(texture2D(sTexture, UV+n).rgb, 1.);
}