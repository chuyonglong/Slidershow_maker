package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlNoisyMirrorEffect extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying highp vec2 vTextureCoord;\nuniform sampler2D sTexture;\n\nuniform float time;\n\nuniform float paramVolume;\nuniform vec2 paramDimen;\nuniform float paramAlpha;\n\n\nvec3 mod289(vec3 x) {\n  return x - floor(x * (1.0 / 289.0)) * 289.0;\n}\n\nvec4 mod289(vec4 x) {\n  return x - floor(x * (1.0 / 289.0)) * 289.0;\n}\n\nvec4 permute(vec4 x) {\n     return mod289(((x*34.0)+1.0)*x);\n}\n\nvec4 taylorInvSqrt(vec4 r)\n{\n  return 1.79284291400159 - 0.85373472095314 * r;\n}\n\nfloat snoise(vec3 v)\n  {\n  const vec2 C = vec2(1.0/6.0, 1.0/3.0) ;\n  const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);\n\n// First corner\n  vec3 i = floor(v + dot(v, C.yyy) );\n  vec3 x0 = v - i + dot(i, C.xxx) ;\n\n// Other corners\n  vec3 g = step(x0.yzx, x0.xyz);\n  vec3 l = 1.0 - g;\n  vec3 i1 = min( g.xyz, l.zxy );\n  vec3 i2 = max( g.xyz, l.zxy );\n\n  // x0 = x0 - 0.0 + 0.0 * C.xxx;\n  // x1 = x0 - i1 + 1.0 * C.xxx;\n  // x2 = x0 - i2 + 2.0 * C.xxx;\n  // x3 = x0 - 1.0 + 3.0 * C.xxx;\n  vec3 x1 = x0 - i1 + C.xxx;\n  vec3 x2 = x0 - i2 + C.yyy; // 2.0*C.x = 1/3 = C.y\n  vec3 x3 = x0 - D.yyy; // -1.0+3.0*C.x = -0.5 = -D.y\n\n// Permutations\n  i = mod289(i);\n  vec4 p = permute( permute( permute(\n             i.z + vec4(0.0, i1.z, i2.z, 1.0 ))\n           + i.y + vec4(0.0, i1.y, i2.y, 1.0 ))\n           + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));\n\n// Gradients: 7x7 points over a square, mapped onto an octahedron.\n// The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)\n  float n_ = 0.142857142857; // 1.0/7.0\n  vec3 ns = n_ * D.wyz - D.xzx;\n\n  vec4 j = p - 49.0 * floor(p * ns.z * ns.z); // mod(p,7*7)\n\n  vec4 x_ = floor(j * ns.z);\n  vec4 y_ = floor(j - 7.0 * x_ ); // mod(j,N)\n\n  vec4 x = x_ *ns.x + ns.yyyy;\n  vec4 y = y_ *ns.x + ns.yyyy;\n  vec4 h = 1.0 - abs(x) - abs(y);\n\n  vec4 b0 = vec4( x.xy, y.xy );\n  vec4 b1 = vec4( x.zw, y.zw );\n\n  //vec4 s0 = vec4(lessThan(b0,0.0))*2.0 - 1.0;\n  //vec4 s1 = vec4(lessThan(b1,0.0))*2.0 - 1.0;\n  vec4 s0 = floor(b0)*2.0 + 1.0;\n  vec4 s1 = floor(b1)*2.0 + 1.0;\n  vec4 sh = -step(h, vec4(0.0));\n\n  vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;\n  vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;\n\n  vec3 p0 = vec3(a0.xy,h.x);\n  vec3 p1 = vec3(a0.zw,h.y);\n  vec3 p2 = vec3(a1.xy,h.z);\n  vec3 p3 = vec3(a1.zw,h.w);\n\n//Normalise gradients\n  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));\n  p0 *= norm.x;\n  p1 *= norm.y;\n  p2 *= norm.z;\n  p3 *= norm.w;\n\n// Mix final noise value\n  vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);\n  m = m * m;\n  return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1),\n            dot(p2,x2), dot(p3,x3) ) );\n  }\n\n// FBM / Octave Noise\n\nconst float scalDiv = 4.;\nconst float scalDivt = 2.1;\nconst float sc1 = 1.0/scalDiv;\nconst float sc2 = sc1/scalDiv;\nconst float sc3 = sc2/scalDiv;\nconst float sc1t = 1.0 /scalDivt;\nconst float sc2t = sc1t/scalDivt;\nconst float sc3t = sc2t/scalDivt;\nfloat FBM(vec3 v) {\n    return 1.   *0.5    * snoise(v*vec3(sc3, sc3, sc3t)) +\n           0.4  *0.25   * snoise(v*vec3(sc2, sc2, sc2t)) +\n           0.15 *0.125  * snoise(v*vec3(sc1, sc1, sc1t));\n}\n\n\n//float mouseX = iMouse.x/iResolution.x;\n//float mouseY = iMouse.y/iResolution.y;\n\nfloat magn = 0.55;  //mouseX;\nfloat speed = 0.06; //mouseY;\n\nfloat zoom = 0.05;\n\nvoid main()\n{\n   float v = paramVolume;\n   vec2 uv = (vTextureCoord.xy / paramDimen.xy) * (1. - 2.*zoom) + vec2(zoom, zoom);\n   float niceNoise1 = FBM( vec3(120.0 * uv*v, speed * 60.*time));\n   float niceNoise2 = FBM( vec3(120.0 * uv*v, speed * 62.*time + 300.));\n   vec4 ccc=texture2D(sTexture, uv + vec2(magn*0.2*niceNoise1,magn*0.21*niceNoise2) );\n   gl_FragColor = vec4(ccc.rgb,ccc.a * paramAlpha);\n}\n";
    private float paramAlpha = 0.2f;
    private float[] paramDimen = {1.0f, 1.0f};
    private float paramVolume = 0.8f;


    public GlNoisyMirrorEffect() {
        super(FRAGMENT_SHADER);filterName = "Noisy Mirror";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramVolume"), this.paramVolume);
        FilterUtilsKt.setFloatVec2(getHandle("paramDimen"), this.paramDimen);
        FilterUtilsKt.setFloat(getHandle("paramAlpha"), this.paramAlpha);
    }
}
