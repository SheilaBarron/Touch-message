uniform vec4 vColor;
varying vec2 uv;

#define dE MengerSponge
#define dEMengerSponge true
//#define antialiasing 0.2

precision lowp float;

/* parametres qui ne devraient jamais êtres changés */
const float HALFPI = 1.570796;
const float MIN_EPSILON = 6e-7;
const float MIN_NORM = 1.5e-7;

/* parametre qui ne devrai pas êtres modifiés durant l'exporation */
const int maxIterations = 9;
const int stepLimit = 60;
const int aoIterations = 8;
const float minRange = 6e-5;
const float bailout = 6.0;

const float scale = 2.0;
const float power = 8.0;
const float surfaceDetail = 0.2;
const float surfaceSmoothness = 0.8;
const float boundingRadius = 5.0;

const vec3 offset = vec3(0.0);
const vec3 shift = vec3(0.0);


const int colorIterations = 4;
const vec3 color1 = vec3(1.0);
const float color1Intensity = 0.45;
const vec3 color2 = vec3(0.0,0.53,0.8);
const float color2Intensity = 0.3;
const vec3 color3 = vec3(1.0,0.53,0.0);
const float color3Intensity = 0.0;
const bool transparent = false;
const float gamma = 1.0;

const vec3 light = vec3(-16.0,100.0,-60.0);
const vec2 ambientColor = vec2(0.8,0.3); //Intesity, color
const vec3 background1Color = vec3(0.0,0.46,0.8); //fond en haut
const vec3 background2Color = vec3(0.0); //fond en bas ( ça fait un dégradé entre les deux)
const vec3 innerGlowColor = vec3(0.0,0.6,0.8);
const float innerGlowIntensity = 0.5;
const vec3 outerGlowColor = vec3(1.0);
const float outerGlowIntensity = 0.02;
const float fog = 0.08;
const float fogFalloff = 0.2;
const float specularity = 0.8;
const float specularExponent = 4.0;

const float aoIntensity = 0.15;
const float aoSpread = 9.0;

const mat3 objectRotation = mat3(1.0);
const mat3 fractalRotation1 = mat3(1.0);
const mat3 fractalRotation2 = mat3(1.0);

const bool depthMap = false;

/*valeur a remplir correctement pour que cela s'affiche bien*/
const vec2 outputSize = vec2(200);



/*parametres qui sont modifiés à chaque drawcall -----(en ce moment en const pour de la debug)----*/

uniform vec3 cameraPosition;// = vec3(0.2,0.2,0.2);
uniform vec4 cameraRotation;// =vec4(0.0,0.0,0.0,1.0);  //quaternion

/* calculs de paramètres de rendus ----------(devrai être fait en dehors du shader...)-----------*/
const float cameraFocalLength = 0.9;
const float aspectRatio = outputSize.x / outputSize.y;  //devrai être calculé à l'extérieur
//const float fovfactor = 1.0 / sqrt(1.0 + cameraFocalLength * cameraFocalLength); // même remarque
const float fovfactor = 0.6;
const float pixelScale = 1.0 / min(outputSize.x, outputSize.y); //même remarque
const float epsfactor = 2.0 * fovfactor * pixelScale * surfaceDetail; //même remarque


vec3 u = vec3(0.0,-1.0,0.0);
vec3 v = vec3(0.0,0.0,1.0);
vec3 w = vec3(1.0,0.0,0.0);


/* -------- ON passe aux définitions des fractales ------------------*/
/*-------------------------------------------------------------------*/



#ifdef dESphereSponge
const float sphereHoles = 4.0;
const float sphereScale = 2.05;
// Adapted from Buddhis algorithm
// http://www.fractalforums.com/3d-fractal-generation/revenge-of-the-half-eaten-menger-sponge/msg21700/
vec3 SphereSponge(vec3 w)
{
    w *= objectRotation;
    float k = scale;
    float d = -10000.0;
    float d1, r, md = 100000.0, cd = 0.0;

    for (int i = 0; i < int(maxIterations); i++) {
        vec3 zz = mod(w * k, sphereHoles) - vec3(0.5 * sphereHoles) + offset;
        r = length(zz);

        // distance to the edge of the sphere (positive inside)
        d1 = (sphereScale - r) / k;
        k *= scale;

        // intersection
        d = max(d, d1);

        if (i < colorIterations) {
            md = min(md, d);
            cd = r;
        }
    }
    //distance, min distance, fractal iteration count (for coloration purpose)
    return vec3(d, cd, md);
}
#endif


