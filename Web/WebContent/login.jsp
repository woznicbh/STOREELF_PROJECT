<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="com.storeelf.util.StringUtils"%>
<%@ page import="org.apache.shiro.SecurityUtils"%>
<%@ page import="java.util.*"%>
<%@ page import="com.storeelf.report.web.Constants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<%
	//Constants.STOREELF_SQL_REFRESH_JOBS.put(Constants.ID_LH_MYSQL_ROLES,"FORCE");
	//Constants.STOREELF_SQL_REFRESH_JOBS.put(Constants.ID_LH_MYSQL_ROLE_PERMS,"FORCE");

	if(SecurityUtils.getSubject() !=null && SecurityUtils.getSubject().isAuthenticated()){
		response.sendRedirect(request.getContextPath());
	}
%>
<head>

<!-- If mobile request then redirect to mobile site  -->
<%
	//set version now TODO: pull from properties file
	System.setProperty("STOREELF_VERSION"	, "v2");

	String ua = request.getHeader("User-Agent").toLowerCase();
	/* if (ua.matches("(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino).*")
			|| ua.substring(0, 4)
					.matches(
							"(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-")) {
		response.sendRedirect(request.getContextPath()
				+ "/mobile/mobile.jsp");
		return;
	} */
%><meta http-equiv="Content-Type"
	content="text/html; charset=ISO-8859-1" />
	<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v2_public_files/css/style.css"
	media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/v2_public_files/css/login.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/script/login.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/jquery-ui-custom.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/jMenu.jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/public/v2_public_files/js/jquery.gritter.min.js"></script>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<!--Slider-in icons-->
<script type="text/javascript">
	$(document).ready(function() {
		$(".username").focus(function() {
			$(".user-icon").css("left", "-48px");
		});
		$(".username").blur(function() {
			$(".user-icon").css("left", "0px");
		});

		$(".password").focus(function() {
			$(".pass-icon").css("left", "-48px");
		});
		$(".password").blur(function() {
			$(".pass-icon").css("left", "0px");
		});
	});
</script>
<title>STOREELF Login</title>
</head>
<body>
	<%Properties props = new Properties();
	props.load(getServletContext().getResourceAsStream("/WEB-INF/classes/shiro.ini"));
	String hideLogin = props.getProperty("ldapRealm.turnOffLogin");
	%>
	<div id="maincontainer">
		<!--WRAPPER-->
		<div id="wrapper">

			<!--SLIDE-IN ICONS-->
			<div class="user-icon"></div>
			<div class="pass-icon"></div>
			<!--END SLIDE-IN ICONS-->

			<!--LOGIN FORM-->
			<form id="loginform" class="login-form" method="post">
				<!--HEADER-->
				<div class="header">
					<!--TITLE-->
					<h1 class="logo">Log<span>Hub</span></h1>
					<!--END TITLE-->
				</div>
				<!--END HEADER-->

				<!--CONTENT-->
				<div class="content">
					
					<%if(hideLogin!=null && hideLogin.equals("false")){ %>
						<!--USERNAME-->
						<h2>User Name</h2>
						<input id="userid" name="user" class="input username" autofocus="autofocus" value=""
							onfocus="this.value=''" type="text" />
						<!--END USERNAME-->
						<!--PASSWORD-->
						<h2>Password</h2>
						<input name="pass" class="input password" value=""
							onfocus="this.value=''" type="password" />
						<!--END PASSWORD-->
						<script>
							$(function(){
							    document.getElementById('userid').focus();
							});
						</script>
					<%} else { %>
						<!-- Pass some id to skip login -->
						<input name="user" value="STOREELF" type="hidden" />
						<script>document.forms["loginform"].submit();</script>
					<%} %>
					<br/><br/>
					<!-- <div style="font-weight: normal; font-size:11px; text-align:center;"><em >*&nbsp;Use StoreElf Windows Login&nbsp;</em></div> -->
				</div>
				<!--END CONTENT-->

				<!--FOOTER-->
				<div class="footer">
					<!--LOGIN BUTTON-->
					<input name="submit" value="Login" class="button" type="submit" />
					<!--END LOGIN BUTTON-->
				</div>
				<!--END FOOTER-->
				<%
					String error = (String) request.getAttribute("shiroLoginFailure");
					if (!StringUtils.isVoid(error)) {
				%>
				<!--ERROR-->
				<div class="error">Login attempt was Unsuccessful.</div>
				<!--END ERROR-->
				<%
					}
				%>

			</form>
			<!--END LOGIN FORM-->

		</div>
		<!--END WRAPPER-->

	</div>
</body>
</html>

