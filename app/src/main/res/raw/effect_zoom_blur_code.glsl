precision mediump float;
 highp vec2 blurCenter = vec2(0.5,0.5);
 highp float blurSize=1.0;

vec4 effect()
{

    highp vec2 samplingOffset = 1.0/100.0 * (blurCenter - vTextureCoord) * blurSize;

    lowp vec4 fragmentColor = texture2D(sTexture, vTextureCoord) * 0.18;
    fragmentColor += texture2D(sTexture, vTextureCoord + samplingOffset) * 0.15;
    fragmentColor += texture2D(sTexture, vTextureCoord + (2.0 * samplingOffset)) *  0.12;
    fragmentColor += texture2D(sTexture, vTextureCoord + (3.0 * samplingOffset)) * 0.09;
    fragmentColor += texture2D(sTexture, vTextureCoord + (4.0 * samplingOffset)) * 0.05;
    fragmentColor += texture2D(sTexture, vTextureCoord - samplingOffset) * 0.15;
    fragmentColor += texture2D(sTexture, vTextureCoord - (2.0 * samplingOffset)) *  0.12;
    fragmentColor += texture2D(sTexture, vTextureCoord - (3.0 * samplingOffset)) * 0.09;
    fragmentColor += texture2D(sTexture, vTextureCoord - (4.0 * samplingOffset)) * 0.05;

    return fragmentColor;
}