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
package fr.paris.lutece.plugins.crm.web.category;

import fr.paris.lutece.plugins.crm.business.demand.category.Category;
import fr.paris.lutece.plugins.crm.service.category.CategoryService;
import fr.paris.lutece.plugins.crm.util.constants.CRMConstants;
import fr.paris.lutece.plugins.crm.web.demand.DemandTypeJspBean;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.message.MessageHtml;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * CategoryJspBean
 *
 */
@RequestScoped
@Named( "crmCategoryJspBean" )
@Controller( controllerJsp = CategoryJspBean.CONTROLLER_JSP, controllerPath = CategoryJspBean.CONTROLLER_PATH, right = DemandTypeJspBean.RIGHT_MANAGE_CRM_DEMAND_TYPES, securityTokenEnabled = true )
public class CategoryJspBean extends MVCAdminJspBean
{
    public static final String CONTROLLER_JSP = "ManageCategories.jsp";
    public static final String CONTROLLER_PATH = "jsp/admin/plugins/crm/";
    private static final long serialVersionUID = 1L;

    // TEMPLATES
    private static final String TEMPLATE_MANAGE_CATEGORIES = "/admin/plugins/crm/category/manage_categories.html";
    private static final String TEMPLATE_CREATE_CATEGORY = "/admin/plugins/crm/category/create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY = "/admin/plugins/crm/category/modify_category.html";

    // VIEWS
    private static final String VIEW_MANAGE_CATEGORIES = "manageCategories";
    private static final String VIEW_CREATE_CATEGORY = "createCategory";
    private static final String VIEW_MODIFY_CATEGORY = "modifyCategory";
    private static final String VIEW_CONFIRM_REMOVE_CATEGORY = "confirmRemoveCategory";

    // ACTIONS
    private static final String ACTION_CREATE_CATEGORY = "createCategory";
    private static final String ACTION_MODIFY_CATEGORY = "modifyCategory";
    private static final String ACTION_REMOVE_CATEGORY = "removeCategory";

    // VARIABLES
    @Inject
    private CategoryService _categoryService;

    @Inject
    @Pager( listBookmark = CRMConstants.MARK_CATEGORIES_LIST, defaultItemsPerPage = CRMConstants.PROPERTY_DEFAULT_LIST_CATEGORY_PER_PAGE, baseUrl = CONTROLLER_PATH
            + CONTROLLER_JSP )
    private IPager<Category, Category> _pager;

    /**
     * Get the manage categories page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param model
     *            the model
     * @return the manage categories page
     */
    @View( value = VIEW_MANAGE_CATEGORIES, defaultView = true )
    public String getManageCategories( HttpServletRequest request, Models model )
    {
        List<Category> listCategories = new ArrayList<>( _categoryService.getCategoriesList( ) );
        _pager.withBaseUrl( getViewUrl( VIEW_MANAGE_CATEGORIES ) ).withListItem( listCategories ).populateModels( request, model, getLocale( ) );

        return getPage( CRMConstants.PROPERTY_PAGE_TITLE_MANAGE_CATEGORIES, TEMPLATE_MANAGE_CATEGORIES );
    }

    /**
     * Get the create category page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the create category page
     */
    @View( VIEW_CREATE_CATEGORY )
    public String getCreateCategory( HttpServletRequest request )
    {
        return getPage( CRMConstants.PROPERTY_PAGE_TITLE_CREATE_CATEGORY, TEMPLATE_CREATE_CATEGORY );
    }

