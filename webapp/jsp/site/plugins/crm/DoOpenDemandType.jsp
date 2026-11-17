<%@page import="fr.paris.lutece.plugins.crm.web.CRMApp"%>

${ pageContext.response.sendRedirect( CRMApp.doOpenDemandType( pageContext.request ) ) }
