<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="html" indent="yes" />
	<xsl:variable name="apos">'</xsl:variable>
	<xsl:template match="/">
		<form class="searchform" method="get">
			<table>
				<xsl:for-each select="form/field">
					<tr>
						<td>
							<div class="searchfield">
								<xsl:choose>
									<xsl:when test="@required='TRUE'">
										<xsl:value-of select="concat(@desc,'*')" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@desc" />
									</xsl:otherwise>
								</xsl:choose>
							</div>
						</td>
						<td>
							<xsl:choose>
								<xsl:when test="@type='TEXT'">
									<input type="text" class="fieldinput" maxlength="10000">
										<xsl:attribute name="id">
											<xsl:value-of select="@id" />
										</xsl:attribute>
										<xsl:attribute name="name">
											<xsl:value-of select="@id" />
										</xsl:attribute>
										<xsl:attribute name="value">
											<xsl:value-of select="@value" />
										</xsl:attribute>
										<xsl:attribute name="onclick">
											<xsl:value-of select="concat('selectAll(',$apos,@id,$apos,')')" />
										</xsl:attribute>
										<xsl:if test="@required='TRUE'">
											<xsl:attribute name="required">
												<xsl:value-of select="@required" />
											</xsl:attribute>
										</xsl:if>
									</input>
								</xsl:when>
								<xsl:when test="@type='TEXTAREA'">
									<textarea class="fieldinput" rows="4" cols="50">
										<xsl:attribute name="id">
											<xsl:value-of select="@id" />
										</xsl:attribute>
										<xsl:attribute name="name">
											<xsl:value-of select="@id" />
										</xsl:attribute>
										<xsl:if test="@required='TRUE'">
											<xsl:attribute name="required">
												<xsl:value-of select="@required" />
											</xsl:attribute>
										</xsl:if>
										<xsl:choose>
											<xsl:when test="translate(@value,' ','')=''">
												<xsl:text>&#x0A;</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="@value" />
											</xsl:otherwise>
										</xsl:choose>
									</textarea>
								</xsl:when>
								<xsl:when test="@type='DATE'">
									<input type="text" class="fieldinput">
										<xsl:attribute name="id">
											<xsl:value-of select="@id" />
										</xsl:attribute>
										<xsl:attribute name="name">
											<xsl:value-of select="@id" />
										</xsl:attribute>
										<xsl:attribute name="value">
											<xsl:value-of select="@value" />
										</xsl:attribute>
										<xsl:if test="@required='TRUE'">
											<xsl:attribute name="required">
												<xsl:value-of select="@required" />
											</xsl:attribute>
										</xsl:if>
									</input>
									<script>
									  <![CDATA[   
									  $(function() {
									        $("]]>#<xsl:value-of select="@id" /><![CDATA[ ").datetimepicker({
									            showSecond: true,
									            showOn: "button",
									            buttonImage: "images/cal.gif",
									            buttonImageOnly: true,
									            timeFormat: "HH:mm:ss",
									            hourGrid: 4,
									            minuteGrid: 10,
									            secondGrid: 10,
									           	dateFormat: 'mm-dd-yy'
									        });
									        
									    });
									    ]]>
									</script>
								</xsl:when>
							</xsl:choose>
						</td>
					</tr>
				</xsl:for-each>
				<tr>
					<td></td>
					<td>
						<input type="submit" value="Search" class="submitbutton"/>
					</td>
				</tr>
			</table>
		</form>
	</xsl:template>
</xsl:stylesheet>