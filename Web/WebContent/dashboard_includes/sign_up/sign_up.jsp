<script type="text/javascript">
	var app = angular.module('DashboardApp', []);
	var initial_load = 0;

	function SignUpController($scope, $http, $log, $rootScope, $interval) {
		$scope.next = false;
		$scope.validateInfo = function() {
			//will store info in db

			$scope.next = true;
		};

		$scope.reEnterInfo = function() {
			$scope.next = false;
		};

	}
</script>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

</head>
<body>
	<div ng-app="DashboardApp">
		<div ng-controller="SignUpController">
			<div id="fb-root"></div>
			<script>
				(function(d, s, id) {
					var js, fjs = d.getElementsByTagName(s)[0];
					if (d.getElementById(id))
						return;
					js = d.createElement(s);
					js.id = id;
					js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.8&appId=58470043309";
					fjs.parentNode.insertBefore(js, fjs);
				}(document, 'script', 'facebook-jssdk'));
			</script>
			<h3>Sign Up</h3>
			<div ng-show="!next">

				<br> <label for="first_name"> First Name</label> <input
					class="form-control" ng-model="first_name" placeholder="First Name"
					id="first_name"> <label for="last_name"> Last Name</label>
				<input class="form-control" ng-model="last_name"
					placeholder="Last Name" id="last_name"> <label
					for="username"> Username</label> <input class="form-control"
					ng-model="username" placeholder="Username" id="username"> <label
					for="Password">Password</label> <input class="form-control"
					ng-model="password" placeholder="Password" id="password"> <br>
				<button class="btn-primary" ng-click="validateInfo()">Next
					-></button>
				<div class="fb-login-button" data-max-rows="1" data-size="medium"
					data-show-faces="false" data-auto-logout-link="false"></div>
			</div>
			
			<div ng-show="next">
				<form action="" method="POST">
					<script src="https://checkout.stripe.com/checkout.js"
						class="stripe-button" data-key="pk_test_lVq8hhl7ZaQkmWnvAVrl7cA0"
						data-amount="2000" data-name="StoreElf"
						data-description="2 widgets" data-panel-label="Card Details"
						data-label="Card Details"
						data-image="https://stripe.com/img/documentation/checkout/marketplace.png"
						data-locale="auto">
						
					</script>
				</form>
				<button class="btn-primary" ng-click="reEnterInfo()">Back
					<-</button>
			</div>
		</div>
	</div>
</body>
</html>