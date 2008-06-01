/*
public ref class Client
{
public:

   // The ThreadProc method is called when the thread starts.
   // It loops ten times, writing to the console and yielding 
   // the rest of its time slice each time, and then ends.
   static void ThreadProc();

};

*/

#ifndef CLIENT_H_
#define CLIENT_H_



class Client {

public:
    Client ();
	void start();
    void resume ();
    DWORD waitForDeath ();
    ~Client () ;

private:
    HANDLE hThread; // client thread handle
    DWORD  dwThreadID;     // client thread id
	static DWORD WINAPI run( LPVOID lpParam );
};

#endif