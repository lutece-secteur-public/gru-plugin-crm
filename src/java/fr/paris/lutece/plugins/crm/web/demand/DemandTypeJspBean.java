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
package fr.paris.lutece.plugins.crm.web.demand;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.crm.business.demand.DemandType;
import fr.paris.lutece.plugins.crm.business.demand.DemandTypeFilter;
import fr.paris.lutece.plugins.crm.service.category.CategoryService;
import fr.paris.lutece.plugins.crm.service.demand.DemandService;
import fr.paris.lutece.plugins.crm.service.demand.DemandTypeService;
import fr.paris.lutece.plugins.crm.service.parameters.AdvancedParametersService;
import fr.paris.lutece.plugins.crm.util.OperatorEnum;
import fr.paris.lutece.plugins.crm.util.TargetEnum;
import fr.paris.lutece.plugins.crm.util.constants.CRMConstants;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * DemandTypeJspBean
 *
 */
@SessionScoped
@Named( "crmDemandTypeJspBean" )
@Controller( controllerJsp = DemandTypeJspBean.CONTROLLER_JSP, controllerPath = DemandTypeJspBean.CONTROLLER_PATH, right = DemandTypeJspBean.RIGHT_MANAGE_CRM_DEMAND_TYPES, securityTokenEnabled = true )
public class DemandTypeJspBean extends MVCAdminJspBean
{
    public static final String RIGHT_MANAGE_CRM_DEMAND_TYPES = "CRM_DEMAND_TYPES_MANAGEMENT";
    public static final String CONTROLLER_JSP = "ManageDemandTypes.jsp";
    public static final String CONTROLLER_PATH = "jsp/admin/plugins/crm/";
    private static final long serialVersionUID = 1L;

    // TEMPLATES
    private static final String TEMPLATE_MANAGE_DEMAND_TYPES = "/admin/plugins/crm/demand/manage_demand_types.html";
    private static final String TEMPLATE_CREATE_DEMAND_TYPE = "/admin/plugins/crm/demand/create_demand_type.html";
    private static final String TEMPLATE_MODIFY_DEMAND_TYPE = "/admin/plugins/crm/demand/modify_demand_type.html";
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "/admin/plugins/crm/demand/manage_advanced_parameters.html";

    // VIEWS
    private static final String VIEW_MANAGE_DEMAND_TYPES = "manageDemandTypes";
    private static final String VIEW_MANAGE_ADVANCED_PARAMETERS = "manageAdvancedParameters";
    private static final String VIEW_CREATE_DEMAND_TYPE = "createDemandType";
    private static final String VIEW_MODIFY_DEMAND_TYPE = "modifyDemandType";
    private static final String VIEW_CONFIRM_REMOVE_DEMAND_TYPE = "confirmRemoveDemandType";
    private static final String VIEW_CONFIRM_PURGE_DEMAND_TYPE = "confirmPurgeDemandType";
    private static final String VIEW_CONFIRM_ENABLE_DEMAND_TYPE = "confirmEnableDemandType";
    private static final String VIEW_CONFIRM_DISABLE_DEMAND_TYPE = "confirmDisableDemandType";
    private static final String VIEW_CONFIRM_REORDER_DEMAND_TYPES = "confirmReorderDemandTypes";

    // ACTIONS
    private static final String ACTION_CREATE_DEMAND_TYPE = "createDemandType";
    private static final String ACTION_MODIFY_DEMAND_TYPE = "modifyDemandType";
    private static final String ACTION_REMOVE_DEMAND_TYPE = "removeDemandType";
    private static final String ACTION_PURGE_DEMAND_TYPE = "purgeDemandType";
    private static final String ACTION_ENABLE_DEMAND_TYPE = "enableDemandType";
    private static final String ACTION_DISABLE_DEMAND_TYPE = "disableDemandType";
    private static final String ACTION_REORDER_DEMAND_TYPES = "reorderDemandTypes";
    private static final String ACTION_MODIFY_ADVANCED_PARAMETERS = "modifyAdvancedParameters";

    // MESSAGES
    private static final String MESSAGE_CONFIRM_ENABLE_DEMAND_TYPE = "crm.message.confirmEnableDemandType";
    private static final String MESSAGE_CONFIRM_DISABLE_DEMAND_TYPE = "crm.message.confirmDisableDemandType";
    private static final String MESSAGE_CONFIRM_REORDER_DEMAND_TYPES = "crm.message.confirmReorderDemandTypes";

