#include <jni.h>
#include <string.h>

void make_a_crash() {
    int *p = 0;
    *p = 1; // This will cause a segmentation fault
}

JNIEXPORT void JNICALL
Java_com_zebra_zebrakotlindemo_CBridge_makeACrash(JNIEnv* env, jobject thiz) {
    make_a_crash();
}