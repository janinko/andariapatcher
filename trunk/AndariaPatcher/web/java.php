<?PHP
$VERSION_FILE = "/home/wwwroot/andaria.net/patcher/version.php";
$countFile = "/home/wwwroot/andaria.net/patcher/hits/hits.txt";
$countBetaFile = "/home/wwwroot/andaria.net/patcher/hits/.txt";

include_once($VERSION_FILE);
?>
<script src="http://java.com/js/deployJava.js"></script>
<script type="text/javascript" >
    //<![CDATA[
  	var appLink = "http://patcher.andaria.net/launch.jnlp";
    var appBetaLink = "http://patcher.andaria.net/beta/launch.jnlp";
    var appLaunchButton = 'http://patcher.andaria.net/spustit.png';
    var appBetaLaunchButton = 'http://patcher.andaria.net/spustitSmall.png';
    
    deployJava.launchButtonPNG = appLaunchButton;

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
                  text = text + '. To ale v pøípadì prohlížeèe Opera nic neznamená. Klikni na spouštìcí odkaz a dej otevøít stažený JNLP soubor v programu javaws (pokud tedy máš JRE 1.6.0 nainstalované).';
              } else {
                  text = text + ', což jest ' + (java_found?"v oukeji.":"špatnì a musíš to vylepšit !") ;
              }
              txt = document.createTextNode(text);
              document.getElementById("java_detect_result").appendChild(txt);
              text = null;
              if (java_found || isOpera) {
            		  	txt = document.createTextNode('Pod operaèním systémem ' + OSName + ' mùžeš Andaria patcher spustit pomocí následujícího tlaèítka:');
            		  	document.getElementById("start_label").appendChild(txt);
           		} else {
                		txt = document.createTextNode('Pod operaèním systémem ' + OSName + ' zatím nemùžeš Andaria patcher spustit pomocí Java Web Start, protože nemáš nainstalovanou správou verzi Javy. Ale následujcí tlaèítko ti instalaci JRE nabídne:');
            				document.getElementById("start_label").appendChild(txt);
           		}
      	} else {
            txt = document.createTextNode('Tvá verze je: ' + java_version + ', takže je to ' + (java_found?"teoreticky v oukeji.":"prakticky špatnì a musíš to vylepšit !") );
    		  	document.getElementById("java_detect_result").appendChild(txt);

         		txt = document.createTextNode('AndariaPatcher neni urèený k bìhu pod operaèním systémem ' + OSName + '. Mùžeš to ale zkusit pomocí následujícího tlaèítka (dej vìdìt jak jsi dopadl):');
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
            alert("XPInstall nepodporuje tvùj prohlížeè. Myslel jsem že máš Firefox ?! hmmh..");
        }
    }
    //]]>
</script>

<font color="#A37D56" style="font-family: Verdana,Arial; font-size: 9pt">
    <h3>O programu:</h3>
    <p>Andaria patcher umožòuje hráèùm oblíbené hry Ultima online jednoduše pøizpùsobit svou instalaci pro shard Andaria. 
    </p>
<p>
        <b>Poslední vydaná verze: <?PHP require_once($VERSION_FILE); echo $ap_version;?></b>. Informace o známých chybách a plánovaných úpravách najdete <a href='http://trac2.assembla.com/andariapatcher/report/3'>na stránkách projektu.</a><br>Po svìtì pobíhá i stará verze patcheru - XUL. Tu již nepoužívejte.
    </p>

    <h4>Jak to funguje ?</h4>
    <ul>
    <li>V první øadì patcher na poèítaèi hráèe <b>NEDÌLÁ</b> nic jiného, než by udìlal sám hráè (stáhne soubor, rozbalí do adresáøe ultimy, spustí start_?.bat). Tedy neprobíhá žádné špehování ani otevírání zadních vrátek (jak se nìkteøí paranoici obávají). Jedinout informaci, kterou pøedává patcher serveru andarie je "+1", kterým zvýší poèítadlo použití. Ostatní komunikace probíhá smìrem k uživateli.
    </li><li> Patcher je projekt otevøený, zdrojové kódy jsou k dispozici všem (GNU GPL) a tudíž tato tvrzení tedy mùže ovìøit každý programátor, jenž se trošku vyzná v Javì.
    </li><li>Veškerá dùležitá èinnost je ukládána do logovacího okna a je možné si zobrazit detailní informace pomocí zaškrtávátka "Debug režim"</li>
    </ul><ul>
    <b>Potrobnìji k èinnosti:</b>
        <li> stáhne se soubor ze serveru Andarie obsahující seznam patchù (obsah sekce download).
        </li><li>Seznam patchù z andarie je porovnán se seznamem nainstalovaných v tvojí ultimì (informace jsou uložené v AndariaPatcher.xml v adresáøi uo).
        </li><li>Po stisknutí tlaèítka "Instalovat" stáhne program všechny potøebné (vybrané) soubory ze serveru Andarie na doèasné úložištì a zároveò stažené zaène instalovat. Pokud instalovaný patch obsahuje instalaèní skript (start_a.bat nebo start_g.bat) bude automaticky spuštìný - jako pøi ruèní instalaci.
        </li><li>Je možné nastavit aby se po ukonèení AndariaPatcheru spustil jiný libovolný program (kupøíkladu AndariaClient.exe). To funguje pouze v pøípadì zavøení tlaèítkem "Zavøít". Zavøení køížkem a pod. tento krok vynechá.
        </li><li>AndariaPatcher obsahuje funkce, které usnadní øešení nìkterých problémù a pøíèin pádù hry (odstranìní .nwb, obnovit registry windows).
    </li></ul>

    <h4>Instalace a spuštìní:</h4>
    <p>Andaria patcher vyžaduje pro svùj chod <A href="http://jdl.sun.com/webapps/getjava/BrowserRedirect?locale=en&host=java.com" >JRE 1.6.0 nebo novìjší</A>. <span id="java_detect_result"></span>
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
            echo "<br><font size='-3'>(Poèet spuštìní za ".date("m. Y")." je ".$hits[0]."x)</font><br>";
       // }// catch () {
        //}
