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
    long remoteFunctionAddress = utils::getRemoteFunctionAddress(scanSize, SIGNATURE_GET_DEV_CLASS, memory, remoteBaseAddress);

    if (remoteFunctionAddress == -1) {
        printf("Unable to find signature\n");
        return -1;
    }

    long mmapAddressDeviceClass = injector::callMmap(pid, DEV_CLASS_LEN);

    dev_class_property_t deviceClassProperty { .val = (void*) mmapAddressDeviceClass };
    long mmapAddressDeviceClassProperty = injector::callMmap(pid, sizeof(deviceClassProperty));
    injector::write(pid, (uint8_t*) mmapAddressDeviceClassProperty, (uint8_t*) &deviceClassProperty, sizeof(deviceClassProperty));

    long params[1];
    params[0] = mmapAddressDeviceClassProperty;

    long returnAddress = injector::callRemoteFunction(pid, remoteFunctionAddress, params, 1);
    /*if (returnAddress == 0) {
        printf("Return address is 0\n");
        return -1;
    }*/

    if (utils::readRemoteMemory(pid, mmapAddressDeviceClass, deviceClass, DEV_CLASS_LEN) != 0) {
        printf("Unable to read remote memory at address %lx\n", returnAddress);
        return -1;
    }

    injector::callMunmap(pid, mmapAddressDeviceClassProperty, sizeof(deviceClassProperty));
    injector::callMunmap(pid, mmapAddressDeviceClass, DEV_CLASS_LEN);

    return 0;
}

int BluetoothPlusPlus::setDeviceClass(dev_class_t *deviceClass) {
    long remoteFunctionAddress = utils::getRemoteFunctionAddress(scanSize, SIGNATURE_SET_DEV_CLASS, memory, remoteBaseAddress);

    if (remoteFunctionAddress == -1) {
        printf("Unable to find signature\n");
        return -1;
    }

    long mmapAddressDeviceClass = injector::callMmap(pid, DEV_CLASS_LEN);
    injector::write(pid, (uint8_t*) mmapAddressDeviceClass, (uint8_t*) deviceClass, DEV_CLASS_LEN);

    dev_class_property_t deviceClassProperty { .val = (void*) mmapAddressDeviceClass };
    long mmapAddressDeviceClassProperty = injector::callMmap(pid, sizeof(deviceClassProperty));
    injector::write(pid, (uint8_t*) mmapAddressDeviceClassProperty, (uint8_t*) &deviceClassProperty, sizeof(deviceClassProperty));

    long params[1];
    params[0] = mmapAddressDeviceClassProperty;

    long returnAddress = injector::callRemoteFunction(pid, remoteFunctionAddress, params, 1);
    /*if (returnAddress == 0) {
        printf("Return address is 0\n");
        return -1;
    }*/

    injector::callMunmap(pid, mmapAddressDeviceClassProperty, sizeof(deviceClassProperty));
    injector::callMunmap(pid, mmapAddressDeviceClass, DEV_CLASS_LEN);

    /*int result;
    if (utils::readRemoteMemory(pid, returnAddress, &result, sizeof(int)) != 0) {
        printf("Unable to read remote memory at address %lx\n", returnAddress);
        return -1;
    }

    return result;*/
    return 0;
}
