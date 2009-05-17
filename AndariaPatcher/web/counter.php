<?
$countFile = "hits/".date("m-y").".txt";
echo $countFile;
$hits = file($countFile);
$hits[0]++;

$fp = fopen($countFile, "w+");
fputs($fp, $hits[0]);

fclose($fp);
?>