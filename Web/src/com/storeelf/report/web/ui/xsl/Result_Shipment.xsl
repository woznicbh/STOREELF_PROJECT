<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="apos">'</xsl:variable>
	<xsl:key name="keyval" match="form/rows/row"
		use="translate(column[@colname='SHIPMENT_NO']/@value,' ','')" />
	<xsl:template match="/">
		<xsl:variable name="rowcount" select="count(form/rows/row)" />
		<xsl:variable name="colcount" select="'10'" />
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
								Shipment No
							</th>
							<th>
								Order No
							</th>
							<th>
								Actual Shipment Date
							</th>
							<th>
								Expected Shipment Date
							</th>
							<th>
								Pickticket No
							</th>
							<th>
								Carrier
							</th>
							<th>
								Service level
							</th>
							<th>
								Ship Node
							</th>
							<th>
								Status
							</th>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each
							select="/form/rows/row[count(. | key('keyval', translate(column[@colname='SHIPMENT_NO']/@value,' ',''))[1]) = 1]">
							<xsl:variable name="keyvalue"
								select="translate(column[@colname='SHIPMENT_NO']/@value,' ','')" />
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
									<xsl:value-of select="column[@colname='SHIPMENT_NO']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='ORDER_NO']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='ACTUAL_SHIPMENT_DATE']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='EXPECTED_SHIPMENT_DATE']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='PICKTICKET_NO']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='CARRIER_SERVICE_CODE']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='SCAC']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='SHIPNODE_KEY']/@value" />
								</td>
								<td>
									<xsl:value-of select="column[@colname='STATUS']/@value" />
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
									<table class="utilityresultdatatable" Style="background-color:white;">
										<thead>
											<tr>
												<th>Carton No</th>
												<th>Tracking No</th>
												<th>Release No</th>
												<th>Order Line No</th>
												<th>Item ID</th>
												<th>Item Description</th>
												<th>Quantity</th>
												<th>Weight</th>
											</tr>
										</thead>
										<tbody>
											<xsl:for-each
												select="/form/rows/row[translate(column[@colname='SHIPMENT_NO']/@value,' ','')=$keyvalue]">
												<tr>
													<td>
														<xsl:value-of select="column[@colname='CONTAINER_SCM']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='TRACKING_NO']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='RELEASE_NO']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='PRIME_LINE_NO']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='ITEM_ID']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='ITEM_DESCRIPTION']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='QUANTITY']/@value" />
													</td>
													<td>
														<xsl:value-of select="column[@colname='CONTAINER_GROSS_WEIGHT']/@value" />
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