<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.crm.web.demand.DemandTypeJspBean"%>

${ crmDemandTypePortletJspBean.init( pageContext.request, DemandTypeJspBean.RIGHT_MANAGE_CRM_DEMAND_TYPES ) }
${ pageContext.response.sendRedirect( crmDemandTypePortletJspBean.doCreate( pageContext.request ) ) }
