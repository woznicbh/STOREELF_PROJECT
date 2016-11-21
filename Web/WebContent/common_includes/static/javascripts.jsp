<!-- Bootstrap core JavaScript ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/angular-strap.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/angular-strap.tpl.min.js"></script>

	<!-- js placed at the end of the document so the pages load faster -->
<!--<script src="<%=request.getContextPath()%>/public/v3/js/jquery.js"></script> -->
<!--<script src="<%=request.getContextPath()%>/public/v3/js/jquery-1.8.3.min.js"></script> -->
    <script class="include" type="text/javascript" src="<%=request.getContextPath()%>/public/v3/js/jquery.dcjqaccordion.2.7.js"></script>
    <script src="<%=request.getContextPath()%>/public/v3/js/jquery.scrollTo.min.js"></script>
<!--<script src="<%=request.getContextPath()%>/public/v3/js/jquery.nicescroll.js" type="text/javascript"></script> -->
<!--<script src="<%=request.getContextPath()%>/public/v3/js/jquery.sparkline.js" type="text/javascript"></script> -->
<!--<script src="<%=request.getContextPath()%>/public/v3/assets/jquery-easy-pie-chart/jquery.easy-pie-chart.js"></script> -->
<!--<script src="<%=request.getContextPath()%>/public/v3/js/owl.carousel.js" ></script> -->
    <script src="<%=request.getContextPath()%>/public/v3/js/jquery.customSelect.min.js" ></script>
    <script src="<%=request.getContextPath()%>/public/v3/js/respond.min.js" ></script>

    <!--common script for all pages-->
    <script src="<%=request.getContextPath()%>/public/v3/js/common-scripts.js"></script>

    <!--script for this page-->
<!-- <script src="<%=request.getContextPath()%>/public/v3/js/sparkline-chart.js"></script> -->
<!-- <script src="<%=request.getContextPath()%>/public/v3/js/easy-pie-chart.js"></script> -->
<!-- <script src="<%=request.getContextPath()%>/public/v3/js/count.js"></script> -->

  <script>
      //custom select box
      $(function(){
    	  try{	sidebar_isActive();	}catch(e){ console.log('error loading sidebar'); }
    	  try{	$('select.styled').customSelect(); }catch(e){ console.log("error loading 'customSelect' jquery plugin"); }
      });

  </script>
