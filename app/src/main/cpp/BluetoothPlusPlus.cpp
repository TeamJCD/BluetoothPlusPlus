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

        printf("Return value is %lx\n", returnValue);
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
        printf("Return value is %lx\n", returnValue);
    }

    injector::callMunmap(pid, remoteDeviceClassPropertyAddress, sizeof(deviceClassProperty));
    injector::callMunmap(pid, remoteDeviceClassAddress, sizeof(dev_class_t));

    return (int) returnValue;
}
