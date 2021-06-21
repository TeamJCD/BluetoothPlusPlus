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

int BluetoothPlusPlus::readDeviceClass(void *deviceClass) {
    long remoteFunctionAddress = utils::getRemoteFunctionAddress(scanSize, SIGNATURE_GET_DEV_CLASS, memory, remoteBaseAddress);

    if (remoteFunctionAddress == -1) {
        printf("Unable to find signature\n");
        return -1;
    }

    long returnAddress = injector::callRemoteFunction(pid, remoteFunctionAddress, nullptr, 0);
    utils::readRemoteMemory(pid, returnAddress, deviceClass, DEV_CLASS_LEN);
    return 0;
}

int BluetoothPlusPlus::setDeviceClass(void *deviceClass) {
    long remoteFunctionAddress = utils::getRemoteFunctionAddress(scanSize, SIGNATURE_SET_DEV_CLASS, memory, remoteBaseAddress);

    if (remoteFunctionAddress == -1) {
        printf("Unable to find signature\n");
        return -1;
    }

    long mmapAddress = injector::callMmap(pid, 0x400);
    injector::write(pid, (uint8_t*) mmapAddress, (uint8_t*) deviceClass, DEV_CLASS_LEN);

    long params[1];
    params[0] = mmapAddress;

    int result;
    long returnAddress = injector::callRemoteFunction(pid, remoteFunctionAddress, params, 1);
    utils::readRemoteMemory(pid, returnAddress, &result, sizeof(int));

    injector::callMunmap(pid, mmapAddress, 0x400);

    return result;
}
