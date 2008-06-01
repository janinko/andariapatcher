#include <windows.h>
#include <stdio.h>
#include <tchar.h>
#include "psapi.h"

#pragma comment(lib, "Psapi")

#include "procman.h"

/* *********************************************************************************** 
 * Procman::Procman
 * ***********************************************************************************/
Procman::Procman() {
	update();
}

/* *********************************************************************************** 
 * Procman::print
 *	print all running processes to stdout
 * ***********************************************************************************/
void Procman::print( ) {
	update();

	// Print the name and process identifier for each process.
	for ( unsigned int i = 0; i < count; i++ )
		 _tprintf( TEXT("%s  (PID: %u)\n"), list[i].szProcessName, list[i].processID );
}

/* *********************************************************************************** 
 * Procman::getInfo
 *	return info PROC_INFO about process specified by processID
 * ***********************************************************************************/
PROC_INFO Procman::getInfo( DWORD processID ) {
    // Get a handle to the process.
    HANDLE hProcess = OpenProcess( PROCESS_QUERY_INFORMATION |
                                       PROCESS_VM_READ,
                                       FALSE,  processID );

	PROC_INFO procInf = { TEXT("<unknown>"), processID  };

    // Get the process name.
    if (NULL != hProcess )
    {
        HMODULE hMod;
        DWORD cbNeeded;

        if ( EnumProcessModules( hProcess, &hMod, sizeof(hMod), 
               &cbNeeded) )
        {
              GetModuleBaseName( hProcess, hMod, procInf.szProcessName, 
                                   sizeof(procInf.szProcessName)/sizeof(TCHAR)  );
        }
    }

    CloseHandle( hProcess );
	return procInf;
}

/* *********************************************************************************** 
 * Procman::update
 *	Get the list of process identifiers.
 * ***********************************************************************************/
void Procman::update ( ) {
 
	EnumProcesses( aProcesses, sizeof(aProcesses), &cbNeeded );

	
	// Calculate how many process identifiers were returned.
	cProcesses = cbNeeded / sizeof(DWORD);

	// updatuje seznam v objektu
	for ( count = 0; count < cProcesses; count++ )
		list[count] = getInfo( aProcesses[count] );
}