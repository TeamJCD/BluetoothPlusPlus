#include <cstring>
#include <jni.h>
#include "btif.h"
#include "btm.h"
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
        }
    }

    JNIEXPORT jint JNICALL
    Java_com_github_teamjcd_bpp_BluetoothDeviceClassSettings_getBluetoothClassNative(__unused JNIEnv* env,
                                                                                     __unused jobject obj) {
        if (!qti) {
            return -1;
        }

        ptrace_attach(pid);

        long so_handle = call_dlopen(pid);
        if (!so_handle) {
            ALOGE("setBluetoothClassNative - Injection failed");
            return -1;
        }

        DEV_CLASS buf;
        bt_property_t prop;
        prop.type = BT_PROPERTY_CLASS_OF_DEVICE;
        prop.val = (void*) buf;
        prop.len = sizeof(buf);

        call_btif_dm_get_adapter_property(pid, so_handle, &prop);

        call_dlclose(pid, so_handle);

        ptrace_detach(pid);

        return *buf;
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

        ALOGD("setBluetoothClassNative - dev_class: 0x%2s%2s%2s", dev_class[0], dev_class[1], dev_class[2]);

        long result = call_BTM_SetDeviceClass(pid, so_handle, dev_class);

        call_dlclose(pid, so_handle);

        ptrace_detach(pid);

        if (result != 0) {
            ALOGE("setBluetoothClassNative - Unable to set dev_class: 0x%2s%2s%2s",
                  dev_class[0], dev_class[1], dev_class[2]);

            return JNI_FALSE;
        }

        return JNI_TRUE;
    }
}
