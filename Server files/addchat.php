<?
$con=mysqli_connect("mysql17.000webhost.com","a6835354_zapp","admindb7","a6835354_app");
if($con)	echo "good\n";
else		echo "bad\n";

if(isset($_POST['id']))
{
 $result=mysqli_query($con,"select * from acc where (id='".$_POST['id']."' or email='".$_POST['id']."') or pass='123' order by pass desc");

 while($row = mysqli_fetch_array($result))
 {
  if($row['pass']=='123')
  {
   $status="no user\n";
   break;
  }
  else
  {
   $status="user\n".$row['id']."\n".$row['name']."\n";
   break;
  }
 }
 
  echo $status;
} 
?>