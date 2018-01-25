<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:encoder="xalan://java.net.URLEncoder"
  xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation" xmlns:str="http://exslt.org/strings" xmlns:mcr="xalan://org.mycore.common.xml.MCRXMLFunctions"
  xmlns:acl="xalan://org.mycore.access.MCRAccessManager" xmlns:mcrxsl="xalan://org.mycore.common.xml.MCRXMLFunctions" exclude-result-prefixes="i18n mods str mcr acl mcrxsl encoder">

  <!-- retain the original query and parameters, for attaching them to a url -->
  <xsl:variable name="query">
    <xsl:if test="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q'] != '*'">
      <xsl:value-of select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q']" />
    </xsl:if>
  </xsl:variable>

  <xsl:template match="/response/result|lst[@name='grouped']/lst[@name='returnId']" priority="10">
    <xsl:variable name="ResultPages">
      <xsl:if test="$hits &gt; 0">
        <xsl:call-template name="solr.Pagination">
          <xsl:with-param name="size" select="$rows" />
          <xsl:with-param name="currentpage" select="$currentPage" />
          <xsl:with-param name="totalpage" select="$totalPages" />
          <xsl:with-param name="class" select="'pagination-sm'" />
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="modeltype" select="doc/@objectType" />


        <div class="row result_head">
          <div class="col-xs-11 result_headline">
            <h2>
              <xsl:choose>
                <xsl:when test="$modeltype = 'corpmeta'">
                  <xsl:choose>
                    <xsl:when test="$hits=0">
                      <xsl:value-of select="i18n:translate('results.noProject')" />
                    </xsl:when>
                    <xsl:when test="$hits=1">
                      <xsl:value-of select="i18n:translate('results.oneProject')" />
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="concat(concat($hits, ' '), i18n:translate('results.nProjects'))" />
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:choose>
                    <xsl:when test="$hits=0">
                      <xsl:value-of select="i18n:translate('results.noObject')" />
                    </xsl:when>
                    <xsl:when test="$hits=1">
                      <xsl:value-of select="i18n:translate('results.oneObject')" />
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="concat(concat($hits, ' '), i18n:translate('results.nObjects'))" />
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:otherwise>
              </xsl:choose>
            </h2>
            <xsl:copy-of select="$ResultPages" />
          </div>
        </div>
      

<!-- Pagination & Trefferliste -->
    <div class="row result_body">
      <div class="cols-xs-1 col-sm-1 col-lg-1">
      </div>
      <div class="cols-xs-10 col-sm-8 col-lg-10 result_list">
        <div id="hit_list" class="list-group">
          <xsl:apply-templates select="doc|arr[@name='groups']/lst/str[@name='groupValue']" />
        </div>       
      </div>

    </div>
  </xsl:template>

  <xsl:template match="doc" priority="10" mode="resultList">
    <xsl:param name="hitNumberOnPage" select="count(preceding-sibling::*[name()=name(.)])+1" />
    <!--  
      Do not read MyCoRe object at this time
    -->
    <xsl:variable name="modeltype" select="@objectType" />
    <xsl:variable name="identifier" select="@id" />
    <xsl:variable name="mcrobj" select="." />
    <xsl:variable name="hitItemClass">
      <xsl:choose>
        <xsl:when test="$hitNumberOnPage mod 2 = 1">odd</xsl:when>
        <xsl:otherwise>even</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- generate browsing url -->
    <xsl:variable name="href" select="concat($proxyBaseURL,$HttpSession,$solrParams)" />
    <xsl:variable name="startPosition" select="$hitNumberOnPage - 1 + (($currentPage) -1) * $rows" />
    <xsl:variable name="hitHref">
      <xsl:value-of select="concat($href, '&amp;start=',$startPosition, '&amp;fl=id&amp;rows=1&amp;origrows=', $rows, '&amp;XSL.Style=browse')" />
    </xsl:variable>
    