// ============================================================================================ //



#ifdef dEMengerSponge
// Pre-calculations
vec3 halfSpongeScale = vec3(0.5) * scale;
// Adapted from Buddhis algorithm
// http://www.fractalforums.com/3d-fractal-generation/revenge-of-the-half-eaten-menger-sponge/msg21700/
vec3 MengerSponge(vec3 w)
{
    w *= objectRotation;
    w = (w * 0.5 + vec3(0.5)) * scale;  // scale [-1, 1] range to [0, 1]

    vec3 v = abs(w - halfSpongeScale) - halfSpongeScale;
    float d1 = max(v.x, max(v.y, v.z));     // distance to the box
    float d = d1;
    float p = 1.0;
    float md = 10000.0;
    vec3 cd = v;

    for (int i = 0; i < int(maxIterations); i++) {
        vec3 a = mod(3.0 * w * p, 3.0);
        p *= 3.0;

        v = vec3(0.5) - abs(a - vec3(1.5)) + offset;
        v *= fractalRotation1;

        // distance inside the 3 axis aligned square tubes
        d1 = min(max(v.x, v.z), min(max(v.x, v.y), max(v.y, v.z))) / p;

        // intersection
        d = max(d, d1);

        if (i < colorIterations) {
            md = min(md, d);
            cd = v;
        }
    }

    // The distance estimate, min distance, and fractional iteration count
    return vec3(d * 2.0 / scale, md, dot(cd, cd));
}
#endif


// ============================================================================================ //

#ifdef dEOctahedralIFS
// Pre-calculations
vec3 scale_offset = offset * (scale - 1.0);

vec3 OctahedralIFS(vec3 w)
{
    w *= objectRotation;
    float d, t;
    float md = 1000.0, cd = 0.0;

    for (int i = 0; i < int(maxIterations); i++) {
        w *= fractalRotation1;
        w = abs(w + shift) - shift;

        // Octahedral
        if (w.x < w.y) w.xy = w.yx;
        if (w.x < w.z) w.xz = w.zx;
        if (w.y < w.z) w.yz = w.zy;

        w *= fractalRotation2;
        w *= scale;
        w -= scale_offset;

        // Record minimum orbit for colouring
        d = dot(w, w);

        if (i < colorIterations) {
            md = min(md, d);
            cd = d;
        }
    }

    return vec3((length(w) - 2.0) * pow(scale, -float(maxIterations)), md, cd);
}
#endif


// ============================================================================================ //

#ifdef dEDodecahedronIFS

// phi = 1.61803399
const float phi = 1.618;  // {"label":"Phi", "min":0.1, "max":3, "step":0.01, "default":1.618, "group":"Fractal"}

// Dodecahedron serpinski
// Thanks to Knighty:
// http://www.fractalforums.com/index.php?topic=3158.msg16982#msg16982
// The normal vectors for the dodecahedra-siepinski folding planes are:
// (phi^2, 1, -phi), (-phi, phi^2, 1), (1, -phi, phi^2), (-phi*(1+phi), phi^2-1, 1+phi), (1+phi, -phi*(1+phi), phi^2-1) and x=0, y=0, z=0 planes.

// Pre-calculations
vec3 scale_offset = offset * (scale - 1.0);

float _IKVNORM_ = 1.0 / sqrt(pow(phi * (1.0 + phi), 2.0) + pow(phi * phi - 1.0, 2.0) + pow(1.0 + phi, 2.0));
float _C1_ = phi * (1.0 + phi) * _IKVNORM_;
float _C2_ = (phi * phi - 1.0) * _IKVNORM_;
float _1C_ = (1.0 + phi) * _IKVNORM_;

vec3 phi3 = vec3(0.5, 0.5 / phi, 0.5 * phi);
vec3 c3   = vec3(_C1_, _C2_, _1C_);