    // VARIABLES
    @Inject
    private AdvancedParametersService _advancedParametersService;

    @Inject
    private DemandTypeService _demandTypeService;

    @Inject
    private DemandService _demandService;

    @Inject
    private CategoryService _categoryService;

    @Inject
    @Pager( listBookmark = CRMConstants.MARK_DEMAND_TYPES_LIST, defaultItemsPerPage = CRMConstants.PROPERTY_DEFAULT_LIST_DEMAND_TYPES_PER_PAGE, baseUrl = CONTROLLER_PATH
            + CONTROLLER_JSP )
    private IPager<DemandType, DemandType> _pager;

    private DemandTypeFilter _dtFilter;

    /**
     * Get the manage demand types page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param model
     *            the model
     * @return the manage demand types page
     */
    @View( value = VIEW_MANAGE_DEMAND_TYPES, defaultView = true )
    public String getManageDemandTypes( HttpServletRequest request, Models model )
    {
        UrlItem url = getUrlManageDemandTypes( request );
        List<DemandType> listDemandTypes = getDemandTypesList( request );
        _pager.withBaseUrl( url.getUrl( ) ).withListItem( listDemandTypes ).populateModels( request, model, getLocale( ) );

        model.put( CRMConstants.MARK_DEMAND_TYPE_FILTER, _dtFilter );
        model.put( CRMConstants.MARK_CATEGORIES_LIST, _categoryService.getCategories( getLocale( ), true, false ) );
        model.put( CRMConstants.MARK_IS_WELL_ORDERED, _demandTypeService.isWellOrdered( ) );
        model.put( CRMConstants.MARK_OPERATORS_LIST, _demandTypeService.getOperatorsList( ) );
        model.put( CRMConstants.MARK_LOCALE, request.getLocale( ) );

        return getPage( CRMConstants.PROPERTY_MANAGE_DEMAND_TYPES_PAGE_TITLE, TEMPLATE_MANAGE_DEMAND_TYPES );
    }

    /**
     * Get the manage advanced parameters page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param model
     *            the model
     * @return the manage advanced parameters page
     */
    @View( value = VIEW_MANAGE_ADVANCED_PARAMETERS, securityTokenAction = ACTION_MODIFY_ADVANCED_PARAMETERS )
    public String getManageAdvancedParameters( HttpServletRequest request, Models model )
    {
        Boolean bDisplayDraft = _advancedParametersService.isParameterValueByKey( CRMConstants.CONSTANT_DISPLAYDRAFT );
        Boolean bUseIdCrmUser = _advancedParametersService.isParameterValueByKey( CRMConstants.CONSTANT_USE_ID_CRM_USER );

        model.put( CRMConstants.MARK_DISPLAYDRAFT, bDisplayDraft );
        model.put( CRMConstants.MARK_USE_IDCRMUSER, bUseIdCrmUser );

        return getPage( CRMConstants.PROPERTY_MANAGE_DEMAND_TYPES_PAGE_TITLE, TEMPLATE_MANAGE_ADVANCED_PARAMETERS );
    }