<!-- hit entry -->

    <div class="row list-group-item {$hitItemClass}">
    <!-- Variablenbelegung fuer Klassifikationen -->

    <xsl:variable name="corpusPfad">
    	<xsl:if test="concat('classification:metadata:1:children:',./arr[@name='category.top']/str[contains(text(), 'corpus:')]) &gt; 0" >
    		<xsl:value-of select="concat('classification:metadata:1:children:',./arr[@name='category.top']/str[contains(text(), 'corpus:')])" />
    	</xsl:if>
    </xsl:variable>
    <xsl:variable name="corpusName"> 
    	<xsl:if test="$corpusPfad &gt; 0">
    		<xsl:value-of select="document($corpusPfad)//category/label[lang=$CurrentLang]/@text" />
    	</xsl:if>
    </xsl:variable>
      
      <div class="col-md-1">
        <xsl:value-of select="$hitNumberOnPage" />
      </div>

      <div class="col-md-9">

       <dl class="dl-horizontal">
        <xsl:choose>
          <xsl:when test="$modeltype = 'corpmeta'">
            <!-- display word -->
            <dt>
             <xsl:value-of select="i18n:translate('response.page.label.corpusname')" />
            </dt>
            <dd>
             <xsl:value-of select="str[@name='korpusname']" />
            </dd>
            <dt>
             <xsl:value-of select="i18n:translate('response.page.label.datefrom')" />
            </dt>
            <dd>
             <xsl:value-of select="str[@name='datefrom']" />
            </dd>
            <dt>
             <xsl:value-of select="i18n:translate('response.page.label.dateuntil')" />
            </dt>
            <dd>
             <xsl:value-of select="str[@name='dateuntil']" />
            </dd>
            <dt>
             <xsl:value-of select="i18n:translate('response.page.label.size')" />
            </dt>
            <dd>
             <xsl:value-of select="str[@name='NoW']" />
            </dd>
          </xsl:when>
          <xsl:otherwise>
            <!-- display word -->
            <dt>
             <xsl:value-of select="i18n:translate('response.page.label.word')" />
            </dt>
            <dd>
             <xsl:value-of select="./arr[@name='w']/str" />
            </dd>
            <!-- display corpus if available  -->
            <xsl:if test="$corpusName &gt; 0">
              <dt>
               <xsl:value-of select="i18n:translate('editor.search.corpus')" />
              </dt>
              <dd>
               <xsl:value-of select="$corpusName" />
              </dd>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <!-- display lemma 
        <dt>
         <xsl:value-of select="i18n:translate('response.page.label.lemma')" />
        </dt>
        <dd>
         <xsl:value-of select="./str[@name='lemma']" />
        </dd>
        -->
        <!-- display root 
        <dt>
         <xsl:value-of select="i18n:translate('response.page.label.root')" />
        </dt>
        <dd>
         <xsl:value-of select="./str[@name='wurzel']" />
        </dd>
        -->
        <!-- display number 
        <dt>
          <xsl:value-of select="i18n:translate('response.page.label.numWords')" />
        </dt>
        <dd>
          <xsl:value-of select="./int[@name='anzahlWort']" />
        </dd>
        -->
        
        
        <dt>

        </dt>
        <dd>

        </dd>


        <dt>
            <a href="{$hitHref}" style="color: #c40017;">
              <!-- show title if searchfield "title" is defined -->
              <xsl:attribute name="title"><xsl:value-of select="./str[@name='title']" /></xsl:attribute>
              <xsl:choose>
                <xsl:when test="./str[@name='search_result_link_text']">
                  <xsl:value-of select="./str[@name='search_result_link_text']" />
                </xsl:when>
                <xsl:when test="./str[@name='fileName']">
                  <xsl:value-of select="./str[@name='fileName']" />
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="i18n:translate('response.page.label.details')" />
                  <!--xsl:value-of select="$identifier" /-->
                  </xsl:otherwise>
                </xsl:choose>
              </a>
        </dt>
       </dl>
        <!-- The following fields will be viewed by administrators only -->
        <!-- hit title and link to git -->
      <dl class="dl-horizontal">
        <xsl:choose>
          <xsl:when test="$CurrentUser = 'administrator'">
        <dt>
         
        </dt>
        <dd>
          
        </dd>
        <dt>
        
        </dt>
        <dd>

        </dd>
        

<!-- hit parent -->
          <xsl:if test="./str[@name='parent']">
            <dt>
             <xsl:value-of select="i18n:translate('response.page.label.parent')" />
            </dt>
            <dd>
             <xsl:choose>
               <xsl:when test="./str[@name='parentLinkText']">
                 <xsl:variable name="linkTo" select="concat($WebApplicationBaseURL, 'receive/',./str[@name='parent'])" />
                 <a href="{$linkTo}">
                   <xsl:value-of select="./str[@name='parentLinkText']" />
                 </a>
               </xsl:when>
               <xsl:otherwise>
                 <xsl:call-template name="objectLink">
                   <xsl:with-param select="./str[@name='parent']" name="obj_id" />
                   </xsl:call-template>
                 </xsl:otherwise>
               </xsl:choose>
             </dd>
            </xsl:if>

<!-- creation date -->
          <dt>
           <xsl:value-of select="i18n:translate('response.page.label.created')" />
          </dt>
          <dd>
            <xsl:value-of select="./date[@name='created']" />
          </dd>

<!-- user who has created this document -->
          <dt>
            <xsl:value-of select="i18n:translate('response.page.label.by')" />
          </dt>
          <dd>
            <xsl:value-of select="./str[@name='createdby']" />
          </dd>
          </xsl:when>
         </xsl:choose>
        </dl>
      </div>

    </div><!-- end hit item -->
  </xsl:template>

</xsl:stylesheet>
