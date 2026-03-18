-- ==========================================
-- Recommended Indexes for Healthcare DB
-- ==========================================

-- 1. user
CREATE INDEX idx_user_email ON user(email);
CREATE INDEX idx_user_phone ON user(phone);
CREATE INDEX idx_user_role ON user(role);

-- 2. patient_profile
CREATE INDEX idx_patient_profile_name ON patient_profile(name);
CREATE INDEX idx_patient_profile_city ON patient_profile(city);

-- 3. doctor_profile
CREATE INDEX idx_doctor_profile_license ON doctor_profile(license_no);
CREATE INDEX idx_doctor_profile_specialization ON doctor_profile(specialization);

-- 4. lab_profile
CREATE INDEX idx_lab_profile_license ON lab_profile(license_no);
CREATE INDEX idx_lab_profile_name ON lab_profile(lab_name);

-- 5. pharmacy_profile
CREATE INDEX idx_pharmacy_profile_license ON pharmacy_profile(license_no);
CREATE INDEX idx_pharmacy_profile_name ON pharmacy_profile(pharmacy_name);

-- 6. insurer_master
CREATE INDEX idx_insurer_master_name ON insurer_master(insurer_name);

-- 7. tpa_profile
CREATE INDEX idx_tpa_profile_name ON tpa_profile(tpa_name);
CREATE INDEX idx_tpa_profile_insurer_id ON tpa_profile(insurer_id);

-- 8. admin_profile
CREATE INDEX idx_admin_profile_permission ON admin_profile(permission_level);

-- 9. hospital
CREATE INDEX idx_hospital_name ON hospital(hospital_name);
CREATE INDEX idx_hospital_city ON hospital(city);

-- 10. encounter
CREATE INDEX idx_encounter_patient ON encounter(patient_id);
CREATE INDEX idx_encounter_doctor ON encounter(doctor_id);
CREATE INDEX idx_encounter_hospital ON encounter(hospital_id);
CREATE INDEX idx_encounter_visit_datetime ON encounter(visit_datetime);

-- 11. patient_note
CREATE INDEX idx_patient_note_encounter ON patient_note(encounter_id);
CREATE INDEX idx_patient_note_patient ON patient_note(patient_id);

-- 12. prescription
CREATE INDEX idx_prescription_patient ON prescription(patient_id);
CREATE INDEX idx_prescription_doctor ON prescription(doctor_id);
CREATE INDEX idx_prescription_encounter ON prescription(encounter_id);
CREATE INDEX idx_prescription_issue_date ON prescription(issue_date);

-- 13. prescription_item
CREATE INDEX idx_prescription_item_rx ON prescription_item(prescription_id);
CREATE INDEX idx_prescription_item_drug ON prescription_item(drug_name);

-- 14. prescription_rules
CREATE INDEX idx_prescription_rules_rx ON prescription_rules(prescription_id);

-- 15. lab_order
CREATE INDEX idx_lab_order_patient ON lab_order(patient_id);
CREATE INDEX idx_lab_order_doctor ON lab_order(doctor_id);
CREATE INDEX idx_lab_order_lab ON lab_order(lab_id);
CREATE INDEX idx_lab_order_encounter ON lab_order(encounter_id);
CREATE INDEX idx_lab_order_order_date ON lab_order(order_date);

-- 16. lab_order_test
CREATE INDEX idx_lab_order_test_order ON lab_order_test(lab_order_id);
CREATE INDEX idx_lab_order_test_name ON lab_order_test(test_name);

-- 17. lab_report
CREATE INDEX idx_lab_report_lab_order ON lab_report(lab_order_id);
CREATE INDEX idx_lab_report_uploaded_at ON lab_report(uploaded_at);

-- 18. lab_result_value
CREATE INDEX idx_lab_result_value_report ON lab_result_value(lab_report_id);
CREATE INDEX idx_lab_result_value_component ON lab_result_value(component_name);

-- 19. pharmacy_order
CREATE INDEX idx_pharmacy_order_patient ON pharmacy_order(patient_id);
CREATE INDEX idx_pharmacy_order_pharmacy ON pharmacy_order(pharmacy_id);
CREATE INDEX idx_pharmacy_order_prescription ON pharmacy_order(prescription_id);
CREATE INDEX idx_pharmacy_order_time ON pharmacy_order(order_time);

-- 20. pharmacy_order_item
CREATE INDEX idx_pharmacy_order_item_order ON pharmacy_order_item(order_id);
CREATE INDEX idx_pharmacy_order_item_drug ON pharmacy_order_item(drug_name);

-- 21. document
CREATE INDEX idx_document_patient ON document(patient_id);
CREATE INDEX idx_document_type ON document(doc_type);

-- 22. insurance_policy
CREATE INDEX idx_insurance_policy_patient ON insurance_policy(patient_id);
CREATE INDEX idx_insurance_policy_insurer ON insurance_policy(insurer_id);
CREATE INDEX idx_insurance_policy_policy_number ON insurance_policy(policy_number);

-- 23. claim
CREATE INDEX idx_claim_patient ON claim(patient_id);
CREATE INDEX idx_claim_policy ON claim(policy_id);
CREATE INDEX idx_claim_hospital ON claim(hospital_id);
CREATE INDEX idx_claim_status ON claim(status);

-- 24. claim_query
CREATE INDEX idx_claim_query_claim ON claim_query(claim_id);
CREATE INDEX idx_claim_query_tpa ON claim_query(raised_by_tpa_id);
CREATE INDEX idx_claim_query_status ON claim_query(status);

-- 25. claim_response
CREATE INDEX idx_claim_response_query ON claim_response(query_id);
CREATE INDEX idx_claim_response_responder ON claim_response(responder_user_id);

-- 26. claim_document
CREATE INDEX idx_claim_document_claim ON claim_document(claim_id);
CREATE INDEX idx_claim_document_doc ON claim_document(doc_id);

-- 27. claim_status_history
CREATE INDEX idx_claim_status_history_claim ON claim_status_history(claim_id);
CREATE INDEX idx_claim_status_history_changed_by ON claim_status_history(changed_by);

-- 28. symptom_log
CREATE INDEX idx_symptom_log_patient ON symptom_log(patient_id);
CREATE INDEX idx_symptom_log_onset_date ON symptom_log(onset_date);

-- 29. patient_query
CREATE INDEX idx_patient_query_patient ON patient_query(patient_id);
CREATE INDEX idx_patient_query_report ON patient_query(linked_report_id);
CREATE INDEX idx_patient_query_prescription ON patient_query(linked_prescription_id);
CREATE INDEX idx_patient_query_created_at ON patient_query(created_at);

-- 30. llm_response
CREATE INDEX idx_llm_response_query ON llm_response(query_id);
CREATE INDEX idx_llm_response_created_at ON llm_response(created_at);

-- 31. consent_grant
CREATE INDEX idx_consent_grant_patient ON consent_grant(patient_id);
CREATE INDEX idx_consent_grant_grantee ON consent_grant(grantee_user_id);
CREATE INDEX idx_consent_grant_valid_from ON consent_grant(valid_from);

-- 32. audit_log
CREATE INDEX idx_audit_log_user ON audit_log(user_id);
CREATE INDEX idx_audit_log_action ON audit_log(action_type);
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp);