// GetMACAdapters.cpp : Defines the entry point for the console application.
//
// Author:	Khalid Shaikh [Shake@ShakeNet.com]
// Date:	April 5th, 2002
//
// This program fetches the MAC address of the localhost by fetching the 
// information through GetAdapatersInfo.  It does not rely on the NETBIOS
// protocol and the ethernet adapter need not be connect to a network.
//
// Supported in Windows NT/2000/XP
// Supported in Windows 95/98/Me
//
// Supports multiple NIC cards on a PC.
#include "stdafx.h"
#include "macId.h"


// Prints the MAC address stored in a 6 byte array to stdout
static void PrintMACaddress(unsigned char MACData[])
{

#ifdef PRINTING_TO_CONSOLE_ALLOWED

	printf("\nMAC Address: %02X-%02X-%02X-%02X-%02X-%02X\n", 
		MACData[0], MACData[1], MACData[2], MACData[3], MACData[4], MACData[5]);

#endif

   char string [256];
   
   WriteConstantString ("MACaddress", string);
}



// Fetches the MAC address and prints it
DWORD GetMACaddress(void)
{
  DWORD MACaddress = 0;
  IP_ADAPTER_INFO AdapterInfo[16];       // Allocate information
                                         // for up to 16 NICs
  DWORD dwBufLen = sizeof(AdapterInfo);  // Save memory size of buffer

  DWORD dwStatus = GetAdaptersInfo(      // Call GetAdapterInfo
			AdapterInfo,                 // [out] buffer to receive data
			&dwBufLen);                  // [in] size of receive data buffer
  assert(dwStatus == ERROR_SUCCESS);  // Verify return value is
                                      // valid, no buffer overflow

  PIP_ADAPTER_INFO pAdapterInfo = AdapterInfo; // Contains pointer to
                                               // current adapter info
  do {
	if (MACaddress == 0)
		MACaddress = pAdapterInfo->Address [5] + pAdapterInfo->Address [4] * 256 + 
					pAdapterInfo->Address [3] * 256 * 256 + 
					pAdapterInfo->Address [2] * 256 * 256 * 256;
    PrintMACaddress(pAdapterInfo->Address); // Print MAC address
    pAdapterInfo = pAdapterInfo->Next;    // Progress through linked list
  }
  while(pAdapterInfo);                    // Terminate if last adapter
  
  return MACaddress;
}
