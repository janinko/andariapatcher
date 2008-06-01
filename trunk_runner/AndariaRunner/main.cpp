#include <string>
#include <vector>
#include <iostream>
using namespace std;

#include <windows.h>
#include <wininet.h>

#include "web.h"
#include "client.h"
#include "mac.h"
#include "hdd.h"
#include "cpu.h"
#include "procman.h"



using namespace openutils;



int main() {
	/*Procman proc;
	proc.print();
*/
	Mac mac;
	//mac.print();

	Cpu cpu;

	Hdd disk;


	cout << "\nCO VSECHNO VIM: \n";
	cout << "CPU ID: " << cpu.id << "\n";

	for (int i=0; i<disk.driveCount;i++) {
		cout << "Hdd ( " << i << ") typ   :" << disk.drives[i].driveType << "\n";
		cout << "Hdd ( " << i << ") model :" << disk.drives[i].modelNumber << "\n";
		cout << "Hdd ( " << i << ") serial:" << disk.drives[i].serialNumber << "\n";
	}
	mac.print();

	cout << "\nDATA POSILANA SERVERU: \n";
	cout << "CPU: " << cpu.getInfo() << "\n";
	cout << "Hdd: " << disk.getInfo() << "\n";
	cout << "Sit: " << mac.getInfo() << "\n";

	cout << "\nPosilam data serveru...\n";
	try {	
		WebForm wf;
		// the web server name is set
		wf.setHost("http://strazci.andaria.net");
		// the script to be executed on the web server...
		wf.setScriptFile("/runner/runner.php");
		// form variables are added to the request object
		wf.putVariable("povoleni", "povoleno");
		wf.putVariable("hddid", disk.getInfo());
		//wf.putVariable("cpuid", cpuHash.c_str() );
		wf.putVariable("cpuid", cpu.getInfo() );
		wf.putVariable("mac", mac.getInfo());
		// data is encoded and send to the server script
		// for processing
		wf.sendRequest();
		// reading back any response
		char response[101];			
		if(wf.getResponse(response,100)) {
			cout << "Odpoved od serveru:\n\n"<< response << "\n\nKonec vypisu.\n";
		}else {
			cout << "Nemùžu se spojit se serverem andarie!" << endl;
		}	
	}catch(WebFormException ex) {
		cout << ex.getMessage() << endl;
	}

	Client cli;
	cli.start();

	while (WAIT_TIMEOUT==cli.waitForDeath())
	{
		cout<<"Cekam az skonci klient...\n";
	}

	return 0;
}
