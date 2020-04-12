<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
<tns:xsltDemoResponse xmlns:tns="http://NodeFeatures_V1_IS" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://NodeFeatures_V1_IS NodeFeatures_V1_IS_InlineSchema1.xsd ">
  <output1>
  <xsl:for-each select="tns:xsltDemo/input1/cd">
        <tr>
          <td><xsl:value-of select="title"/></td>
          <td><xsl:value-of select="artist"/></td>
        </tr>
      </xsl:for-each>
  </output1>
</tns:xsltDemoResponse>
</xsl:template>
  
</xsl:stylesheet>
