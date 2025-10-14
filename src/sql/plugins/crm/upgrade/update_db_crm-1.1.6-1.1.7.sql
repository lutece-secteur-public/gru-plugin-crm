-- liquibase formatted sql
-- changeset crm:update_db_crm-1.1.6-1.1.7.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- ALTER TABLE crm_category
--
ALTER TABLE crm_category ADD COLUMN code VARCHAR(50) UNIQUE;