<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="apos">'</xsl:variable>
	<xsl:key name="keyval" match="form/rows/row"
		use="translate(concat(column[@colname='STATUS']/@value,column[@colname='SHIPNODE_KEY']/@value,column[@colname='PICKTICKET_NO']/@value,column[@colname='RELEASE_NO']/@value),' ','')" />
	<xsl:template match="/">
		<xsl:variable name="rowcount" select="count(form/rows/row)" />
		<xsl:variable name="colcount" select="'9'" />
		<xsl:variable name="isvalidselect" select="/form/@isvalidselect" />
		<div class="results">
			<div class="center-col"><xsl:value-of select="'Results'"/></div>
			<div class="utilityresultdata">
				<table class="utilityresultdatatable">
					<thead>
						<tr>
							<th>
							</th>
							<th>
								Order No
							</th>
							<th>
								Order Date
							</th>
							<th>
								Last Modified Time Stamp
							</th>
							<th>
								Release No
							</th>
							<th>
								Ship Node
							</th>
							<th>
								Pickticket No
							</th>
							<th>
								Status
							</th>
							<th>
								Ship To
							</th>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each
							select="/form/rows/row[count(. | key('keyval', translate(concat(column[@colname='STATUS']/@value,column[@colname='SHIPNODE_KEY']/@value,column[@colname='PICKTICKET_NO']/@value,column[@colname='RELEASE_NO']/@value),' ',''))[1]) = 1]">
							<xsl:variable name="keyvalue"
								select="translate(concat(column[@colname='STATUS']/@value,column[@colname='SHIPNODE_KEY']/@value,column[@colname='PICKTICKET_NO']/@value,column[@colname='RELEASE_NO']/@value),' ','')" />
							<xsl:variable name="imgid" select="concat('exp',$keyvalue)" />
							<xsl:variable name="pos" select="position() mod 2" />
							<tr>
								<xsl:attribute name="class">
									<xsl:value-of select="concat('datarow-',$pos)" />
								</xsl:attribute>
								<td>
									<div class="expimg">
										<img src="images/plus.gif">
											<xsl:attribute name="id">
											<xsl:value-of select="$imgid"></xsl:value-of>
										</xsl:attribute>
											<xsl:attribute name="onclick">
											<xsl:value-of
												select="concat('expand(',$apos,$keyvalue,$apos,',',$apos,$imgid,$apos,')')"></xsl:value-of>
										</xsl:attribute>
										</img>
									</div>
								</td>
								<td>
									<xsl:value-of select="column[@colname='SALES_ORDER_NO']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='ORDER_DATE']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='MODIFYTS']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='RELEASE_NO']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='SHIPNODE_KEY']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='PICKTICKET_NO']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='STATUS']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='SHIP_TO']/@value" />
								</td>
							</tr>
							<tr>
								<xsl:attribute name="class">
									<xsl:value-of select="concat('datarow-',$pos)" />
								</xsl:attribute>
								<td style="display:none">
									<xsl:attribute name="colspan">
										<xsl:value-of select="$colcount" />
									</xsl:attribute>
									<xsl:attribute name="id">
											<xsl:value-of select="$keyvalue" />
										</xsl:attribute>
									<table class="utilityresultdatatable" style="background-color:white;">
										<thead>
											<tr>
												<th>Item ID</th>
												<th>Line No</th>
												<th>Quantity</th>
											</tr>
										</thead>
										<tbody>
											<xsl:for-each
												select="/form/rows/row[translate(concat(column[@colname='STATUS']/@value,column[@colname='SHIPNODE_KEY']/@value,column[@colname='PICKTICKET_NO']/@value,column[@colname='RELEASE_NO']/@value),' ','')=$keyvalue]">
												<tr>
													<td>
														<xsl:value-of select="column[@colname='ITEM_ID']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='PRIME_LINE_NO']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='STATUS_QUANTITY']/@value" />
													</td>
												</tr>
											</xsl:for-each>
										</tbody>
									</table>
								</td>
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
				</table>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>