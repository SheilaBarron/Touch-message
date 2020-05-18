varying vec2 uv;
uniform sampler2D screenTexture;

void main()
{ 
    gl_FragColor = texture2D(screenTexture, (uv + vec2(1)) / vec2(2));
}