#include <windows.h>
#include <iostream>

#include "client.h"

// doba po kterou bude cekat, nez znova zkusi jestli bezi klient
// stejne tak na tom bude zaviset, zkouska, jestli muze hrat a posle
// nove info serveru o bezicich procesech (v dalsi verzi).
#define CLIENT_WAIT_TIMER 2000

Client::Client () {

}

static char* getExePath() {
	 char*  path; // client path
		HKEY hKey;				// Declare a key to store the result
	DWORD buffersize = 1024;	// Declare the size of the data buffer
	path = new char[buffersize];// Declare the buffer

	RegOpenKeyEx (HKEY_LOCAL_MACHINE, 
	"SOFTWARE\\Origin Worlds Online\\Ultima Online\\1.0",NULL,KEY_READ,&hKey);
	
	// Query the registry value
	RegQueryValueEx(hKey,"ExePath",NULL,NULL,(LPBYTE) path,&buffersize);
	//RegQueryValueEx(hKey,"InstCDPath",NULL,NULL,(LPBYTE) path,&buffersize);
	
	//std::cout << "uo exe path " << path << "\n\n";

	// Close the Registry Key
	RegCloseKey (hKey);
	return path;
}

static char* getPath() {
	 char*  path; // client path
		HKEY hKey;				// Declare a key to store the result
	DWORD buffersize = 1024;	// Declare the size of the data buffer
	path = new char[buffersize];// Declare the buffer

	RegOpenKeyEx (HKEY_LOCAL_MACHINE, 
	"SOFTWARE\\Origin Worlds Online\\Ultima Online\\1.0",NULL,KEY_READ,&hKey);
	
	// Query the registry value
	RegQueryValueEx(hKey,"InstCDPath",NULL,NULL,(LPBYTE) path,&buffersize);
	
	//std::cout << "uo exe path " << path << "\n\n";

	// Close the Registry Key
	RegCloseKey (hKey);
	return path;
}
void Client::start()
{
	hThread =  CreateThread(0,
					 0,
					 run,
					 0,
					 0,
					 &dwThreadID);

}

Client::~Client () { CloseHandle (hThread); }

void Client::resume () { ResumeThread (hThread); }

DWORD Client::waitForDeath ()
{
    return WaitForSingleObject (hThread, CLIENT_WAIT_TIMER);
}

DWORD WINAPI Client::run( LPVOID lpParam ) 

{
	HKEY hKey;				// Declare a key to store the result
	DWORD buffersize = 1024;	// Declare the size of the data buffer

	char*  path = new char[buffersize]; // client path
	char*  exePath = new char[buffersize]; // uo  path

	RegOpenKeyEx (HKEY_LOCAL_MACHINE, 
	"SOFTWARE\\Origin Worlds Online\\Ultima Online\\1.0",NULL,KEY_READ,&hKey);
	
	// Query the registry value
	RegQueryValueEx(hKey,"ExePath",NULL,NULL,(LPBYTE) exePath,&buffersize);
	RegQueryValueEx(hKey,"InstCDPath",NULL,NULL,(LPBYTE) path,&buffersize);
	
	//std::cout << "uo exe path " << path << "\n\n";

	// Close the Registry Key
	RegCloseKey (hKey);

    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
	printf("Startuji uo\n");

    // Start the child process. 
    if( !CreateProcess( NULL,   // No module name (use command line)
		exePath,        // Command line
        NULL,           // Process handle not inheritable
        NULL,           // Thread handle not inheritable
        FALSE,          // Set handle inheritance to FALSE
        0,              // No creation flags
        NULL,           // Use parent's environment block
		path,           // Use parent's starting directory 
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
