#include <windows.h>
#include <iostream>

HANDLE createAppThread(DWORD &dwThreadID);
DWORD WINAPI runClient( LPVOID lpParam );