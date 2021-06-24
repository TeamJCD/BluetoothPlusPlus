#include <cstring>
#include <stdexcept>
#include <sys/stat.h>
#include "BluetoothPlusPlus.h"
#include "injector.h"
#include "utils.h"

using namespace std;

BluetoothPlusPlus::BluetoothPlusPlus() {
    const char *processName = "com.android.bluetooth";
    if ((pid = utils::getProcessId(processName)) == -1) {
        throw runtime_error("Unable to find process");
    }

    if ((remoteBaseAddress = utils::getRemoteBaseAddress(pid, LIBRARY_PATH)) == 0) {
        throw runtime_error("Unable to get remote base address");
    }

    if (injector::attach(pid) != 0) {
        throw runtime_error("Unable to attach to process");
    }

    //long scanSize = 4148176;
    struct stat buf{};
    if (stat(LIBRARY_PATH, &buf) != 0) {
        throw runtime_error("Unable to get file status");
    }

    scanSize = buf.st_size;
    memory = (char*) malloc(sizeof(char) * scanSize);
    if (utils::readRemoteMemory(pid, remoteBaseAddress, memory, scanSize) != 0) {
        throw runtime_error("Unable to read remote memory");
    }
}

BluetoothPlusPlus::~BluetoothPlusPlus() {
    free(memory);
    injector::detach(pid);
}

int BluetoothPlusPlus::getDeviceClass(dev_class_t *deviceClass) {
    // TODO BEGIN: remove this again, just for debugging purposes
    /*#if defined(__aarch64__) // arm64 qti
        const char* setTraceLevelSignature = "\xe8\x03\x20\x2a\x1f\x1d\x00\x72\xa0\x00\x00\x54\x28\x14\x00\xb0\x08\xbd\x41\xf9\x00\x01\x00\x39\xc0\x03\x5f\xd6\x28\x14\x00\xb0\x08\xbd\x41\xf9\x00\x01\x40\x39\xc0\x03\x5f\xd6";
    #else // arm aosp
        const char* setTraceLevelSignature = "\x28\x04\xd0\x05\x49\x79\x44\x09\x68\x08\x70\x70\x47\x02\x48\x78\x44\x00\x68\x00\x78\x70\x47\xa8\x73\x17\x00\xb2\x73\x17\x00\x2d";
    #endif
    long setTraceLevelAddress = utils::getRemoteFunctionAddress(scanSize, setTraceLevelSignature, memory, remoteBaseAddress);
    ALOGD("setTraceLevel address: %lx", setTraceLevelAddress);
    injector::callRemoteFunction(pid, setTraceLevelAddress, new long[]{6}, 1);
    */// TODO END

    long remoteDeviceClassAddress = injector::callMmap(pid, sizeof(dev_class_t));

    dev_class_property_t deviceClassProperty { .val = (void*) remoteDeviceClassAddress };
    long remoteDeviceClassPropertyAddress = injector::callMmap(pid, sizeof(deviceClassProperty));
    injector::write(pid, (uint8_t*) remoteDeviceClassPropertyAddress, (uint8_t*) &deviceClassProperty, sizeof(deviceClassProperty));

    long remoteFunctionAddress = utils::getRemoteFunctionAddress(scanSize, SIGNATURE_GET_DEV_CLASS, memory, remoteBaseAddress);

    if (remoteFunctionAddress == -1) {
        injector::callMunmap(pid, remoteDeviceClassPropertyAddress, sizeof(deviceClassProperty));
        injector::callMunmap(pid, remoteDeviceClassAddress, sizeof(dev_class_t));

        printf("Unable to find signature\n");
        return -1;
    }

    long returnValue = injector::callRemoteFunction(pid, remoteFunctionAddress, new long[]{ remoteDeviceClassPropertyAddress }, 1);
    if (returnValue != 0) {
        injector::callMunmap(pid, remoteDeviceClassPropertyAddress, sizeof(deviceClassProperty));
        injector::callMunmap(pid, remoteDeviceClassAddress, sizeof(dev_class_t));

        printf("Return value is %ld\n", returnValue);
        return (int) returnValue;
    }

    if (utils::readRemoteMemory(pid, remoteDeviceClassAddress, deviceClass, sizeof(dev_class_t)) != 0) {
        injector::callMunmap(pid, remoteDeviceClassPropertyAddress, sizeof(deviceClassProperty));
        injector::callMunmap(pid, remoteDeviceClassAddress, sizeof(dev_class_t));

        printf("Unable to read remote memory at address %lx\n", remoteDeviceClassAddress);
        return -1;
    }

    injector::callMunmap(pid, remoteDeviceClassPropertyAddress, sizeof(deviceClassProperty));
    injector::callMunmap(pid, remoteDeviceClassAddress, sizeof(dev_class_t));

    return 0;
}

int BluetoothPlusPlus::setDeviceClass(dev_class_t *deviceClass) {
    long remoteDeviceClassAddress = injector::callMmap(pid, sizeof(dev_class_t));
    injector::write(pid, (uint8_t*) remoteDeviceClassAddress, (uint8_t*) deviceClass, sizeof(dev_class_t));

    dev_class_property_t deviceClassProperty { .val = (void*) remoteDeviceClassAddress };
    long remoteDeviceClassPropertyAddress = injector::callMmap(pid, sizeof(deviceClassProperty));
    injector::write(pid, (uint8_t*) remoteDeviceClassPropertyAddress, (uint8_t*) &deviceClassProperty, sizeof(deviceClassProperty));

    long remoteFunctionAddress = utils::getRemoteFunctionAddress(scanSize, SIGNATURE_SET_DEV_CLASS, memory, remoteBaseAddress);

    if (remoteFunctionAddress == -1) {
        injector::callMunmap(pid, remoteDeviceClassPropertyAddress, sizeof(deviceClassProperty));
        injector::callMunmap(pid, remoteDeviceClassAddress, sizeof(dev_class_t));

        printf("Unable to find signature\n");
        return -1;
    }

    long returnValue = injector::callRemoteFunction(pid, remoteFunctionAddress, new long[]{ remoteDeviceClassPropertyAddress }, 1);
    if (returnValue != 0) {
        printf("Return value is %ld\n", returnValue);
    }

    injector::callMunmap(pid, remoteDeviceClassPropertyAddress, sizeof(deviceClassProperty));
    injector::callMunmap(pid, remoteDeviceClassAddress, sizeof(dev_class_t));

    return (int) returnValue;
}
