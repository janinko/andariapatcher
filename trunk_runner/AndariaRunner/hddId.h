#include <stdlib.h>
#include <stddef.h>
#include <string.h>
#include <windows.h>
#include <winioctl.h>

   //  Required to ensure correct PhysicalDrive IOCTL structure setup
#pragma pack(1)

	//  special include from the MS DDK
//#include "c:\win2kddk\inc\ddk\ntddk.h"
//#include "c:\win2kddk\inc\ntddstor.h"



HDD_ID getHardDriveComputerID ();
static void dump_buffer (const char* title,
			const unsigned char* buffer,
			int len);