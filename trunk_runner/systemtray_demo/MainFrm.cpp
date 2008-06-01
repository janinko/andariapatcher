// MainFrm.cpp : implementation of the CMainFrame class
//

#include "stdafx.h"
#include "SystemTrayDemo.h"

#include "MainFrm.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

#define	WM_ICON_NOTIFY			WM_USER+10


/////////////////////////////////////////////////////////////////////////////
// CMainFrame

IMPLEMENT_DYNCREATE(CMainFrame, CFrameWnd)

BEGIN_MESSAGE_MAP(CMainFrame, CFrameWnd)
	//{{AFX_MSG_MAP(CMainFrame)
	ON_WM_CREATE()
	ON_COMMAND(ID_POPUP_OPTION1, OnPopupOption1)
	ON_UPDATE_COMMAND_UI(ID_POPUP_OPTION1, OnUpdatePopupOption1)
	ON_COMMAND(ID_POPUP_OPTION2, OnPopupOption2)
    ON_COMMAND(ID_POPUP_ANIMATE, OnAnimate)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CMainFrame construction/destruction

CMainFrame::CMainFrame()
{
	m_Option1Enabled = TRUE;
}

CMainFrame::~CMainFrame()
{
}

/////////////////////////////////////////////////////////////////////////////
// CMainFrame diagnostics

#ifdef _DEBUG
void CMainFrame::AssertValid() const
{
	CFrameWnd::AssertValid();
}

void CMainFrame::Dump(CDumpContext& dc) const
{
	CFrameWnd::Dump(dc);
}

#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CMainFrame message handlers

int CMainFrame::OnCreate(LPCREATESTRUCT lpCreateStruct) 
{
	if (CFrameWnd::OnCreate(lpCreateStruct) == -1)
		return -1;
	
	// Create the tray icon
	if (!m_TrayIcon.Create(NULL,                            // Parent window
                           WM_ICON_NOTIFY,                  // Icon notify message to use
                           _T("This is a Tray Icon - Right click on me!"),  // tooltip
                           ::LoadIcon(NULL, IDI_ASTERISK),  // Icon to use
                           IDR_POPUP_MENU))                 // ID of tray icon
		return -1;

    m_TrayIcon.SetMenuDefaultItem(4, TRUE);
	
	return 0;
}

/////////////////////////////////////////////////////////////////////////////
// CMainFrame/CSystemTray menu message handlers

void CMainFrame::OnPopupOption1() 
{
	MessageBox(_T("You chose Option 1"));	
}

void CMainFrame::OnUpdatePopupOption1(CCmdUI* pCmdUI) 
{
	pCmdUI->Enable(m_Option1Enabled);
}

void CMainFrame::OnPopupOption2() 
{
	m_Option1Enabled = !m_Option1Enabled;

	CString str;
	str.Format(_T("You chose option 2. Option 1 is now %s"), 
		m_Option1Enabled? _T("Enabled") : _T("Disabled"));

	MessageBox(str);
}

void CMainFrame::OnAnimate()
{
    m_TrayIcon.SetIconList(IDI_ICON1, IDI_ICON4);
    m_TrayIcon.Animate(50,2);
}
