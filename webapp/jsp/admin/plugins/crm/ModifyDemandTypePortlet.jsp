<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.crm.web.demand.DemandTypeJspBean"%>

${ crmDemandTypePortletJspBean.init( pageContext.request, DemandTypeJspBean.RIGHT_MANAGE_CRM_DEMAND_TYPES ) }
${ crmDemandTypePortletJspBean.getModify( pageContext.request ) }
