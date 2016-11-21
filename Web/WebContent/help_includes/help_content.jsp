<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
	ANGULARJS_APP = angular.module('HelpDetailsApp', []);
	
 	
</script>
<div ng-app="HelpDetailsApp">
	<div ng-controller="HelpDetailsController">
<div>	
<%String pageName = "/help_includes/contents/" + request.getParameter("pageName") + ".jsp";%>

<jsp:include page="<%=pageName%>"/>
</div>
</div>
</div>