<?xml version="1.0" encoding="utf-8"?>
  <!-- ============================================== -->
  <!-- $Revision$ $Date$ -->
  <!-- ============================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:basket="xalan://org.mycore.frontend.basket.MCRBasketManager" xmlns:mcr="http://www.mycore.org/" xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
  xmlns:mcrver="xalan://org.mycore.common.MCRCoreVersion"
  xmlns:mcrxsl="xalan://org.mycore.common.xml.MCRXMLFunctions" exclude-result-prefixes="xlink basket mcr mcrver mcrxsl i18n">

  <xsl:import href="resource:xsl/layout/common-layout.xsl" />

  <xsl:output method="html" doctype-system="about:legacy-compat" indent="yes" omit-xml-declaration="yes" media-type="text/html"
    version="5" />
  <xsl:strip-space elements="*" />
  <!--xsl:param name="MIR.CustomLayout.JS" select="''" /-->
  <!-- Various versions -->
  <xsl:variable name="bootstrap.version" select="'3.3.6'" />
  <xsl:variable name="bootswatch.version" select="$bootstrap.version" />
  <xsl:variable name="fontawesome.version" select="'4.5.0'" />
  <xsl:variable name="jquery.version" select="'1.11.3'" />
  <xsl:variable name="jquery.migrate.version" select="'1.2.1'" />
  <!-- End of various versions -->
  <xsl:variable name="PageTitle" select="/*/@title" />

  <xsl:template match="/site">
    <html lang="{$CurrentLang}" class="no-js">
      <head>
        <meta charset="utf-8" />
        <title>
          <xsl:value-of select="$PageTitle" />
        </title>
        <xsl:comment>
          Mobile viewport optimization
        </xsl:comment>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <link href="{$WebApplicationBaseURL}css/fileupload.css" rel="stylesheet" />
        <link href="//netdna.bootstrapcdn.com/font-awesome/{$fontawesome.version}/css/font-awesome.min.css" rel="stylesheet" />
        <link href="//netdna.bootstrapcdn.com/bootstrap/{$bootstrap.version}/css/bootstrap.min.css" rel="stylesheet" />
        <script type="text/javascript" src="//code.jquery.com/jquery-{$jquery.version}.min.js"></script>
        <script type="text/javascript" src="//code.jquery.com/jquery-migrate-{$jquery.migrate.version}.min.js"></script>
        
        <link href="{$WebApplicationBaseURL}webjars/font-awesome/{$fontawesome.version}/css/font-awesome.min.css" rel="stylesheet" />
        <link href="{$WebApplicationBaseURL}webjars/bootstrap/{$bootstrap.version}/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="{$WebApplicationBaseURL}css/main.css" />
        <script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/{$jquery.version}/jquery.min.js"></script>
        <script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-migrate/{$jquery.migrate.version}/jquery-migrate.min.js"></script>
        <!--xsl:if test="string-length($MIR.CustomLayout.JS) &gt; 0">
            <script type="text/javascript" src="{$WebApplicationBaseURL}js/{$MIR.CustomLayout.JS}"></script>
        </xsl:if-->
        <script type="text/javascript" src="{$WebApplicationBaseURL}js/mein.js"></script>
        <script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-confirm/2.7.0/jquery.confirm.min.js"></script>
        <script type="text/javascript" src="{$WebApplicationBaseURL}js/base.js" rel="stylesheet"></script>
        <xsl:copy-of select="head/*" />
      </head>

      <body>

        <header>
            <xsl:call-template name="navigation" />
        </header>

        <div class="container" id="page"> <!-- style="margin-bottom:30px;" -->
        <!--Kruemmelleiste -->
              <div class="row detail_row bread_plus"  itemprop="breadcrumb">
                <div class="col-xs-12">
                  <ul>
                   <xsl:choose>
                    <xsl:when test="$CurrentLang = 'de'" > 
                    <li class="crumbs">
                      <a href="http://www.uni-hamburg.de">UHH</a>
                      <span class="sep">>>></span>
                    </li>
                    <li class="crumbs">
                      <a href="https://www.gwiss.uni-hamburg.de">Fakultät für Geisteswissenschaften</a>
                      <span class="sep">>>></span>
                    </li>
                    <li class="crumbs">
                      <a href="https://www.morphilo.uni-hamburg.de">Morphilo</a>
                    </li>
                   </xsl:when> 
                   <xsl:otherwise>
                    <li class="crumbs">
                      <a href="http://www.uni-hamburg.de">UHH</a>
                      <span class="sep">>>></span>
                    </li>
                    <li class="crumbs">
                      <a href="https://www.gwiss.uni-hamburg.de">Faculty of Humanities</a>
                      <span class="sep">>>></span>
                    </li>
                    <li class="crumbs">
                      <a href="https://www.morphilo.uni-hamburg.de">Morphilo</a>
                    </li>
                   </xsl:otherwise>
                  </xsl:choose>
                  </ul>
                </div>
              </div>
          <!-- Kruemmelleiste Ende -->
          
          <div id="main_content" style="padding-top:10px">
            <xsl:call-template name="print.writeProtectionMessage" />
            <xsl:choose>
              <xsl:when test="$readAccess='true'">
                <xsl:copy-of select="*[not(name()='head')]" />
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="printNotLoggedIn" />
              </xsl:otherwise>
            </xsl:choose>
          </div>
        </div>


        <footer class="panel-footer" role="contentinfo">
        <div class="container">
        <div style="padding-bottom: 25px;">
             <hr class="footer-textfluss" />
               <div class="row footer-textfluss">
                  <!-- Sprache des Footers in choose-Umgebung -->
                  <xsl:choose>
                   <xsl:when test="$CurrentLang = 'de'" >
                   <p>© 
                    <a href="http://www.gwiss.uni-hamburg.de/gwin.html">gwin</a>, 
                    <a href="http://www.uni-hamburg.de/">Universität Hamburg</a>, 2013-2016
                    <span class="pull-right">
                      <a href="{$WebApplicationBaseURL}content/brand/contact.xml">
                        <img src="{$WebApplicationBaseURL}images/dart-round-gr.gif" alt="Contact us" /> Kontakt</a> &#160;|&#160; 
                      <a href="{$WebApplicationBaseURL}content/brand/impressum.xml">
                        <img src="{$WebApplicationBaseURL}images/dart-round-gr.gif" alt="Legal notice" /> Impressum</a> &#160;|&#160; 
                      <a href="#top">Nach oben 
                        <img src="{$WebApplicationBaseURL}images/top.gif" alt="Top of page" /></a>
                    </span>
                   </p>
                   </xsl:when> 
                   <xsl:otherwise>
                    <p>© 
                    <a href="http://www.gwiss.uni-hamburg.de/gwin.html">gwin</a>, 
                    <a href="http://www.uni-hamburg.de/">Hamburg University</a>, 2013-2016
                    <span class="pull-right">
                      <a href="{$WebApplicationBaseURL}content/brand/contact.xml">
                        <img src="{$WebApplicationBaseURL}images/dart-round-gr.gif" alt="Contact us" /> Contact us</a> &#160;|&#160; 
                      <a href="{$WebApplicationBaseURL}content/brand/impressum.xml">
                        <img src="{$WebApplicationBaseURL}images/dart-round-gr.gif" alt="Legal notice" /> Imprint</a> &#160;|&#160; 
                      <a href="#top">Go up 
                        <img src="{$WebApplicationBaseURL}images/top.gif" alt="Top of page" /></a>
                    </span>
                   </p>
                   </xsl:otherwise>
                  </xsl:choose>
                </div> 
              </div>
          </div>
        </footer>
       

<!-- MyCore Logo am Ende der Darstellung -->
<!--
        <xsl:variable name="mcr_version" select="concat('MyCoRe ',mcrver:getCompleteVersion())" />
        <div id="powered_by" class="text-center">
          <a href="http://www.mycore.de">
            <img src="{$WebApplicationBaseURL}content/images/mycore_logo_powered_120x30_blaue_schrift_frei.png" title="{$mcr_version}" alt="powered by MyCoRe" />
          </a>
        </div>
-->
        <script type="text/javascript">
          <!-- Bootstrap & Query-Ui button conflict workaround  -->
          if (jQuery.fn.button){jQuery.fn.btn = jQuery.fn.button.noConflict();}
        </script>
        <!--
        <script type="text/javascript" src="//netdna.bootstrapcdn.com/bootstrap/{$bootstrap.version}/js/bootstrap.min.js"></script>
        -->
        <script type="text/javascript" src="{$WebApplicationBaseURL}webjars/bootstrap/{$bootstrap.version}/js/bootstrap.min.js"></script>
        <script>
          $( document ).ready(function() {
            $('.overtext').tooltip();
            $.confirm.options = {
              text: "<xsl:value-of select="i18n:translate('confirm.text')" />",
              title: "<xsl:value-of select="i18n:translate('confirm.title')" />",
              confirmButton: "<xsl:value-of select="i18n:translate('confirm.confirmButton')" />",
              cancelButton: "<xsl:value-of select="i18n:translate('confirm.cancelButton')" />",
              post: false
            }
          });
        </script>
       </body>
    </html>
  </xsl:template>


  <!-- create navigation -->
  <xsl:template name="navigation">
    <div id="header_box" class="clearfix container">
 <!-- loginMenu wird unten in der Leiste aufgerufen, darum hier weg -->
 <!-- <div id="options_nav_box">
        <nav>
          <ul class="nav navbar-nav pull-right">
            <xsl:call-template name="loginMenu" />
          </ul>
        </nav>
      </div>
