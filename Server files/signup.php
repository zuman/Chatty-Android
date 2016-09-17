<?
$con=mysqli_connect("mysql17.000webhost.com","a6835354_zapp","admindb7","a6835354_app");
if($con)	echo "good\n";
else		echo "bad\n";

if(isset($_POST['name']))
{
 $result=mysqli_query($con,"select id, email from acc where id='".$_POST['id']."' or email='".$_POST['email']."'");

 $status=1;
 while($row = mysqli_fetch_array($result))
 {
  if($row['id']==$_POST['id'])
  {
   $status="id exists\n";
   break;
  }
  else if($row['email']==$_POST['email'])
  {
   $status="email exists\n";
   break;
  }
 }

 if($status==1)
  $status=mysqli_query($con,"insert into acc values('".$_POST['name']."','".$_POST['id']."','".$_POST['email']."','".$_POST['pass']."')");

 if($status==1)
  echo "everything ok\n";
 else
  echo $status;
} 
?>