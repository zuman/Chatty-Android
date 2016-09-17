<?
$con=mysqli_connect("mysql17.000webhost.com","a6835354_zapp","admindb7","a6835354_app");
if($con)	echo "good\n";
else		echo "bad\n";

if(isset($_POST['from']))
{
 $status=mysqli_query($con,"insert into msgs(`from`,`to`,`msg`,`timestamp`,`status`) values('".$_POST['from']."','".$_POST['to']."','".$_POST['msg']."',ADDTIME(now(),'09:30:00.000000'),'unread')");
 echo $status."\n";
} 
?>