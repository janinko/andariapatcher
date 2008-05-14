#include "stdafx.h"

//using namespace System;

char *getCpuId (void)
{
  static char Ret[100];
  unsigned long dwStandard = 0; 
  unsigned long dwFeature = 0; 
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
  sprintf_s(Ret,"%08X%08X",dwFeature,dwStandard);
  return Ret;
};

