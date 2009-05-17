<script type="text/javascript">
	//<![CDATA[

var java_version =  "nezji�t�n�";
var java_vendor = "-";
var java_method = "-";
var java_found = false;
var jnlp_support = false;
var jnlp_support_txt = "nepodporuje !";
var txt;

var appLink = "http://strazci.andaria.net/patcher/launch.jnlp";
var appBetaLink = "http://strazci.andaria.net/patcher/beta/launch.jnlp";
var cabURL = 'http://javadl-esd.sun.com/update/1.6.0/jinstall-6-windows-i586.cab';
//	var cabURL = 'http://java.sun.com/update/1.5.0/jinstall-1_5_0_05-windows-i586.cab';
var downloadURL = "http://jdl.sun.com/webapps/getjava/BrowserRedirect?locale=en&host=java.com";

var isIE = /*@cc_on!@*/false;
var isMoz = ( navigator.userAgent.toLowerCase().indexOf("firefox") != -1 );
var isOpera = ( navigator.userAgent.toLowerCase().indexOf("opera") != -1 );

var OSName="Unknown OS";
if (navigator.appVersion.indexOf("Win")!=-1) OSName="Windows";
if (navigator.appVersion.indexOf("Mac")!=-1) OSName="MacOS";
if (navigator.appVersion.indexOf("X11")!=-1) OSName="Linux";
if (navigator.appVersion.indexOf("Linux")!=-1) OSName="Linux";

function launch(link) {
	if (isIE && (OSName == "Windows") ) {
		document.write("<OBJECT CODEBASE='"+ cabURL +"' CLASSID='clsid:5852F5ED-8BF4-11D4-A245-0080C6F74284' HEIGHT=0 WIDTH=0>");
		document.write("<PARAM NAME=app VALUE="+ link +">");
		document.write("<PARAM NAME=back VALUE=false>");
		document.write("</OBJECT>");
	} else {
		if ( java_found ) {
			window.location = link;
		} else {
			if (isMoz && (OSName == "Windows") ) {
				installJRE();
			} else {
				window.location = link;
			}
		} 
	}
}

function patcherCounter() {
  if (document.getElementById() ) {
    document.getElementById('patcherCounter').src = "http://strazci.andaria.net/patcher/counter.php";
  }
}
function update_jre_version ()
{
// detect java and jnlp


	try {
		java_method = "LiveConnect";
		java_version = java.lang.System.getProperty("java.version");
		java_vendor = java.lang.System.getProperty("java.vendor");
		java_found = true;
	} catch (e1) {
	  // prvni metoda selhala Apllet nefunguje v opere
		if ( !isOpera ){
			try {
				java_method = "Applet";
				document.getElementById("applet_holder").innerHTML = '<applet id="javaversion_applet" codebase="http://strazci.andaria.net/patcher" code="JavaVersion.class" mayscript="mayscript" width="0" height="0"></applet>';
				java_version = document.javaversion_applet.getJavaVersion();
				java_vendor = document.javaversion_applet.getJavaVendor();
				java_found = true;
				jnlp_support = true;
			} catch (e2) {
				java_found = false;
			}
		}
	}

	if (navigator.mimeTypes && navigator.mimeTypes.length && navigator.mimeTypes['application/x-java-jnlp-file']) {
		jnlp_support = true;

	}
	if ( jnlp_support ) {
		jnlp_support_txt = "podporuje.";
	}

// Display results
	if (OSName == "Linux" || OSName == "Windows" ) {
			txt = document.createTextNode('Tv� verze je: ' + java_version + ' (od ' + java_vendor + '). Form�t Java Web Start tv�j prohl�e� ' + jnlp_support_txt);

			document.getElementById("java_detect_result").appendChild(txt);
		if (java_found) {
			txt = document.createTextNode('Pod opera�n�m syst�mem ' + OSName + ' m��e� Andaria patcher spustit pomoc� Java Web Start:');
			document.getElementById("start_label").appendChild(txt);
		} else {
			if (isIE || isMoz)  { // kdyz msie
				txt = document.createTextNode('Pod opera�n�m syst�mem ' + OSName + ' m��e� Andaria patcher spustit pomoc� Java Web Start. Sice nem� nainstalovanou javu, ale n�sledujc� tla��tko to naprav�:');
				document.getElementById("start_label").appendChild(txt);
			} else {
				txt = document.createTextNode('Pod opera�n�m syst�mem ' + OSName + ' m��e� Andaria patcher spustit pomoc� Java Web Start. Sice nem� nainstalovanou javu, ale n�sledujc� tla��tko ti instalaci JRE nab�dne:');
				document.getElementById("start_label").appendChild(txt);
			}
		}
	}
}


