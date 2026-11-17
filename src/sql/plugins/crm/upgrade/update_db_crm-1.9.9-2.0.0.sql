-- liquibase formatted sql
-- changeset crm:update_db_crm-1.9.9-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url = 'ti ti-list' WHERE id_right = 'CRM_DEMAND_TYPES_MANAGEMENT';

-- changeset crm:update_db_crm-1.9.9-2.0.0-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_type WHERE id_portlet_type = 'CRM_DEMAND_TYPE_PORTLET'
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form,icon_name) VALUES ('CRM_DEMAND_TYPE_PORTLET','crm.portlet.demandType.name','plugins/crm/CreateDemandTypePortlet.jsp','plugins/crm/ModifyDemandTypePortlet.jsp','fr.paris.lutece.plugins.crm.business.portlet.DemandTypePortletHome','crm','plugins/crm/DoCreateDemandTypePortlet.jsp','','/admin/plugins/crm/portlet/list_category.html','','plugins/crm/DoModifyDemandTypePortlet.jsp','','/admin/plugins/crm/portlet/list_category.html','',NULL);

-- changeset crm:update_db_crm-1.9.9-2.0.0-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'CRM_DEMAND_TYPE_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('CRM_DEMAND_TYPE_PORTLET', 'Défaut', 'skin/plugins/crm/portlet/demand_type_portlet.html');

-- changeset crm:update_db_crm-1.9.9-2.0.0-rev3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:3 SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=database() AND TABLE_NAME IN ('core_style_mode_stylesheet','core_stylesheet','core_style')
DELETE FROM core_style_mode_stylesheet WHERE id_style = 1012 AND id_stylesheet = 809;
DELETE FROM core_stylesheet WHERE id_stylesheet = 809;
DELETE FROM core_style WHERE id_style = 1012;

-- changeset crm:update_db_crm-1.9.9-2.0.0-rev4.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_portlet_type SET url_docreate = 'plugins/crm/DoCreateDemandTypePortlet.jsp', url_domodify = 'plugins/crm/DoModifyDemandTypePortlet.jsp' WHERE id_portlet_type = 'CRM_DEMAND_TYPE_PORTLET';
