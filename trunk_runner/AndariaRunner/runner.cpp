#include "stdafx.h"
#include "runner.h"

DWORD WINAPI runClient( LPVOID lpParam ) 

{
    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
	printf("Startuji uo\n");

    // Start the child process. 
    if( !CreateProcess( NULL,   // No module name (use command line)
        "c:\\uo\\AndariaClient.exe",        // Command line
        NULL,           // Process handle not inheritable
        NULL,           // Thread handle not inheritable
        FALSE,          // Set handle inheritance to FALSE
        0,              // No creation flags
        NULL,           // Use parent's environment block
        "c:\\uo",           // Use parent's starting directory 
        &si,            // Pointer to STARTUPINFO structure
        &pi )           // Pointer to PROCESS_INFORMATION structure
    ) 
    {
        printf( "CreateProcess failed (%d)\n", GetLastError() );
        return 0;
    }

    // Wait until child process exits.
    WaitForSingleObject( pi.hProcess, INFINITE );

    // Close process and thread handles. 
    CloseHandle( pi.hProcess );
    CloseHandle( pi.hThread );
	 return 1;
}

HANDLE createAppThread(DWORD dwThreadID) {
	return CreateThread(NULL,
                             0,
                             runClient,
                             NULL,
                             0,
                             &dwThreadID);
}