// Process the result of the XPInstall.

function checkInstall(name, result)
{
   // Installation failed.

   if (result)
   {
      alert("Instalace bal��ku " + name + " se nepoda�ila ! \n(" + result + ")");
   }

   // Installation successful, so try to reload current page.

   else
   {
      window.location.reload();
   }
}

// Install the JRE with XPInstall verification.

function installJRE()
{
   // The browser supports XPInstall.

   if (InstallTrigger.enabled())
   {
      txt = "J2SE(TM) Runtime Environment 6 Update 3";
      var xpi = new Object();
      xpi[txt] = "http://java.sun.com/update/1.6.0/jre-6u3-windows-i586-jc.xpi";

      // Try to install the package and process the results.

      InstallTrigger.install(xpi, checkInstall);
   }

   // The browser does not support XPInstall.

   else
   {
      alert("XPInstall nepodporuje tv�j prohl�e�. Myslel jsem �e m� Firefox ?! hmmh..");
   }
}

	//]]>
</script>

<font color="#A37D56" style="font-family: Verdana,Arial; font-size: 9pt">
    <h3>O programu:</h3>
    <p>Andaria patcher umo��uje hr���m obl�ben� hry Ultima online jednodu�e p�izp�sobit svou instalaci pro shard Andaria. Toto je druh� verze, kter� narozd�l od p�edchoz� verze psan� pomoc� XUL je naprogramovan� v jazyce Java.
    </p>
<p>
        <b>Posledn� vydan� verze: 1.3</b>
    </p>
    
    <h4>Jak to funguje, co to um� nebo bude um�t ?</h4>
    <ul>
        <li>St�hne se soubor ze serveru Andarie obsahuj�c� seznam patch�.
        </li><li>Porovn�n� se obsahu soubor� (MD5 sou�ty) se seznamem ji� nainstalovan�ch patch�.
        </li><li>Po stisknut� tla��tka "Instalovat" st�hne program v�echny pot�ebn� soubory ze serveru Andarie a za�ne je instalovat. Pokud instalovan� patch obsahuje instala�n� skript (start_a.bat || start_g.bat) bude spu�t�n.
        </li><li>Andaria patcher umo��uje spustit po zav�en� libovoln� program (kup��kladu AndariaClient.exe).
        </li><li>Andaria patcher obsahuje funkce, kter� usnadn� �e�en� n�kter�ch probl�m� a p���in p�d� hry.
        </li><li>Mezi dal�� vymo�enosti bude pat�it mo�nost ulo�en� profilu postavy (desktop, makra) a jejich snadn� obnoven�.
        </li><li>Jako dal�� vymo�enost je pl�novan� automatick� spou���n� a update uomapy.
    </li></ul>
    
    <h4>Jak se mohu p�idat k v�voji nebo ziskat zdrojov� soubory aplikace ?</h4>
    <ul>
        <li>Zdrojov� k�d patcheru aktu�ln� i v�vojov� verze je mo�n� <a href="http://trac2.assembla.com/andariapatcher/">st�hnout a prohl�et na str�nk�ch projektu</a>.
        <li>Pokud chce� jen nahl�dnout, je k dispozici tak� <a href="http://strazci.andaria.net/patcher/javadoc/index.html">JavaDoc</a>.
        <li>Pokud chce� k projektu jakkoliv p�isp�t, kontaktuj mn� (p0l0us/korneus) na f�ru Andarie, p�ez JABBER, ICQ, IRC, mail.... Pot�eba jsou ob�as grafici a neust�le program�to�i v Jav� (nemus� b�t ��dn� prof�k).
    </ul>
    <h4>Instalace a spu�t�n�:</h4>
    <p>Andaria patcher vy�aduje pro sv�j chod <A href="http://jdl.sun.com/webapps/getjava/BrowserRedirect?locale=en&host=java.com" >JRE 1.5 nebo nov�j��</A>. <span id="java_detect_result"></span>
<p>            
<span id="start_label"></span>
<br><br><div align="center">
<INPUT onclick="launch(appLink);" type="image" border="0" src="http://strazci.andaria.net/patcher/spustit.png" alt="Launch"></div><br>

<div align="center"><font size="-2">[<A ONCLICK="launch(appBetaLink);">betaverze 1.4<?PHP
// nacte pocet spusteni patcheru v poslednim mesici
        $betaCountFile = "../strazci/patcher/beta/hits/".date("m-y").".txt";
        $betaHits = file($betaCountFile);
        echo " (".$betaHits[0].")";
    //}
?></A>]</FONT></DIV>
<div id="applet_holder"></div>

