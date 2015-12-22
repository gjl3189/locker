#include "main.h"

static const char gVertexShader[] =
    "attribute vec4 vPosition;\n"
    "attribute vec2 TexCoordIn;\n"
    "varying vec2 TexCoordOut;\n"
	"uniform mat4 Modelview;\n"
    "void main() {\n"
    "	gl_Position = vPosition * Modelview;\n"
	"	TexCoordOut = TexCoordIn;\n"
    "}\n";

static const char gFragmentShader[] =
    "precision mediump float;\n"
    "varying vec2 TexCoordOut;\n"
    "uniform sampler2D Texture;\n"
    "void main() {\n"
	//add by Jack
	"  vec2 tmp = TexCoordOut.xy;\n"
	"  float x = TexCoordOut.x;\n"
	"  tmp.x = 1.0 - TexCoordOut.y;\n"
	"  tmp.y = x;\n"
	"  gl_FragColor = texture2D(Texture, tmp);\n"
	//end
//	"  gl_FragColor = texture2D(Texture, TexCoordOut);\n"
    "}\n";

GLuint loadShader(GLenum type, const char* source){
	GLuint shader = glCreateShader(type);
	if(shader){
		glShaderSource(shader, 1, &source, NULL);
		glCompileShader(shader);
		GLint compiled = 0;
		glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
		if(!compiled){
			GLint infoLen = 0;
			glGetShaderiv(shader,GL_INFO_LOG_LENGTH, &infoLen);
			if (infoLen) {
				char* buf = (char*) malloc(infoLen);
			    if (buf) {
			    	glGetShaderInfoLog(shader, infoLen, NULL, buf);
			    	LOGE("Could not compile shader %d:\n%s\n",
			        type, buf);
			        free(buf);
			    }
			    glDeleteShader(shader);
			    shader = 0;
			}
		}
	}
	return shader;
}

GLuint createProgram(const char* pVertexSource, const char* pFragmentSource) {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, pVertexSource);
    if (!vertexShader) {
        return 0;
    }

    GLuint pixelShader = loadShader(GL_FRAGMENT_SHADER, pFragmentSource);
    if (!pixelShader) {
        return 0;
    }

    GLuint program = glCreateProgram();
    if (program) {
        glAttachShader(program, vertexShader);
        glAttachShader(program, pixelShader);
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
        if (linkStatus != GL_TRUE) {
            GLint bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength) {
                char* buf = (char*) malloc(bufLength);
                if (buf) {
                    glGetProgramInfoLog(program, bufLength, NULL, buf);
                    LOGE("Could not link program:\n%s\n", buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = 0;
        }
    }
    return program;
}

GLfloat rot[16] = {
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	0, 0, 0, 1,
};

GLuint vertVBO, texVBO, indsVBO;
float projection[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
void orthoM(float m[], float left, float right, float bottom, float top, float near, float far) {
    if (left == right) {
    	LOGE("left == right");
        return;
    }
    if (bottom == top) {
        LOGE("bottom == top");
        return;
    }
    if (near == far) {
        LOGE("near == far");
        return;
    }

    const float r_width  = 1.0f / (right - left);
    const float r_height = 1.0f / (top - bottom);
    const float r_depth  = 1.0f / (far - near);
    const float x =  2.0f * (r_width);
    const float y =  2.0f * (r_height);
    const float z = -2.0f * (r_depth);
    const float tx = -(right + left) * r_width;
    const float ty = -(top + bottom) * r_height;
    const float tz = -(far + near) * r_depth;
    m[0] = x;		m[5] = y;		m[10] = z;		m[12] = tx;
    m[13] = ty;		m[14] = tz;		m[15] = 1.0f;	m[1] = 0.0f;
    m[2] = 0.0f;	m[3] = 0.0f;	m[4] = 0.0f;	m[6] = 0.0f;
    m[7] = 0.0f;	m[8] = 0.0f;	m[9] = 0.0f;	m[11] = 0.0f;
}

void translateM(float m[], float x, float y, float z) {
	int i = 0;
    for (i=0 ; i<4 ; i++) {
        int mi = i;
        m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
    }
}
bool setupGraphics(int w, int h, void* texels,int width,int height){
	if(texels != NULL){
		glEnable(GL_TEXTURE_2D);
		glGenTextures(1, &texture);
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texels);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	program = createProgram(gVertexShader, gFragmentShader);
	if (!program) {
	        LOGE("Could not create program.");
	        return 0;
	}
	glUseProgram(program);

	ah.position = glGetAttribLocation(program, "vPosition");
	ah.texCoord = glGetAttribLocation(program, "TexCoordIn");
	uh.sampler = glGetUniformLocation(program, "Texture");
	modelviewUniform = glGetUniformLocation(program, "Modelview");

	glEnableVertexAttribArray(ah.texCoord);
	glEnableVertexAttribArray(ah.position);

	//创建缓冲区对象，返回1个缓冲对象给顶点VB0
	glGenBuffers(1, &vertVBO);
	//激活缓冲区对象，类型为顶点数据
	glBindBuffer(GL_ARRAY_BUFFER, vertVBO);
	//初始化缓冲区对象，数据只指定1次，但可以多次作为绘图的源数据
	glBufferData(GL_ARRAY_BUFFER, getVertexSize(), getVertices(), GL_STATIC_DRAW);

	glGenBuffers(1, &texVBO);
	glBindBuffer(GL_ARRAY_BUFFER, texVBO);
	glBufferData(GL_ARRAY_BUFFER, getVertexSize(), getTexCoords(), GL_DYNAMIC_DRAW);

	glGenBuffers(1, &indsVBO);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indsVBO);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndexSize(), getIndices(), GL_STATIC_DRAW);
	memset(projection, 0, 16);

	glViewport(0, 0, w, h);
}

