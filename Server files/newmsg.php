<?
$con=mysqli_connect("mysql17.000webhost.com","a6835354_zapp","admindb7","a6835354_app");
if($con)	echo "good\n";
else		echo "bad\n";

if(isset($_POST['id']))
{
 $result=mysqli_query($con,"SELECT * FROM msgs where `to`='".$_POST['id']."' and `status`='unread' order by timestamp asc limit 1");
 $status=0;
 
 while($row = mysqli_fetch_array($result))
 {
  $status=1;
  $sno=$row['sno'];
  echo $row['from']."\n";
  echo $row['timestamp']."\n";
  echo $row['msg']."\n";
  mysqli_query($con,"update msgs set `status`='acked' where sno=".$sno);
 }
 
 if($status==0)
  echo $status."\n";
} 
?>