-->   
      <div class="pull-right" style="list-style-type:none;padding-top: 20px;">
         <xsl:call-template name="languageMenu" />
      </div>
        
      <div class="row" style="margin-top:25px;margin-bottom:25px;">
      <div class="col-sm-4" style="padding-left:15px;">
        <a href="http://www.uni-hamburg.de">
        <img src="{$WebApplicationBaseURL}images/uhh-logo.gif" />
        </a>
      </div>
      <div class="col-sm-8">
        <div class="headertext pull-right">
          <span style="font-size: 18px;">
          <strong>
           <a href="{concat($WebApplicationBaseURL,substring($loaded_navigation_xml/@hrefStartingPage,2),$HttpSession)}" class="headerlink"><xsl:value-of select="i18n:translate('main.page.head.title')" />
           </a>
          </strong>
         </span>
        </div>
      </div>
    </div>
    
      <!--div style="font-size:200%;margin:6px" id="project_logo_box">
        <a href="{concat($WebApplicationBaseURL,substring($loaded_navigation_xml/@hrefStartingPage,2),$HttpSession)}">
          <img style="margin-right:10px" alt="Skeleton" src="{$WebApplicationBaseURL}images/uhh-logo.gif" />

      </a>
      </div>
      <div>
        <a href="{concat($WebApplicationBaseURL,substring($loaded_navigation_xml/@hrefStartingPage,2),$HttpSession)}">
          Epigraphische Datenbank zum antiken Kleinasien
        </a>
      </div-->
      
      </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="navbar navbar-default" style="margin-bottom:0px;min-height:0px;">
      <div class="container">

        <div class="navbar-header">
          <button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".main-nav-entries">
            <span class="sr-only"> Toggle navigation </span>
            <span class="icon-bar">
            </span>
            <span class="icon-bar">
            </span>
            <span class="icon-bar">
            </span>
          </button>
        </div>



        <nav class="collapse navbar-collapse main-nav-entries">
          <ul class="nav navbar-nav pull-left">
            <xsl:apply-templates select="$loaded_navigation_xml/menu[@id='project']" />
            <!--xsl:apply-templates select="$loaded_navigation_xml/menu[@id='browse']" /-->
            <!--xsl:apply-templates select="$loaded_navigation_xml/menu[@id='publish']" /-->
            <xsl:apply-templates select="$loaded_navigation_xml/menu[@id='software']" />
            <xsl:apply-templates select="$loaded_navigation_xml/menu[@id='database']" />
            <xsl:apply-templates select="$loaded_navigation_xml/menu[@id='staff']" />
            <xsl:apply-templates select="$loaded_navigation_xml/menu[@id='misc']" />
          </ul>
          <!--  -->
          <ul id="userMenu" class="nav navbar-nav navbar-right" style="padding-right:3px">
            <xsl:call-template name="loginMenu" />
          </ul>
        </nav>

      </div><!-- /container -->
    </div>
  </xsl:template>

</xsl:stylesheet>
