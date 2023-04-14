precision mediump float;
highp float verticalMirror=-0.5;
highp float horizontalMirror=0.0;
vec4 effect() {
    highp vec2 p = vTextureCoord;

    if (verticalMirror < 0.0) {
        if (p.x > -verticalMirror) {
            p.x = -2.0 * verticalMirror - p.x;
        }
    } else if (verticalMirror > 0.0) {
        if (p.x < verticalMirror) {
            p.x = 2.0 * verticalMirror - p.x;
        }
    }
    if (horizontalMirror < 0.0) {
        if (p.y > -horizontalMirror) {
            p.y = -2.0 * horizontalMirror - p.y;
        }
    } else if (horizontalMirror > 0.0) {
        if (p.y < horizontalMirror) {
            p.y = 2.0 * horizontalMirror - p.y;
        }
    }

    if (p.x > 1.0 || p.x < 0.0 || p.y > 1.0 || p.y < 0.0) {
        return vec4(0.0);
    } else {
        return texture2D(sTexture, p);
    }
}
        