#include <cstring>
#include <jni.h>
#include "bpp_jni.h"
#include "injector.h"
#include "ptrace.h"
#include "utils.h"

extern "C" {
    JNIEXPORT void JNICALL
    Java_com_github_teamjcd_bpp_BluetoothDeviceClassSettings_classInitNative(__unused JNIEnv* env,
                                                                             __unused jclass clazz) {
        // TODO set qti = true if libbluetooth_qti.so exists
        qti = true;

        if (qti) {
            pid = get_pid();
            ALOGD("classInitNative - Bluetooth pid: %d", pid);
        }
    }

    JNIEXPORT jint JNICALL
    Java_com_github_teamjcd_bpp_BluetoothDeviceClassSettings_getBluetoothClassNative(__unused JNIEnv* env,
                                                                                     __unused jobject obj) {
        // TODO
        return 0;
    }

    JNIEXPORT jboolean JNICALL
    Java_com_github_teamjcd_bpp_BluetoothDeviceClassSettings_setBluetoothClassNative(JNIEnv *env,
                                                                                     __unused jobject obj,
                                                                                     jbyteArray value) {
        if (!qti) {
            return JNI_TRUE;
        }

        ptrace_attach(pid);

        long so_handle = call_dlopen(pid);
        if (!so_handle) {
            ALOGE("setBluetoothClassNative - Injection failed");
            return JNI_FALSE;
        }

        jbyte *val = env->GetByteArrayElements(value, nullptr);

        DEV_CLASS *dev_class = nullptr;
        memcpy(dev_class, val, DEV_CLASS_LEN);

        //call_dlsym?

        ptrace_detach(pid);
        return JNI_TRUE;
    }
}
