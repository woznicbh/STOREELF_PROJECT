<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>


<label>Sign Up</label>
<br>
<input ng-model="first_name" placeholder="First Name">
<br>
<input ng-model="last_name" placeholder="Last Name">
<br>
<input ng-model="username" placeholder ="Username"> 
<br>
<input ng-model="password" placeholder = "Password">

<form action="" method="POST">
<script
  src="https://checkout.stripe.com/checkout.js" class="stripe-button"
  data-key="pk_test_lVq8hhl7ZaQkmWnvAVrl7cA0"
  data-amount="2000"
  data-name="StoreElf"
  data-description="2 widgets"
  data-panel-label="Card Details"
  data-label="Card Details"
  data-image="https://stripe.com/img/documentation/checkout/marketplace.png"
  data-locale="auto">
</script>
</form>
</body>
</html>