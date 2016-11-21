// another file and/or another anonymous function
//(function(){
 // using the function form of use-strict...
// "use strict";
  // accessing the module in another.
  // this can be done by calling angular.module without the []-brackets
 	angular.module('search', [])
 	//ANGULARJS_APP//.module('STOREELFSearchHistory')
    //.controller('myctrl', ['search_history', function(request_uri){
      //..
    //}])
  .service('STOREELFSearchService', function($http, $q, $log, $location){
	  var _data			= '';
	  var _post_url		= STOREELF_ROOT_URI+'/_internal_api/UserSession/search_history';
	  var _request_uri	= '';

	  this.setPostUrl = function(post_url){	  		_post_url = post_url;};
	  this.getPostUrl = function(){		return 		_post_url;};

	  this.setRequestUri = function(request_uri){	_request_uri = request_uri;};
	  this.getRequestUri = function(){  return 		_request_uri;};

	  this.getData = function(){		return _data;};

	  this.getHistory = function(){
		  try{
			$log.debug('search.js->getHistory->request_uri='+this.getRequestUri());
			$http({
			    method: 'POST',
			    url: _post_url,
			    data: "request_uri=" + this.getRequestUri(),
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function(data, status) {
				_data = data;
				return _data;
			});
			return '';
		  }catch(e){
			  $log.error('error accessing search history');
			  $log.error(e);
		  }
	  };

	  //TODO add clear history implementation
	  this.clearSearchHistory = function(){
		  try{
				$http({
				    method: 'POST',
				    url: _post_url, //STOREELF_ROOT_URI+'/_internal_api/UserSession/search_history',
				    data: "request_uri=" + this.getRequestUri(),
				    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
				}).success(function(data, status) {
					//console.log("data-->"+data)
					_data = data;
					return _data;
				});
				return '';
		  }catch(e){
			  $log.error('error accessing clearing history');
			  $log.error(e);
		  }
	  };

	  this.preview = function(searchObject, search_parameters){
		  try{
			  //set search parameters on mouse_over
			  $log.debug('search.js->preview->search_parameters='+search_parameters);
			  for ( property in search_parameters ) {
				  searchObject[property] = search_parameters[property]
			  }
			  return searchObject;
		  }catch(e){
			  $log.error('error displaying preview');
			  $log.error(e);
		  }
	  };

	  this.updateAddressBar = function(key, value){
		  value = (value === undefined) ? '' : value;
		  $log.debug('search.js->updateAddressBar->setting key \''+ key +'\' and value \''+value+'\'');
		  $location.search(key, value);
	  };

	  this.autoFillForm = function(searchObject, search_parameters){
		  $log.debug('search.js->autoFillForm->search_parameters='+search_parameters);
		  this.preview(searchObject, search_parameters);
	  };

	  this.injectIntoScope = function(scopeObject){
		  scopeObject._STOREELFSearchService = this;
	  };
  })

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