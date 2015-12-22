LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := CyWave
LOCAL_CFLAGS    := -Werror
LOCAL_SRC_FILES := main.cpp data.cpp
LOCAL_LDLIBS    := -llog -lGLESv2 -ljnigraphics

include $(BUILD_SHARED_LIBRARY)