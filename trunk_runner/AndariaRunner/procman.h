#ifndef PROCMAN_H_
#define PROCMAN_H_

#define MAX_PROC_COUNT 1024

typedef struct
{
	TCHAR szProcessName[MAX_PATH];// = TEXT("<unknown>");
	DWORD processID;
} PROC_INFO;

class Procman {
private:
	 DWORD aProcesses[1024], cbNeeded, cProcesses;
public:
	//TODO: predelat promenou list na dynamicke pole a zbavit se promene count.
	// seznam procesu
	PROC_INFO list[MAX_PROC_COUNT];
	// pocet procesu 
	
	unsigned int count;

	// print all processes
	void print( );
	// print one process
	PROC_INFO getInfo( DWORD processID );
	void update ( );
	Procman();
};

#endif