    /**
     * Get the create demand type page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param model
     *            the model
     * @return the create demand type page
     */
    @View( VIEW_CREATE_DEMAND_TYPE )
    public String getCreateDemandType( HttpServletRequest request, Models model )
    {
        model.put( CRMConstants.MARK_CATEGORIES_LIST, _categoryService.getCategories( getLocale( ), false, false ) );
        model.put( CRMConstants.MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) ) );
        model.put( CRMConstants.MARK_MAX_ORDER, _demandTypeService.findMaxOrder( ) );
        model.put( CRMConstants.MARK_TARGETS_LIST, _demandTypeService.getTargetsList( ) );
        model.put( CRMConstants.MARK_LOCALE, request.getLocale( ) );

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            model.put( CRMConstants.MARK_ROLE_REF_LIST, _demandTypeService.getRolesList( ) );
        }

        return getPage( CRMConstants.PROPERTY_CREATE_DEMAND_TYPE_PAGE_TITLE, TEMPLATE_CREATE_DEMAND_TYPE );
    }

    /**
     * Create a demand type
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_CREATE_DEMAND_TYPE )
    public String doCreateDemandType( HttpServletRequest request )
    {
        DemandType demandType = new DemandType( );
        String strUrl = getDemandTypeData( request, demandType );

        if ( StringUtils.isBlank( strUrl ) )
        {
            // The newly created demand type is placed at the head of the list
            _demandTypeService.create( demandType );

            return redirect( request, getUrlManageDemandTypes( request ).getUrl( ) );
        }

        return redirect( request, strUrl );
    }

    /**
     * Get the confirm remove demand type page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @View( value = VIEW_CONFIRM_REMOVE_DEMAND_TYPE, securityTokenAction = ACTION_REMOVE_DEMAND_TYPE )
    public String getConfirmRemoveDemandType( HttpServletRequest request )
    {
        return getConfirmDemandTypeAction( request, ACTION_REMOVE_DEMAND_TYPE, CRMConstants.MESSAGE_CONFIRM_REMOVE_DEMAND_TYPE );
    }

    /**
     * Remove a demand type
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_REMOVE_DEMAND_TYPE )
    public String doRemoveDemandType( HttpServletRequest request )
    {
        DemandType demandType = getAuthorizedDemandType( request );

        if ( demandType != null )
        {
            _demandTypeService.remove( demandType.getIdDemandType( ) );

            return redirect( request, getUrlManageDemandTypes( request ).getUrl( ) );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Get the modify demand type page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param model
     *            the model
     * @return the modify demand type page
     */
    @View( VIEW_MODIFY_DEMAND_TYPE )
    public String getModifyDemandType( HttpServletRequest request, Models model )
    {
        DemandType demandType = getAuthorizedDemandType( request );

        if ( demandType != null )
        {
            model.put( CRMConstants.MARK_DEMAND_TYPE, demandType );
            model.put( CRMConstants.MARK_CATEGORIES_LIST, _categoryService.getCategories( getLocale( ), false, false ) );
            model.put( CRMConstants.MARK_MAX_ORDER, _demandTypeService.findMaxOrder( ) );
            model.put( CRMConstants.MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) ) );
            model.put( CRMConstants.MARK_TARGETS_LIST, _demandTypeService.getTargetsList( ) );
            model.put( CRMConstants.MARK_LOCALE, request.getLocale( ) );

            if ( SecurityService.isAuthenticationEnable( ) )
            {
                model.put( CRMConstants.MARK_ROLE_REF_LIST, _demandTypeService.getRolesList( ) );
            }

            return getPage( CRMConstants.PROPERTY_MODIFY_DEMAND_TYPE_PAGE_TITLE, TEMPLATE_MODIFY_DEMAND_TYPE );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Modify a demand type
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_MODIFY_DEMAND_TYPE )
    public String doModifyDemandType( HttpServletRequest request )
    {
        DemandType demandTypeStored = getAuthorizedDemandType( request );

        if ( demandTypeStored != null )
        {
            DemandType demandType = new DemandType( );
            demandType.setIdDemandType( demandTypeStored.getIdDemandType( ) );
            String strUrl = getDemandTypeData( request, demandType );

            if ( StringUtils.isBlank( strUrl ) )
            {
                _demandTypeService.update( demandType );

                return redirect( request, getUrlManageDemandTypes( request ).getUrl( ) );
            }

            return redirect( request, strUrl );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Modify the advanced parameters
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_MODIFY_ADVANCED_PARAMETERS )
    public String doModifyAdvancedParameters( HttpServletRequest request )
    {
        String strModifyDraftDisplay = request.getParameter( CRMConstants.PARAMETER_CHECKBOX_DRAFT_DISPLAY );
        String strUseIdCrmUser = request.getParameter( CRMConstants.PARAMETER_CHECKBOX_USE_IDCRMUSER );

        if ( StringUtils.isNotBlank( strModifyDraftDisplay ) )
        {
            _advancedParametersService.modifyParameterStringValueByKey( CRMConstants.CONSTANT_DISPLAYDRAFT, CRMConstants.CONSTANT_TRUE );
        }
        else
        {
            _advancedParametersService.modifyParameterStringValueByKey( CRMConstants.CONSTANT_DISPLAYDRAFT, CRMConstants.CONSTANT_FALSE );
        }

        _advancedParametersService.modifyParameterStringValueByKey( CRMConstants.CONSTANT_USE_ID_CRM_USER,
                StringUtils.isNotBlank( strUseIdCrmUser ) ? CRMConstants.CONSTANT_TRUE : CRMConstants.CONSTANT_FALSE );

        return redirectView( request, VIEW_MANAGE_DEMAND_TYPES );
    }

    /**
     * Get the confirm reorder demand types page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @View( value = VIEW_CONFIRM_REORDER_DEMAND_TYPES, securityTokenAction = ACTION_REORDER_DEMAND_TYPES )
    public String getConfirmReorderDemandTypes( HttpServletRequest request )
    {
        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REORDER_DEMAND_TYPES, getActionUrl( ACTION_REORDER_DEMAND_TYPES ),
                AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Reorder the demand types
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_REORDER_DEMAND_TYPES )
    public String doReorderDemandTypes( HttpServletRequest request )
    {
        _demandTypeService.doReorderDemandTypes( );

        return redirect( request, getUrlManageDemandTypes( request ).getUrl( ) );
    }

    /**
     * Get the confirm purge demand type page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @View( value = VIEW_CONFIRM_PURGE_DEMAND_TYPE, securityTokenAction = ACTION_PURGE_DEMAND_TYPE )
    public String getConfirmPurgeDemandType( HttpServletRequest request )
    {
        return getConfirmDemandTypeAction( request, ACTION_PURGE_DEMAND_TYPE, CRMConstants.MESSAGE_CONFIRM_PURGE_DEMAND_TYPE );
    }

    /**
     * Purge the demands of a demand type
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_PURGE_DEMAND_TYPE )
    public String doPurgeDemandType( HttpServletRequest request )
    {
        DemandType demandType = getAuthorizedDemandType( request );

        if ( demandType != null )
        {
            _demandService.removeByIdDemandType( demandType.getIdDemandType( ) );

            return redirect( request, getUrlManageDemandTypes( request ).getUrl( ) );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Get the confirm enable demand type page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @View( value = VIEW_CONFIRM_ENABLE_DEMAND_TYPE, securityTokenAction = ACTION_ENABLE_DEMAND_TYPE )
    public String getConfirmEnableDemandType( HttpServletRequest request )
    {
        return getConfirmDemandTypeAction( request, ACTION_ENABLE_DEMAND_TYPE, MESSAGE_CONFIRM_ENABLE_DEMAND_TYPE );
    }

    /**
     * Enable a demand type
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_ENABLE_DEMAND_TYPE )
    public String doEnableDemandType( HttpServletRequest request )
    {
        DemandType demandType = getAuthorizedDemandType( request );

        if ( demandType != null )
        {
            demandType.setDateEnd( null );
            _demandTypeService.update( demandType );

            return redirect( request, getUrlManageDemandTypes( request ).getUrl( ) );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Get the confirm disable demand type page
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @View( value = VIEW_CONFIRM_DISABLE_DEMAND_TYPE, securityTokenAction = ACTION_DISABLE_DEMAND_TYPE )
    public String getConfirmDisableDemandType( HttpServletRequest request )
    {
        return getConfirmDemandTypeAction( request, ACTION_DISABLE_DEMAND_TYPE, MESSAGE_CONFIRM_DISABLE_DEMAND_TYPE );
    }

    /**
     * Disable a demand type
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the JSP URL to redirect
     */
    @Action( ACTION_DISABLE_DEMAND_TYPE )
    public String doDisableDemandType( HttpServletRequest request )
    {
        DemandType demandType = getAuthorizedDemandType( request );

        if ( demandType != null )
        {
            demandType.setDateEnd( new Date( ) );
            _demandTypeService.update( demandType );

            return redirect( request, getUrlManageDemandTypes( request ).getUrl( ) );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    // PRIVATE METHODS

    /**
     * Get the confirmation page of an action on a demand type
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param strAction
     *            the action to confirm
     * @param strMessage
     *            the confirmation message key
     * @return the JSP URL to redirect
     */
    private String getConfirmDemandTypeAction( HttpServletRequest request, String strAction, String strMessage )
    {
        String strIdDemandType = request.getParameter( CRMConstants.PARAMETER_ID_DEMAND_TYPE );

        if ( StringUtils.isNotBlank( strIdDemandType ) && StringUtils.isNumeric( strIdDemandType ) )
        {
            int nIdDemandType = Integer.parseInt( strIdDemandType );
            UrlItem url = new UrlItem( getActionUrl( strAction ) );
            url.addParameter( CRMConstants.PARAMETER_ID_DEMAND_TYPE, nIdDemandType );

            return redirect( request, AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Get the demand type given by the request, when the user is authorized to see it
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the demand type, null when it does not exist or the user is not authorized
     */
    private DemandType getAuthorizedDemandType( HttpServletRequest request )
    {
        String strIdDemandType = request.getParameter( CRMConstants.PARAMETER_ID_DEMAND_TYPE );

        if ( StringUtils.isNotBlank( strIdDemandType ) && StringUtils.isNumeric( strIdDemandType ) )
        {
            DemandType demandType = _demandTypeService.findByPrimaryKey( Integer.parseInt( strIdDemandType ) );

            if ( ( demandType != null ) && AdminWorkgroupService.isAuthorized( demandType, (User) getUser( ) ) )
            {
                return demandType;
            }
        }

        return null;
    }

    /**
     * Get the demand type data
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param demandType
     *            the demand type
     * @return an empty string if there are no errors, the AdminMessage url otherwise
     */
    private String getDemandTypeData( HttpServletRequest request, DemandType demandType )
    {
        String strUrl = StringUtils.EMPTY;
        String strLabel = request.getParameter( CRMConstants.PARAMETER_DEMAND_TYPE_LABEL );
        String strUrlResource = request.getParameter( CRMConstants.PARAMETER_URL_RESOURCE );

        if ( StringUtils.isNotBlank( strLabel ) && StringUtils.isNotBlank( strUrlResource ) )
        {
            String strUrlInfo = request.getParameter( CRMConstants.PARAMETER_URL_INFO );
            String strUrlContact = request.getParameter( CRMConstants.PARAMETER_URL_CONTACT );
            String strDateBegin = request.getParameter( CRMConstants.PARAMETER_DATE_BEGIN );
            String strDateEnd = request.getParameter( CRMConstants.PARAMETER_DATE_END );
            String strOrder = request.getParameter( CRMConstants.PARAMETER_ORDER );
            String strIdCategory = request.getParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY );
            String strWorkgroupKey = request.getParameter( CRMConstants.PARAMETER_WORKGROUP_KEY );
            String strRoleKey = request.getParameter( CRMConstants.PARAMETER_ROLE_KEY );
            String strTarget = request.getParameter( CRMConstants.PARAMETER_TARGET );
            String strUrlDelete = request.getParameter( CRMConstants.PARAMETER_URL_DELETE );
            String strIncludeIdCrmUser = request.getParameter( CRMConstants.PARAMETER_INCLUDE_ID_CRM_USER );
            String strNeedAuthentication = request.getParameter( CRMConstants.PARAMETER_NEED_AUTHENTICATION );
            String strNeedValidation = request.getParameter( CRMConstants.PARAMETER_NEED_VALIDATION );
            int nOrder = 0;

            if ( StringUtils.isNotBlank( strOrder ) && StringUtils.isNumeric( strOrder ) )
            {
                nOrder = Integer.parseInt( strOrder );
            }

            int nIdCategory = -1;

            if ( StringUtils.isNotBlank( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
            {
                nIdCategory = Integer.parseInt( strIdCategory );
            }

            int nTarget = 0;

            if ( StringUtils.isNotBlank( strTarget ) && StringUtils.isNumeric( strTarget ) )
            {
                nTarget = Integer.parseInt( strTarget );
            }

            Date dateBegin = null;
            Date dateEnd = null;

            if ( StringUtils.isNotBlank( strDateBegin ) )
            {
                dateBegin = DateUtil.formatDateLongYear( strDateBegin, getLocale( ) );

                if ( dateBegin != null )
                {
                    if ( dateBegin.before( new Date( 0 ) ) )
                    {
                        strUrl = AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_INVALID_DATE_BEFORE_70, AdminMessage.TYPE_STOP );
                    }
                }
                else
                {
                    strUrl = AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_INVALID_DATEBEGIN, AdminMessage.TYPE_STOP );
                }
            }

            if ( StringUtils.isBlank( strUrl ) && StringUtils.isNotBlank( strDateEnd ) )
            {
                dateEnd = DateUtil.formatDateLongYear( strDateEnd, getLocale( ) );

                if ( dateEnd != null )
                {
                    if ( dateEnd.before( new Date( 0 ) ) )
                    {
                        strUrl = AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_INVALID_DATE_BEFORE_70, AdminMessage.TYPE_STOP );
                    }
                }
                else
                {
                    strUrl = AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_INVALID_DATEEND, AdminMessage.TYPE_STOP );
                }
            }

            // validate period (dateEnd > dateBegin )
            if ( StringUtils.isBlank( strUrl ) && ( dateBegin != null ) && ( dateEnd != null ) )
            {
                if ( dateEnd.before( dateBegin ) )
                {
                    strUrl = AdminMessageService.getMessageUrl( request, CRMConstants.MESSAGE_ERROR_DATEEND_BEFORE_DATEBEGIN, AdminMessage.TYPE_STOP );
                }
            }

            if ( StringUtils.isBlank( strUrl ) )
            {
                demandType.setLabel( strLabel );
                demandType.setUrlResource( StringUtils.isNotBlank( strUrlResource ) ? strUrlResource : StringUtils.EMPTY );
                demandType.setUrlInfo( StringUtils.isNotBlank( strUrlInfo ) ? strUrlInfo : StringUtils.EMPTY );
                demandType.setUrlContact( StringUtils.isNotBlank( strUrlContact ) ? strUrlContact : StringUtils.EMPTY );
                demandType.setOrder( nOrder );
                demandType.setIdCategory( nIdCategory );
                demandType.setDateBegin( dateBegin );
                demandType.setDateEnd( dateEnd );
                demandType.setWorkgroup( StringUtils.isNotBlank( strWorkgroupKey ) ? strWorkgroupKey : StringUtils.EMPTY );
                demandType.setRole( StringUtils.isNotBlank( strRoleKey ) ? strRoleKey : StringUtils.EMPTY );
                demandType.setTarget( TargetEnum.getTarget( nTarget ) );
                demandType.setUrlDelete( StringUtils.isNotBlank( strUrlDelete ) ? strUrlDelete : StringUtils.EMPTY );
                demandType.setIncludeIdCrmUser( strIncludeIdCrmUser != null );
                demandType.setNeedAuthentication( strNeedAuthentication != null );
                demandType.setNeedValidation( strNeedValidation != null );
            }
        }
        else
        {
            strUrl = AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        return strUrl;
    }

    /**
     * Get the list of demand types
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return a list of {@link DemandType}
     */
    private List<DemandType> getDemandTypesList( HttpServletRequest request )
    {
        List<DemandType> listDemandTypes;
        String strSession = request.getParameter( CRMConstants.PARAMETER_SESSION );

        // Check if it s a search or not
        if ( StringUtils.isNotBlank( strSession ) )
        {
            // find demand types from search
            listDemandTypes = doSearchDemandTypesList( request );
        }
        else
        {
            // Find all demand types
            // Reinit the filter stored in the session
            _dtFilter = null;
            listDemandTypes = _demandTypeService.findAll( );
        }

        // Get only the demand types the user is authorized to ses
        listDemandTypes = (List<DemandType>) AdminWorkgroupService.getAuthorizedCollection( listDemandTypes, (User) getUser( ) );

        // Sort the list
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        if ( StringUtils.isNotBlank( strSortedAttributeName ) )
        {
            String strAscSort = request.getParameter( Parameters.SORTED_ASC );
            boolean bIsAscSort = true;

            if ( StringUtils.isNotBlank( strAscSort ) )
            {
                bIsAscSort = Boolean.parseBoolean( strAscSort );
            }

            Collections.sort( listDemandTypes, new AttributeComparator( strSortedAttributeName, bIsAscSort ) );
        }

        return listDemandTypes;
    }

    /**
     * Search the demand types given the search parameters of the request, or the filter stored in the session
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return a list of {@link DemandType}
     */
    private List<DemandType> doSearchDemandTypesList( HttpServletRequest request )
    {
        String strSearch = request.getParameter( CRMConstants.PARAMETER_SEARCH );

        if ( StringUtils.isNotBlank( strSearch ) || ( _dtFilter == null ) )
        {
            _dtFilter = new DemandTypeFilter( );

            String strLabel = request.getParameter( CRMConstants.PARAMETER_LABEL );
            String strUrlForm = request.getParameter( CRMConstants.PARAMETER_URL_RESOURCE );
            String strIdCategory = request.getParameter( CRMConstants.PARAMETER_CATEGORY_ID_CATEGORY );
            String strDateBegin = request.getParameter( CRMConstants.PARAMETER_DATE_BEGIN );
            String strDateEnd = request.getParameter( CRMConstants.PARAMETER_DATE_END );
            String strOperatorDateBegin = request.getParameter( CRMConstants.PARAMETER_OPERATOR_DATE_BEGIN );
            String strOperatorDateEnd = request.getParameter( CRMConstants.PARAMETER_OPERATOR_DATE_END );
            String strUrlDelete = request.getParameter( CRMConstants.PARAMETER_URL_DELETE );

            if ( StringUtils.isNotBlank( strLabel ) )
            {
                _dtFilter.setLabel( strLabel );
            }

            if ( StringUtils.isNotBlank( strUrlForm ) )
            {
                _dtFilter.setUrlResource( strUrlForm );
            }

            if ( StringUtils.isNotBlank( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
            {
                int nIdCategory = Integer.parseInt( strIdCategory );
                _dtFilter.setIdCategory( nIdCategory );
            }

            if ( StringUtils.isNotBlank( strDateBegin ) )
            {
                Date dateBegin = DateUtil.formatDateLongYear( strDateBegin, getLocale( ) );

                if ( dateBegin != null )
                {
                    if ( dateBegin.after( new Date( 0 ) ) )
                    {
                        _dtFilter.setDateBegin( dateBegin );
                    }
                }
            }

            if ( StringUtils.isNotBlank( strDateEnd ) )
            {
                Date dateEnd = DateUtil.formatDateLongYear( strDateEnd, getLocale( ) );

                if ( dateEnd != null )
                {
                    if ( dateEnd.after( new Date( 0 ) ) )
                    {
                        _dtFilter.setDateEnd( dateEnd );
                    }
                }
            }

            if ( StringUtils.isNotBlank( strOperatorDateBegin ) && StringUtils.isNumeric( strOperatorDateBegin ) )
            {
                int nIdOperatorDateBegin = Integer.parseInt( strOperatorDateBegin );

                if ( nIdOperatorDateBegin < OperatorEnum.values( ).length )
                {
                    _dtFilter.setOperatorDateBegin( OperatorEnum.values( ) [nIdOperatorDateBegin] );
                }
            }

            if ( StringUtils.isNotBlank( strOperatorDateEnd ) && StringUtils.isNumeric( strOperatorDateBegin ) )
            {
                int nIdOperatorDateEnd = Integer.parseInt( strOperatorDateEnd );

                if ( nIdOperatorDateEnd < OperatorEnum.values( ).length )
                {
                    _dtFilter.setOperatorDateEnd( OperatorEnum.values( ) [nIdOperatorDateEnd] );
                }
            }

            if ( StringUtils.isNotBlank( strUrlDelete ) )
            {
                _dtFilter.setUrlDelete( strUrlDelete );
            }
        }

        return _demandTypeService.findByFilter( _dtFilter );
    }

    /**
     * Get the url of the manage demand types page, keeping the session and the sort parameters
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return the url
     */
    private UrlItem getUrlManageDemandTypes( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( getViewUrl( VIEW_MANAGE_DEMAND_TYPES ) );
        url.addParameter( CRMConstants.PARAMETER_SESSION, CRMConstants.PARAMETER_SESSION );

        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        if ( StringUtils.isNotBlank( strSortedAttributeName ) )
        {
            String strAscSort = request.getParameter( Parameters.SORTED_ASC );

            if ( StringUtils.isNotBlank( strAscSort ) )
            {
                url.addParameter( Parameters.SORTED_ASC, strAscSort );
            }

            url.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, strSortedAttributeName );
        }

        return url;
    }
}
