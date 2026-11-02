<?xml version="1.0" encoding="UTF-8"?>
<!--
  Групує путівки за типом (type): для кожного значення type створюється
  окремий елемент з таким самим іменем (holiday, weekend, ...), у який
  потрапляють усі путівки цього типу. Групування за методом Мюнха (xsl:key).
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes" encoding="UTF-8" standalone="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:key name="packages-by-type" match="package" use="type"/>

    <xsl:template match="/tourist_voucher">
        <tourist_voucher_by_type>
            <xsl:apply-templates select="title | traveling_agencies" mode="plain"/>
            <packages>
                <xsl:for-each select="packages/package[generate-id() = generate-id(key('packages-by-type', type)[1])]">
                    <xsl:sort select="type"/>
                    <xsl:element name="{type}">
                        <xsl:apply-templates select="key('packages-by-type', type)" mode="plain"/>
                    </xsl:element>
                </xsl:for-each>
            </packages>
        </tourist_voucher_by_type>
    </xsl:template>

    <!-- Копіювання елементів без зайвих оголошень просторів імен (xmlns:xsi) -->
    <xsl:template match="*" mode="plain">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="plain"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
