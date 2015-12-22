#ifndef MAIN_H
#define MAIN_H

#include <jni.h>
#include <android/log.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/bitmap.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>
#include "data.h"

#define LOG_TAG "____"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct _AttributeHandle {
	GLuint position;
	GLuint texCoord;
}AttributeHandle;

typedef struct _UniformHandle {
	GLuint sampler;
}UniformHandle;

AttributeHandle ah;
UniformHandle uh;
GLint modelviewUniform;
GLuint program;
GLuint texture;

int mWidth;
int mHeight;

#endif
