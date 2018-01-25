<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
  xmlns:mcrxml="xalan://org.mycore.common.xml.MCRXMLFunctions"
  xmlns:xlink="http://www.w3.org/1999/xlink" exclude-result-prefixes="mods xlink">
  <xsl:import href="xslImport:solr-document:solr/searchfields-solr.xsl" />

  <xsl:template match="mycoreobject">
    <xsl:apply-imports />

    <field name="hasFiles">
      <xsl:value-of select="count(structure/derobjects/derobject)&gt;0" />
    </field>
    <field name="w">
      <xsl:value-of select="metadata/def.morphiloContainer/morphiloContainer/morphilo/w/text()[string-length(normalize-space(.))>0]" />
    </field>
    <field name="end">
      <xsl:value-of select="metadata/def.morphiloContainer/morphiloContainer/morphilo/w/@end[string-length(normalize-space(.))>0]" />
    </field>
    <field name="begin">
      <xsl:value-of select="metadata/def.morphiloContainer/morphiloContainer/morphilo/w/@begin[string-length(normalize-space(.))>0]" />
    </field>
    <field name="occurrence">
      <xsl:value-of select="metadata/def.morphiloContainer/morphiloContainer/morphilo/w/@occurrence[string-length(normalize-space(.))>0]" />
    </field>
    <field name="wordtype">
      <xsl:value-of select="metadata/def.morphiloContainer/morphiloContainer/morphilo/w/@wordtype[string-length(normalize-space(.))>0]" />
    </field>
  </xsl:template>

</xsl:stylesheet>