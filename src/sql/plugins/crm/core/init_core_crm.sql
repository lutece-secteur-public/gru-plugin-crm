-- liquibase formatted sql
-- changeset crm:init_core_crm.sql
-- validCheckSum: 8:b01263849b482fe7e42dc19f69bce11b
-- validCheckSum: 9:118f075658005bf5ba29a40f0f0765ea
-- preconditions onFail:MARK_RAN onError:WARN

--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('CRM_DEMAND_TYPES_MANAGEMENT','crm.adminFeature.demand_type_management.name',3,'jsp/admin/plugins/crm/ManageDemandTypes.jsp','crm.adminFeature.demand_type_management.description',0,'crm','APPLICATIONS','ti ti-list', NULL);

--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('CRM_DEMAND_TYPES_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('CRM_DEMAND_TYPES_MANAGEMENT',2);

-- changeset crm:init_core_crm-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_type WHERE id_portlet_type = 'CRM_DEMAND_TYPE_PORTLET'
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form,icon_name) VALUES ('CRM_DEMAND_TYPE_PORTLET','crm.portlet.demandType.name','plugins/crm/CreateDemandTypePortlet.jsp','plugins/crm/ModifyDemandTypePortlet.jsp','fr.paris.lutece.plugins.crm.business.portlet.DemandTypePortletHome','crm','plugins/crm/DoCreateDemandTypePortlet.jsp','','/admin/plugins/crm/portlet/list_category.html','','plugins/crm/DoModifyDemandTypePortlet.jsp','','/admin/plugins/crm/portlet/list_category.html','',NULL);

-- changeset crm:init_core_crm-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'CRM_DEMAND_TYPE_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('CRM_DEMAND_TYPE_PORTLET', 'Défaut', 'skin/plugins/crm/portlet/demand_type_portlet.html');
