#include <memory>
#include <stdio.h>
#include <stdlib.h>
#include "md5wrapper.h"

#include "cpu.h"

//using namespace System;

Cpu::Cpu() 
{ 
	unsigned long dwStandard = 0; 
	unsigned long dwFeature = 0; 
	// ziskat data z procesoru pomoci assembleru
	_asm { 
		push ebx
		push ecx
		push edx
		mov  eax, 1 
		cpuid 
		mov  dwStandard, eax 
		mov  dwFeature, edx 
		pop  ecx
		pop  ebx
		pop  edx
	};
	//sfornatovani dat do promene ID
	sprintf(id,"%08X%08X",dwFeature,dwStandard);
}

/* *********************************************************************************** 
 * Cpu::getInfo
 * vrati retezec popisujici procesor
 * ***********************************************************************************/

char * Cpu::getInfo()
{
	
	// creating a wrapper object
	md5wrapper md5;
	// create a hash from a string
	std::string cpuHash = md5.getHashFromString(id);
	// prevod na char *
	char * result = new char[cpuHash.length() + 1];	
	strcpy (result, cpuHash.c_str());

	return result;
}