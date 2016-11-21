<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:variable name="rowcount" select="count(form/WSResult/ItemList/Item)" />
		<xsl:variable name="isvalidselect" select="/form/@isvalidselect" />
		<div class="results">
			<div class="center-col">
				<xsl:value-of select="'RESULTS'" />
			</div>
			<div class="resultdata">
				<table class="utilityresultdatatable">
					<thead>
						<tr>
							<th>
								Item ID
							</th>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each select="form/WSResult/ItemList/Item">
							<xsl:sort order="ascending" select="@colindex"
								data-type="number" />
							<xsl:variable name="pos" select="position() mod 2" />
							<tr>
								<xsl:attribute name="class">
									<xsl:value-of select="concat('datarow-',$pos)" />
								</xsl:attribute>
								<td>
									<xsl:value-of select="@ItemID" />
								</td>
							</tr>
						</xsl:for-each>
						<xsl:if test="$rowcount='0'">
							<tr>
								<td>
									<xsl:value-of select="'No Data Selected'" />
								</td>
							</tr>
						</xsl:if>
					</tbody>
				</table>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>