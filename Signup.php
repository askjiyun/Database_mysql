<?php
    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android)
    {
        $username = $_POST['username'];
        $studentid = $_POST['studentid'];
        $emails=$_POST['emails'];
        $passwords=$_POST['passwords'];

        if(empty($username)){
            $errMSG = "username";
        }
        else if(empty($studentid)){
            $errMSG = "studentid";
        }
        else if(empty($emails)){
            $errMSG = "emails";
        }
        else if(empty($passwords)){
            $errMSG = "passwords";
        }

        if(!isset($errMSG)){
            try {
                $stmt=$con->prepare('INSERT INTO Users(username,studentid,emails,passwords) VALUES (:username, :studentid, :emails, :passwords)');
                $stmt->bindParam(':username', $username);
                $stmt->bindParam(':studentid', $studentid);
                $stmt->bindParam(':emails', $emails);
                $hashed_password = password_hash($passwords, PASSWORD_DEFAULT);
                $stmt->bindParam(':passwords', $hashed_password);


                if($stmt->execute())
                {
                    $successMSG = "SUCCESS";
                }
                else{
                    $errMSG = "FAIL";
                }

            } catch(PDOException $e){
                die("Database error: " . $e->getMessage());
            }
        }
    }
?>

<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;
 
        $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
 
    if( !$android )
    {
?>
    <html>
       <body>
            <form action="<?php $_PHP_SELF ?>" method="POST">
                Name: <input type = "text" name = "username" />
                StudentID: <input type = "text" name = "studentid" />
                Emails: <input type = "text" name = "emails" />
                Password: <input type = "text" name = "passwords" />
                <input type = "submit" name = "submit" />
            </form>
 
       </body>
    </html>
<?php
    }
?>