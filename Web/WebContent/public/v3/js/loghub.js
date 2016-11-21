/*!
 * STOREELF.js
 * @author tkmagh4
 */ 
var GLOBAL_LOADING_COUNT	= 0;
var GLOBAL_UNLOADING_COUNT	= 0;
var GLOBAL_LOAD_COMPLETE = false;

function loadingNotification(active){
	try {
	    var trigger = (active == true && active !== null && active !== 'undefined');
	    
	    $('#loading').parent().fadeIn(200);
//	    $(".btn:contains('Search')").attr("id", "pulsate-regular");
//	    $(".btn:contains('Search')").attr("disable", "disable");
	    
	    if(active == 'ERROR'){
	    	GLOBAL_LOADING_COUNT	= 0;
	    	GLOBAL_UNLOADING_COUNT	= 0;
        	GLOBAL_LOAD_COMPLETE = false;
	    	$('#loading').css('width', '0%');
	    	$('#loading').attr('class', 'progress-bar progress-bar-danger');
	    	console.log('error loading');
	    }else{
	    	if(trigger){
	    		GLOBAL_LOADING_COUNT++;
		        $('#loading').css('width', ((GLOBAL_UNLOADING_COUNT / GLOBAL_LOADING_COUNT) * 100)+'%');
		        console.log('loading: '+GLOBAL_LOADING_COUNT+' | complete:'+GLOBAL_UNLOADING_COUNT);
		    }else{
		    	GLOBAL_UNLOADING_COUNT++;	
	        	$('#loading').css('width', ((GLOBAL_UNLOADING_COUNT / GLOBAL_LOADING_COUNT) * 100)+'%');
	        	
		        if(GLOBAL_LOADING_COUNT==GLOBAL_UNLOADING_COUNT){ 
		        	GLOBAL_LOADING_COUNT	= 0;
		        	GLOBAL_UNLOADING_COUNT	= 0;
		        	$('#loading').attr('class', 'progress-bar progress-bar-success');
		        	$('#loading').css('width', '100%');
		        }
		        //console.log('loading: '+GLOBAL_LOADING_COUNT+' | complete:'+GLOBAL_UNLOADING_COUNT);
		    }
	    }
	    if(GLOBAL_LOADING_COUNT==GLOBAL_UNLOADING_COUNT){
	    	$('#loading').parent().fadeOut(2000);
	    	console.log('loading complete!');
	    }else {
	    	console.log(((GLOBAL_UNLOADING_COUNT / GLOBAL_LOADING_COUNT) * 100)+'% (loading:'+GLOBAL_LOADING_COUNT+' |'+isJson(active)+'| unloading:' + GLOBAL_UNLOADING_COUNT+')');
		}
	} catch (e) {console.log('loading element missing, add progress tracker');}
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
