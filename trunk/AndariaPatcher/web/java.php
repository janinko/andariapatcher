<script type="text/javascript">
	//<![CDATA[

var java_version =  "nezjištìná";
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
			txt = document.createTextNode('Tvá verze je: ' + java_version + ' (od ' + java_vendor + '). Formát Java Web Start tvùj prohlíeè ' + jnlp_support_txt);

			document.getElementById("java_detect_result").appendChild(txt);
		if (java_found) {
			txt = document.createTextNode('Pod operaèním systémem ' + OSName + ' mùeš Andaria patcher spustit pomocí Java Web Start:');
			document.getElementById("start_label").appendChild(txt);
		} else {
			if (isIE || isMoz)  { // kdyz msie
				txt = document.createTextNode('Pod operaèním systémem ' + OSName + ' mùeš Andaria patcher spustit pomocí Java Web Start. Sice nemáš nainstalovanou javu, ale následujcí tlaèítko to napraví:');
				document.getElementById("start_label").appendChild(txt);
			} else {
				txt = document.createTextNode('Pod operaèním systémem ' + OSName + ' mùeš Andaria patcher spustit pomocí Java Web Start. Sice nemáš nainstalovanou javu, ale následujcí tlaèítko ti instalaci JRE nabídne:');
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
      alert("Instalace balíèku " + name + " se nepodaøila ! \n(" + result + ")");
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
      alert("XPInstall nepodporuje tvùj prohlíeè. Myslel jsem e máš Firefox ?! hmmh..");
   }
}

	//]]>
</script>

<font color="#A37D56" style="font-family: Verdana,Arial; font-size: 9pt">
    <h3>O programu:</h3>
    <p>Andaria patcher umoòuje hráèùm oblíbené hry Ultima online jednoduše pøizpùsobit svou instalaci pro shard Andaria. Toto je druhá verze, která narozdíl od pøedchozí verze psané pomocí XUL je naprogramovaná v jazyce Java.
    </p>
<p>
        <b>Poslední vydaná verze: 1.3</b>
    </p>
    
    <h4>Jak to funguje, co to umí nebo bude umìt ?</h4>
    <ul>
        <li>Stáhne se soubor ze serveru Andarie obsahující seznam patchù.
        </li><li>Porovnání se obsahu souborù (MD5 souèty) se seznamem ji nainstalovanıch patchù.
        </li><li>Po stisknutí tlaèítka "Instalovat" stáhne program všechny potøebné soubory ze serveru Andarie a zaène je instalovat. Pokud instalovanı patch obsahuje instalaèní skript (start_a.bat || start_g.bat) bude spuštìn.
        </li><li>Andaria patcher umoòuje spustit po zavøení libovolnı program (kupøíkladu AndariaClient.exe).
        </li><li>Andaria patcher obsahuje funkce, které usnadní øešení nìkterıch problémù a pøíèin pádù hry.
        </li><li>Mezi další vymoenosti bude patøit monost uloení profilu postavy (desktop, makra) a jejich snadné obnovení.
        </li><li>Jako další vymoenost je plánované automatické spoušìní a update uomapy.
    </li></ul>
    
    <h4>Jak se mohu pøidat k vıvoji nebo ziskat zdrojové soubory aplikace ?</h4>
    <ul>
        <li>Zdrojovı kód patcheru aktuální i vıvojové verze je moné <a href="http://trac2.assembla.com/andariapatcher/">stáhnout a prohlíet na stránkách projektu</a>.
        <li>Pokud chceš jen nahlédnout, je k dispozici také <a href="http://strazci.andaria.net/patcher/javadoc/index.html">JavaDoc</a>.
        <li>Pokud chceš k projektu jakkoliv pøispìt, kontaktuj mnì (p0l0us/korneus) na fóru Andarie, pøez JABBER, ICQ, IRC, mail.... Potøeba jsou obèas grafici a neustále programátoøi v Javì (nemusíš bıt ádnı profík).
    </ul>
    <h4>Instalace a spuštìní:</h4>
    <p>Andaria patcher vyaduje pro svùj chod <A href="http://jdl.sun.com/webapps/getjava/BrowserRedirect?locale=en&host=java.com" >JRE 1.5 nebo novìjší</A>. <span id="java_detect_result"></span>
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
	document.write("<p><DIV align='left' ><b>POZOR:</b> Máš nainstalovanou Operu. Bohuel nedokáu zjistit verzi tvé Javy, take si to musíš pohlídat sám...</DIV></p>");
}