?>
<br><br>
<b>Beta Patcheru verze <?php echo $ap_versionBeta;?></b><br>

<script type="text/javascript">
    //<![CDATA[
    // display JNLP button
     deployJava.launchButtonPNG = appBetaLaunchButton;
    deployJava.createWebStartLaunchButton(appBetaLink, '1.6.0');
    //]]>
</script>
<br>
<font size=-2>mùže být nestabilní, nefunkèní, ale nemìla by poškodit ultimu jako takovou ani nic ostatního</font>
</div><br>

<div id="applet_holder"></div>

<script type="text/javascript">
    //<![CDATA[
    update_jre_version();

    if (OSName != "Linux" && OSName != "Windows" ) {
        document.write("<p><DIV align='left' ><b>Je mi líto. Pro tvùj operaèní systém není Andaria patcher uzpoùsobený. Andaria patcher funguje jen na operaèních systémech Windows a Linux ! Ty máš podle všeho "+OSName+" takže budeš muset použít ruèní instalaci. Ale zkusit to mùžeš .-)</b></DIV></p>");
    }
    //]]>

</script>
            </div>
            </p>
            <p><i><b>Tip:</b> Jestliže chceš mít <b>ikonku na spouštìní patcheru na ploše</b>, postupuj následovnì:
                <UL>
                    <LI>Nejdøíve si Andaria Patcher nainstaluj pomocí ikonky výše.</LI>
                    <LI>Stistkni Tlaèítko start -> Spustit -> Pøíkaz: <b>javaws -viewer</b></LI>
                    <LI>Spustí se ti program <b>Java Web Start</b> a v nìm bys mìl najít Andaria Patcher.</LI>
                    <LI>Nyní ho mùžeš buï spustit poklikáním.</LI>
                    <LI>Nebo pravým tlaèítkem otevøít menu a vytvoøit si na ploše ikonu Andaria patcheru pomocí položky <b>"Install shortcuts"</b></LI>
                </UL>
            </i></p>
            <p><i><b>Tip:</b> Máš už instalaci UO opatchovanou a <b>nechceš patchovat vše znova</b> ?
            <UL>V položce nastavení je tlaèítko, které umožní øíct Andaria patcheru, že všechny dostupné soubory už byly ze serveru Andarie nainstalované. </UL>
            </i></p>

    <hr>
        <h4>Jak se mohu pøidat k vývoji nebo ziskat zdrojové soubory aplikace ?</h4>
    <ul>
        <li>Všechny objevené chyby, novinky a prùbìh i plánování vývoje je <a href='http://trac2.assembla.com/andariapatcher/report/6'>v tomto reportu.</a></li>
        <li>Zdrojový kód patcheru aktuální i vývojové verze je možné <a href="http://trac2.assembla.com/andariapatcher/">stáhnout a prohlížet na stránkách projektu</a>.
        <li>Pokud chceš jen nahlédnout, je k dispozici také <a href="http://strazci.andaria.net/patcher/javadoc/index.html">JavaDoc</a>.
        <li>Pokud chceš k projektu jakkoliv pøispìt, kontaktuj mnì (p0l0us/korneus) na fóru Andarie, pøez JABBER, ICQ, IRC, mail.... Potøeba jsou obèas grafici a neustále programátoøi v Javì (nemusíš být žádný profík).
    </ul>

    <hr>
    Autor patcheru: p0l0us alias Martin Polehla :-)

</font>
<iframe width=0 height=0 id='patcherCounter' style="width:0px; height:0px; border: 0px"></iframe>