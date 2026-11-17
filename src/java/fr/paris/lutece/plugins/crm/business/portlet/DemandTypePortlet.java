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
package fr.paris.lutece.plugins.crm.business.portlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.crm.business.demand.DemandType;
import fr.paris.lutece.plugins.crm.business.demand.category.Category;
import fr.paris.lutece.plugins.crm.service.category.CategoryService;
import fr.paris.lutece.plugins.crm.service.demand.DemandTypeService;
import fr.paris.lutece.plugins.crm.util.CrmUtils;
import fr.paris.lutece.plugins.crm.util.constants.CRMConstants;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;

/**
 * This class represents business objects DemandTypePortlet
 */
public class DemandTypePortlet extends PortletHtmlContent
{
    private static final String TEMPLATE_PORTLET_DEFAULT = "skin/plugins/crm/portlet/demand_type_portlet.html";
    private static DemandTypePortletHome _portletHome = CDI.current( ).select( DemandTypePortletHome.class ).get( );
    private static CategoryService _categoryService = CDI.current( ).select( CategoryService.class ).get( );
    private static DemandTypeService _demandTypeService = CDI.current( ).select( DemandTypeService.class ).get( );

    // Constants
    private int _nIdCategory;

    /**
     * Sets the identifier of the portlet type to the value specified in the ArticlesListPortletHome class
     */
    public DemandTypePortlet( )
    {
    }

    /**
     * Returns the HTML code of the demand types of the portlet, grouped by category, rendered with the template chosen for the portlet
     *
     * @param request
     *            The HTTP Servlet request
     * @return the HTML code of the portlet
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        Map<String, Object> model = createPortletModel( );
        List<Category> listCategories = new ArrayList<>( );
        Map<String, List<DemandType>> mapDemandTypes = new HashMap<>( );

        if ( _nIdCategory != CrmUtils.CONSTANT_ID_NULL )
        {
            Category category = null;

            if ( _nIdCategory != CrmUtils.convertStringToInt( CRMConstants.NO_CATEGORY ) )
            {
                category = _categoryService.findByPrimaryKey( _nIdCategory );
            }
            else
            {
                category = new Category( );
                category.setIdCategory( _nIdCategory );
                category.setName( " " );
            }

            if ( category != null )
            {
                listCategories.add( category );
                mapDemandTypes.put( Integer.toString( _nIdCategory ), _demandTypeService.findForLuteceUser( request, _nIdCategory ) );
            }
        }
        else
        {
            listCategories.addAll( _categoryService.getCategoriesList( ) );
            mapDemandTypes = _demandTypeService.findForLuteceUser( request );
        }

        model.put( CRMConstants.MARK_CATEGORIES_LIST, listCategories );
        model.put( CRMConstants.MARK_MAP_DEMAND_TYPES_LIST, mapDemandTypes );

        return renderTemplate( request, TEMPLATE_PORTLET_DEFAULT, model );
    }

    /**
     * the Category id associated to the portlet
     * 
     * @return the Category id associated to the portlet
     */
    public int getIdCategory( )
    {
        return _nIdCategory;
    }

    /**
     * set the Category id associated to the portlet
     * 
     * @param _nIdCategory
     *            set the Category id associated to the portlet
     */
    public void setIdCategory( int _nIdCategory )
    {
        this._nIdCategory = _nIdCategory;
    }

    /**
     * Updates the current instance of the form portlet object
     */
    public void update( )
    {
        _portletHome.update( this );
    }

    /**
     * Removes the current instance of the the form portlet object
     */
    public void remove( )
    {
        _portletHome.remove( this );
    }
}
