-- liquibase formatted sql
-- changeset crm:update_db_crm-1.0.1-1.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Update crm_notification
--
ALTER TABLE crm_notification CHANGE object object VARCHAR(255) DEFAULT '' NOT NULL;
