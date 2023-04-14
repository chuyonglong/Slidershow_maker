mediump float;

highp float fractionalWidthOfPixel=0.01;
highp float aspectRatio=1.0;

const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);

vec4 effect()
{
    highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);
    highp vec2 samplePos = vTextureCoord - mod(vTextureCoord, sampleDivisor) + 0.5 * sampleDivisor;
    highp vec2 textureCoordinateToUse = vec2(vTextureCoord.x, (vTextureCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));
    highp vec2 adjustedSamplePos = vec2(samplePos.x, (samplePos.y * aspectRatio + 0.5 - 0.5 * aspectRatio));
    highp float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);
    lowp vec3 sampledColor = texture2D(sTexture, samplePos).rgb;
    highp float dotScaling = 1.0 - dot(sampledColor, W);
    lowp float checkForPresenceWithinDot = 1.0 - step(distanceFromSamplePoint, (fractionalWidthOfPixel * 0.5) * dotScaling);
    return vec4(vec3(checkForPresenceWithinDot), 1.0);
}