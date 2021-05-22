<?php
require 'vendor/autoload.php';

// Create a client for http://localhost (binding to a host is optional)


$client = new GuzzleHttp\Client(['base_uri'=>'https://shopping-dot-projetcloud-313614.ew.r.appspot.com']);
$account = null;
try {
  echo "REQUEST /createAccount :\n";
  $res = $client->post('/createAccount', [GuzzleHttp\RequestOptions::JSON => ['login' => 'loginTest2', 'password' => 'password']]);
  echo "Code retour : {$res->getStatusCode()}\n";
  $account = json_decode($res->getBody(), true);
} catch (\GuzzleHttp\Exception\ClientException $e) {
	echo json_decode($e->getResponse()->getBody())->message."\n";
	echo "Set Default Account\n";
	$account = array('login'=>'loginTest2', 'password'=>'eded82fead402293e0ad6f774aa4cb1d77245c2bc62f3b4c4f3dfcaacb336fe2');
}
try {
  echo "Account : ".print_r($account, true);
  $accountLogin = $account['login'];
  $accountPwd = 'password'; // PASSWORD RETURN IS HASHED;

  echo "\nREQUEST /getAccount :\n";
  $res = $client->post('/getAccount', [GuzzleHttp\RequestOptions::JSON => ['login' => $account['login'], 'password' => $accountPwd]]);
  echo "Code retour : {$res->getStatusCode()}\n";
  $accountId = json_decode($res->getBody(), true)['id'];
  echo "Account Id : {$accountId}\n";

  echo "\nREQUEST /getallbooks :\n";
  $res = $client->get('/getallbooks');
  echo "Code retour : {$res->getStatusCode()}\n";
  $array = json_decode($res->getBody(), true);
  $firstBook = $array[0];
  echo "First Book : ".print_r($firstBook, true);
  $isbn = $firstBook['isbn'];

  echo "\nREQUEST /getBookByIsbn :\n";
  $res = $client->get("/getBookByIsbn?isbn={$isbn}");
  echo "Code retour : {$res->getStatusCode()}\n";
  $stock = json_decode($res->getBody(), true)['stock'];
  echo "Stock : {$stock}\n";

  echo "\nREQUEST /buyBook :\n";
  $res = $client->post('/buyBook', [GuzzleHttp\RequestOptions::JSON => ['isbn' => $isbn, 'accountId' => $accountId, 'quantity' => $stock+43]]);
  echo "Code retour : {$res->getStatusCode()}\n";

  echo "\nREQUEST /getAllMyOrders/{accountId} :\n";
  $res = $client->get("/getAllMyOrders/{$accountId}");
  echo "Code retour : {$res->getStatusCode()}\n";
  $array = json_decode($res->getBody(), true);
  echo "ALL ORDERS : ".print_r($array, true)."\n";
  $id = end($array)['id'];
  echo "LAST ID : {$id}\n";

  echo "\nREQUEST /getOrder/{orderId} :\n";
  $res = $client->get("/getOrder/{$id}");
  echo "Code retour : {$res->getStatusCode()}\n";
  $orderString = print_r(json_decode($res->getBody(), true), true);
  echo "Order : {$orderString}\n";
} catch (\GuzzleHttp\Exception\ClientException $e) {
	echo json_decode($e->getResponse()->getBody())->message."\n";
}
?>
