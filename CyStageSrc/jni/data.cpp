#include "data.h"

float* rippleSource = NULL;
float* rippleDest = NULL;
float* rippleCoeff = NULL;

float* rippleVertices = NULL;
float* rippleTexCoords = NULL;
unsigned short* rippleIndicies = NULL;

float texCoordFactorS;
float texCoordOffsetS;
float texCoordFactorT;
float texCoordOffsetT;

unsigned int screenWidth;
unsigned int screenHeight;
unsigned int poolWidth;
unsigned int poolHeight;
unsigned int touchRadius;

//add by Jack: 优化性能
unsigned int vertexSize;
unsigned int getVertexSize() {
    return vertexSize;
}

unsigned int indexSize;
unsigned int getIndexSize() {
    return indexSize;
}

unsigned int indexCount;
unsigned int getIndexCount() {
    return indexCount;
}

void setPoolData(int pw, int ph){
	poolWidth = pw;
	poolHeight = ph;

	vertexSize = poolWidth * poolHeight * 2 * sizeof(float);
	indexSize = (poolHeight - 1) * (poolWidth * 2 + 2) * sizeof(unsigned short);
	indexCount = indexSize / sizeof(unsigned short);
}
//end

void initRipple(int width, int height, int factor, int radius, int texWidth, int texHeight) {
	screenWidth = width;
	screenHeight = height;

	setPoolData(width/factor, height/factor);

	touchRadius = radius;

	//modify by Jack: 全屏适配
//	if ((float)screenHeight / screenWidth < (float) texWidth/ texHeight) {
//		texCoordFactorS = (float)(texHeight * screenHeight) / (screenWidth * texWidth);
//	    texCoordOffsetS = (1.f - texCoordFactorS) / 2.f;

	    texCoordFactorT = 1.f;
	    texCoordOffsetT = 0.f;
	    texCoordFactorS = 1.f;
	    texCoordOffsetS = 0.f;

//	} else {
//	    texCoordFactorS = 1.f;
//	    texCoordOffsetS = 0.f;
//
//	    texCoordFactorT = (float)(screenWidth * texWidth) / (texHeight * screenHeight);
//	    texCoordOffsetT = (1.f - texCoordFactorT) / 2.f;
//	}

//	LOGI("texcoordFactorS = %f, texcoordOffsetS = %f, texcoordFactorT = %f, texcoordOffsetT = %f", texCoordFactorS,texCoordOffsetS,texCoordFactorT,texCoordOffsetT);

	freeBuffers();

	int i = 0;
	int j = 0;

	// init coffe
	rippleCoeff = (float *)malloc((touchRadius * 2 + 1) * (touchRadius * 2 + 1) * sizeof(float));
	for (i=0; i<=2*touchRadius; i++) {
		for (j=0; j<=2*touchRadius; j++) {
			float distance = sqrt((j - touchRadius) * (j - touchRadius) + (i - touchRadius) * (i - touchRadius));

			if (distance <= touchRadius) {
				float factor = (distance / touchRadius);

			    // goes from -512 -> 0
			    rippleCoeff[i * (touchRadius * 2 + 1) + j] = -(cos(factor * 3.1415926f) + 1.f) * 128.f;
			} else {
			    rippleCoeff[i * (touchRadius * 2 + 1) + j] = 0.f;
			}
		}
	}

	// init ripple map
	// +2 for padding the border
	rippleSource = (float *)malloc((poolWidth + 2) * (poolHeight + 2) * sizeof(float));
	rippleDest = (float *)malloc((poolWidth + 2) * (poolHeight + 2) * sizeof(float));
	memset(rippleSource, 0, (poolWidth + 2) * (poolHeight + 2) * sizeof(float));
	memset(rippleDest, 0, (poolWidth + 2) * (poolHeight + 2) * sizeof(float));

	rippleVertices = (float *)malloc(poolWidth * poolHeight * 2 * sizeof(float));
	rippleTexCoords = (float *)malloc(poolWidth * poolHeight * 2 * sizeof(float));
	rippleIndicies = (unsigned short *)malloc((poolHeight - 1) * (poolWidth * 2 + 2) * sizeof(unsigned short));

	// niit mesh
	for (i=0; i<poolHeight; i++) {
		for (j=0; j<poolWidth; j++) {
	        rippleVertices[(i*poolWidth+j)*2+0] = -1.f + j*(2.f/(poolWidth-1));
	        rippleVertices[(i*poolWidth+j)*2+1] = 1.f - i*(2.f/(poolHeight-1));

	        rippleTexCoords[(i*poolWidth+j)*2+0] = (float)i/(poolHeight-1) * texCoordFactorS + texCoordOffsetS;
	        rippleTexCoords[(i*poolWidth+j)*2+1] = (1.f - (float)j/(poolWidth-1)) * texCoordFactorT + texCoordOffsetT;
	    }
	}

	unsigned int index = 0;
	for (i=0; i<poolHeight-1; i++) {
		for (j=0; j<poolWidth; j++) {
			if (i%2 == 0) {
				// emit extra index to create degenerate triangle
			    if (j == 0) {
			    	rippleIndicies[index++] = (short)(i * poolWidth + j);
			    }

			    rippleIndicies[index++] = (short)(i * poolWidth + j);
			    rippleIndicies[index++] = (short)((i + 1) * poolWidth + j);

			    // emit extra index to create degenerate triangle
			    if (j == (poolWidth-1)) {
			    	rippleIndicies[index++] = (short)((i + 1) * poolWidth + j);
			    }
			} else {
			    // emit extra index to create degenerate triangle
			    if (j == 0) {
			    	rippleIndicies[index++] = (short)((i + 1) * poolWidth + j);
			    }

			    rippleIndicies[index++] = (short)((i + 1) * poolWidth + j);
			    rippleIndicies[index++] = (short)(i * poolWidth + j);

			    // emit extra index to create degenerate triangle
			    if (j == (poolWidth - 1)) {
			    	rippleIndicies[index++] = (short)(i * poolWidth + j);
			    }
			}
		}
	}
}
//static int x = 0;
//int del(){
//	int i, j;
//
//	j = getIndexCount() - x;
//	for (i = 0; i < j; i ++){
//		getIndices()[i] = getIndices()[i+1];
//	}
//	x+=10;
//	LOGI("x = %d", x);
//	return j;
//
//
//}

