-- Test Categories Sample Data
-- This script creates sample test categories for organizing different types of exams

SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- Delete existing test categories if needed (optional - remove if you want to keep existing data)
-- DELETE FROM test_categories WHERE 1=1;

-- ================================================================================
-- ENGLISH PROFICIENCY TESTS
-- ================================================================================

-- TOEIC Test
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'TOEIC')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'TOEIC', 'TOEIC Test', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- TOEFL Test
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'TOEFL')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'TOEFL', 'TOEFL iBT', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- IELTS Test
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'IELTS')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'IELTS', 'IELTS Academic', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Cambridge English
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'CAMBRIDGE')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'CAMBRIDGE', 'Cambridge English Exams', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- ACADEMIC SUBJECTS
-- ================================================================================

-- Mathematics
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'MATH')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'MATH', 'Mathematics', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Science
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'SCIENCE')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'SCIENCE', 'Science', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Physics
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'PHYSICS')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'PHYSICS', 'Physics', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Chemistry
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'CHEMISTRY')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'CHEMISTRY', 'Chemistry', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Biology
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'BIOLOGY')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'BIOLOGY', 'Biology', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- STANDARDIZED TESTS
-- ================================================================================

-- SAT
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'SAT')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'SAT', 'SAT Reasoning Test', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ACT
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'ACT')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'ACT', 'ACT Test', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- GRE
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'GRE')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'GRE', 'GRE General Test', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- GMAT
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'GMAT')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'GMAT', 'GMAT', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- PROFESSIONAL CERTIFICATIONS
-- ================================================================================

-- IT Certifications
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'IT_CERT')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'IT_CERT', 'IT Certifications', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Project Management
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'PM_CERT')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'PM_CERT', 'Project Management', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Accounting & Finance
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'ACCT_CERT')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'ACCT_CERT', 'Accounting & Finance', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Medical & Healthcare
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'MEDICAL')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'MEDICAL', 'Medical & Healthcare', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- LANGUAGE TESTS (OTHER THAN ENGLISH)
-- ================================================================================

-- Japanese Language
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'JLPT')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'JLPT', 'Japanese Language (JLPT)', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Chinese Language
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'HSK')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'HSK', 'Chinese Language (HSK)', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Korean Language
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'TOPIK')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'TOPIK', 'Korean Language (TOPIK)', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Spanish Language
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'DELE')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'DELE', 'Spanish Language (DELE)', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- French Language
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'DELF')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'DELF', 'French Language (DELF/DALF)', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- German Language
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'GOETHE')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'GOETHE', 'German Language (Goethe)', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- APTITUDE & SKILL TESTS
-- ================================================================================

-- Cognitive Ability
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'COGNITIVE')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'COGNITIVE', 'Cognitive Ability Tests', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Personality Assessment
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'PERSONALITY')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'PERSONALITY', 'Personality Assessment', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Typing & Computer Skills
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'TYPING')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'TYPING', 'Typing & Computer Skills', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- PRACTICE & MOCK TESTS
-- ================================================================================

-- General Practice
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'PRACTICE')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'PRACTICE', 'Practice Tests', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Sample Tests
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'SAMPLE')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'SAMPLE', 'Sample Tests', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Custom Tests
IF NOT EXISTS (SELECT 1 FROM test_categories WHERE code = 'CUSTOM')
    INSERT INTO test_categories (id, code, name, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'CUSTOM', 'Custom Tests', SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

COMMIT TRANSACTION;

PRINT 'Successfully created test categories!';
PRINT 'Total categories: 28';
PRINT '';
PRINT 'Category Groups:';
PRINT '- English Proficiency: 4 categories (TOEIC, TOEFL, IELTS, Cambridge)';
PRINT '- Academic Subjects: 5 categories (Math, Science, Physics, Chemistry, Biology)';
PRINT '- Standardized Tests: 4 categories (SAT, ACT, GRE, GMAT)';
PRINT '- Professional Certifications: 4 categories (IT, Project Management, Accounting, Medical)';
PRINT '- Language Tests: 6 categories (Japanese, Chinese, Korean, Spanish, French, German)';
PRINT '- Aptitude & Skills: 3 categories (Cognitive, Personality, Typing)';
PRINT '- Practice & Mock: 3 categories (Practice, Sample, Custom)';
