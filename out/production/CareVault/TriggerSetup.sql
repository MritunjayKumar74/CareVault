-- ============================================================
--  Mrisuvas CareVault - Trigger Setup
--  Database: medical_db
-- ============================================================

USE medical_db;

DROP TRIGGER IF EXISTS trg_check_refill_limit;
DROP TRIGGER IF EXISTS trg_claim_status_history;

DELIMITER $$

-- ============================================================
-- TRIGGER 1: Block pharmacy order if prescription is invalid
-- ============================================================
CREATE TRIGGER trg_check_refill_limit
    BEFORE INSERT ON pharmacy_order
    FOR EACH ROW
BEGIN
    DECLARE v_status          VARCHAR(20);
    DECLARE v_expiry_date     DATE;
    DECLARE v_refills_allowed INT;
    DECLARE v_refills_used    INT;

    SELECT status, expiry_date
    INTO   v_status, v_expiry_date
    FROM   prescription
    WHERE  prescription_id = NEW.prescription_id;

    IF v_status IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'TRIGGER: Prescription not found.';
    END IF;
    
    IF v_status != 'ACTIVE' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'TRIGGER: Prescription is not ACTIVE.';
    END IF;

    IF v_expiry_date IS NOT NULL AND v_expiry_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'TRIGGER: Prescription has expired.';
    END IF;
    
    SELECT refills_allowed, refills_used
    INTO   v_refills_allowed, v_refills_used
    FROM   prescription_rules
    WHERE  prescription_id = NEW.prescription_id;
    
    IF v_refills_used >= v_refills_allowed THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'TRIGGER: Refill limit reached. Cannot place order.';
    END IF;    
END$$

-- ============================================================
-- TRIGGER 2: Auto-log claim status changes to history table
-- ============================================================
CREATE TRIGGER trg_claim_status_history
    AFTER UPDATE ON claim
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO claim_status_history
            (claim_id, old_status, new_status, changed_by, changed_at)
        VALUES
            (NEW.claim_id, OLD.status, NEW.status, NEW.patient_id, NOW());
END IF;
END$$


DROP TRIGGER IF EXISTS trg_audit_pharmacy_order;

-- =============================================================
-- TRIGGER 3: Auto-insert pharmacy order creation into audit log
-- =============================================================

CREATE TRIGGER trg_audit_pharmacy_order
AFTER INSERT ON pharmacy_order
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (user_id, action_type, entity_type, entity_id, timestamp)
    VALUES (NEW.patient_id, 'CREATE', 'PHARMACY_ORDER', NEW.order_id, NOW());
END$$

DELIMITER ;

-- Verify
SHOW TRIGGERS FROM medical_db;