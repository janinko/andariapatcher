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

using namespace std;



void PrintProcessNameAndID( DWORD processID )
{
    TCHAR szProcessName[MAX_PATH] = TEXT("<unknown>");

    // Get a handle to the process.

    HANDLE hProcess = OpenProcess( PROCESS_QUERY_INFORMATION |
                                   PROCESS_VM_READ,
                                   FALSE, processID );

    // Get the process name.

    if (NULL != hProcess )
    {
        HMODULE hMod;
        DWORD cbNeeded;

        if ( EnumProcessModules( hProcess, &hMod, sizeof(hMod), 
             &cbNeeded) )
        {
            GetModuleBaseName( hProcess, hMod, szProcessName, 
                               sizeof(szProcessName)/sizeof(TCHAR) );
        }
    }

    // Print the process name and identifier.

    _tprintf( TEXT("%s  (PID: %u)\n"), szProcessName, processID );

    CloseHandle( hProcess );
}


void runner()
{
    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
/*
    if( argc != 2 )
    {
        printf("Usage: %s [cmdline]\n", argv[0]);
        return;
    }*/

    // Start the child process. 
    if( !CreateProcess( NULL,   // No module name (use command line)
        "d:\\uo\\AndariaClient.exe",        // Command line
        NULL,           // Process handle not inheritable
        NULL,           // Thread handle not inheritable
        FALSE,          // Set handle inheritance to FALSE
        0,              // No creation flags
        NULL,           // Use parent's environment block
        "d:\\uo",           // Use parent's starting directory 
        &si,            // Pointer to STARTUPINFO structure
        &pi )           // Pointer to PROCESS_INFORMATION structure
    ) 
    {
        printf( "CreateProcess failed (%d)\n", GetLastError() );
        return;
    }

    // Wait until child process exits.
    WaitForSingleObject( pi.hProcess, INFINITE );

    // Close process and thread handles. 
    CloseHandle( pi.hProcess );
    CloseHandle( pi.hThread );
}


int main(int argc, char *argv[])
{
    // Get the list of process identifiers.

    DWORD aProcesses[1024], cbNeeded, cProcesses;
    unsigned int i;

    if ( !EnumProcesses( aProcesses, sizeof(aProcesses), &cbNeeded ) )
        return 0;

    // Calculate how many process identifiers were returned.

    cProcesses = cbNeeded / sizeof(DWORD);

//cout << "procesu: " << cProcesses;

    // Print the name and process identifier for each process.

    for ( i = 0; i < cProcesses; i++ )
        if( aProcesses[i] != 0 )
            PrintProcessNameAndID( aProcesses[i] );

//   system("d:\\uo\\AndariaClient.exe");  
  // ShellExecute( NULL, "open", "d:\\uo\\AndariaClient.exe", NULL, "d:\\uo", SW_NORMAL );
  runner();
   cout<<"necum\n";    

	HDD_ID hddId = getHardDriveComputerID();

	cout << "CPU id____: " << getCpuId () << "\n";
	cout << "MAC Addr__: " << GetMACaddress () << "\n";
	cout << "Hdd id num: " << hddId.idNumber << "\n";
	cout << "Hdd Model_: " << hddId.HardDriveModelNumber << "\n";
	cout << "Hdd Serial: " << hddId.HardDriveSerialNumber << "\n";
 
   system("PAUSE");
   return EXIT_SUCCESS;
}
