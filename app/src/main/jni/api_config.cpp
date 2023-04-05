#include <jni.h>
#include <string>

std::string SERVER_URL          = "384644595049675a36706554504f7363363549504e76677055364f4854516450686d58354b45356e6e364a51446f58354f4958613969716f53534c55664b70743a4f5467334e6a55304d7a49784d475a6c5a474e6959513d3d";
std::string API_KEY             = "36714754694e48754471536837424b493851696e4d754462386e5837363975626f57554170684a347862633d3a4f5467334e6a55304d7a49784d475a6c5a474e6959513d3d";
std::string PURCHASE_CODE       = "673256682f35616a41734e4c4977483962724352667064344a5542347176574f412f353136565452584e434d35727a6175705048586e46696f6e6763324772303a4f5467334e6a55304d7a49784d475a6c5a474e6959513d3d";
std::string ONESIGNAL_APP_ID    = "xxxxxxxxxxxxxxxxxxxxxxxxxxxx";
std::string YOUTUBE_API_KEY     = "xxxxxxxxxxxxxxxxxxxxxxxxxxxx";
std::string TERMS_URL           = "384644595049675a36706554504f7363363549504e745a364457494c59464c3166313462616c555171374a7a785033496f586f574147436235395168614151503a4f5467334e6a55304d7a49784d475a6c5a474e6959513d3d";


//WARNING: ==>> Don't change anything below.
extern "C" JNIEXPORT jstring JNICALL
Java_com_code_files_AppConfig_getApiServerUrl(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF(SERVER_URL.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_code_files_AppConfig_getApiKey(
        JNIEnv* env,
jclass clazz) {
return env->NewStringUTF(API_KEY.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_code_files_AppConfig_getPurchaseCode(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF(PURCHASE_CODE.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_code_files_AppConfig_getOneSignalAppID(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF(ONESIGNAL_APP_ID.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_code_files_AppConfig_getYoutubeApiKey(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF(YOUTUBE_API_KEY.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_code_files_AppConfig_getTermsUrl(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF(TERMS_URL.c_str());
}