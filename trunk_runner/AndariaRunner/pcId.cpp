// pcId.cpp : Defines the entry point for the console application.
//


//  diskid32.cpp


//  for displaying the details of hard drives in a command window


//  06/11/00  Lynn McGuire  written with many contributions from others,
//                            IDE drives only under Windows NT/2K and 9X,
//                            maybe SCSI drives later
//  11/20/03  Lynn McGuire  added ReadPhysicalDriveInNTWithZeroRights
//  10/26/05  Lynn McGuire  fix the flipAndCodeBytes function
//  01/22/08  Lynn McGuire  incorporate changes from Gonzalo Diethelm,
//                             remove media serial number code since does 
//                             not work on USB hard drives or thumb drives
//  01/29/08  Lynn McGuire  add ReadPhysicalDriveInNTUsingSmart


#include "stdafx.h"
#include "runner.h"
using namespace std;


int main(int argc, char *argv[])
{
	getProcssList();
	cout<<"necum\n";    

	HDD_ID hddId = getHardDriveComputerID();

	cout << "CPU id____: " << getCpuId () << "\n";
	cout << "MAC Addr__: " << GetMACaddress () << "\n";
	cout << "Hdd id num: " << hddId.idNumber << "\n";
	cout << "Hdd Model_: " << hddId.HardDriveModelNumber << "\n";
	cout << "Hdd Serial: " << hddId.HardDriveSerialNumber << "\n";
 
	// promene pro ulozeni informacio vlaknu
	DWORD  dwThreadID;
	HANDLE hThread;

	hThread =  CreateThread(NULL,
                             0,
                             runClient,
                             NULL,
                             0,
                             &dwThreadID);
    if(!hThread)
    {
      // Could not create thread
      cout << "Nepodarilo se vytvorit vlakno s klientem\n";
	  return EXIT_FAILURE;
    }
   
	
	while (WAIT_TIMEOUT==WaitForSingleObject(hThread, 2000))
	{
		cout<<"stale pracuju\n";
	}
   return EXIT_SUCCESS;
}
