#ifndef HDD_H_
#define HDD_H_
	#define  TITLE   "DiskId32"
	#define  IDENTIFY_BUFFER_SIZE  512

	   //  IOCTL commands
	#define  DFP_GET_VERSION          0x00074080
	#define  DFP_SEND_DRIVE_COMMAND   0x0007c084
	#define  DFP_RECEIVE_DRIVE_DATA   0x0007c088

	#define  FILE_DEVICE_SCSI              0x0000001b
	#define  IOCTL_SCSI_MINIPORT_IDENTIFY  ((FILE_DEVICE_SCSI << 16) + 0x0501)
	#define  IOCTL_SCSI_MINIPORT 0x0004D008  //  see NTDDSCSI.H for definition

	#define SMART_GET_VERSION               CTL_CODE(IOCTL_DISK_BASE, 0x0020, METHOD_BUFFERED, FILE_READ_ACCESS)
	#define SMART_SEND_DRIVE_COMMAND        CTL_CODE(IOCTL_DISK_BASE, 0x0021, METHOD_BUFFERED, FILE_READ_ACCESS | FILE_WRITE_ACCESS)
	#define SMART_RCV_DRIVE_DATA            CTL_CODE(IOCTL_DISK_BASE, 0x0022, METHOD_BUFFERED, FILE_READ_ACCESS | FILE_WRITE_ACCESS)

	   //  Bits returned in the fCapabilities member of GETVERSIONOUTPARAMS 
	#define  CAP_IDE_ID_FUNCTION             1  // ATA ID command supported
	#define  CAP_IDE_ATAPI_ID                2  // ATAPI ID command supported
	#define  CAP_IDE_EXECUTE_SMART_FUNCTION  4  // SMART commannds supported

	   //  Valid values for the bCommandReg member of IDEREGS.
	#define  IDE_ATAPI_IDENTIFY  0xA1  //  Returns ID sector for ATAPI.
	#define  IDE_ATA_IDENTIFY    0xEC  //  Returns ID sector for ATA.

	   //  Max number of drives assuming primary/secondary, master/slave topology
	#define  MAX_IDE_DRIVES  16

	typedef struct
	{
		char serialNumber [1024];
		char modelNumber [1024];
		char revisionNumber [1024];
		int driveType;
		long idNumber;
	} HDD_ID;

	   // The following struct defines the interesting part of the IDENTIFY
	   // buffer:
	typedef struct _IDSECTOR
	{
	   USHORT  wGenConfig;
	   USHORT  wNumCyls;
	   USHORT  wReserved;
	   USHORT  wNumHeads;
	   USHORT  wBytesPerTrack;
	   USHORT  wBytesPerSector;
	   USHORT  wSectorsPerTrack;
	   USHORT  wVendorUnique[3];
	   CHAR    sSerialNumber[20];
	   USHORT  wBufferType;
	   USHORT  wBufferSize;
	   USHORT  wECCSize;
	   CHAR    sFirmwareRev[8];
	   CHAR    sModelNumber[40];
	   USHORT  wMoreVendorUnique;
	   USHORT  wDoubleWordIO;
	   USHORT  wCapabilities;
	   USHORT  wReserved1;
	   USHORT  wPIOTiming;
	   USHORT  wDMATiming;
	   USHORT  wBS;
	   USHORT  wNumCurrentCyls;
	   USHORT  wNumCurrentHeads;
	   USHORT  wNumCurrentSectorsPerTrack;
	   ULONG   ulCurrentSectorCapacity;
	   USHORT  wMultSectorStuff;
	   ULONG   ulTotalAddressableSectors;
	   USHORT  wSingleWordDMA;
	   USHORT  wMultiWordDMA;
	   BYTE    bReserved[128];
	} IDSECTOR, *PIDSECTOR;


	typedef struct _SRB_IO_CONTROL
	{
	   ULONG HeaderLength;
	   UCHAR Signature[8];
	   ULONG Timeout;
	   ULONG ControlCode;
	   ULONG ReturnCode;
	   ULONG Length;
	} SRB_IO_CONTROL, *PSRB_IO_CONTROL;

	   //  GETVERSIONOUTPARAMS contains the data returned from the 
	   //  Get Driver Version function.
	typedef struct _GETVERSIONOUTPARAMS
	{
	   BYTE bVersion;      // Binary driver version.
	   BYTE bRevision;     // Binary driver revision.
	   BYTE bReserved;     // Not used.
	   BYTE bIDEDeviceMap; // Bit map of IDE devices.
	   DWORD fCapabilities; // Bit mask of driver capabilities.
	   DWORD dwReserved[4]; // For future use.
	} GETVERSIONOUTPARAMS, *PGETVERSIONOUTPARAMS, *LPGETVERSIONOUTPARAMS;

	class Hdd {
		// todo: udelat destruktor tridy a zkontrolovat jestli mi nekde nezustava nejak pamet
	private:
		// Define global buffers.
		BYTE IdOutCmd [sizeof (SENDCMDOUTPARAMS) + IDENTIFY_BUFFER_SIZE - 1];
		BOOL DoIDENTIFY (HANDLE, PSENDCMDINPARAMS, PSENDCMDOUTPARAMS, BYTE, BYTE, PDWORD);

		char *ConvertToString (DWORD diskdata [256], int firstIndex, int lastIndex, char* buf);
		void PrintIdeInfo (int drive, DWORD diskdata [256]);
		int ReadPhysicalDriveInNTWithAdminRights(void);
		int ReadPhysicalDriveInNTUsingSmart (void);
		int ReadPhysicalDriveInNTWithZeroRights (void);
		int ReadIdeDriveAsScsiDriveInNT (void);
		char *flipAndCodeBytes (const char * str, int pos, int flip, char * buf);
		int ReadDrivePortsInWin9X (void);
		long getDriveId (HDD_ID * hddId);
		void dump_buffer (const char* title, const unsigned char* buffer, int len);

	public:
		// info o diskach
		HDD_ID	drives[MAX_IDE_DRIVES];
		// pocet disku
		int driveCount;

		char * getInfo();
		Hdd();
	};


#endif