-- ============================================================
-- CAREVAULT SAMPLE DATA SEEDING
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ==========================
-- 1) Users
-- ==========================
INSERT INTO USER (user_id, role, email, phone, password_hash, status) VALUES
(1,'PATIENT','alice@example.com','9999000001','hash_pass1','ACTIVE'),
(2,'PATIENT','bob@example.com','9999000002','hash_pass2','ACTIVE'),
(3,'DOCTOR','dr.smith@example.com','9999000101','hash_pass3','ACTIVE'),
(4,'LAB','lab1@example.com','9999000201','hash_pass4','ACTIVE'),
(5,'PHARMACY','pharma1@example.com','9999000301','hash_pass5','ACTIVE'),
(6,'ADMIN','admin1@example.com','9999000401','hash_pass6','ACTIVE'),
(7,'TPA','tpa1@example.com','9999000501','hash_pass7','ACTIVE');

-- ==========================
-- 2) Role Profiles
-- ==========================
INSERT INTO PATIENT_PROFILE (patient_id, name, dob, city, phone) VALUES
(1,'Alice','1990-05-15','New Delhi','9999000001'),
(2,'Bob','1988-11-23','Gurgaon','9999000002');

INSERT INTO DOCTOR_PROFILE (doctor_id, license_no, specialization) VALUES
(3,'DOC12345','Cardiology');

INSERT INTO LAB_PROFILE (lab_id, license_no, lab_name, address) VALUES
(4,'LAB123','LabCorp Delhi','123 Lab Street, Delhi');

INSERT INTO PHARMACY_PROFILE (pharmacy_id, license_no, pharmacy_name, address) VALUES
(5,'PHARMA123','Healthy Pharmacy','45 Pharma Road, Delhi');

INSERT INTO ADMIN_PROFILE (admin_id, permission_level) VALUES
(6,'SUPER_ADMIN');

INSERT INTO TPA_PROFILE (tpa_id, insurer_id, tpa_name, contact_details) VALUES
(7,1,'TPA One','tpa-contact@example.com');

-- ==========================
-- 3) Insurer
-- ==========================
INSERT INTO INSURER_MASTER (insurer_id, insurer_name, support_contact, status) VALUES
(1,'BestHealth Insurer','support@besthealth.com','ACTIVE');

-- ==========================
-- 4) Hospital
-- ==========================
INSERT INTO HOSPITAL (hospital_id, hospital_name, address, city, license_no) VALUES
(1,'City Hospital','12 Health Ave, Delhi','New Delhi','HOSP123');

-- ==========================
-- 5) Encounter
-- ==========================
INSERT INTO ENCOUNTER (encounter_id, patient_id, doctor_id, hospital_id, visit_datetime, complaint_summary, diagnosis_summary, doctor_notes) VALUES
(1,1,3,1,'2026-02-01 10:00:00','Chest pain','Angina','Patient needs monitoring');

-- ==========================
-- 6) Patient Notes
-- ==========================
INSERT INTO PATIENT_NOTE (note_id, encounter_id, patient_id, note_text) VALUES
(1,1,1,'Patient is responding well to treatment.');

-- ==========================
-- 7) Prescription + Items + Rules
-- ==========================
INSERT INTO PRESCRIPTION (prescription_id, encounter_id, patient_id, doctor_id, issue_date, expiry_date, status, verify_token) VALUES
(1,1,1,3,'2026-02-01','2026-03-01','ACTIVE','VERIFYTOKEN123');

INSERT INTO PRESCRIPTION_ITEM (rx_item_id, prescription_id, drug_name, strength, dosage, frequency, duration_days, instructions) VALUES
(1,1,'Aspirin','75mg','Tablet','Once Daily',30,'After food'),
(2,1,'Atorvastatin','10mg','Tablet','Once Daily',30,'Evening');

INSERT INTO PRESCRIPTION_RULES (prescription_id, refills_allowed, refills_used, partial_dispense_allowed, substitution_allowed) VALUES
(1,2,0,0,0);

-- ==========================
-- 8) Lab Orders
-- ==========================
INSERT INTO LAB_ORDER (lab_order_id, encounter_id, patient_id, doctor_id, lab_id, order_date, status) VALUES
(1,1,1,3,4,'2026-02-01','ORDERED');

