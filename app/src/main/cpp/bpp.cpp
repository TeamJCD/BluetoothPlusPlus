#include <bpp.h>
#include <jni.h>

extern "C" {
  JNIEXPORT void JNICALL
  Java_com_github_teamjcd_bpp_fragment_BppMainFragment_classInitNative(JNIEnv* env, jclass clazz) {
    if (!hal_util_load_bt_library(&interface)) {
      ALOGE("Failed to open the Bluetooth module");
    }
  }

  JNIEXPORT jstring JNICALL
  Java_com_github_teamjcd_bpp_fragment_BppMainFragment_getAddressNative(JNIEnv* env, jobject obj) {
    interface->get_adapter_property(BT_PROPERTY_BDADDR);
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
  }
}

int hal_util_load_bt_library(const bt_interface_t** interface) {
  bt_interface_t* itf{nullptr};

  void* handle = dlopen(kLibbluetooth, RTLD_NOW);
  if (!handle) {
    const char* err_str = dlerror();
    ALOGE("%s: failed to load bluetooth library, error=%s", __func__, err_str ? err_str : "error unknown");
    goto error;
  }

  itf = (bt_interface_t*) dlsym(handle, kBluetoothInterfaceSym);
  if (!itf) {
    ALOGE("%s: failed to load symbol from Bluetooth library ", __func__, kBluetoothInterfaceSym);
    goto error;
  }

  ALOGI("%s: loaded HAL path=%s btinterface=%s handle=%s", __func__, kLibbluetooth, itf, handle);
  *interface = itf;
  return 0;

  error:
    *interface = NULL;
    if (handle) dlclose(handle);
    return -EINVAL;
}
