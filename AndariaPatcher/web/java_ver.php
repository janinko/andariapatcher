<script src="http://java.com/js/deployJava.js"></script>
<script >
  //var dir = location.href.substring(0,location.href.lastIndexOf('/')+1);
  var url = "http://strazci.andaria.net/patcher/beta/launch.jnlp";
  deployJava.launchButtonPNG = 'http://strazci.andaria.net/patcher/spustit.png';
  deployJava.createWebStartLaunchButton(url, '1.6.0');
  
  var JREsList = deployJava.getJREs();
  for (var i = 0 ; i < JREsList.length ; ++i ) {
    document.write (JREsList[i] + "<br>");
  }
  
</script >

<?php

?>
