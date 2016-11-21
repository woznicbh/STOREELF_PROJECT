<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="com.storeelf.report.web.model.SQLModel"%>
<%@page import="com.storeelf.util.StringUtils"%>
<%@page import="com.storeelf.report.web.init.ReportActivator"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.storeelf.report.web.*"%>
<%@ page import="com.storeelf.report.web.comparator.*"%>
<%@ page import="com.storeelf.util.SQLUtils"%>
<head>
<style type="text /css">
.notifications {
	background: #FAE9E9;
	margin-bottom: 10px;
	margin-top: 5px;
	border: 0px solid #000;
}

.notification {
	background: #FF00FF;
	margin-bottom: 10px;
	margin-top: 5px;
	color: #4678BE;
	margin: 10px;
	border-width: 0px;
	background: transparent;
	width: 90%;
	font: normal 0.8em Arial, Geneva, Helvetica, sans-serif;
}
</style>
<%
	List<String> lSQLIdlist = new ArrayList<String>(
			Constants.SQL_MAP.keySet());
	Iterator<String> it = lSQLIdlist.iterator();
%>
<script>
    function refreshResultSet() {


        $.gritter
                .add({
                    title : 'SQL Refresh triggered',
                    text : "The refresh for SQL ID <b>" + $("#sqlidlist").val()
                            + "</b> has been triggered succesfully",
                    // (string | optional) the image to display on the left
                    image : 'http://www.iconsdb.com/icons/preview/moth-green/check-mark-11-xxl.png',
                    // (bool | optional) if you want it to fade out on its own or just sit there
                    sticky : false,
                    // (int | optional) the time you want it to be alive for before fading out (milliseconds)
                    time : 8000
                });
        var parameters = {
            "sqlid" : $("#sqlidlist").val(),




            "type" : "refreshSql"
        };

        $.post( "refreshSQL.jsp", parameters)


                .done(function( data ) {

                //DO SOMETHING WITH THIS DATA
                $( ".messagearea" ).html( data );

                    $.gritter.add({

                        title : 'SQL Refresh completed',
                        text : "The SQL ID <b>"
                                + $("#sqlidlist").val()
                                + "</b> has been refreshed succesfully",
                        // (string | optional) the image to display on the left
                        image : 'http://www.iconsdb.com/icons/preview/moth-green/check-mark-11-xxl.png',
                        // (bool | optional) if you want it to fade out on its own or just sit there
                        sticky : false,
                        // (int | optional) the time you want it to be alive for before fading out (milliseconds)
                        time : 8000


                    });
                });
    }

    function showSQL(){

        $.gritter
        .add({
            title : 'Show SQL Triggered',
            text : "Show SQL <b>" + $("#sqlidlist").val()
                    + "</b> has been triggered succesfully",
            // (string | optional) the image to display on the left
            image : 'http://www.iconsdb.com/icons/preview/moth-green/check-mark-11-xxl.png',
            // (bool | optional) if you want it to fade out on its own or just sit there
            sticky : false,
            // (int | optional) the time you want it to be alive for before fading out (milliseconds)
            time : 500
        });

        var parameters = {
          "sqlid" : $("#sqlidlist").val(),
          "type" : "showSql"
          };
            $.post( "refreshSQLframe.jsp", parameters)
                .done(function( data ) {

                //DO SOMETHING WITH THIS DATA
                $( ".messagearea" ).html( data );

                    $.gritter.add({
                        title : 'SQL Statement Shown',
                        text : "The SQL ID <b>"
                                + $("#sqlidlist").val()
                                + "</b> SQL statement is shown below",
                        // (string | optional) the image to display on the left
                       // image : 'http://www.iconsdb.com/icons/preview/moth-green/check-mark-11-xxl.png',
                        // (bool | optional) if you want it to fade out on its own or just sit there
                        sticky : false,
                        // (int | optional) the time you want it to be alive for before fading out (milliseconds)
                        time : 1500
                    });
                });

    }
</script>
<script>
$( document ).ready(function() {
    $('#headerimg').remove();
    $('#jMenu').remove();
    $('#footer').remove();
});
</script>
<jsp:include page="common_includes/static/head.jsp" />
</head>

<body>
	<div id="maincontainer">

		<div id="mainbody">
			<div class="callout">
				<div id="notification" class="notifications">
					<p id="notitext" class="notification"></p>
				</div>

				<%
                    String sqlidval = request.getParameter("sqlid");
                    String typeid = request.getParameter("type");
                    String sql = null;
                    if (!StringUtils.isVoid(typeid)) {
                        if (typeid.equals("refreshSql")){
                            if(!StringUtils.isVoid(sqlidval)){
                                //SQLModel mdl = SQLModel.getModelObject(sqlidval);
                                //mdl.refreshResultSet();
                            	if(Constants.STOREELF_SQL_REFRESH_JOBS.get(sqlidval).equals("RUNNING")==false || SQLUtils.getThreadStatus(sqlidval)==null){
                    				Constants.STOREELF_SQL_REFRESH_JOBS.put(sqlidval, "FORCE");
                    			}
                            }
                        }
                    }

                %>
				<div class="searchform">
					<input type="text" value="Select SQL Id:" readonly="readonly"
						class="fieldlabel" />
					<select class="fieldinput" id="sqlidlist" size="1">
						<%
							while (it.hasNext()) {
								String sqlid = it.next();
						%>
						<option value="<%=sqlid%>"><%=sqlid%></option>
						<%
							}
						                        %>
                    </select>
                    <input class="button" type="button" value="Submit" onclick="refreshResultSet()" />
                    <input class="button" type="button" value="Show SQL" onclick="showSQL()" />
                </div>
            </div>
            <div class="messagearea">
                        <%
                        out.print(sql);
                        %>

            </div>
        </div>

        <jsp:include page="common_includes/static/footer.jsp" />

	</div>
</body>
</html>
