
SET FOREIGN_KEY_CHECKS = 0;  

DROP TABLE IF EXISTS audit_log;  
DROP TABLE IF EXISTS consent_grant;  
DROP TABLE IF EXISTS llm_response;  
DROP TABLE IF EXISTS patient_query;  
DROP TABLE IF EXISTS symptom_log;  
DROP TABLE IF EXISTS claim_status_history;  
DROP TABLE IF EXISTS claim_document;  
DROP TABLE IF EXISTS claim_response;  
DROP TABLE IF EXISTS claim_query;  
DROP TABLE IF EXISTS claim;  
DROP TABLE IF EXISTS insurance_policy;  
DROP TABLE IF EXISTS document;  
DROP TABLE IF EXISTS pharmacy_order_item;  
DROP TABLE IF EXISTS pharmacy_order;  
DROP TABLE IF EXISTS lab_result_value;  
DROP TABLE IF EXISTS lab_report;  
DROP TABLE IF EXISTS lab_order_test;  
DROP TABLE IF EXISTS lab_order;  
DROP TABLE IF EXISTS prescription_rules;  
DROP TABLE IF EXISTS prescription_item;  
DROP TABLE IF EXISTS prescription;  
DROP TABLE IF EXISTS patient_note;  
DROP TABLE IF EXISTS encounter;  
DROP TABLE IF EXISTS admin_profile;  
DROP TABLE IF EXISTS tpa_profile;  
DROP TABLE IF EXISTS pharmacy_profile;  
DROP TABLE IF EXISTS lab_profile;  
DROP TABLE IF EXISTS doctor_profile;  
DROP TABLE IF EXISTS patient_profile;  
DROP TABLE IF EXISTS hospital;  
DROP TABLE IF EXISTS insurer_master;  
DROP TABLE IF EXISTS user;  
 
SET FOREIGN_KEY_CHECKS = 1;  


CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    role ENUM('PATIENT','DOCTOR','LAB','PHARMACY','ADMIN','TPA') NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE patient_profile (
    patient_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dob DATE NOT NULL,
    city VARCHAR(100),
    phone VARCHAR(20),
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE doctor_profile (
    doctor_id INT PRIMARY KEY,
    license_no VARCHAR(50) UNIQUE NOT NULL,
    specialization VARCHAR(100),
    FOREIGN KEY (doctor_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE lab_profile (
    lab_id INT PRIMARY KEY,
    license_no VARCHAR(50) UNIQUE NOT NULL,
    lab_name VARCHAR(255) NOT NULL,
    address TEXT,
    FOREIGN KEY (lab_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE pharmacy_profile (
    pharmacy_id INT PRIMARY KEY,
    license_no VARCHAR(50) UNIQUE NOT NULL,
    pharmacy_name VARCHAR(255) NOT NULL,
    address TEXT,
    FOREIGN KEY (pharmacy_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE admin_profile (
    admin_id INT PRIMARY KEY,
    permission_level VARCHAR(50),
    FOREIGN KEY (admin_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE insurer_master (
    insurer_id INT AUTO_INCREMENT PRIMARY KEY,
    insurer_name VARCHAR(255) NOT NULL,
    support_contact VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE TABLE tpa_profile (
    tpa_id INT PRIMARY KEY,
    insurer_id INT,
    tpa_name VARCHAR(255) NOT NULL,
    contact_details TEXT,
    FOREIGN KEY (tpa_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (insurer_id) REFERENCES insurer_master(insurer_id) ON DELETE SET NULL
);

CREATE TABLE hospital (
    hospital_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_name VARCHAR(255) NOT NULL,
    address TEXT,
    city VARCHAR(100),
    license_no VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE encounter (
    encounter_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    hospital_id INT,
    visit_datetime TIMESTAMP NOT NULL,
    complaint_summary TEXT,
    diagnosis_summary TEXT,
    doctor_notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (hospital_id) REFERENCES hospital(hospital_id) ON DELETE SET NULL
);

CREATE TABLE patient_note (
    note_id INT AUTO_INCREMENT PRIMARY KEY,
    encounter_id INT NOT NULL,
    patient_id INT NOT NULL,
    note_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (encounter_id) REFERENCES encounter(encounter_id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE prescription (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    encounter_id INT NOT NULL,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    verify_token VARCHAR(100) UNIQUE,
    FOREIGN KEY (encounter_id) REFERENCES encounter(encounter_id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE prescription_item (
    rx_item_id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id INT NOT NULL,
    drug_name VARCHAR(255) NOT NULL,
    strength VARCHAR(50),
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration_days INT,
    instructions TEXT,
    FOREIGN KEY (prescription_id) REFERENCES prescription(prescription_id) ON DELETE CASCADE
);

CREATE TABLE prescription_rules (
    prescription_id INT PRIMARY KEY,
    refills_allowed INT DEFAULT 0,
    refills_used INT DEFAULT 0,
    partial_dispense_allowed TINYINT(1) DEFAULT 0,
    substitution_allowed TINYINT(1) DEFAULT 0,
    FOREIGN KEY (prescription_id) REFERENCES prescription(prescription_id) ON DELETE CASCADE
);

CREATE TABLE lab_order (
    lab_order_id INT AUTO_INCREMENT PRIMARY KEY,
    encounter_id INT NOT NULL,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    lab_id INT,
    order_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (encounter_id) REFERENCES encounter(encounter_id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (lab_id) REFERENCES user(user_id) ON DELETE SET NULL
);

CREATE TABLE lab_order_test (
    lot_id INT AUTO_INCREMENT PRIMARY KEY,
    lab_order_id INT NOT NULL,
    test_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (lab_order_id) REFERENCES lab_order(lab_order_id) ON DELETE CASCADE
);

CREATE TABLE lab_report (
    lab_report_id INT AUTO_INCREMENT PRIMARY KEY,
    lab_order_id INT UNIQUE NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    report_file_path VARCHAR(500),
    report_hash VARCHAR(64),
    FOREIGN KEY (lab_order_id) REFERENCES lab_order(lab_order_id) ON DELETE CASCADE
);

CREATE TABLE lab_result_value (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    lab_report_id INT NOT NULL,
    component_name VARCHAR(255) NOT NULL,
    value VARCHAR(100),
    unit VARCHAR(50),
    reference_range VARCHAR(100),
    abnormal_flag TINYINT(1) DEFAULT 0,
    FOREIGN KEY (lab_report_id) REFERENCES lab_report(lab_report_id) ON DELETE CASCADE
);

CREATE TABLE pharmacy_order (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id INT NOT NULL,
    patient_id INT NOT NULL,
    pharmacy_id INT NOT NULL,
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (prescription_id) REFERENCES prescription(prescription_id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (pharmacy_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE pharmacy_order_item (
    poi_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    drug_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    dispensed_time TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES pharmacy_order(order_id) ON DELETE CASCADE
);

CREATE TABLE document (
    doc_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doc_type VARCHAR(50),
    uploader_role VARCHAR(20),
    linked_entity_type VARCHAR(50),
    linked_entity_id INT,
    file_path VARCHAR(500),
    file_hash VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    visibility_tag VARCHAR(50),
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE insurance_policy (
    policy_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    insurer_id INT NOT NULL,
    policy_number VARCHAR(100) NOT NULL,
    coverage_start DATE NOT NULL,
    coverage_end DATE NOT NULL,
    sum_insured DECIMAL(15,2),
    deductible DECIMAL(15,2),
    plan_type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (insurer_id) REFERENCES insurer_master(insurer_id) ON DELETE CASCADE
);

CREATE TABLE claim (
    claim_id INT AUTO_INCREMENT PRIMARY KEY,
    policy_id INT NOT NULL,
    patient_id INT NOT NULL,
    hospital_id INT,
    admission_date DATE,
    discharge_date DATE,
    claim_type VARCHAR(50),
    preauth_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'SUBMITTED',
    approved_amount DECIMAL(15,2),
    rejection_reason TEXT,
    FOREIGN KEY (policy_id) REFERENCES insurance_policy(policy_id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (hospital_id) REFERENCES hospital(hospital_id) ON DELETE SET NULL
);

CREATE TABLE claim_query (
    query_id INT AUTO_INCREMENT PRIMARY KEY,
    claim_id INT NOT NULL,
    raised_by_tpa_id INT,
    query_text TEXT NOT NULL,
    raised_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    status VARCHAR(20) DEFAULT 'OPEN',
    FOREIGN KEY (claim_id) REFERENCES claim(claim_id) ON DELETE CASCADE,
    FOREIGN KEY (raised_by_tpa_id) REFERENCES user(user_id) ON DELETE SET NULL
);

CREATE TABLE claim_response (
    response_id INT AUTO_INCREMENT PRIMARY KEY,
    query_id INT UNIQUE NOT NULL,
    responder_user_id INT NOT NULL,
    response_text TEXT NOT NULL,
    responded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (query_id) REFERENCES claim_query(query_id) ON DELETE CASCADE,
    FOREIGN KEY (responder_user_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE claim_document (
    claim_doc_id INT AUTO_INCREMENT PRIMARY KEY,
    claim_id INT NOT NULL,
    doc_id INT NOT NULL,
    FOREIGN KEY (claim_id) REFERENCES claim(claim_id) ON DELETE CASCADE,
    FOREIGN KEY (doc_id) REFERENCES document(doc_id) ON DELETE CASCADE
);

CREATE TABLE claim_status_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    claim_id INT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by INT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (claim_id) REFERENCES claim(claim_id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES user(user_id) ON DELETE SET NULL
);

CREATE TABLE symptom_log (
    symptom_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    symptom_text TEXT NOT NULL,
    severity VARCHAR(20),
    onset_date DATE,
    duration VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE patient_query (
    query_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    linked_report_id INT,
    linked_prescription_id INT,
    question_text TEXT NOT NULL,
    consent_used TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (linked_report_id) REFERENCES lab_report(lab_report_id) ON DELETE SET NULL,
    FOREIGN KEY (linked_prescription_id) REFERENCES prescription(prescription_id) ON DELETE SET NULL
);

CREATE TABLE llm_response (
    response_id INT AUTO_INCREMENT PRIMARY KEY,
    query_id INT UNIQUE NOT NULL,
    response_text TEXT NOT NULL,
    safety_label VARCHAR(50),
    disclaimer_version VARCHAR(20),
    model_version VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (query_id) REFERENCES patient_query(query_id) ON DELETE CASCADE
);

CREATE TABLE consent_grant (
    consent_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    grantee_user_id INT NOT NULL,
    scope TEXT,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (patient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (grantee_user_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE SET NULL
);
