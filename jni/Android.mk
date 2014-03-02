LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := sndplyr
LOCAL_SRC_FILES := sound_player.c
include $(BUILD_SHARED_LIBRARY)