<script type="text/javascript">
	//<![CDATA[

update_jre_version();


if ( isOpera ) {
	document.write("<p><DIV align='left' ><b>POZOR:</b> M� nainstalovanou Operu. Bohu�el nedok�u zjistit verzi tv� Javy, tak�e si to mus� pohl�dat s�m...</DIV></p>");
}

if ( isOpera &&  !jnlp_support ) {
	document.write("<p><DIV align='left' ><b>POZOR:</b> M� nainstalovanou Operu, ale ta neum� pracovat se soubory Java Web Start. Abys chybu napravil, postupuj podle n�sleduj�c�ch bod�:<UL><LI>V menu opery vyber: N�stroje Nastaven� Pokro�il� volby Stahov�n�.</LI><LI>Stiskni tla��tko p�idat.</LI><LI>Vypl� �daje:<UL><LI>MIME Typ: x-java-jnlp-file</LI><LI>P��pona soubor�: jnlp</LI></UL></LI><LI>Za�krtni pole \"Otev��t v�choz� aplikac�\"</LI><LI>Stiskni OK</LI><LI>Stiskni OK</LI></UL></DIV></p>");
}

if (OSName != "Linux" && OSName != "Windows" ) {
	document.write("<p><DIV align='left' ><b>Je mi l�to. Pro tv�j opera�n� syst�m nen� Andaria patcher uzpo�soben�. Andaria patcher funguje jen na opera�n�ch syst�mech Windows a Linux ! Ty m� podle v�eho "+OSName+" tak�e bude� muset pou��t ru�n� instalaci.</b></DIV></p>");

}
	//]]>

</script>

            </div>

 
            </p>

            <p><i><b>Tip:</b> Jestli�e chce� m�t <b>ikonku na spou�t�n� patcheru na plo�e</b>, postupuj n�sledovn�: 
                <UL>
                    <LI>Nejd��ve si Andaria Patcher nainstaluj pomoc� ikonky v��e.</LI>
                    <LI>Stistkni Tla��tko start -> Spustit -> P��kaz: <b>javaws -viewer</b></LI>
                    <LI>Spust� se ti program <b>Java Web Start</b> a v n�m bys m�l naj�t Andaria Patcher.</LI>
                    <LI>Nyn� ho m��e� bu� spustit poklik�n�m.</LI>
                    <LI>Nebo prav�m tla��tkem otev��t menu a vytvo�it si na plo�e ikonu Andaria patcheru pomoc� polo�ky <b>"Install shortcuts"</b></LI>
                </UL>
            </i></p>
            <p><i><b>Tip:</b> M� u� instalaci UO opatchovanou a <b>nechce� patchovat v�e znova</b> ?
            <UL>V polo�ce nastaven� je tla��tko, kter� umo�n� ��ct Andaria patcheru, �e v�echny dostupn� soubory u� byly ze serveru Andarie nainstalovan�. </UL>
            </i></p>
   
    <hr>
    <h4>Novinky pro verzi 1.3</h4>
    <ul>
        <li>Opraven� dal�� chybky, u� funguje jako hodinky</li>
    </ul>
    <h4>Novinky pro verzi 1.2</h4>
    <ul>
        <li>Byla opravena chyba stahov�n� p�ede�l� verze - d�kujme <b>Finwesovi</b> za nahl�en� chyby a otestov�n� opravy.</li>
    </ul>
    <h4>Novinky pro verzi 1.1</h4>
    <ul>
        <li>Um� si s�m st�hnout unrar.exe z internetu (ze str�nek Andarie).
        </li><li>Opraven� tla��tko pro hled�n� unrar.exe na disku.
        </li><li>Opraven� n�kter� nedostatky diakritiky.
        </li><li>Opraveno chybn� hl�en� o tom, �e soubor nelze otev��t ke kontrole hash.
        </li><li>Nov�, moc kr�sn� splash screen - d�kujme Nasirovi, vl�dci elf�.
        </li><li>Opraven pravopis t�to str�nky - d�kujme Aquarkku :->.
        </li><li>Nov�, hez�� spou�t�c� tla��tko - d�kujme Kailovy a jeho nov�mu tabletu.
    </li>
    </ul>
    
    <hr>
    Autor patcheru: p0l0us alias Martin Polehla :-)
    
    
</font>
<iframe width=0 height=0 id='patcherCounter' style="width:0px; height:0px; border: 0px"></iframe>