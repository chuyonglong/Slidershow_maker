package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlSpiralsFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nconst float PI2 = 6.283185307;\nconst vec2 bigCircleCenter = vec2(0.5,0.5);\nvec2 smallCircleCenter = vec2(0.5,0.5);\nconst float R = 0.5;\nuniform float paramIntensity;\nuniform float paramMotion;\nuniform float paramSpeed;\nfloat spirals;\nfloat moveR;\nfloat speed;\nvec2 getHelix(float theta, vec2 center) { return (spirals*(pow(theta/PI2, 3.0)))*vec2(cos(theta),sin(theta)); }\nfloat findNowPieceRatio(vec2 nowVector,float theta,vec2 center) {\n    vec2 oldRef= vec2(0.0);\n    for(int fI=0 ; fI<16 ; ++fI) {\n        vec2 nowRef = getHelix(theta+float(fI)*PI2,center);\n        float n = length(nowVector);\n        float R = length(nowRef);\n        if (n<R) {\n            float r = length(oldRef);\n            return (n-r)/(R-r);\n        }\n        oldRef = nowRef;\n    }\n    return 0.0;\n}\nfloat nearestDistanceInCircle(vec2 p) { return (R-length(p-bigCircleCenter)); }\nvec2 findIntersectWichCircle(vec2 p,vec2 dir) {\n    for(int i=0 ; i<5 ; ++i) {\n        float moveDis = nearestDistanceInCircle(p);\n        p = p+dir*moveDis;\n        if (R-length(p-bigCircleCenter) < 0.00001) { return p; }\n    }\n    return p;\n}\nvec4 render(vec2 uv) {\n    vec2 dir = uv-smallCircleCenter;\n    float theta = mod(atan(dir.y,dir.x)+PI2+3.0*time,PI2);\n    float ratio = findNowPieceRatio(dir,theta,smallCircleCenter);\n    dir = normalize(dir);\n    vec2 pOnCircle = findIntersectWichCircle(smallCircleCenter, dir);\n    float r = 0.1;\n    float lineL = length(pOnCircle-smallCircleCenter)-r;\n    vec2 fetchUV = smallCircleCenter+dir*(r+lineL*ratio);\n    return vec4(vec3(ratio), 1.0)*texture2D(sTexture, fetchUV);\n}\nvec2 uvDeform(vec2 uv) {\n    float depth = 5.0;\n    vec3 planeCenter = vec3(smallCircleCenter, depth);\n    float strength = 6.0;\n    vec2 target = strength*vec2(cos(speed*time/2.0), sin(speed*time/2.0));\n    vec3 planeNormal = normalize(vec3(target, -1.0));\n    vec3 rayFrom = vec3(uv, 0.0);\n    vec3 ray = vec3(0.0, 0.0,1.0);\n    float t = dot((planeCenter-rayFrom), planeNormal) / (dot(ray, planeNormal));\n    vec3 hitPoint = rayFrom + t*ray - vec3(bigCircleCenter,0.0);\n    hitPoint = hitPoint/hitPoint.z;\n    return hitPoint.xy*depth + bigCircleCenter;\n}\nvoid main() {\n    spirals = 0.02625 - (paramIntensity / 100.0)*0.025;\n    moveR = 0.005 + (paramMotion / 100.0)*0.25;\n    speed = 0.01 + (paramSpeed / 100.0)*4.0;\n    smallCircleCenter = bigCircleCenter + moveR*vec2(cos(-0.3*speed*time), sin(0.2*speed*time));\n    gl_FragColor = render(uvDeform(vTextureCoord));\n}\n";
    private float paramIntensity = 80.0f;
    private float paramMotion = 50.0f;
    private float paramSpeed = 25.0f;

    public GlSpiralsFilter() {
        super(FRAGMENT_SHADER);filterName = "Spirals";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramMotion"), this.paramMotion);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }
}
