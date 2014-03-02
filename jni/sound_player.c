#include "sound_player.h"

JNIEXPORT jstring 
Java_com_wordpress_tslantz_stringendo_SoundPlayer_getMyData(
        JNIEnv * env, jobject this) {
    return (*env)->NewStringUTF(
        env,
        "Oogly Boogly"
    );
}