void initiateRippleAtLocation(float x, float y) {
    unsigned int xIndex = (unsigned int)((x / screenWidth) * poolWidth);
    unsigned int yIndex = (unsigned int)((y / screenHeight) * poolHeight);
//    LOGI("xIndex = %d, yIndex = %d", xIndex, yIndex);

    int i = 0;
    int j = 0;
    for (i=(int)yIndex - (int)touchRadius; i<=(int)yIndex + (int)touchRadius; i++) {
        for (j=(int)xIndex - (int)touchRadius; j<=(int)xIndex + (int)touchRadius; j++) {
            if (j >= 0 && j < poolWidth &&
                i >= 0 && i < poolHeight) {
                // +1 to both x/y values because the border is padded
                int indexSource = (poolWidth + 2) * (i + 1) + j + 1;
                int indexCoeff = (i - (yIndex - touchRadius)) * (touchRadius * 2 + 1) + j - (xIndex - touchRadius);
                rippleSource[indexSource] += rippleCoeff[indexCoeff];
            }
        }
    }
}

int runSimulation() {
	int needRefresh = 0;

	int x = 0;
	int y = 0;
	for (y=0; y<poolHeight; ++y) {
		// +1 to both x/y values because the border is padded
		int indexL = (y + 1) * (poolWidth + 2) - 1 + 1;	// left
		int indexT = (y + 0) * (poolWidth + 2) + 0 + 1;	// top
		int indexB = (y + 2) * (poolWidth + 2) + 0 + 1;	// bottom
		int indexR = (y + 1) * (poolWidth + 2) + 1 + 1;	// right
		int indexC = (y + 1) * (poolWidth + 2) + 0 + 1; // center
		for (x=0; x<poolWidth; x++) {
			// simulation buffers...
			// * - denotes current pixel
			//
			//       t
			//     l * r
			//       b
			float t = rippleSource[indexT + x];
			float b = rippleSource[indexB + x];
			float l = rippleSource[indexL + x];
			float r = rippleSource[indexR + x];

			float result = (t + b + l + r) / 2.0f - rippleDest[indexC + x];
			result -= result / 20.f;
			rippleDest[indexC + x] = result;

			t = rippleDest[indexT + x];
			b = rippleDest[indexB + x];
			l = rippleDest[indexL + x];
			r = rippleDest[indexR + x];

			float s_offset = ((b - t) / 2048.0f);
			float t_offset = ((l - r) / 2048.0f);

			// clamp
			float clampFactor = 0.5f;
			s_offset = (s_offset < -clampFactor) ? -clampFactor : s_offset;
			t_offset = (t_offset < -clampFactor) ? -clampFactor : t_offset;
			s_offset = (s_offset > clampFactor) ? clampFactor : s_offset;
			t_offset = (t_offset > clampFactor) ? clampFactor : t_offset;

			float s_tc = (float)y / (poolHeight - 1) * texCoordFactorS + texCoordOffsetS;
			float t_tc = (1.f - (float)x / (poolWidth - 1)) * texCoordFactorT + texCoordOffsetT;

			int index = (y * poolWidth + x) * 2;
			rippleTexCoords[index + 0] = s_tc + s_offset;
			rippleTexCoords[index + 1] = t_tc + t_offset;

			if (needRefresh == 0) {
				needRefresh = ((s_offset > 0.001f || s_offset < -0.001f) || (t_offset > 0.001f || t_offset < -0.001f));
			}
		}
	}

	float* pTmp = rippleDest;
	rippleDest = rippleSource;
	rippleSource = pTmp;

    return needRefresh;
}

float* getVertices() {
    return rippleVertices;
}

float* getTexCoords() {
    return rippleTexCoords;
}

unsigned short* getIndices() {
    return rippleIndicies;
}

void freeBuffers() {
    free(rippleCoeff);
    rippleCoeff = NULL;

    free(rippleSource);
    rippleSource = NULL;

    free(rippleDest);
    rippleDest = NULL;

    free(rippleVertices);
    rippleVertices = NULL;

    free(rippleTexCoords);
    rippleTexCoords = NULL;

    free(rippleIndicies);
    rippleIndicies = NULL;
}