INSERT INTO LAB_ORDER_TEST (lot_id, lab_order_id, test_name) VALUES
(1,1,'Blood Sugar'),
(2,1,'Lipid Profile');

INSERT INTO LAB_REPORT (lab_report_id, lab_order_id, uploaded_at, report_file_path, report_hash) VALUES
(1,1,'2026-02-02 14:00:00','/reports/lab1.pdf','hash_lab1');

INSERT INTO LAB_RESULT_VALUE (result_id, lab_report_id, component_name, value, unit, reference_range, abnormal_flag) VALUES
(1,1,'Glucose',95,'mg/dL','70-110',0),
(2,1,'LDL',120,'mg/dL','0-130',0);

-- ==========================
-- 9) Pharmacy Orders
-- ==========================
INSERT INTO PHARMACY_ORDER (order_id, prescription_id, patient_id, pharmacy_id, order_time, status) VALUES
(1,1,1,5,'2026-02-01 12:00:00','ACTIVE');

INSERT INTO PHARMACY_ORDER_ITEM (poi_id, order_id, drug_name, quantity) VALUES
(1,1,'Aspirin',30),
(2,1,'Atorvastatin',30);

-- ==========================
-- 10) Documents
-- ==========================
INSERT INTO DOCUMENT (doc_id, patient_id, doc_type, uploader_role, linked_entity_type, linked_entity_id, file_path, file_hash, visibility_tag) VALUES
(1,1,'Insurance','PATIENT','INSURANCE_POLICY',1,'/docs/insurance1.pdf','hash_doc1','PRIVATE');

-- ==========================
-- 11) Insurance Policy
-- ==========================
INSERT INTO INSURANCE_POLICY (policy_id, patient_id, insurer_id, policy_number, coverage_start, coverage_end, sum_insured, deductible, plan_type, status) VALUES
(1,1,1,'POL123','2026-01-01','2026-12-31',100000,5000,'HEALTH','ACTIVE');

-- ==========================
-- 12) Claims
-- ==========================
INSERT INTO CLAIM (claim_id, policy_id, patient_id, hospital_id, admission_date, discharge_date, claim_type, preauth_id, status, approved_amount) VALUES
(1,1,1,1,'2026-02-01','2026-02-03','HOSPITALIZATION','PRE123','SUBMITTED',0);

INSERT INTO CLAIM_STATUS_HISTORY (history_id, claim_id, old_status, new_status, changed_by) VALUES
(1,1,NULL,'SUBMITTED',1);

INSERT INTO CLAIM_QUERY (query_id, claim_id, raised_by_tpa_id, query_text, status) VALUES
(1,1,7,'Provide missing documents','OPEN');

INSERT INTO CLAIM_RESPONSE (response_id, query_id, responder_user_id, response_text) VALUES
(1,1,6,'Documents provided');

INSERT INTO CLAIM_DOCUMENT (claim_doc_id, claim_id, doc_id) VALUES
(1,1,1);

-- ==========================
-- 13) SymptomLog + PatientQuery + LLMResponse
-- ==========================
INSERT INTO SYMPTOM_LOG (symptom_id, patient_id, symptom_text, severity, onset_date) VALUES
(1,1,'Chest Pain',7,'2026-01-31');

INSERT INTO PATIENT_QUERY (query_id, patient_id, question_text) VALUES
(1,1,'What is the next step in treatment?');

INSERT INTO LLM_RESPONSE (response_id, query_id, response_text) VALUES
(1,1,'Patient should continue prescribed medication and monitor vitals.');

-- ==========================
-- 14) Consent Grant + Audit Log
-- ==========================
INSERT INTO CONSENT_GRANT (consent_id, patient_id, grantee_user_id, scope, valid_from, valid_to, status) VALUES
(1,1,3,'READ_ENCOUNTER','2026-02-01','2026-03-01','ACTIVE');

INSERT INTO AUDIT_LOG (log_id, user_id, action_type, entity_type, entity_id) VALUES
(1,1,'CREATE','PRESCRIPTION',1);

SET FOREIGN_KEY_CHECKS = 1;
