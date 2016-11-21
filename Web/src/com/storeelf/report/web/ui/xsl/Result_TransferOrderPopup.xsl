<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:variable name="rowcount" select="count(form/rows/row)" />
		<xsl:variable name="colcount" select="count(form/coldescs/coldesc)" />
		<xsl:variable name="isvalidselect" select="/form/@isvalidselect" />
		<div class="results">
			<div class="center-col"><xsl:value-of select="'Results'"/></div>
			<div class="utilityresultdata">
				<xsl:element name="table">
					<xsl:attribute name="class">utilityresultdatatable</xsl:attribute>
					<xsl:if test="$colcount &lt; 3">					
						<xsl:attribute name="style">width:35%</xsl:attribute>
					</xsl:if>
					<thead>
						<tr><th></th>
							<xsl:for-each select="form/coldescs/coldesc">
								<xsl:sort order="ascending" select="@colindex"
									data-type="number" />
								<th>
									<xsl:value-of select="@coldesc" />
								</th>
							</xsl:for-each>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each select="form/rows/row">
						<tr><td/>
							<xsl:variable name="pos" select="position() mod 2" />							
								<xsl:attribute name="class">
									<xsl:value-of select="concat('datarow-',$pos)" />
								</xsl:attribute>
								<xsl:for-each select="column">
									<xsl:sort order="ascending" select="@colindex"
										data-type="number" />
									<td>
										<xsl:value-of select="@value" />
									</td>
								</xsl:for-each>
							</tr>
						</xsl:for-each>
						<xsl:if test="$rowcount='0' and $isvalidselect='TRUE'">
							<tr>
								<td>
									<xsl:attribute name="colspan">
										<xsl:value-of select="$colcount" />
									</xsl:attribute>
									<xsl:value-of select="'No Data Selected'" />
								</td>
							</tr>
						</xsl:if>
					</tbody>
				</xsl:element>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>