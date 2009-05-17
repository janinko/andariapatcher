
<script src="http://java.com/js/deployJava.js"></script>
<script type="text/javascript" >
    //<![CDATA[
	  var appLink = "http://strazci.andaria.net/patcher/launch.jnlp";
    var appBetaLink = "http://strazci.andaria.net/patcher/beta/launch.jnlp";
    deployJava.launchButtonPNG = 'http://strazci.andaria.net/patcher/spustit.png';
            
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
    var java_version =  "nezjištìná";
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
      	      var text = 'Tvá verze je: ' + java_version;

              if (isOpera) {
                  text = text + '. To ale v pøípadì prohlíeèe Opera nic neznamená. Klikni na spouštìcí odkaz a dej otevøít staenı JNLP soubor v programu javaws (pokud tedy máš JRE 1.6.0 nainstalované).';
              } else {
                  text = text + ', co jest ' + (java_found?"v oukeji.":"špatnì a musíš to vylepšit !") ;
              }
              txt = document.createTextNode(text);
              document.getElementById("java_detect_result").appendChild(txt);
              text = null;      		  	
              if (java_found || isOpera) {
            		  	txt = document.createTextNode('Pod operaèním systémem ' + OSName + ' mùeš Andaria patcher spustit pomocí následujícího tlaèítka:');
            		  	document.getElementById("start_label").appendChild(txt);
           		} else {
                		txt = document.createTextNode('Pod operaèním systémem ' + OSName + ' zatím nemùeš Andaria patcher spustit pomocí Java Web Start, protoe nemáš nainstalovanou správou verzi Javy. Ale následujcí tlaèítko ti instalaci JRE nabídne:');
            				document.getElementById("start_label").appendChild(txt);
           		}
      	} else {
            txt = document.createTextNode('Tvá verze je: ' + java_version + ', take je to ' + (java_found?"teoreticky v oukeji.":"prakticky špatnì a musíš to vylepšit !") );
    		  	document.getElementById("java_detect_result").appendChild(txt);
    
         		txt = document.createTextNode('AndariaPatcher neni urèenı k bìhu pod operaèním systémem ' + OSName + '. Mùeš to ale zkusit pomocí následujícího tlaèítka (dej vìdìt jak jsi dopadl):');
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
            alert("Instalace balíèku " + name + " se nepodaøila ! \n(" + result + ")");
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
    <p>Andaria patcher vyaduje pro svùj chod <A href="http://jdl.sun.com/webapps/getjava/BrowserRedirect?locale=en&host=java.com" >JRE 1.6.0 nebo novìjší</A>. <span id="java_detect_result"></span>
<p>            
<span id="start_label"></span>
<br><br><div align="center">

<script type="text/javascript">
    //<![CDATA[
    // display JNLP button
    deployJava.createWebStartLaunchButton(appLink, '1.6.0');
    //]]>
</script>

</div><br>

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
    
    if (OSName != "Linux" && OSName != "Windows" ) {
        document.write("<p><DIV align='left' ><b>Je mi líto. Pro tvùj operaèní systém není Andaria patcher uzpoùsobenı. Andaria patcher funguje jen na operaèních systémech Windows a Linux ! Ty máš podle všeho "+OSName+" take budeš muset pouít ruèní instalaci. Ale zkusit to mùeš .-)</b></DIV></p>");
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