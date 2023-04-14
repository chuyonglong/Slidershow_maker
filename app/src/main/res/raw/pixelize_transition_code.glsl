const ivec2 squaresMin= ivec2(20)/* = ivec2(20) */; // minimum number of squares (when the effect is at its higher level)
int steps = 50/* = 50 */; // zero disable the stepping

/*float d(){
    return min(progress, 1.0 - progress);
}*/
float dist(float p){
    return steps>0 ? ceil((min(p, 1.0 - p)) * float(steps)) / float(steps) : (min(p, 1.0 - p));
}


vec2 squareSize(float p) {
    return 2.0 * dist(p) / vec2(squaresMin);
}

vec4 transition(vec2 uv) {
    vec2 p = dist(progress)>0.0 ? (floor(uv / squareSize(progress)) + 0.5) * squareSize(progress) : uv;
    return mix(getFromColor(p), getToColor(p), progress);
}
