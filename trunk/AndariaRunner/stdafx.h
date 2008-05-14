// stdafx.h : include file for standard system include files,
// or project specific include files that are used frequently, but
// are changed infrequently
//

//#define PRINTING_TO_CONSOLE_ALLOWED

#include "targetver.h"
#include <stdio.h>
#include <iostream>
#include <tchar.h>

#include <windows.h>
#include <psapi.h>
#include <cstdlib>
#include <Tlhelp32.h>

typedef struct
{
	char HardDriveSerialNumber[1024];
	char HardDriveModelNumber[1024];
	long idNumber;
} HDD_ID;

//#pragma once
#include "macId.h"
#include "hddId.h"
#include "cpuId.h"
#include "process.h"
#include "runner.h"
#include "socket.h"

#define  TITLE   "DiskId32"



void WriteConstantString (char *entry, char *string);