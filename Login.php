<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbcon.php');

$studentid = isset($_POST['studentid']) ? $_POST['studentid'] : '';
$passwords = isset($_POST['passwords']) ? $_POST['passwords'] : '';

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!empty($studentid)) {

    $sql = "SELECT * FROM users WHERE studentid = :studentid";
    $stmt = $con->prepare($sql);
    $stmt->bindParam(':studentid', $studentid);
    $stmt->execute();

    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($row && password_verify($passwords, $row['passwords'])) {
        $data = array(
            'username' => $row["username"],
            'studentid' => $row["studentid"],
            'emails' => $row["emails"],
            'passwords' => $row["passwords"]
        );

        if (!$android) {
            echo "<pre>";
            print_r($data);
            echo '</pre>';
        } else {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("users" => $data), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    } else {
        echo "'" . $studentid . "' no studentid OR wrong password.";
    }
} else {
    echo " login. ";
}
?>

<?php
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android) {
?>
    <html>

    <body>
        <form action="<?php $_PHP_SELF ?>" method="POST">
            StudentID: <input type="text" name="studentid" /><br>
            PASSWORD: <input type="password" name="passwords" /><br>
            <input type="submit" />
        </form>
    </body>

    </html>
<?php
}
?>