vec3 DodecahedronIFS(vec3 w)
{
    w *= objectRotation;
    float d, t;
    float md = 1000.0, cd = 0.0;

    for (int i = 0; i < int(maxIterations); i++) {
        w *= fractalRotation1;
        w = abs(w + shift) - shift;

        t = w.x * phi3.z + w.y * phi3.y - w.z * phi3.x;
        if (t < 0.0) w += vec3(-2.0, -2.0, 2.0) * t * phi3.zyx;

        t = -w.x * phi3.x + w.y * phi3.z + w.z * phi3.y;
        if (t < 0.0) w += vec3(2.0, -2.0, -2.0) * t * phi3.xzy;

        t = w.x * phi3.y - w.y * phi3.x + w.z * phi3.z;
        if (t < 0.0) w += vec3(-2.0, 2.0, -2.0) * t * phi3.yxz;

        t = -w.x * c3.x + w.y * c3.y + w.z * c3.z;
        if (t < 0.0) w += vec3(2.0, -2.0, -2.0) * t * c3.xyz;

        t = w.x * c3.z - w.y * c3.x + w.z * c3.y;
        if (t < 0.0) w += vec3(-2.0, 2.0, -2.0) * t * c3.zxy;

        w *= fractalRotation2;
        w *= scale;
        w -= scale_offset;

        // Record minimum orbit for colouring
        d = dot(w, w);

        if (i < colorIterations) {
            md = min(md, d);
            cd = d;
        }
    }

    return vec3((length(w) - 2.0) * pow(scale, -float(maxIterations)), md, cd);
}
#endif




// ============================================================================================ //



#ifdef dEMandelbox
const float sphereScale = 1.0;          // {"label":"Sphere scale", "min":0.01, "max":3,    "step":0.01,    "default":1,    "group":"Fractal", "group_label":"Additional parameters"}
const float boxScale = 0.5;             // {"label":"Box scale",    "min":0.01, "max":3,    "step":0.001,   "default":0.5,  "group":"Fractal"}
const float boxFold = 1.0;              // {"label":"Box fold",     "min":0.01, "max":3,    "step":0.001,   "default":1,    "group":"Fractal"}
const float fudgeFactor = 0.0;          // {"label":"Box size fudge factor",     "min":0, "max":100,    "step":0.001,   "default":0,    "group":"Fractal"}

// Pre-calculations
const float mR2 = boxScale * boxScale;    // Min radius
const float fR2 = sphereScale * mR2;      // Fixed radius
vec2  scaleFactor = vec2(scale, abs(scale)) / mR2;

// Details about the Mandelbox DE algorithm:
// http://www.fractalforums.com/3d-fractal-generation/a-mandelbox-distance-estimate-formula/
vec3 Mandelbox(vec3 w)
{
    w *= objectRotation;
    float md = 1000.0;
    vec3 c = w;

    // distance estimate
    vec4 p = vec4(w.xyz, 1.0),
        p0 = vec4(w.xyz, 1.0);  // p.w is knighty's DEfactor

    for (int i = 0; i < int(maxIterations); i++) {
        // box fold:
        // if (p > 1.0) {
        //   p = 2.0 - p;
        // } else if (p < -1.0) {
        //   p = -2.0 - p;
        // }
        p.xyz = clamp(p.xyz, -boxFold, boxFold) * 2.0 * boxFold - p.xyz;  // box fold
        p.xyz *= fractalRotation1;

        // sphere fold:
        // if (d < minRad2) {
        //   p /= minRad2;
        // } else if (d < 1.0) {
        //   p /= d;
        // }
        float d = dot(p.xyz, p.xyz);
        p.xyzw *= clamp(max(fR2 / d, mR2), 0.0, 1.0);  // sphere fold

        p.xyzw = p * scaleFactor.xxxy + p0 + vec4(offset, 0.0);
        p.xyz *= fractalRotation2;

        if (i < colorIterations) {
            md = min(md, d);
            c = p.xyz;
        }
    }

    // Return distance estimate, min distance, fractional iteration count
    return vec3((length(p.xyz) - fudgeFactor) / p.w, md, 0.33 * log(dot(c, c)) + 1.0);
}
#endif



// ============================================================================================ //

#ifdef dEMandelbulb
const float juliaFactor = 0.0; // {"label":"Juliabulb factor", "min":0, "max":1, "step":0.01, "default":0, "group":"Fractal", "group_label":"Additional parameters"}
const float radiolariaFactor = 0.0; // {"label":"Radiolaria factor", "min":-2, "max":2, "step":0.1, "default":0, "group":"Fractal"}
const float radiolaria = 0.0;       // {"label":"Radiolaria", "min":0, "max":1, "step":0.01, "default": 0, "group":"Fractal"}


// Scalar derivative approach by Enforcer:
// http://www.fractalforums.com/mandelbulb-implementation/realtime-renderingoptimisations/
void powN(float p, inout vec3 z, float zr0, inout float dr)
{
    float zo0 = asin(z.z / zr0);
    float zi0 = atan(z.y, z.x);
    float zr = pow(zr0, p - 1.0);
    float zo = zo0 * p;
    float zi = zi0 * p;
    float czo = cos(zo);

    dr = zr * dr * p + 1.0;
    zr *= zr0;

    z = zr * vec3(czo * cos(zi), czo * sin(zi), sin(zo));
}



