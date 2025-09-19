#include <jni.h>
#include <stdio.h>
#include "hd_camera_protocol_cmd.h"

JNIEXPORT jint JNICALL
Java_NativeFunctions_addNumbers(JNIEnv *env, jclass clazz, jint a, jint b) {
    return a + b;
}

JNIEXPORT jstring JNICALL
Java_NativeFunctions_getMessage(JNIEnv *env, jclass clazz) {
    char  buf[1024];
    snprintf(buf,1024,"Hello from JNI! PIC_ID_DELETE_ALL = %d",PIC_ID_DELETE_ALL);
    return (*env)->NewStringUTF(env, buf);
}

JNIEXPORT jstring JNICALL
Java_NativeFunctions_uartVersion(JNIEnv *env, jclass clazz) {
    char  buf[1024];
    snprintf(buf,1024,"PIC_ID_DELETE_ALL = %d",PIC_ID_DELETE_ALL);
    return (*env)->NewStringUTF(env, buf);
}