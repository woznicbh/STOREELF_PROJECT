/*---LEFT BAR ACCORDION----*/
$(function() {
	try{
	    $('#nav-accordion').dcAccordion({
	        eventType: 'click',
	        autoClose: true,
	        saveState: true,
	        disableLink: true,
	        speed: 'slow',
	        showCount: false,
	        autoExpand: true,
	//        cookie: 'dcjq-accordion-1',
	        classExpand: 'dcjq-current-parent'
	    });
	}catch(e){
		console.log('side-bar failed to load');
	}
});

var Script = function () {

//    sidebar dropdown menu auto scrolling

    jQuery('#sidebar .sub-menu > a').click(function () {
        var o = ($(this).offset());
        diff = 250 - o.top;
        if(diff>0)
            $("#sidebar").scrollTo("-="+Math.abs(diff),500);
        else
            $("#sidebar").scrollTo("+="+Math.abs(diff),500);        
    });

//    sidebar toggle
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
    
      $(function() {
    	  var wSize = $(window).width();   	    
    	    if (wSize <= 768) {
          		console.log("wsize= "+wSize);
          		GLOBAL_SIDEBAR_TOGGLE();
          	}
    	    
/*        function responsiveView() {
            var wSize = $(window).width();
            if (wSize <= 768) {
                $('#container').addClass('sidebar-close');
                $('#sidebar > ul').hide();
            	$('aside').removeClass('hidden');
            	$('#main-content').attr('class', 'col-md-10');
            }

            if (wSize > 768) {
                $('#container').removeClass('sidebar-close');
                $('#sidebar > ul').show();
            	$('aside').attr('class', $('aside').attr('class')+' hidden');
            	$('#main-content').attr('class', 'col-md-12');
            }
        }
        $(window).on('load', responsiveView);
        $(window).on('resize', responsiveView);*/
    	  
    });

    $('.fa-bars').click(GLOBAL_SIDEBAR_TOGGLE);
    
/*    $("#sidebar").mouseover(function(){
    	if($('#sidebar > ul').is(":visible") === false){
    		GLOBAL_SIDEBAR_TOGGLE();
    	}
    });
    $("#sidebar").mouseleave(function(){
    	if($('#sidebar > ul').is(":visible") === true){
    		GLOBAL_SIDEBAR_TOGGLE();
    	}
    });
    */

// custom scrollbar
try{
    $("#sidebar").niceScroll({styler:"fb",cursorcolor:"#e8403f", cursorwidth: '3', cursorborderradius: '10px', background: '#404040', spacebarenabled:false, cursorborder: ''});

    $("html").niceScroll({styler:"fb",cursorcolor:"#e8403f", cursorwidth: '6', cursorborderradius: '10px', background: '#404040', spacebarenabled:false,  cursorborder: '', zindex: '1000'});
}catch(e){
	console.log("jQuery plugin 'niceScroll' disabled");
}
// widget tools

    jQuery('.panel .tools .fa-chevron-down').click(function () {
        var el = jQuery(this).parents(".panel").children(".panel-body");
        if (jQuery(this).hasClass("fa-chevron-down")) {
            jQuery(this).removeClass("fa-chevron-down").addClass("fa-chevron-up");
            el.slideUp(200);
        } else {
            jQuery(this).removeClass("fa-chevron-up").addClass("fa-chevron-down");
            el.slideDown(200);
        }
    });

    jQuery('.panel .tools .fa-times').click(function () {
        jQuery(this).parents(".panel").parent().remove();
    });


//    tool tips
    $('.tooltips').tooltip();

//    popovers
    $('.popovers').popover();



// custom bar chart
    if ($(".custom-bar-chart")) {
        $(".bar").each(function () {
            var i = $(this).find(".value").html();
            $(this).find(".value").html("");
            $(this).find(".value").animate({
                height: i
            }, 2000);
        });
    }

//collapse sidebar    
//GLOBAL_SIDEBAR_TOGGLE();
}();