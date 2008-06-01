#ifndef MAC_H_
#define MAC_H_

#define MAX_IF_COUNT 4
typedef char* MAC ;

class Mac {
private:
public:
	MAC addrList[MAX_IF_COUNT];
	DWORD addrsId[MAX_IF_COUNT];
	short int addrCnt;

	Mac();
	char * getInfo();
	void print();
	~Mac();
};

#endif