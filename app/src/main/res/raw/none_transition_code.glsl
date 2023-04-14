vec4 transition (vec2 uv) {
  return mix(
  getFromColor(uv),
  getToColor(uv),
  0.0
  );
}