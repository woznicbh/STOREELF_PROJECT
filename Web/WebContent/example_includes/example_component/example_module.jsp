<script type="text/javascript">

	//your app MUST be named starting with it's associated Servlet name
	ANGULARJS_APP = angular.module('ExampleComponentApp', ['search', 'enterkey']);

	/**
		this will add a custom directive as 'my-custom-attribute'; the JS will auto-insert the '-'
		Example:
			<div my-custom-attribute="something" targetAttribute="">text</div>
			<img my-custom-attribute="something" src=""/>
	**/
	exampleApp.directive('mycustomattribute', function() {
	    return {
	        link: function(scope, element, attrs) {
	            var fullPathUrl = "<%=request.getContextPath()%>"+"/";
	            if(element[0].tagName === "IMG") {
	                attrs.$set('targetAttribute', fullPathUrl + attrs.fullPath);
	            }
	        },
	    }
	});

	//define your controller, function name MUST end with 'Controller'
 	function ExampleModuleController($scope, $http, $log, $location, STOREELFSearchService){
 		var post_url = STOREELF_ROOT_URI+'/Example/ExampleComponentServlet/example_module';

 		//define your controllers calling function
		$scope.getObjects = function(parameter) {


 			$http({
				//request method, this should remain 'POST'
			    method: 'POST',

				//this is the path/name of your Servlet
			    url: post_url,

			    //the first parameter should define the requested page with the following parameters using your servlet's required parameters
			    data: "paramOne=" + parameter.fieldName,

			    //DO NOT TOUCH, header type will stay the same
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				/*
				 * your return data can be defined as anything within this AngularJS application's scope
				 * for example: $scope.orders OR $scope.items
				 */
				$scope.results = data;

				$scope.getSearchHistory();
			});
		};

		$scope.$on('$locationChangeStart', function () {
			if(!jQuery.isEmptyObject($location.search())) $scope.autoSearch($location.search());
 		});

		$scope.getSearchHistory = function(){
			//-- set search history
			STOREELFSearchService.setRequestUri(post_url);
			STOREELFSearchService.getHistory();
			STOREELFSearchService.injectIntoScope($scope);
			$scope.search_history_data = STOREELFSearchService.getData();
		};

		$scope.autoSearch = function(hash){
			$scope.getSearchHistory();
 			$scope.searchOrders(hash);
 			STOREELFSearchService.autoFillForm($scope.search, hash);
 		};
	}
</script>

<div ng-app="ExampleComponentApp">
	<!--
		The ngController directive attaches a controller class to the view.
		This is a key aspect of how angular supports the principles behind the Model-View-Controller design pattern.

		ng-controller should be defined as your defined controller above

		@see http://docs.angularjs.org/api/ng/directive/ngController
	-->
	<div ng-controller="ExampleModuleController" >
		<div class="col-sm-12">
			<ol class="breadcrumb">
				<li><a>Example Section</a></li>
				<li><a>Example Component</a></li>
				<li class="active">Example Module</li>
			</ol>
		</div>

		<form class="form-horizontal" role="form">

			<!--
				The ngModel directive binds an input,select, textarea (or custom form control)
				to a property on the scope using NgModelController, which is created and exposed by this directive.

				@see http://docs.angularjs.org/api/ng/directive/ngModel
			-->
			<div class="form-group">
			    <label class="col-sm-2 control-label">Text input</label>
			    <div class="col-sm-10">
					<input type="text" class="form-control" ng-model="parameter.fieldName" placeholder="Text input">
			    </div>
			  </div>

			  <!--
				The ngClick directive allows you to specify custom behavior when an element is clicked.

				ng-click will invoke the function you've defined in your Controller's scope.
				The 'parameter' will be composed of the passed ng-model object(s)
				Example: getObjects(parameter) OR getObjects(parameter, static_option)

				@See: http://docs.angularjs.org/api/ng/directive/ngClick

				------------------------
				For FORM element EXAMPLES:

				@See: http://getbootstrap.com/css/#forms

				type="submit"
				or
				type="button"
				-->
			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button class="btn btn-default" type="submit" ng-click="getObjects(parameter)">Search</button>
			     	<div class="btn-group">
						<button data-toggle="dropdown" class="btn btn-default dropdown-toggle" type="button">History <span class="caret"></span></button>
						<ul role="menu" class="dropdown-menu">
							<li><a href="#"><i class="fa fa-clock-o"></i> Clear Search History (I don't work yet ... sry)</a></li>
							<li class="divider"></li>
							<li ng-repeat="result in search_history_data" ng-mouseover="_STOREELFSearchService.preview(search, result.SEARCH_PARAMETERS);"><a href="#"><i class="fa fa-clock-o" ng-bind-template=" {{result.SEARCH_DATE}}"></i></a></li>
						</ul>
					</div><!-- /btn-group -->
			    </div>
			  </div>
		</form>

		<!-- SIMPLE TABLE -->
		<div>
			<table class="row table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Column Name #1</th>
						<th>Column Name #2</th>
						<th>Column Name #3</th>
						<th>Column Name #4</th>
					</tr>
				</thead>
				<!--
					The ngRepeat directive instantiates a template once per item from a collection.

					@see http://docs.angularjs.org/api/ng/directive/ngRepeat
				-->
				<tbody>
					<tr ng-repeat="result in results">
						<td>{{result.FIELD_NAME_1}}</td>
						<td>{{result.FIELD_NAME_2}}</td>
						<td>{{result.FIELD_NAME_3}}</td>
						<td>{{result.FIELD_NAME_4}}</td>
					</tr>
				</tbody>
			</table>
		</div>

		<!-- SIMPLE TABLE WITH NESTING -->
		<div>
			<table class="row table table-hover table-condensed table-bordered table-responsive">
				<thead>
					<tr>
						<th>Column Name #1</th>
						<th>Column Name #2</th>
						<th>Column Name #3</th>
						<th>Column Name #4</th>
					</tr>
				</thead>
				<!--
					The ngRepeat directive instantiates a template once per item from a collection.

					@see http://docs.angularjs.org/api/ng/directive/ngRepeat
				-->
				<tbody>
					<tr ng-repeat-start="result in results">
						<td>
							<button type="button" class="btn btn-default btn-sm" ng-click="toggle = !toggle">
								<span ng-class="{false: 'glyphicon glyphicon-plus', true: 'glyphicon glyphicon-minus'}[toggle==true]"></span>
							</button>
	              		</td>
						<td>{{result.FIELD_NAME_1}}</td>
						<td>{{result.FIELD_NAME_2}}</td>
						<td>{{result.FIELD_NAME_3}}</td>
						<td>{{result.FIELD_NAME_4}}</td>
					</tr>
					<tr ng-show="toggle" ng-animate="'box'" ng-repeat-end>
						<td colspan="9">
							<table class="table table-hover">
								<tr>
									<th>Column Name #5</th>
									<th>Column Name #6</th>
								</tr>
								<tr>
									<td>{{result.FIELD_NAME_5}}</td>
									<td>{{result.FIELD_NAME_6}}</td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

	</div>
</div>