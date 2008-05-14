#include <windows.h>
#include <Iphlpapi.h>
#include <Assert.h>
#pragma comment(lib, "iphlpapi.lib")


DWORD GetMACaddress(void);
//static void PrintMACaddress(unsigned char MACData[]);