$﻿(document).ready(function() {
    // replace placeholder USERNAME with username

    var userID = $("#currentUser strong").html();
    var newHref = "";
    var hostname = window.location.origin;
    
    if ( (typeof(userID) == "undefined") )
    {
      sessionStorage.setItem("url",document.location);
      newHref = hostname + '/morphilo/servlets/MCRLoginServlet';
      $("a[href='"+ hostname + "/morphilo/servlets/solr/select?q=createdby:USERNAME']").attr('href',newHref);  
    }
    else if (  (sessionStorage.getItem("url") == hostname + "/morphilo/servlets/MCRLoginServlet") )
    {
      newHref = hostname + '/morphilo/servlets/solr/select?q=createdby:' + userID + ' AND objectType:corpmeta';
      window.location = newHref;
      sessionStorage.setItem("url","");
    }
    else
    {
      newHref = hostname + '/morphilo/servlets/solr/select?q=createdby:' + userID + ' AND objectType:corpmeta';
      $("a[href='"+ hostname + "/morphilo/servlets/solr/select?q=createdby:USERNAME']").attr('href',newHref);
    }
});
