 <script type="text/javascript">
    try{
    	//TODO set GLOBALS
    	var STOREELF_CONTEXT_PATH				= '<%=request.getContextPath()%>';
    	var STOREELF_VERSION					= '<%=System.getProperty("STOREELF_VERSION")%>';
    	// var STOREELF_ROOT_URI				= STOREELF_CONTEXT_PATH+'/'+STOREELF_VERSION;
    	var STOREELF_ROOT_URI					= '';
    	var ANGULARJS_APP					= null;
    	var ANGULARJS_PAGINATION_pageSize	= 10;

    	//set GLOBAL filters
    	var ANGULARJS_FILTER_startFrom	= function() {
    	    return function(input, start) {
    	        start = +start;
    	        if(input !=null && input !== 'undefined') return input.slice(start); else return null;
    	    };
    	};

    	var ANGULARJS_FILTER_range		= function() {
  		  return function(input, total) {
  			if(input){
	  		    total = parseInt(total);
	  		    for (var i=0; i<total; i++) input.push(i);
	  		    return input;
  			}else{
	  			 return null;
  			}
  		  };
  		};

  		var Script = function () {
  	         //Common Script Goes Here
  	         //^ don't listen to that guy ... nothing goes here
  	    }();
    }catch(e){
    	console.log('error loading globals');
    }

    /*!
     * @author tkmagh4
     */
    var GLOBAL_LOADING_COUNT	= 0;
    var GLOBAL_UNLOADING_COUNT	= 0;
    var GLOBAL_LOAD_COMPLETE = false;


    function writeCookie(name,value,days) {
        var date, expires;
        if (days) {
            date = new Date();
            date.setTime(date.getTime()+(days*24*60*60*1000));
            expires = "; expires=" + date.toGMTString();
                }else{
            expires = "";
        }
        document.cookie = name + "=" + Base64.encode(JSON.stringify(value)) + expires + "; path=/";
    }

    function readCookie(name) {
        var i, c, ca, nameEQ = name + "=";
        ca = document.cookie.split(';');
        for(i=0;i < ca.length;i++) {
            c = ca[i];
            while (c.charAt(0)==' ') {
                c = c.substring(1,c.length);
            }
            if (c.indexOf(nameEQ) == 0) {
                return Base64.decode( c.substring(nameEQ.length,c.length) );
            }
        }
        return '';
    }

    function isLoading(){
    	return GLOBAL_LOADING_COUNT!=GLOBAL_UNLOADING_COUNT;
    }

    function isJson(str) {
        try {
            JSON.parse(str);
        } catch (e) {
        	console.log('error validating JSON data; returning false')
            return false;
        }
        return true;
    }
    /*
    function isJSON(value) {
        try {
            JSON.stringify(value);
            return true;
        } catch (ex) {
            return false;
        }
    }*/

    function sidebar_isActive(){
    	try{
    		$("a[app-controller-label]").each(function(){
    			if( $(this).attr('app-controller-label') === $('[ng-controller]').attr('ng-controller') ){
    				$(this).parent().parent().parent().children('a')[0].click();
    				$(this).click();
    				$(this).parent().attr('class', $(this).parent().attr('class')+' active');
    			}
    		});
    	}catch(e){
    		console.log('error checking sidebar link active status')
    	}
    }

    function getNumberOfPages(dataLength, pageSize){
    	return Math.ceil(dataLength/pageSize);
    }
    
    var GLOBAL_SIDEBAR_TOGGLE = (function () {
      	 
       	console.log("GLOBAL_SIDEBAR_TOGGLE() executed");
           if ($('aside').hasClass("hidden") === true) {
               /*$('#main-content').css({ 'margin-left': '0px'});
               $('#sidebar').css({ 'margin-left': '-230px' });
               $('#sidebar > ul').hide();
               $("#container").addClass("sidebar-closed");*/
           	
           	$('aside').removeClass('hidden');
           	if($('aside').hasClass("col-md-3")){
           		$('#main-content').attr('class', 'col-md-9 col-md-offset-3');
           	}else{
           		$('#main-content').attr('class', 'col-md-10 col-md-offset-2');
           	}

           } else {
               /*$('#main-content').css({ 'margin-left': '230px'});
               $('#sidebar > ul').show();
               $('#sidebar').css({ 'margin-left': '0'});
               $("#container").removeClass("sidebar-closed");*/
           	$('aside').attr('class', $('aside').attr('class')+' hidden');
           	$('#main-content').attr('class', 'col-md-12');
           };
       });
    
   function getTk() {
		//$scope.username =String(document.getElementById("username").innerHTML);
	
		//console.log($scope.username);
		
		//temp.split("<div id=\"username\">")[1]
		//return $('.username').text().trim();
		return String(document.getElementById("username").innerHTML);
	}

    
    </script>
    
    <%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
    <div id="username" style="display: none"><shiro:principal/></div>
