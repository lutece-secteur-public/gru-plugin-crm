/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.crm.web.portlet;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.crm.business.portlet.DemandTypePortlet;
import fr.paris.lutece.plugins.crm.business.portlet.DemandTypePortletHome;
import fr.paris.lutece.plugins.crm.service.category.CategoryService;
import fr.paris.lutece.plugins.crm.util.CrmUtils;
import fr.paris.lutece.plugins.crm.util.constants.CRMConstants;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class provides the user interface to manage DemandTypePortletJspBean Portlet
 */
@RequestScoped
@Named( "crmDemandTypePortletJspBean" )
public class DemandTypePortletJspBean extends PortletJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants
    private static final long serialVersionUID = 1L;
    private static final String ACTION_CREATE_PORTLET = "crm.createPortlet";
    private static final String ACTION_MODIFY_PORTLET = "crm.modifyPortlet";
    private static final String MESSAGE_INVALID_TOKEN = "crm.message.invalidToken";
    private static final String MESSAGE_PORTLET_NOT_FOUND = "crm.message.portletNotFound";

    @Inject
    private CategoryService _categoryService;

    @Inject
    private DemandTypePortletHome _portletHome;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strIdPortletType = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        model.put( CRMConstants.MARK_CATEGORIES_LIST, _categoryService.getCategories( getLocale( ), true, true ) );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_CREATE_PORTLET ) );
        HtmlTemplate template = getCreateTemplate( strIdPage, strIdPortletType, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        DemandTypePortlet portlet = StringUtils.isNumeric( strPortletId ) ? (DemandTypePortlet) PortletHome.findByPrimaryKey( CrmUtils.convertStringToInt( strPortletId ) )
                : null;

        if ( portlet == null )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_NOT_FOUND, getLocale( ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( CRMConstants.MARK_CATEGORIES_LIST, _categoryService.getCategories( getLocale( ), true, true ) );
        model.put( CRMConstants.MARK_CATEGORY_ID_CATEGORY, portlet.getIdCategory( ) );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_MODIFY_PORTLET ) );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doCreate( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_CREATE_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        DemandTypePortlet portlet = new DemandTypePortlet( );
        // gets the identifier of the parent page
        String strCategory = request.getParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY );
        int nIdCategory = CrmUtils.convertStringToInt( strCategory );
        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setIdCategory( nIdCategory );

        // Portlet creation
        _portletHome.create( portlet );

        // Displays the page with the new Portlet
        return getPageUrl( portlet.getPageId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doModify( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_MODIFY_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        // recovers portlet attributes
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strCategory = request.getParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY );
        int nIdCategory = CrmUtils.convertStringToInt( strCategory );

        DemandTypePortlet portlet = StringUtils.isNumeric( strPortletId ) ? (DemandTypePortlet) PortletHome.findByPrimaryKey( CrmUtils.convertStringToInt( strPortletId ) )
                : null;

        if ( portlet == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        // retrieve portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setIdCategory( nIdCategory );

        // Portlet update
        _portletHome.update( portlet );

        // displays the page withe the potlet updated
        return getPageUrl( portlet.getPageId( ) );
    }
}