// The fractal calculation
//
// Calculate the closest distance to the fractal boundary and use this
// distance as the size of the step to take in the ray marching.
//
// Fractal formula:
//    z' = z^p + c
//
// For each iteration we also calculate the derivative so we can estimate
// the distance to the nearest point in the fractal set, which then sets the
// maxiumum step we can move the ray forward before having to repeat the calculation.
//
//   dz' = p * z^(p-1)
//
// The distance estimation is then calculated with:
//
//   0.5 * |z| * log(|z|) / |dz|
//
vec3 Mandelbulb(vec3 w)
{
    w *= objectRotation;

    vec3 z = w;
    vec3 c = mix(w, offset, juliaFactor);
    vec3 d = w;
    float dr = 1.0;
    float r  = length(z);
    float md = 10000.0;

    for (int i = 0; i < int(maxIterations); i++) {
        powN(power, z, r, dr);

        z += c;

        if (z.y > radiolariaFactor) {
            z.y = mix(z.y, radiolariaFactor, radiolaria);
        }

        r = length(z);

        if (i < colorIterations) {
            md = min(md, r);
            d = z;
        }

        if (r > bailout) break;
    }

    return vec3(0.5 * log(r) * r / dr, md, 0.33 * log(dot(d, d)) + 1.0);
}
#endif


vec3 rotate_vector( vec4 quat, vec3 vec )
{
return vec + cross( 2.0 * quat.xyz, cross( quat.xyz, vec) + quat.w * vec);
}



// Define the ray direction from the pixel coordinates
vec3 rayDirection(vec2 pixel)
{

    vec2 p = uv;
    p.x *= aspectRatio;
    u = rotate_vector(cameraRotation, vec3(1.0, 0.0, 0.0));
    v = rotate_vector(cameraRotation,  vec3(0.0, 1.0, 0.0));
    w = rotate_vector(cameraRotation, vec3(0.0, 0.0, 1.0));
    vec3 d = (p.x * v  - p.y * u  + cameraFocalLength * w);

    return normalize(d);
}


// Intersect bounding sphere
//
// If we intersect then set the tmin and tmax values to set the start and
// end distances the ray should traverse.
bool intersectBoundingSphere(vec3 origin,
                             vec3 direction,
                             out float tmin,
                             out float tmax)
{
    bool hit = false;
    float b = dot(origin, direction);
    float c = dot(origin, origin) - boundingRadius;
    float disc = b*b - c;           // discriminant
    tmin = tmax = 0.0;

    if (disc > 0.0) {
        // Real root of disc, so intersection
        float sdisc = sqrt(disc);
        float t0 = -b - sdisc;          // closest intersection distance
        float t1 = -b + sdisc;          // furthest intersection distance

        if (t0 >= 0.0) {
            // Ray intersects front of sphere
            tmin = t0;
            tmax = t0 + t1;
        } else if (t0 < 0.0) {
            // Ray starts inside sphere
            tmax = t1;
        }
        hit = true;
    }

    return hit;
}



// Calculate the gradient in each dimension from the intersection point
vec3 generateNormal(vec3 z, float d)
{
    float e = max(d * 0.5, MIN_NORM);

    float dx1 = dE(z + vec3(e, 0, 0)).x;
    float dx2 = dE(z - vec3(e, 0, 0)).x;

    float dy1 = dE(z + vec3(0, e, 0)).x;
    float dy2 = dE(z - vec3(0, e, 0)).x;

    float dz1 = dE(z + vec3(0, 0, e)).x;
    float dz2 = dE(z - vec3(0, 0, e)).x;

    return normalize(vec3(dx1 - dx2, dy1 - dy2, dz1 - dz2));
}


// Blinn phong shading model
// http://en.wikipedia.org/wiki/BlinnPhong_shading_model
// base color, incident, point of intersection, normal
vec3 blinnPhong(vec3 color, vec3 p, vec3 n)
{
    // Ambient colour based on background gradient
    vec3 ambColor = clamp(mix(background2Color, background1Color, (sin(n.y * HALFPI) + 1.0) * 0.5), 0.0, 1.0);
    ambColor = mix(vec3(ambientColor.x), ambColor, ambientColor.y);

    vec3  halfLV = normalize(light - p);
    float diffuse = max(dot(n, halfLV), 0.0);
    float specular = pow(diffuse, specularExponent);

    return ambColor * color + color * diffuse + specular * specularity;
}


