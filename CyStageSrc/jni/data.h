#ifndef DATA_H
#define DATA_H

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <math.h>

void initRipple(int width, int height, int factor, int radius, int texWidth, int texHeight);
void initiateRippleAtLocation(float x, float y);
int runSimulation();
float* getVertices();
float* getTexCoords();
unsigned short* getIndices();
unsigned int getVertexSize();
unsigned int getIndexSize();
unsigned int getIndexCount();
void setPoolData(int pw, int ph);
void freeBuffers();
//int del();

#endif
