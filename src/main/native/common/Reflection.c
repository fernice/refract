#include <jni.h>
#include <stdbool.h>

bool thrown(JNIEnv* env) {
    if ((*env)->ExceptionCheck(env) == JNI_TRUE)
    {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        return true;
    }
    return false;
}

jclass AccessibleObject_class;
jmethodID AccessibleObject_setAccessible_methodID;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    
    AccessibleObject_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/reflect/AccessibleObject"));
    if (thrown(env)) return -1;
    AccessibleObject_setAccessible_methodID = (*env)->GetMethodID(env, AccessibleObject_class, "setAccessible0", "(Z)Z");
    if (thrown(env)) return -1;
    
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    (*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6);
    
    (*env)->DeleteGlobalRef(env, AccessibleObject_class);
}

/*
 * Class:     de_bewotec_playground_Reflection
 * Method:    setAccessible
 * Signature: (Ljava/lang/reflect/AccessibleObject;Z)V
 */
JNIEXPORT void JNICALL Java_org_fernice_refract_internal_Reflection_setAccessible
  (JNIEnv *env, jclass clazz, jobject object, jboolean accessible)
{
    (*env)->CallBooleanMethod(env, object, AccessibleObject_setAccessible_methodID, accessible);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE)
    {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }
}
