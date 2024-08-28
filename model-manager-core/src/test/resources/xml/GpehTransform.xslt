<?xml version="1.0" encoding="UTF-8"?>

<xsl:transform version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.ericsson.com/PmEvents">

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<xsl:apply-templates select="/eventspecification" />
	</xsl:template>

	<xsl:template match="/eventspecification">
		<xsl:element name="eventspecification" namespace="http://www.ericsson.com/PmEvents">
			<xsl:apply-templates select="general" />
			
			<xsl:element name="extracolumns">
				<xsl:element name="extracolumn"><xsl:attribute name="type">TIMESTAMP</xsl:attribute>EVENT_TIME</xsl:element>
				<xsl:element name="extracolumn"><xsl:attribute name="type">BIGINT</xsl:attribute>UTC_OFFSET</xsl:element>
				<xsl:element name="extracolumn"><xsl:attribute name="type">TEXT</xsl:attribute>SUBNET_NAME</xsl:element>
				<xsl:element name="extracolumn"><xsl:attribute name="type">TEXT</xsl:attribute>NE_NAME</xsl:element>
				<xsl:element name="extracolumn"><xsl:attribute name="type">TEXT</xsl:attribute>EVENT_NAME</xsl:element>
				<xsl:element name="extracolumn"><xsl:attribute name="type">TEXT</xsl:attribute>EVENT_VERSION</xsl:element>
			</xsl:element>

			<xsl:apply-templates select="parameters" />

			<xsl:element name="structuretypes"/>

			<xsl:element name="events">
				<xsl:apply-templates select="externalevents" />
				<xsl:apply-templates select="internalevents" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="general">
		<xsl:variable name="spaceString" select="' '"/>
		<xsl:variable name="nullString" select="''"/>
		
  		<xsl:element name="general">
	  		<xsl:element name="docno"><xsl:value-of select="docno" /></xsl:element>
	  		<xsl:element name="revision"><xsl:value-of select="revision" /></xsl:element>
	  		<xsl:element name="date"><xsl:value-of select="date" /></xsl:element>
	  		<xsl:element name="author"><xsl:value-of select="author" /></xsl:element>
	  		<xsl:element name="increment"><xsl:value-of select="increment" /></xsl:element>
	  		<xsl:element name="ffv"><xsl:value-of select="translate(ffv, '&#x20;&#x9;&#xD;&#xA;', '')" /></xsl:element>
	  		<xsl:element name="fiv"><xsl:value-of select="revision" /></xsl:element>
	  		<xsl:element name="revisionhistory">
	  			<xsl:for-each select="revisionhistory/revisionhistoryelem">
	  				<xsl:element name="revisionhistoryelem">
				  		<xsl:element name="revision"><xsl:value-of select="revision" /></xsl:element>
	  					<xsl:element name="comment"><xsl:value-of select="comment" /></xsl:element>
	  				</xsl:element>
	  			</xsl:for-each>
	  		</xsl:element>
  		</xsl:element>
	</xsl:template>

	<xsl:template match="parameters">
		<xsl:element name="parametertypes">
			<xsl:apply-templates select="parameter" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="parameter">
		<xsl:element name="parametertype">
			<xsl:element name="name">
				<xsl:value-of select="paramname" />
			</xsl:element>
			
			<xsl:choose>
				<xsl:when test="paramtype='UINT'">
					<xsl:choose>
						<!-- We don't have to deal with the boundary case when the number of bits is 65
						and the valid flag is on because no field in the GPEH definition file has a size
						of 65 bits  -->
						<xsl:when test="numberofbits>64">
							<xsl:element name="type">BINARY</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:element name="type"><xsl:value-of select="paramtype" /></xsl:element>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="type"><xsl:value-of select="paramtype" /></xsl:element>
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:element name="description">
				<xsl:value-of select="paramdescription" />
			</xsl:element>

			<!--  GPEH counts valid bits on parameters, normalised format does not -->
			<xsl:element name="numberofbits">
				<xsl:choose>
					<xsl:when test="paramname='EVENT_PARAM_MESSAGE_CONTENTS'">
						-1
					</xsl:when>
					<xsl:when test="usevalidbit='Yes'">
						<xsl:value-of select="numberofbits - 1" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="numberofbits" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>

			<xsl:element name="usevalid">
				<xsl:value-of select="usevalidbit" />
			</xsl:element>
			<xsl:apply-templates select="range" />
			<xsl:apply-templates select="resolution" />
			<xsl:element name="unit">
				<xsl:value-of select="paramunit" />
			</xsl:element>
			<xsl:element name="comment">
				RNC Application Mode=<xsl:value-of select="rncapplicationmode" />.
				<xsl:value-of select="paramcomments" />
			</xsl:element>
			<xsl:apply-templates select="enumeration[1]" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="range">
		<xsl:element name="range">
			<xsl:element name="low">
				<xsl:value-of select="low" />
			</xsl:element>
			<xsl:element name="high">
				<xsl:value-of select="high" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="resolution">
		<xsl:element name="resolution">
			<xsl:value-of select="paramresolution" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="enumeration">
		<xsl:element name="enumeration">
			<xsl:comment>rncapplicationmode=<xsl:value-of select="@rncapplicationmode" /></xsl:comment>
			<xsl:for-each select="enumValue ">
				<xsl:element name="enum">
					<xsl:attribute name="internal"><xsl:value-of select="translate(., '-()= abcdefghijklmnopqrstuvwxyz', '_____ABCDEFGHIJKLMNOPQRSTUVWXYZ')" /></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="@value" /></xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template match="externalevents">
			<xsl:apply-templates select="event" />
	</xsl:template>

	<xsl:template match="internalevents">
			<xsl:apply-templates select="event" />
	</xsl:template>
	
	<xsl:template match="event">
		<xsl:element name="event">
			<xsl:element name="name">
				<xsl:value-of select="eventname" />
			</xsl:element>
			<xsl:element name="id">
				<xsl:value-of select="eventid" />
			</xsl:element>
			<xsl:element name="protocol">
				<xsl:value-of select="protocol" />
			</xsl:element>
			<xsl:element name="triggerdescription">
				<xsl:value-of select="triggerdescription" />
			</xsl:element>
			<xsl:element name="triggerevent">
				<xsl:value-of select="triggerevent" />
			</xsl:element>
			<xsl:element name="comment">
				<xsl:value-of select="eventcomments" />
			</xsl:element>
			<xsl:element name="osscomment">
				<xsl:value-of select="osscomments" />
			</xsl:element>
			<xsl:element name="elements">
				<xsl:apply-templates select="fileformat" />
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="fileformat">
		<xsl:for-each select="param">
			<xsl:element name="param"><xsl:value-of select="." /></xsl:element>
		</xsl:for-each>
	</xsl:template>
</xsl:transform>