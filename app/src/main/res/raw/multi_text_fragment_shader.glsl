precision mediump float;
uniform sampler2D u_Texture;
varying vec2 v_TexCoordinate;

void main()                    		
{
    vec4 p = (texture2D(u_Texture, v_TexCoordinate));

    gl_FragColor = p;
}                                                                     	

