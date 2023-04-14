precision highp float;

uniform float _motion;
 float paramVolume=0.2;
 float paramGrid=0.2;
 vec2 paramDimen=vec2(1.0,1.0);
 float paramAlpha=0.1;

float sat( float t ) {
    return clamp( t, 0.0, 1.0 );
}

vec2 sat( vec2 t ) {
    return clamp( t, 0.0, 1.0 );
}

float remap  ( float t, float a, float b ) {
    return sat( (t - a) / (b - a) );
}

float linterp( float t ) {
    return sat( 1.0 - abs( 2.0*t - 1.0 ) );
}

vec3 spectrum_offset( float t ) {
    vec3 ret;
    float lo = step(t,0.5);
    float hi = 1.0-lo;
    float w = linterp( remap( t, 1.0/6.0, 5.0/6.0 ) );
    float neg_w = 1.0-w;
    ret = vec3(lo,1.0,hi) * vec3(neg_w, w, neg_w);
    return pow( ret, vec3(1.0/2.2) );
}

//note: [0;1]
float rand( vec2 n ) {
    return fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

//note: [-1;1]
float srand( vec2 n ) {
    return rand(n) * 2.0 - 1.0;
}

float trunc( float x, float num_levels )
{
    return floor(x*num_levels) / num_levels;
}
vec2 trunc( vec2 x, float num_levels )
{
    return floor(x*num_levels) / num_levels;
}

vec4 effect( )
{
    vec2 uv = vTextureCoord.xy/ paramDimen;

    float _motion = mod(_motion, 32.0); // + modelmat[0].x + modelmat[0].z;
    float GLITCH = paramVolume;
    float gnm = sat( GLITCH );
    float rnd0 = rand( trunc( vec2(_motion, _motion), 6.0 ) );
    float r0 = sat((1.0-gnm)*0.7 + rnd0);
    float rnd1 = rand( vec2(trunc( uv.x, paramGrid * r0 ), _motion) ); //橫的個格子
    float r1 = 0.5 - 0.5 * gnm + rnd1;
    r1 = 1.0 - max( 0.0, ((r1<1.0) ? r1 : 0.9999999) ); //note: weird ass bug on old drivers
    float rnd2 = rand( vec2(trunc( uv.y, 40.0*r1 ), _motion) ); //vert
    float r2 = sat( rnd2 );

    float rnd3 = rand( vec2(trunc( uv.y, 10.0*r0 ), _motion) );
    float r3 = (1.0-sat(rnd3+0.8)) - 0.1;

    float pxrnd = rand( uv + _motion );

    float ofs = 0.05 * r2 * GLITCH * ( rnd0 > 0.5 ? 1.0 : -1.0 );
    ofs += 0.5 * pxrnd * ofs;

    uv.y += 0.1 * r3 * GLITCH;

    const int NUM_SAMPLES = 10;
    const float RCP_NUM_SAMPLES_F = 1.0 / float(NUM_SAMPLES);

    vec4 sum = vec4(0.0);
    vec3 wsum = vec3(0.0);
    for( int i=0; i<NUM_SAMPLES; ++i )
    {
        float t = float(i) * RCP_NUM_SAMPLES_F;
        uv.x = sat( uv.x + ofs * t );
        vec4 samplecol = texture2D( sTexture, uv );
        vec3 s = spectrum_offset( t );
        samplecol.rgb = samplecol.rgb * s;
        sum += samplecol;
        wsum += s;
    }
    sum.rgb /= wsum;
    sum.a *= RCP_NUM_SAMPLES_F;


    return vec4(sum.rgb, sum.a*paramAlpha);
}