precision highp float;

int orientation=1;
vec4 effect() {
    return texture2D(sTexture, fract(vTextureCoord*exp2(ceil(-log2(orientation == 1 ? vTextureCoord.x : vTextureCoord.y)))));
}