int renderFrame() {
	glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	glClear( GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

	glUniformMatrix4fv(modelviewUniform, 1, 0, &rot[0]);

	glBufferData(GL_ARRAY_BUFFER, getVertexSize(), getTexCoords(), GL_DYNAMIC_DRAW);

	glUseProgram(program);

	int ret = runSimulation();

	glBindBuffer(GL_ARRAY_BUFFER, vertVBO);//顶点
	glVertexAttribPointer(ah.position, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(float), 0);

	glBindBuffer(GL_ARRAY_BUFFER, texVBO);//纹理
	glVertexAttribPointer(ah.texCoord, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(float), 0);

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indsVBO);
	glDrawElements(GL_TRIANGLE_STRIP, getIndexCount(), GL_UNSIGNED_SHORT, 0);

	//add by Jack:支持脏渲染
	return ret;
}

extern "C" {
    JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_init(JNIEnv * env, jobject obj, jobject bitmap, jint factor, jint radio, jint width, jint height);
    //add by Jack:在上层初始化纹理
//    JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_init2(JNIEnv * env, jobject obj, jint factor, jint wBmp, jint hBmp, jint radio, jint width, jint height);
    JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_init2(JNIEnv * env, jobject obj, jint wBmp, jint hBmp, jint factor, jint radius, jint width, jint height);

    JNIEXPORT jint JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_onDrawFrame(JNIEnv * env, jobject obj);

    JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_initiateRippleAtLocation(JNIEnv * env, jobject obj,  jfloat x, jfloat y);

    JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_onDestroy(JNIEnv * env, jobject obj);

    JNIEXPORT jint JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_getVersionCode(JNIEnv * env, jobject obj);
};

JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_init(JNIEnv * env, jobject obj, jobject bitmap, jint factor, jint radius, jint width, jint height){
	int ret;
	if(bitmap != NULL){
		AndroidBitmapInfo info;
		void* pixels = NULL;
		if((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0){
			return;
		}

		if((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0){
			return;
		}
		initRipple(width, height, factor, radius, info.width, info.height);
		setupGraphics(width, height, pixels, info.width, info.height);
		AndroidBitmap_unlockPixels(env, bitmap);
	}else{
		return;
	}
}
JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_init2(JNIEnv * env, jobject obj, jint wBmp, jint hBmp, jint factor, jint radius, jint width, jint height){
	initRipple(width, height, factor, radius, wBmp, hBmp);
	setupGraphics(width, height, NULL, wBmp, hBmp);
}
JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_initiateRippleAtLocation(JNIEnv * env, jobject obj,  jfloat x, jfloat y) {
	initiateRippleAtLocation(x, y);
}

JNIEXPORT jint JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_onDrawFrame(JNIEnv * env, jobject obj){
	renderFrame();
}

JNIEXPORT void JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_onDestroy(JNIEnv * env, jobject obj){
	freeBuffers();
}

int versionCode = 1000;

JNIEXPORT jint JNICALL Java_com_cyou_cma_cengine_wave_util_CyWaveLib_getVersionCode(JNIEnv * env, jobject obj){
	return versionCode;
}
