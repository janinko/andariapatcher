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

#include <windows.h>
#include <Iphlpapi.h>
#include <Assert.h>
#include <iostream>
#include <sstream>
#include <stdlib.h>
#include "md5wrapper.h"

using namespace std;

#pragma comment(lib, "iphlpapi.lib")

#include "mac.h"

/* *********************************************************************************** 
 * Mac::Mac
 * ***********************************************************************************/
Mac::Mac() 
{
	// vysledna adresa
	DWORD address = 0;
	// alokace pameti pro 16 sitovych karet
	IP_ADAPTER_INFO AdapterInfo[MAX_IF_COUNT];
	// velikost bufferu pro ukladani
	DWORD dwBufLen = sizeof(AdapterInfo);

	// zjisteni informaci o sitove karte
	DWORD dwStatus = GetAdaptersInfo( 
						AdapterInfo,                 // vystup
						&dwBufLen);                  // velikost bufferu
						assert(dwStatus == ERROR_SUCCESS);  // overi jestli neni problem, treba preteceni bufferu

	PIP_ADAPTER_INFO pAdapterInfo = AdapterInfo; // pointer na sitovku se kterou se pracuje
	//char * list;
	
	

	for (addrCnt=0;pAdapterInfo;addrCnt++)
	{
		// zabere misto v pameti
		addrList[addrCnt] = (char *) malloc(17 + 1);	
		// sestavi adresu a ulozi do pole adres
		sprintf(addrList[addrCnt], "%02X-%02X-%02X-%02X-%02X-%02X",  pAdapterInfo->Address[0], pAdapterInfo->Address[1], pAdapterInfo->Address[2], pAdapterInfo->Address[3], pAdapterInfo->Address[4], pAdapterInfo->Address[5]);

		addrsId[addrCnt] = pAdapterInfo->Address [5] + pAdapterInfo->Address [4] * 256 + 
					pAdapterInfo->Address [3] * 256 * 256 + 
					pAdapterInfo->Address [2] * 256 * 256 * 256;

		pAdapterInfo = pAdapterInfo->Next;    // dalsi
	}
}

/* *********************************************************************************** 
 * Mac::print
 * Prints the MAC address stored in a 6 byte array to stdout
 * ***********************************************************************************/

void Mac::print()
{
	for (int i=0;i<addrCnt;i++)
		cout << addrList[i] << "MAC("<< i<<") Adresa:"<< addrsId[i] <<"\n";
}

/* *********************************************************************************** 
 * Mac::getInfo
 * vrati retezec popisujici sitovky
 * ***********************************************************************************/

char * Mac::getInfo()
{

	// vrati hash pro vsechny sitovky oddeleny strdnikem.
	// duvodem je, aby server ziskal pouze unikatni identifikator klienta, ale neziskal
	// zadne konkretni informace.

	// creating a wrapper object
	md5wrapper md5;
	// create a hash from a string
	std::string hash;

	// sestavy vysledek
	std::stringstream sstr;
	std::string str;
	for (int i=0;i<addrCnt;i++)
	{
		hash = md5.getHashFromString(addrList[i]);
		sstr << hash << ":";
		str = sstr.str();
	}
	// prevod na ceckovy char *
	char * result = new char[str.length() + 1];	
	strcpy (result, str.c_str());
	return result;
}

/* *********************************************************************************** 
 * Mac::~Mac
 * ***********************************************************************************/
Mac::~Mac() {
	// uvolnit pamet je slusnost 
	for (int i=0;i<addrCnt;i++)
		free(addrList[i]);

}