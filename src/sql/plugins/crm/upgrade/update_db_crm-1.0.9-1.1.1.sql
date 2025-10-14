-- liquibase formatted sql
-- changeset crm:update_db_crm-1.0.9-1.1.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- ALTER TABLE crm_demand
--
ALTER TABLE crm_demand  ADD COLUMN remote_id VARCHAR(255);
ALTER TABLE crm_demand ADD UNIQUE INDEX (id_demand_type,remote_id);