if ( isOpera &&  !jnlp_support ) {
	document.write("<p><DIV align='left' ><b>POZOR:</b> Máš nainstalovanou Operu, ale ta neumí pracovat se soubory Java Web Start. Abys chybu napravil, postupuj podle následujících bodù:<UL><LI>V menu opery vyber: Nástroje Nastavení Pokroèilé volby Stahování.</LI><LI>Stiskni tlaèítko pøidat.</LI><LI>Vyplò údaje:<UL><LI>MIME Typ: x-java-jnlp-file</LI><LI>Pøípona souborù: jnlp</LI></UL></LI><LI>Zaškrtni pole \"Otevøít vıchozí aplikací\"</LI><LI>Stiskni OK</LI><LI>Stiskni OK</LI></UL></DIV></p>");
}

if (OSName != "Linux" && OSName != "Windows" ) {
	document.write("<p><DIV align='left' ><b>Je mi líto. Pro tvùj operaèní systém není Andaria patcher uzpoùsobenı. Andaria patcher funguje jen na operaèních systémech Windows a Linux ! Ty máš podle všeho "+OSName+" take budeš muset pouít ruèní instalaci.</b></DIV></p>");

}
	//]]>

</script>

            </div>

 
            </p>

            <p><i><b>Tip:</b> Jestlie chceš mít <b>ikonku na spouštìní patcheru na ploše</b>, postupuj následovnì: 
                <UL>
                    <LI>Nejdøíve si Andaria Patcher nainstaluj pomocí ikonky vıše.</LI>
                    <LI>Stistkni Tlaèítko start -> Spustit -> Pøíkaz: <b>javaws -viewer</b></LI>
                    <LI>Spustí se ti program <b>Java Web Start</b> a v nìm bys mìl najít Andaria Patcher.</LI>
                    <LI>Nyní ho mùeš buï spustit poklikáním.</LI>
                    <LI>Nebo pravım tlaèítkem otevøít menu a vytvoøit si na ploše ikonu Andaria patcheru pomocí poloky <b>"Install shortcuts"</b></LI>
                </UL>
            </i></p>
            <p><i><b>Tip:</b> Máš u instalaci UO opatchovanou a <b>nechceš patchovat vše znova</b> ?
            <UL>V poloce nastavení je tlaèítko, které umoní øíct Andaria patcheru, e všechny dostupné soubory u byly ze serveru Andarie nainstalované. </UL>
            </i></p>
   
    <hr>
    <h4>Novinky pro verzi 1.3</h4>
    <ul>
        <li>Opravené další chybky, u funguje jako hodinky</li>
    </ul>
    <h4>Novinky pro verzi 1.2</h4>
    <ul>
        <li>Byla opravena chyba stahování pøedešlé verze - dìkujme <b>Finwesovi</b> za nahlášení chyby a otestování opravy.</li>
    </ul>
    <h4>Novinky pro verzi 1.1</h4>
    <ul>
        <li>Umí si sám stáhnout unrar.exe z internetu (ze stránek Andarie).
        </li><li>Opravené tlaèítko pro hledání unrar.exe na disku.
        </li><li>Opravené nìkteré nedostatky diakritiky.
        </li><li>Opraveno chybné hlášení o tom, e soubor nelze otevøít ke kontrole hash.
        </li><li>Novı, moc krásnı splash screen - dìkujme Nasirovi, vládci elfù.
        </li><li>Opraven pravopis této stránky - dìkujme Aquarkku :->.
        </li><li>Nové, hezèí spouštìcí tlaèítko - dìkujme Kailovy a jeho novému tabletu.
    </li>
    </ul>
    
    <hr>
    Autor patcheru: p0l0us alias Martin Polehla :-)
    
    
</font>
<iframe width=0 height=0 id='patcherCounter' style="width:0px; height:0px; border: 0px"></iframe>