// Ambient occlusion approximation.
// Based upon boxplorer's implementation which is derived from:
// http://www.iquilezles.org/www/material/nvscene2008/rwwtt.pdf
float ambientOcclusion(vec3 p, vec3 n, float eps)
{
    float o = 1.0;                  // Start at full output colour intensity
    eps *= aoSpread;                // Spread diffuses the effect
    float k = aoIntensity / eps;    // Set intensity factor
    float d = 2.0 * eps;            // Start ray a little off the surface

    for (int i = 0; i < aoIterations; ++i) {
        o -= (d - dE(p + n * d).x) * k;
        d += eps;
        k *= 0.5;                   // AO contribution drops as we move further from the surface
    }

    return clamp(o, 0.0, 1.0);
}


// Calculate the output colour for each input pixel
vec4 render(vec2 pixel)
{
    vec3  ray_direction = rayDirection(pixel);
    float ray_length = minRange;
    vec3  ray = cameraPosition + ray_length * ray_direction;
    vec4  bg_color = vec4(clamp(mix(background2Color, background1Color, (sin(ray_direction.y * HALFPI) + 1.0) * 0.5), 0.0, 1.0), 1.0);
    vec4  color = bg_color;

    float eps = MIN_EPSILON;
    vec3  dist;
    vec3  normal = vec3(0.0);
    int   steps = 0;
    bool  hit = false;
    float tmin = 0.0;
    float tmax = 10000.0;

    if (intersectBoundingSphere(ray, ray_direction, tmin, tmax)) {
        ray_length = tmin;
        ray = cameraPosition + ray_length * ray_direction;

        for (int i = 0; i < stepLimit; i++) {
            steps = i;
            dist = dE(ray);
            dist.x *= surfaceSmoothness;

            // If we hit the surface on the previous step check again to make sure it wasn't
            // just a thin filament
            if (hit && dist.x < eps || ray_length > tmax || ray_length < tmin) {
                steps--;
                break;
            }

            hit = false;
            ray_length += dist.x;
            ray = cameraPosition + ray_length * ray_direction;
            eps = ray_length * epsfactor;

            if (dist.x < eps || ray_length < tmin) {
                hit = true;
            }
        }
    }

    // Found intersection?
    float glowAmount = float(steps)/float(stepLimit);
    float glow;

    if (hit) {
        float aof = 1.0, shadows = 1.0;
        glow = clamp(glowAmount * innerGlowIntensity * 3.0, 0.0, 1.0);

        if (steps < 1 || ray_length < tmin) {
            normal = normalize(ray);
        } else {
            normal = generateNormal(ray, eps);
            aof = ambientOcclusion(ray, normal, eps);
        }

        color.rgb = mix(color1, mix(color2, color3, dist.y * color2Intensity), dist.z * color3Intensity);
        color.rgb = blinnPhong(clamp(color.rgb * color1Intensity, 0.0, 1.0), ray, normal);
        color.rgb *= aof;
        color.rgb = mix(color.rgb, innerGlowColor, glow);
        color.rgb = mix(bg_color.rgb, color.rgb, exp(-pow(ray_length * exp(fogFalloff), 2.0) * fog));
        color.a = 1.0;
    } else {
        // Apply outer glow and fog
        ray_length = tmax;
        color.rgb = mix(bg_color.rgb, color.rgb, exp(-pow(ray_length * exp(fogFalloff), 2.0)) * fog);
        glow = clamp(glowAmount * outerGlowIntensity * 3.0, 0.0, 1.0);
        color.rgb = mix(color.rgb, outerGlowColor, glow);
        if (transparent) color = vec4(0.0);
    }

    // if (depthMap) {
    //     color.rgb = vec3(ray_length / 10.0);
    // }

    return color;
}




void main()
{
    gl_FragColor = vColor; //for debug
    vec4 color = vec4(0.0);
    float n = 0.0;

    #ifdef antialiasing
        for (float x = 0.0; x < 1.0; x += float(antialiasing)) {
            for (float y = 0.0; y < 1.0; y += float(antialiasing)) {
                color += render(gl_FragCoord.xy + vec2(x, y));
                n += 1.0;
            }
        }
        color /= n;
    #else
        color = render(gl_FragCoord.xy);
    #endif
     if (color.a < 0.00392) discard;
    gl_FragColor = vec4(pow(color.rgb, vec3(1.0 / gamma)), color.a);
}
