<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:key name="shipnode"
		match="form/sql[@App='OMS']/rows/row/column[@colname='SHIPNODE_KEY']"
		use="@value" />
	<xsl:key name="givshipnode"
		match="form/sql[@App='GIV']/rows/row/column[@colname='SHIPNODE_KEY']"
		use="@value" />
	<xsl:template match="/">
		<xsl:variable name="rowcount" select="count(//row)" />
		<xsl:variable name="colcount" select="'7'" />
		<xsl:variable name="isvalidselect" select="form/sql[@App='OMS']/@isvalidselect" />

		<div class="results">
			<div class="center-col"><xsl:value-of select="'Results'" /></div>
			<xsl:variable name="itemid"
				select="form/sql[@App='OMS']/rows/row/column[@colname='ITEM_ID']/@value" />
			<xsl:variable name="description"
				select="form/sql[@App='OMS']/rows/row/column[@colname='SHORT_DESCRIPTION']/@value" />
			<xsl:variable name="totsupply"
				select="sum(form/sql[@App='OMS']/rows/row[count(column[@colname='INV_TYPE' and @value='SUPPLY']) = 1 and column[@colname='QUANTITY']/@value &gt; 0]/column[@colname='QUANTITY']/@value)" />
			<xsl:variable name="totdemand"
				select="sum(form/sql[@App='OMS']/rows/row[count(column[@colname='INV_TYPE' and @value='DEMAND']) = 1]/column[@colname='QUANTITY']/@value)" />
			<xsl:variable name="availtoec" select="$totsupply - $totdemand" />
			<div class="utilityresultdata">
				<table class="utilityresultdatatable">
					<thead>
						<tr>
							<th>Item ID</th>
							<th>Description</th>
							<th>Total Supply</th>
							<th>Total Demand</th>
							<th>Available To EComm</th>
						</tr>
					</thead>
					<tbody>
						<xsl:choose>
							<xsl:when test="$rowcount &gt; 0">
								<tr>
									<td>
										<xsl:value-of select="$itemid" />
									</td>
									<td>
										<xsl:value-of select="$description" />
									</td>
									<td>
										<xsl:value-of select="$totsupply" />
									</td>
									<td>
										<xsl:value-of select="$totdemand" />
									</td>
									<xsl:call-template name="COLDATATemplate">
										<xsl:with-param name="data" select="$availtoec" />
									</xsl:call-template>
								</tr>
								<tr>
									<td colspan="5">
										<xsl:call-template name="OMSTemplate" />
									</td>
								</tr>
								<tr>
									<td colspan="5">
										<xsl:call-template name="WMOSTemplate" />
									</td>
								</tr>
								<tr>
									<td colspan="5">
										<xsl:call-template name="FSTemplate" />
									</td>
								</tr>
							</xsl:when>
							<xsl:when test="$rowcount='0' and $isvalidselect='TRUE'">
								<tr>
									<td>
										<xsl:attribute name="colspan">
                                        <xsl:value-of
											select="$colcount" />
                                    </xsl:attribute>
										<xsl:value-of select="'No Data Selected'" />
									</td>
								</tr>
							</xsl:when>
						</xsl:choose>
					</tbody>
				</table>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="OMSTemplate">
		<table
			style="width:50%; margin-left:auto; margin-right:auto; background-color:white;"
			class="utilityresultdatatable">
			<thead>
				<tr>
					<th style="background-color: #8E92D0;font-size:15px;" colspan="4">OMS
					</th>
				</tr>
				<tr>
					<th>Ship Node</th>
					<th>Supply</th>
					<th>Demand</th>
					<th>Available To Fulfill</th>
				</tr>
			</thead>
			<tbody>
				<!-- EFC Summary Row -->
				<xsl:call-template name="OMSROWHeader">
					<xsl:with-param name="headNodekey" select="'EFCS'" />
					<xsl:with-param name="pos" select="'0'" />
					<xsl:with-param name="rowid" select="'EFC'" />
					<xsl:with-param name="expand" select="'1'" />
					<xsl:with-param name="headsup">
						<xsl:value-of
							select="sum(/form/sql[@App='OMS']/rows/row[(count(column[@colname='INV_TYPE' and @value='SUPPLY']) = 1) and (count(column[@colname='SHIPNODE_KEY' and @value[starts-with(.,'EFC')]]) = 1)]/column[@colname='QUANTITY']/@value)" />
					</xsl:with-param>
					<xsl:with-param name="headdem">
						<xsl:value-of
							select="sum(/form/sql[@App='OMS']/rows/row[(count(column[@colname='INV_TYPE' and @value='DEMAND']) = 1) and (count(column[@colname='SHIPNODE_KEY' and @value[starts-with(.,'EFC')]]) = 1)]/column[@colname='QUANTITY']/@value)" />
					</xsl:with-param>
					<xsl:with-param name="RowCount"
						select="count(/form/sql[@App='OMS']/rows/row[(count(column[@colname='SHIPNODE_KEY' and @value[starts-with(.,'EFC')]]) = 1)])" />
				</xsl:call-template>

				<!-- EFC Detail Row -->
				<xsl:for-each
					select="form/sql[@App='OMS']/rows/row/column[@colname='SHIPNODE_KEY' and @value[starts-with(.,'EFC')]][count(. | key('shipnode', @value)[1]) = 1]">
					<xsl:variable name="pos" select="position() mod 2"></xsl:variable>

					<xsl:call-template name="OMSROW">
						<xsl:with-param name="shipnodekey" select="@value" />
						<xsl:with-param name="rowid" select="concat('invEFC',position())" />
						<xsl:with-param name="displayStyle" select="''" />
						<xsl:with-param name="rowclass" select="concat('efcrow-',$pos)" />
						
					</xsl:call-template>

				</xsl:for-each>

				<!-- RDC Summary Row -->
				<xsl:call-template name="OMSROWHeader">
					<xsl:with-param name="headNodekey" select="'RDCS'" />
					<xsl:with-param name="pos" select="'0'" />
					<xsl:with-param name="rowid" select="'RDC'" />
					<xsl:with-param name="headsup">
						<xsl:value-of
							select="sum(/form/sql[@App='OMS']/rows/row[(count(column[@colname='INV_TYPE' and @value='SUPPLY']) = 1) and (count(column[@colname='SHIPNODE_KEY' and @value[starts-with(.,'RDC')]]) = 1)]/column[@colname='QUANTITY']/@value)" />
					</xsl:with-param>
					<xsl:with-param name="headdem">
						<xsl:value-of
							select="sum(/form/sql[@App='OMS']/rows/row[(count(column[@colname='INV_TYPE' and @value='DEMAND']) = 1) and (count(column[@colname='SHIPNODE_KEY' and @value[starts-with(.,'RDC')]]) = 1)]/column[@colname='QUANTITY']/@value)" />
					</xsl:with-param>
					<xsl:with-param name="RowCount"
						select="count(/form/sql[@App='OMS']/rows/row[(count(column[@colname='SHIPNODE_KEY' and @value[starts-with(.,'RDC')]]) = 1)])" />
				</xsl:call-template>
				<!-- RDC Detail Row -->
				<xsl:for-each
					select="form/sql[@App='OMS']/rows/row/column[@colname='SHIPNODE_KEY' and @value[starts-with(.,'RDC')]][count(. | key('shipnode', @value)[1]) = 1]">
					<xsl:variable name="pos" select="position() mod 2"></xsl:variable>
					<xsl:call-template name="OMSROW">
						<xsl:with-param name="shipnodekey" select="@value" />
						<xsl:with-param name="rowid" select="concat('invRDC',position())" />
						<xsl:with-param name="displayStyle" select="'display:none'" />
						<xsl:with-param name="rowclass" select="concat('rdcrow-',$pos)" />
					</xsl:call-template>
				</xsl:for-each>

				<!-- STORE Summary Row -->
				<xsl:call-template name="OMSROWHeader">
					<xsl:with-param name="headNodekey" select="'STORES'" />
					<xsl:with-param name="pos" select="'0'" />
					<xsl:with-param name="rowid" select="'store'" />
					<xsl:with-param name="headsup">
						<xsl:value-of
							select="sum(/form/sql[@App='GIV']/rows/row[(count(column[@colname='INV_TYPE' and @value='SUPPLY']) = 1) and (count(column[@colname='SHIPNODE_KEY' and contains('0123456789', substring(@value,1,1))]) = 1)]/column[@colname='QUANTITY']/@value)" />
					</xsl:with-param>
					<xsl:with-param name="headdem">
						<xsl:value-of
							select="sum(/form/sql[@App='GIV']/rows/row[(count(column[@colname='INV_TYPE' and @value='DEMAND']) = 1) and (count(column[@colname='SHIPNODE_KEY'  and contains('0123456789', substring(@value,1,1))]) = 1)]/column[@colname='QUANTITY']/@value)" />
					</xsl:with-param>
					<xsl:with-param name="RowCount"
						select="count(/form/sql[@App='GIV']/rows/row[(count(column[@colname='SHIPNODE_KEY' and contains('0123456789', substring(@value,1,1))]) = 1)])" />
				</xsl:call-template>
				<!-- STORE Detail Row -->
				<xsl:for-each
					select="form/sql[@App='GIV']/rows/row/column[@colname='SHIPNODE_KEY' and contains('0123456789', substring(@value,1,1))][count(. | key('givshipnode', @value)[1]) = 1]">
					<xsl:variable name="pos" select="position() mod 2"></xsl:variable>
					<xsl:call-template name="GIVROW">
						<xsl:with-param name="shipnodekey" select="@value" />
						<xsl:with-param name="rowid"
							select="concat('invstore',position())" />
						<xsl:with-param name="rowclass" select="concat('storerow-',$pos)" />
						<xsl:with-param name="displayStyle" select="'display:none'" />
					</xsl:call-template>
				</xsl:for-each>
			</tbody>
		</table>
	</xsl:template>

	<!-- OMS Header ROW Template -->
	<xsl:template name="OMSROWHeader">
		<xsl:param name="headNodekey" />
		<xsl:param name="pos" />
		<xsl:param name="rowid" />
		<xsl:param name="headsup" />
		<xsl:param name="headdem" />
		<xsl:param name="RowCount" />
		<xsl:param name="expand" />

		<xsl:variable name="availtoecommnod" select="$headsup - $headdem" />

		<tr>
			<xsl:attribute name="id"><xsl:value-of select="$rowid" /></xsl:attribute>
			<xsl:attribute name="class"><xsl:value-of
				select="concat('datarow-',$pos)" /></xsl:attribute>

			<xsl:choose>


				<xsl:when test="$RowCount &gt; '0'">
					<td align="left">
						<xsl:variable name="imgId" select="concat('inv',$rowid)" />
						<xsl:variable name="quot">&#39;</xsl:variable>

						<img style="padding-right:5px;">
							<xsl:attribute name="id"><xsl:value-of
								select="$imgId" /></xsl:attribute>
							<xsl:attribute name="onclick"><xsl:value-of
								select="concat( concat( concat( concat('expandInvUtilityStore(',$quot),$imgId),$quot),')')" />
							</xsl:attribute>
							<xsl:attribute name="src">
								<xsl:choose>
									<xsl:when test="$expand='1'">images/minus.gif</xsl:when>
									<xsl:otherwise>images/plus.gif</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</img>
						<xsl:value-of select="$headNodekey" />
					</td>

					<xsl:call-template name="COLDATATemplate">
						<xsl:with-param name="data" select="$headsup" />
					</xsl:call-template>
					<xsl:call-template name="COLDATATemplate">
						<xsl:with-param name="data" select="$headdem" />
					</xsl:call-template>
					<xsl:call-template name="COLDATATemplate">
						<xsl:with-param name="data" select="$availtoecommnod" />
					</xsl:call-template>

				</xsl:when>
				<xsl:otherwise>
					<td align="left">
						<xsl:value-of select="$headNodekey" />
					</td>
					<td colspan="3" align="center">
						<xsl:value-of select="'No Data Found'" />
					</td>
				</xsl:otherwise>
			</xsl:choose>
		</tr>
	</xsl:template>


	<!-- OMS ROW Template -->
	<xsl:template name="OMSROW">
		<xsl:param name="shipnodekey" />
		<xsl:param name="rowid" />
		<xsl:param name="displayStyle" />
		<xsl:param name="rowclass" />

		<xsl:if test="not(translate($shipnodekey,' ','')='')">
			<xsl:variable name="sup"
				select="sum(/form/sql[@App='OMS']/rows/row[(count(column[@colname='INV_TYPE' and @value='SUPPLY']) = 1) and (count(column[@colname='SHIPNODE_KEY' and @value=$shipnodekey]) = 1)]/column[@colname='QUANTITY']/@value)" />
			<xsl:variable name="dem"
				select="sum(/form/sql[@App='OMS']/rows/row[(count(column[@colname='INV_TYPE' and @value='DEMAND']) = 1) and (count(column[@colname='SHIPNODE_KEY' and @value=$shipnodekey]) = 1)]/column[@colname='QUANTITY']/@value)" />
			<xsl:variable name="availtoecommnod" select="$sup - $dem" />
			<tr>
				<xsl:attribute name="id">
                    <xsl:value-of select="$rowid" />
                </xsl:attribute>
				<xsl:attribute name="style">
                    <xsl:value-of select="$displayStyle" />
                </xsl:attribute>
				<xsl:attribute name="class">
				<xsl:value-of select="$rowclass" />
            	</xsl:attribute>

				<td>
					<xsl:value-of select="$shipnodekey" />
				</td>

				<xsl:call-template name="COLDATATemplate">
					<xsl:with-param name="data" select="$sup" />
				</xsl:call-template>
				<xsl:call-template name="COLDATATemplate">
					<xsl:with-param name="data" select="$dem" />
				</xsl:call-template>
				<xsl:call-template name="COLDATATemplate">
					<xsl:with-param name="data" select="$availtoecommnod" />
				</xsl:call-template>

			</tr>
		</xsl:if>
	</xsl:template>


	<!-- GIV STORE DATA ROW TEMPLATE -->

	<xsl:template name="GIVROW">
		<xsl:param name="shipnodekey" />
		<xsl:param name="rowid" />
		<xsl:param name="displayStyle" />
		<xsl:param name="rowclass" />

		<xsl:if test="not(translate($shipnodekey,' ','')='')">
			<xsl:variable name="sup"
				select="sum(/form/sql[@App='GIV']/rows/row[(count(column[@colname='INV_TYPE' and @value='SUPPLY']) = 1) and (count(column[@colname='SHIPNODE_KEY' and @value=$shipnodekey]) = 1)]/column[@colname='QUANTITY']/@value)" />
			<xsl:variable name="dem"
				select="sum(/form/sql[@App='GIV']/rows/row[(count(column[@colname='INV_TYPE' and @value='DEMAND']) = 1) and (count(column[@colname='SHIPNODE_KEY' and @value=$shipnodekey]) = 1)]/column[@colname='QUANTITY']/@value)" />
			<xsl:variable name="availtoecommnod" select="$sup - $dem" />
			<tr>
				<xsl:attribute name="id">
                    <xsl:value-of select="$rowid" />
                </xsl:attribute>
				<xsl:attribute name="style">
                    <xsl:value-of select="$displayStyle" />
                </xsl:attribute>
				<xsl:attribute name="class">
				<xsl:value-of select="$rowclass" />
            	</xsl:attribute>

				<td>
					<xsl:value-of select="$shipnodekey" />
				</td>

				<xsl:call-template name="COLDATATemplate">
					<xsl:with-param name="data" select="$sup" />
				</xsl:call-template>
				<xsl:call-template name="COLDATATemplate">
					<xsl:with-param name="data" select="$dem" />
				</xsl:call-template>
				<xsl:call-template name="COLDATATemplate">
					<xsl:with-param name="data" select="$availtoecommnod" />
				</xsl:call-template>

			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Data or Zero -->
	<xsl:template name="COLDATATemplate">
		<xsl:param name="data" />
		<td>
			<xsl:choose>
				<xsl:when test="$data &gt;= '0'">
					<xsl:value-of select="$data" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'0'" />
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>

	<!-- WMOS Template -->
	<xsl:template name="WMOSTemplate">
		<table class="utilityresultdatatable" style="background-color:white;">
			<thead>
				<tr>
					<th style="background-color: #8E92D0;font-size:15px;" colspan="10">
						<xsl:value-of select="'WMOS INVENTORY'" />
					</th>
				</tr>
				<tr>
					<th>EFC</th>
					<xsl:for-each select="/form/sql[@App='EFC']">
						<xsl:if test="position()=1">
							<xsl:for-each select="coldescs/coldesc">
								<xsl:sort data-type="number" select="@colindex" />
								<th>
									<xsl:value-of select="@coldesc" />
								</th>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="/form/sql[@App='EFC']">
					<xsl:variable name="pos" select="position() mod 2" />
					<xsl:variable name="EFC" select="@Node" />
					<xsl:for-each select="rows/row">
						<tr>
							<xsl:attribute name="class">
                                <xsl:value-of select="concat('datarow-',$pos)" />
                            </xsl:attribute>
							<th>
								<xsl:value-of select="$EFC" />
							</th>
							<xsl:for-each select="column">
								<xsl:sort data-type="number" select="@colindex" />
								<td>
									<xsl:value-of select="@value" />
								</td>
							</xsl:for-each>
						</tr>
					</xsl:for-each>
				</xsl:for-each>
			</tbody>
		</table>
	</xsl:template>
	<!-- Fashion Sales Template -->
	<xsl:template name="FSTemplate">
		<table class="utilityresultdatatable"
			style="width:50%; margin-left:auto; margin-right:auto; background-color:white;">
			<thead>
				<tr>
					<th colspan="4" style="background-color: #8E92D0;font-size:15px;">
						<xsl:value-of select="'FASHION SALES'" />
					</th>
				</tr>
				<tr>
					<xsl:for-each select="/form/sql[@App='FS']">
						<xsl:if test="position()=1">
							<xsl:for-each select="coldescs/coldesc">
								<xsl:sort data-type="number" select="@colindex" />
								<th>
									<xsl:value-of select="@coldesc" />
								</th>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="/form/sql[@App='FS']">
					<xsl:for-each select="rows/row">
						<xsl:variable name="pos" select="position() mod 2" />
						<tr>
							<xsl:attribute name="class">
                                <xsl:value-of select="concat('datarow-',$pos)" />
                            </xsl:attribute>
							<xsl:for-each select="column">
								<xsl:sort data-type="number" select="@colindex" />
								<td>
									<xsl:value-of select="@value" />
								</td>
							</xsl:for-each>
						</tr>
					</xsl:for-each>
				</xsl:for-each>
			</tbody>
		</table>
	</xsl:template>
</xsl:stylesheet>