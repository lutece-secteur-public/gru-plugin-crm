-- liquibase formatted sql
-- changeset crm:update_db_crm-1.0.2-1.0.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- CRM-8 : Add the possibility to define the target of the demand type links
--
ALTER TABLE crm_demand_type ADD COLUMN target SMALLINT DEFAULT 0 NOT NULL;
