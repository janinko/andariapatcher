<?PHP
$VERSION_FILE = "/home/wwwroot/andaria.net/patcher/version.php";
$countFile = "/home/wwwroot/andaria.net/patcher/hits/".date("m-y").".txt";
$countBetaFile = "/home/wwwroot/andaria.net/patcher/beta/hits/".date("m-y").".txt";
?>
<script src="http://java.com/js/deployJava.js"></script>
<script type="text/javascript" >
    //<![CDATA[
	var appLink = "http://patcher.andaria.net/launch.jnlp";
    var appBetaLink = "http://patcher.andaria.net/beta/launch.jnlp";
    deployJava.launchButtonPNG = 'http://patcher.andaria.net/spustit.png';

    // os detection
    var isIE = /*@cc_on!@*/false;
    var isMoz = ( navigator.userAgent.toLowerCase().indexOf("firefox") != -1 );
    var isOpera = ( navigator.userAgent.toLowerCase().indexOf("opera") != -1 );

    var OSName="Unknown OS";
    if (navigator.appVersion.indexOf("Win")!=-1) OSName="Windows";
    if (navigator.appVersion.indexOf("Mac")!=-1) OSName="MacOS";
    if (navigator.appVersion.indexOf("X11")!=-1) OSName="Linux";
    if (navigator.appVersion.indexOf("Linux")!=-1) OSName="Linux";

	  // java detection
    var java_required_version = '1.6.0+';
    var java_version =  "nezji�t�n�";
    var url = appBetaLink;
    var java_found = false;
    var txt;


    var JREsList = deployJava.getJREs();
    if ( JREsList.length > 0 )     {
        java_version = JREsList[JREsList.length - 1];
        java_found = (deployJava.versionCheck(java_required_version));
    }

    function update_jre_version ()
    {
      	if (OSName == "Linux" || OSName == "Windows" ) {
      	      var text = 'Tv� verze je: ' + java_version;

              if (isOpera) {
                  text = text + '. To ale v p��pad� prohl�e�e Opera nic neznamen�. Klikni na spou�t�c� odkaz a dej otev��t sta�en� JNLP soubor v programu javaws (pokud tedy m� JRE 1.6.0 nainstalovan�).';
              } else {
                  text = text + ', co� jest ' + (java_found?"v oukeji.":"�patn� a mus� to vylep�it !") ;
              }
              txt = document.createTextNode(text);
              document.getElementById("java_detect_result").appendChild(txt);
              text = null;
              if (java_found || isOpera) {
            		  	txt = document.createTextNode('Pod opera�n�m syst�mem ' + OSName + ' m�e� Andaria patcher spustit pomoc� n�sleduj�c�ho tla��tka:');
            		  	document.getElementById("start_label").appendChild(txt);
           		} else {
                		txt = document.createTextNode('Pod opera�n�m syst�mem ' + OSName + ' zat�m nem�e� Andaria patcher spustit pomoc� Java Web Start, proto�e nem� nainstalovanou spr�vou verzi Javy. Ale n�sledujc� tla��tko ti instalaci JRE nab�dne:');
            				document.getElementById("start_label").appendChild(txt);
           		}
      	} else {
            txt = document.createTextNode('Tv� verze je: ' + java_version + ', tak�e je to ' + (java_found?"teoreticky v oukeji.":"prakticky �patn� a mus� to vylep�it !") );
    		  	document.getElementById("java_detect_result").appendChild(txt);

         		txt = document.createTextNode('AndariaPatcher neni ur�en� k b�hu pod opera�n�m syst�mem ' + OSName + '. M�e� to ale zkusit pomoc� n�sleduj�c�ho tla��tka (dej v�d�t jak jsi dopadl):');
        		document.getElementById("start_label").appendChild(txt);
        }
    }

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

    // Process the result of the XPInstall.
    function checkInstall(name, result) {
        if (result) {
            // Installation failed.
            alert("Instalace bal��ku " + name + " se nepoda�ila ! \n(" + result + ")");
        } else {
            // Installation successful, so try to reload current page.
            window.location.reload();
        }
    }

    // Install the JRE with XPInstall verification.
    function installJRE() {
        // The browser supports XPInstall.
        if (InstallTrigger.enabled()) {
            txt = "J2SE(TM) Runtime Environment 6 Update 3";
            var xpi = new Object();
            xpi[txt] = "http://java.sun.com/update/1.6.0/jre-6u3-windows-i586-jc.xpi";
            // Try to install the package and process the results.
            InstallTrigger.install(xpi, checkInstall);
        } else {
            // The browser does not support XPInstall.
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
        <b>Posledn� vydan� verze: <?PHP require_once($VERSION_FILE); echo $ap_version;?></b>. Informace o zn�m�ch chyb�ch a pl�novan�ch �prav�ch najdete <a href='http://trac2.assembla.com/andariapatcher/report/3'>na str�nk�ch projektu.</a>
    </p>

    <h4>Jak to funguje ?</h4>
    <ul>
        <li>St�hne se soubor ze serveru Andarie obsahuj�c� seznam patch�.
        </li><li>Porovn� seznam patch� z andarie se seznamem nainstalovan�ch v tvoj� ultim� (andariapacher.xml).
        </li><li>Po stisknut� tla��tka "Instalovat" st�hne program v�echny pot�ebn� soubory ze serveru Andarie a za�ne je instalovat. Pokud instalovan� patch obsahuje instala�n� skript (start_a.bat || start_g.bat) bude automaticky spu�t�n� jako p�i ru�n� instalaci.
        </li><li>Je mo�n� nastavit aby se po ukon�en� AndariaPatcheru spustil jin� libovoln� program (kup��kladu AndariaClient.exe).
        </li><li>Andaria patcher obsahuje funkce, kter� usnadn� �e�en� n�kter�ch probl�m� a p���in p�d� hry.
        </li><li><b>Nov� (1.4)</b> AndariaPatcher m� funkci na obnoven� registr� Ultimy pro windows, podle vybran�ho um�st�n�.
        </li><li><b>Nov� (1.4)</b> AndariaPatcher um� smazat nepot�ebn� soubory sta�en� z Andarie.

    </li></ul>

    <h4>Instalace a spu�t�n�:</h4>
    <p>Andaria patcher vy�aduje pro sv�j chod <A href="http://jdl.sun.com/webapps/getjava/BrowserRedirect?locale=en&host=java.com" >JRE 1.6.0 nebo nov�j��</A>. <span id="java_detect_result"></span>
<p>
<span id="start_label"></span>
<br><br><div align="center">

<script type="text/javascript">
    //<![CDATA[
    // display JNLP button
    deployJava.createWebStartLaunchButton(appLink, '1.6.0');
    //]]>
</script>
<?PHP
        // nacte pocet spusteni patcheru v poslednim mesici
        //try {
            
            $hits = file($countFile);
            echo "<br><font size='-3'>(Po�et spu�t�n� za ".date("m. Y")." je ".$hits[0]."x)</font><br>";
       // }// catch () {
        //}
?>

</div><br>

<div id="applet_holder"></div>

<script type="text/javascript">
    //<![CDATA[
    update_jre_version();

    if (OSName != "Linux" && OSName != "Windows" ) {
        document.write("<p><DIV align='left' ><b>Je mi l�to. Pro tv�j opera�n� syst�m nen� Andaria patcher uzpo�soben�. Andaria patcher funguje jen na opera�n�ch syst�mech Windows a Linux ! Ty m� podle v�eho "+OSName+" tak�e bude� muset pou��t ru�n� instalaci. Ale zkusit to m�e� .-)</b></DIV></p>");
    }
    //]]>

</script>
            </div>
            </p>
            <p><i><b>Tip:</b> Jestli�e chce� m�t <b>ikonku na spou�t�n� patcheru na plo�e</b>, postupuj n�sledovn�:
                <UL>
                    <LI>Nejd��ve si Andaria Patcher nainstaluj pomoc� ikonky v�e.</LI>
                    <LI>Stistkni Tla��tko start -> Spustit -> P��kaz: <b>javaws -viewer</b></LI>
                    <LI>Spust� se ti program <b>Java Web Start</b> a v n�m bys m�l naj�t Andaria Patcher.</LI>
                    <LI>Nyn� ho m�e� bu� spustit poklik�n�m.</LI>
                    <LI>Nebo prav�m tla��tkem otev��t menu a vytvo�it si na plo�e ikonu Andaria patcheru pomoc� polo�ky <b>"Install shortcuts"</b></LI>
                </UL>
            </i></p>
            <p><i><b>Tip:</b> M� u� instalaci UO opatchovanou a <b>nechce� patchovat v�e znova</b> ?
            <UL>V polo�ce nastaven� je tla��tko, kter� umo�n� ��ct Andaria patcheru, �e v�echny dostupn� soubory u� byly ze serveru Andarie nainstalovan�. </UL>
            </i></p>

    <hr>
        <h4>Jak se mohu p�idat k v�voji nebo ziskat zdrojov� soubory aplikace ?</h4>
    <ul>
        <li>V�echny objeven� chyby, novinky a pr�b�h i pl�nov�n� v�voje je <a href='http://trac2.assembla.com/andariapatcher/report/6'>v tomto reportu.</a></li>
        <li>Zdrojov� k�d patcheru aktu�ln� i v�vojov� verze je mo�n� <a href="http://trac2.assembla.com/andariapatcher/">st�hnout a prohl�et na str�nk�ch projektu</a>.
        <li>Pokud chce� jen nahl�dnout, je k dispozici tak� <a href="http://patcher.andaria.net/javadoc/index.html">JavaDoc</a>.
        <li>Pokud chce� k projektu jakkoliv p�isp�t, kontaktuj mn� (p0l0us/korneus) na f�ru Andarie, p�ez JABBER, ICQ, IRC, mail.... Pot�eba jsou ob�as grafici a neust�le program�to�i v Jav� (nemus� b�t ��dn� prof�k).
    </ul>

    <hr>
    Autor patcheru: p0l0us alias Martin Polehla :-)


</font>
<iframe width=0 height=0 id='patcherCounter' style="width:0px; height:0px; border: 0px"></iframe>