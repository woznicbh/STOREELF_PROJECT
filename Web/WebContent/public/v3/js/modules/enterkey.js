
// another file and/or another anonymous function
//(function(){
 // using the function form of use-strict...
// "use strict";
  // accessing the module in another.
  // this can be done by calling angular.module without the []-brackets
 	angular.module('enterkey', [])
 	//ANGULARJS_APP//.module('STOREELFSearchHistory')
    //.controller('myctrl', ['search_history', function(request_uri){
      //..
    //}])

 	.directive('ngEnter', function () {
		console.log('---');
	    return function (scope, element, attrs) {
	        element.bind("keydown keypress", function (event) {
	            if(event.which === 13) {
	                scope.$apply(function (){
	                    scope.$eval(attrs.ngEnter);
	                });
	                event.preventDefault();
	            }
	        });
	    };
	});


/*  .service('STOREELFSecurityService', function($http, $q){
	  var _data			= '';

	  this.getData = function(){		return _data;}

	  this.getX = function(){
			return '';
	  }
  })
*/
//  .service('myService', function($http, $q){
//	  var _test = 'test-value';
//	  this.getTest = function(){
//		  return _test;
//	  }
//  });


  // appending another service/controller/filter etc to the same module-call inside the same file
  /*.factory('SomethingService', ['$dep1', '$dep2', function($dep1, $dep2){
	  return function(arg1, arg2){}
  }]);*/

  // you can of course use angular.module('mymod') here as well
  //angular.module('MODULE_NAME').controller('ControllerName', ['$dep1', function($dep1){}])
//})();