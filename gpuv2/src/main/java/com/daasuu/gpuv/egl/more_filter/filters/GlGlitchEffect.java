package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlGlitchEffect extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying highp vec2 vTextureCoord;\nuniform sampler2D sTexture;\n\nuniform float time;\nuniform float paramVolume;\nuniform float paramGrid;\nuniform vec2 paramDimen;\nuniform float paramAlpha;\n\nfloat sat( float t ) {\n\treturn clamp( t, 0.0, 1.0 );\n}\n\nvec2 sat( vec2 t ) {\n\treturn clamp( t, 0.0, 1.0 );\n}\n\n//remaps inteval [a;b] to [0;1]\nfloat remap  ( float t, float a, float b ) {\n\treturn sat( (t - a) / (b - a) );\n}\n\n//note: /\\ t=[0;0.5;1], y=[0;1;0]\nfloat linterp( float t ) {\n\treturn sat( 1.0 - abs( 2.0*t - 1.0 ) );\n}\n\nvec3 spectrum_offset( float t ) {\n\tvec3 ret;\n\tfloat lo = step(t,0.5);\n\tfloat hi = 1.0-lo;\n\tfloat w = linterp( remap( t, 1.0/6.0, 5.0/6.0 ) );\n\tfloat neg_w = 1.0-w;\n\tret = vec3(lo,1.0,hi) * vec3(neg_w, w, neg_w);\n\treturn pow( ret, vec3(1.0/2.2) );\n}\n\n//note: [0;1]\nfloat rand( vec2 n ) {\n  return fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);\n}\n\n//note: [-1;1]\nfloat srand( vec2 n ) {\n\treturn rand(n) * 2.0 - 1.0;\n}\n\nfloat trunc( float x, float num_levels )\n{\n\treturn floor(x*num_levels) / num_levels;\n}\nvec2 trunc( vec2 x, float num_levels )\n{\n\treturn floor(x*num_levels) / num_levels;\n}\n\nvoid main( )\n{\n\tvec2 uv = vTextureCoord.xy/ paramDimen;\n\n\tfloat time = mod(time, 32.0); // + modelmat[0].x + modelmat[0].z;\n\tfloat GLITCH = paramVolume;\n\tfloat gnm = sat( GLITCH );\n\tfloat rnd0 = rand( trunc( vec2(time, time), 6.0 ) );\n\tfloat r0 = sat((1.0-gnm)*0.7 + rnd0);\n\tfloat rnd1 = rand( vec2(trunc( uv.x, paramGrid * r0 ), time) ); //橫的個格子\n\tfloat r1 = 0.5 - 0.5 * gnm + rnd1;\n\tr1 = 1.0 - max( 0.0, ((r1<1.0) ? r1 : 0.9999999) ); //note: weird ass bug on old drivers\n\tfloat rnd2 = rand( vec2(trunc( uv.y, 40.0*r1 ), time) ); //vert\n\tfloat r2 = sat( rnd2 );\n\n\tfloat rnd3 = rand( vec2(trunc( uv.y, 10.0*r0 ), time) );\n\tfloat r3 = (1.0-sat(rnd3+0.8)) - 0.1;\n\n\tfloat pxrnd = rand( uv + time );\n\n\tfloat ofs = 0.05 * r2 * GLITCH * ( rnd0 > 0.5 ? 1.0 : -1.0 );\n\tofs += 0.5 * pxrnd * ofs;\n\n\tuv.y += 0.1 * r3 * GLITCH;\n\n    const int NUM_SAMPLES = 10;\n    const float RCP_NUM_SAMPLES_F = 1.0 / float(NUM_SAMPLES);\n\n\tvec4 sum = vec4(0.0);\n\tvec3 wsum = vec3(0.0);\n\tfor( int i=0; i<NUM_SAMPLES; ++i )\n\t{\n\t\tfloat t = float(i) * RCP_NUM_SAMPLES_F;\n\t\tuv.x = sat( uv.x + ofs * t );\n\t\tvec4 samplecol = texture2D( sTexture, uv, -10.0 );\n\t\tvec3 s = spectrum_offset( t );\n\t\tsamplecol.rgb = samplecol.rgb * s;\n\t\tsum += samplecol;\n\t\twsum += s;\n\t}\n\tsum.rgb /= wsum;\n\tsum.a *= RCP_NUM_SAMPLES_F;\n\n\tgl_FragColor.a = sum.a*paramAlpha;\n\tgl_FragColor.rgb = sum.rgb; // * outcol0.a;\n}\n";
    private float paramAlpha = 0.1f;
    private float[] paramDimen = {1.0f, 1.0f};
    private float paramGrid = 0.2f;
    private float paramVolume = 0.2f;

    public GlGlitchEffect() {
        super(FRAGMENT_SHADER);
        filterName = "Glitch";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramVolume"), this.paramVolume);
        FilterUtilsKt.setFloatVec2(getHandle("paramDimen"), this.paramDimen);
        FilterUtilsKt.setFloat(getHandle("paramGrid"), this.paramGrid);
        FilterUtilsKt.setFloat(getHandle("paramAlpha"), this.paramAlpha);
    }
}
