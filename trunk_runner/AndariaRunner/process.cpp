#include "stdafx.h"
#include "process.h"

void PrintProcessNameAndID( DWORD processID )
{
    TCHAR szProcessName[MAX_PATH] = TEXT("<unknown>");

    // Get a handle to the process.
    HANDLE hProcess = OpenProcess( PROCESS_QUERY_INFORMATION |
                                       PROCESS_VM_READ,
                                       FALSE,  processID );

    // Get the process name.
    if (NULL != hProcess )
    {
        HMODULE hMod;
        DWORD cbNeeded;

        if ( EnumProcessModules( hProcess, &hMod, sizeof(hMod), 
               &cbNeeded) )
        {
              GetModuleBaseName( hProcess, hMod, szProcessName, 
                                   sizeof(szProcessName)/sizeof(TCHAR)  );
        }
    }
    // Print the process name and identifier.
    _tprintf( TEXT("%s  (PID: %u)\n"), szProcessName, processID );
    CloseHandle( hProcess );

}


void getProcssList( )

{
   // Get the list of process identifiers.
   DWORD aProcesses[1024], cbNeeded, cProcesses;
    unsigned int i;
   if ( !EnumProcesses( aProcesses, sizeof(aProcesses), &cbNeeded ) )
        return;

    // Calculate how many process identifiers were returned.
    cProcesses = cbNeeded / sizeof(DWORD);

    // Print the name and process identifier for each process.
    for ( i = 0; i < cProcesses; i++ )
        PrintProcessNameAndID( aProcesses[i] );

}



