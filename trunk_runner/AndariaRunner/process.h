#include <windows.h>
#include <stdio.h>
#include <tchar.h>
#include "psapi.h"

#pragma comment(lib, "Psapi")

void getProcssList();
void PrintProcessNameAndID( DWORD processID );