    /**
     * Create a category
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_CREATE_CATEGORY )
    public String doCreateCategory( HttpServletRequest request )
    {
        String strName = request.getParameter( CRMConstants.PARAMETER_CATEGORY_NAME );
        String strDescription = request.getParameter( CRMConstants.PARAMETER_CATEGORY_DESCRIPTION );
        String strCode = request.getParameter( CRMConstants.PARAMETER_CATEGORY_CODE );

        if ( StringUtils.isNotBlank( strName ) && StringUtils.isNotBlank( strDescription ) && StringUtils.isNotBlank( strCode ) )
        {
            if ( _categoryService.findByCode( strCode ) != null )
            {
                addError( CRMConstants.MESSAGE_CATEGORY_CODE_ALREADY_EXISTS, getLocale( ) );

                return redirectView( request, VIEW_CREATE_CATEGORY );
            }

            Category category = new Category( );
            category.setName( strName );
            category.setDescription( strDescription );
            category.setCode( strCode );
            _categoryService.createCategory( category );

            return redirectView( request, VIEW_MANAGE_CATEGORIES );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Get the confirm remove category page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @View( value = VIEW_CONFIRM_REMOVE_CATEGORY, securityTokenAction = ACTION_REMOVE_CATEGORY )
    public String getConfirmRemoveCategory( HttpServletRequest request )
    {
        String strCategoryId = request.getParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY );

        if ( StringUtils.isNotBlank( strCategoryId ) && StringUtils.isNumeric( strCategoryId ) )
        {
            int nId = Integer.parseInt( strCategoryId );
            UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CATEGORY ) );
            url.addParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY, nId );

            return redirect( request,
                    AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_CONFIRM_REMOVE_CATEGORY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Remove a category
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_REMOVE_CATEGORY )
    public String doRemoveCategory( HttpServletRequest request )
    {
        String strCategoryId = request.getParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY );

        if ( StringUtils.isNotBlank( strCategoryId ) && StringUtils.isNumeric( strCategoryId ) )
        {
            int nId = Integer.parseInt( strCategoryId );
            MessageHtml error = _categoryService.removeCategory( nId, getLocale( ) );

            if ( error == null )
            {
                return redirectView( request, VIEW_MANAGE_CATEGORIES );
            }

            Object [ ] args = {
                    error
            };

            return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_CANNOT_REMOVE_CATEGORY, args, AdminMessage.TYPE_STOP ) );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Get the modify category page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param model
     *            the model
     * @return the modify category page
     */
    @View( VIEW_MODIFY_CATEGORY )
    public String getModifyCategory( HttpServletRequest request, Models model )
    {
        String strCategoryId = request.getParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY );

        if ( StringUtils.isNotBlank( strCategoryId ) && StringUtils.isNumeric( strCategoryId ) )
        {
            int nId = Integer.parseInt( strCategoryId );
            Category category = _categoryService.findByPrimaryKey( nId );

            if ( category != null )
            {
                model.put( CRMConstants.MARK_CATEGORY, category );

                return getPage( CRMConstants.PROPERTY_PAGE_TITLE_MODIFY_CATEGORY, TEMPLATE_MODIFY_CATEGORY );
            }
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Modify a category
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_MODIFY_CATEGORY )
    public String doModifyCategory( HttpServletRequest request )
    {
        String strCategoryId = request.getParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY );

        if ( StringUtils.isNotBlank( strCategoryId ) && StringUtils.isNumeric( strCategoryId ) )
        {
            String strName = request.getParameter( CRMConstants.PARAMETER_CATEGORY_NAME );
            String strDescription = request.getParameter( CRMConstants.PARAMETER_CATEGORY_DESCRIPTION );
            String strCode = request.getParameter( CRMConstants.PARAMETER_CATEGORY_CODE );

            if ( StringUtils.isNotBlank( strName ) && StringUtils.isNotBlank( strDescription ) && StringUtils.isNotBlank( strCode ) )
            {
                int nId = Integer.parseInt( strCategoryId );
                Category category = _categoryService.findByPrimaryKey( nId );

                if ( category != null )
                {
                    Category categoryWithCode = _categoryService.findByCode( strCode );

                    if ( categoryWithCode != null && categoryWithCode.getIdCategory( ) != nId )
                    {
                        addError( CRMConstants.MESSAGE_CATEGORY_CODE_ALREADY_EXISTS, getLocale( ) );

                        return redirect( request, VIEW_MODIFY_CATEGORY, CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY, nId );
                    }

                    category.setName( strName );
                    category.setDescription( strDescription );
                    category.setCode( strCode );
                    _categoryService.updateCategory( category );

                    return redirectView( request, VIEW_MANAGE_CATEGORIES );
                }

                return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
            }

            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }
}
