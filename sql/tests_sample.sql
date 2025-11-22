-- Sample Tests Data
-- This script creates 10 sample tests with proper relationships to test categories
-- 
-- IMPORTANT: Run this script AFTER the following scripts:
-- 1. test_categories_sample.sql (creates test categories)
-- 2. toeic_restructured_complete.sql (creates question sets and questions)
--
-- The script will link TOEIC tests to existing question sets created by toeic_restructured_complete.sql

SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- Get test category IDs
DECLARE @toeic_cat_id UNIQUEIDENTIFIER = (SELECT id FROM test_categories WHERE code = 'TOEIC');
DECLARE @toefl_cat_id UNIQUEIDENTIFIER = (SELECT id FROM test_categories WHERE code = 'TOEFL');
DECLARE @ielts_cat_id UNIQUEIDENTIFIER = (SELECT id FROM test_categories WHERE code = 'IELTS');
DECLARE @math_cat_id UNIQUEIDENTIFIER = (SELECT id FROM test_categories WHERE code = 'MATH');
DECLARE @science_cat_id UNIQUEIDENTIFIER = (SELECT id FROM test_categories WHERE code = 'SCIENCE');
DECLARE @sat_cat_id UNIQUEIDENTIFIER = (SELECT id FROM test_categories WHERE code = 'SAT');
DECLARE @practice_cat_id UNIQUEIDENTIFIER = (SELECT id FROM test_categories WHERE code = 'PRACTICE');

-- ================================================================================
-- TEST 1: TOEIC Practice Test 1
-- ================================================================================
DECLARE @test1_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'TOEIC Practice Test 1')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test1_id,
        1,
        'TOEIC Practice Test 1',
        N'Full-length TOEIC practice test covering all 7 parts. This test simulates the actual TOEIC exam format with Listening (Parts 1-4) and Reading (Parts 5-7) sections.',
        'Listening & Reading',
        120,
        N'Recommended for intermediate to advanced English learners preparing for the TOEIC exam.',
        @toeic_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 1: TOEIC Practice Test 1';
    
    -- Link all TOEIC question sets to Test 1 (200 questions across all 7 parts)
    IF EXISTS (SELECT 1 FROM question_sets WHERE title LIKE 'Part%' AND is_deleted = 0)
    BEGIN
        INSERT INTO test_question_set_details (id, test_id, question_set_id, display_order, inserted_at, updated_at, is_deleted)
        SELECT NEWID(), @test1_id, qs.id, qs.display_order, SYSUTCDATETIME(), SYSUTCDATETIME(), 0
        FROM question_sets qs
        WHERE qs.title LIKE 'Part%'
        AND qs.is_deleted = 0
        ORDER BY qs.display_order;
        
        PRINT 'Linked ' + CAST(@@ROWCOUNT AS VARCHAR) + ' question sets to Test 1';
    END
    ELSE
    BEGIN
        PRINT 'WARNING: No question sets found. Run toeic_restructured_complete.sql first.';
    END
END

-- ================================================================================
-- TEST 2: TOEIC Practice Test 2
-- ================================================================================
DECLARE @test2_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'TOEIC Practice Test 2')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test2_id,
        2,
        'TOEIC Practice Test 2',
        N'Complete TOEIC practice examination featuring authentic business and workplace scenarios. Includes all question types from Parts 1-7.',
        'Listening & Reading',
        120,
        N'Focus on business vocabulary and professional communication contexts.',
        @toeic_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 2: TOEIC Practice Test 2';
    
    -- Link all TOEIC question sets to Test 2 (same question sets, different test instance)
    IF EXISTS (SELECT 1 FROM question_sets WHERE title LIKE 'Part%' AND is_deleted = 0)
    BEGIN
        INSERT INTO test_question_set_details (id, test_id, question_set_id, display_order, inserted_at, updated_at, is_deleted)
        SELECT NEWID(), @test2_id, qs.id, qs.display_order, SYSUTCDATETIME(), SYSUTCDATETIME(), 0
        FROM question_sets qs
        WHERE qs.title LIKE 'Part%'
        AND qs.is_deleted = 0
        ORDER BY qs.display_order;
        
        PRINT 'Linked ' + CAST(@@ROWCOUNT AS VARCHAR) + ' question sets to Test 2';
    END
END

-- ================================================================================
-- TEST 3: TOEIC Listening Focus
-- ================================================================================
DECLARE @test3_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'TOEIC Listening Focus')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test3_id,
        3,
        'TOEIC Listening Focus',
        N'Concentrated practice on TOEIC Listening section only (Parts 1-4). Perfect for improving listening comprehension skills.',
        'Listening',
        45,
        N'100 listening questions covering photographs, question-response, conversations, and short talks.',
        @toeic_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 3: TOEIC Listening Focus';
    
    -- Link only Listening section question sets (Parts 1-4)
    IF EXISTS (SELECT 1 FROM question_sets WHERE title LIKE 'Part 1%' AND is_deleted = 0)
    BEGIN
        INSERT INTO test_question_set_details (id, test_id, question_set_id, display_order, inserted_at, updated_at, is_deleted)
        SELECT NEWID(), @test3_id, qs.id, qs.display_order, SYSUTCDATETIME(), SYSUTCDATETIME(), 0
        FROM question_sets qs
        WHERE (qs.title LIKE 'Part 1%' OR qs.title LIKE 'Part 2%' OR qs.title LIKE 'Part 3%' OR qs.title LIKE 'Part 4%')
        AND qs.is_deleted = 0
        ORDER BY qs.display_order;
        
        PRINT 'Linked ' + CAST(@@ROWCOUNT AS VARCHAR) + ' Listening question sets (Parts 1-4) to Test 3';
    END
END

-- ================================================================================
-- TEST 4: TOEIC Reading Focus
-- ================================================================================
DECLARE @test4_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'TOEIC Reading Focus')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test4_id,
        4,
        'TOEIC Reading Focus',
        N'Specialized TOEIC Reading section practice (Parts 5-7). Enhance your grammar, vocabulary, and reading comprehension skills.',
        'Reading',
        75,
        N'100 reading questions including incomplete sentences, text completion, and reading comprehension passages.',
        @toeic_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 4: TOEIC Reading Focus';
    
    -- Link only Reading section question sets (Parts 5-7)
    IF EXISTS (SELECT 1 FROM question_sets WHERE title LIKE 'Part 5%' AND is_deleted = 0)
    BEGIN
        INSERT INTO test_question_set_details (id, test_id, question_set_id, display_order, inserted_at, updated_at, is_deleted)
        SELECT NEWID(), @test4_id, qs.id, qs.display_order, SYSUTCDATETIME(), SYSUTCDATETIME(), 0
        FROM question_sets qs
        WHERE (qs.title LIKE 'Part 5%' OR qs.title LIKE 'Part 6%' OR qs.title LIKE 'Part 7%')
        AND qs.is_deleted = 0
        ORDER BY qs.display_order;
        
        PRINT 'Linked ' + CAST(@@ROWCOUNT AS VARCHAR) + ' Reading question sets (Parts 5-7) to Test 4';
    END
END

-- ================================================================================
-- TEST 5: TOEFL iBT Practice Test
-- ================================================================================
DECLARE @test5_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'TOEFL iBT Practice Test')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test5_id,
        5,
        'TOEFL iBT Practice Test',
        N'Comprehensive TOEFL iBT practice test covering Reading and Listening sections. Academic English proficiency assessment.',
        'Reading & Listening',
        120,
        N'Suitable for students planning to study at English-speaking universities.',
        @toefl_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 5: TOEFL iBT Practice Test';
END

-- ================================================================================
-- TEST 6: IELTS Academic Reading
-- ================================================================================
DECLARE @test6_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'IELTS Academic Reading')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test6_id,
        6,
        'IELTS Academic Reading',
        N'IELTS Academic Reading module practice with three reading passages and 40 questions. Tests reading comprehension and analysis skills.',
        'Reading',
        60,
        N'Three passages of increasing difficulty with various question types including multiple choice, matching, and True/False/Not Given.',
        @ielts_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 6: IELTS Academic Reading';
END

-- ================================================================================
-- TEST 7: Mathematics Placement Test
-- ================================================================================
DECLARE @test7_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'Mathematics Placement Test')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test7_id,
        7,
        'Mathematics Placement Test',
        N'Comprehensive mathematics assessment covering algebra, geometry, trigonometry, and basic calculus. Determines appropriate course placement.',
        'Mathematical Reasoning',
        90,
        N'50 questions ranging from basic arithmetic to advanced mathematics. Calculator may be permitted for certain sections.',
        @math_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 7: Mathematics Placement Test';
END

-- ================================================================================
-- TEST 8: General Science Assessment
-- ================================================================================
DECLARE @test8_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'General Science Assessment')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test8_id,
        8,
        'General Science Assessment',
        N'Multi-disciplinary science test covering biology, chemistry, physics, and earth science fundamentals.',
        'Scientific Knowledge',
        75,
        N'60 multiple-choice questions testing scientific concepts, methodology, and analytical thinking.',
        @science_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 8: General Science Assessment';
END

-- ================================================================================
-- TEST 9: SAT Practice Test
-- ================================================================================
DECLARE @test9_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'SAT Practice Test')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test9_id,
        9,
        'SAT Practice Test',
        N'Full-length SAT practice examination including Evidence-Based Reading and Writing, and Math sections.',
        'Critical Reading & Mathematics',
        180,
        N'Simulates actual SAT testing conditions. Includes both no-calculator and calculator-permitted math sections.',
        @sat_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 9: SAT Practice Test';
END

-- ================================================================================
-- TEST 10: Quick English Skills Diagnostic
-- ================================================================================
DECLARE @test10_id UNIQUEIDENTIFIER = NEWID();
IF NOT EXISTS (SELECT 1 FROM tests WHERE name = 'Quick English Skills Diagnostic')
BEGIN
    INSERT INTO tests (id, number, name, description, skill, duration_minutes, note, test_category_id, inserted_at, updated_at, is_deleted)
    VALUES (
        @test10_id,
        10,
        'Quick English Skills Diagnostic',
        N'Short diagnostic test to assess current English proficiency level across grammar, vocabulary, and comprehension.',
        'English Language',
        30,
        N'25 questions designed to quickly evaluate English language skills. Ideal for initial assessment or progress tracking.',
        @practice_cat_id,
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Test 10: Quick English Skills Diagnostic';
END

COMMIT TRANSACTION;

-- ================================================================================
-- VERIFICATION
-- ================================================================================

PRINT '';
PRINT 'Setup completed successfully!';
PRINT '';
PRINT '=== Tests Summary ===';
SELECT 
    t.number as [Test Number],
    t.name as [Test Name],
    tc.name as [Category],
    t.duration_minutes as [Duration (min)],
    t.skill as [Skill Focus]
FROM tests t
LEFT JOIN test_categories tc ON t.test_category_id = tc.id
WHERE t.is_deleted = 0
ORDER BY t.number;

PRINT '';
PRINT 'Total Tests Created: 10';
PRINT '';
PRINT 'Test Breakdown:';
PRINT '- TOEIC Tests: 4 (Full tests, Listening focus, Reading focus)';
PRINT '- TOEFL Test: 1 (iBT Practice)';
PRINT '- IELTS Test: 1 (Academic Reading)';
PRINT '- Mathematics Test: 1 (Placement Test)';
PRINT '- Science Test: 1 (General Assessment)';
PRINT '- SAT Test: 1 (Practice Test)';
PRINT '- Diagnostic Test: 1 (Quick English Skills)';
PRINT '';
PRINT '=== Question Set Linkages ===';
SELECT 
    t.number as [Test #],
    t.name as [Test Name],
    COUNT(tqsd.id) as [Question Sets Linked]
FROM tests t
LEFT JOIN test_question_set_details tqsd ON t.id = tqsd.test_id AND tqsd.is_deleted = 0
WHERE t.is_deleted = 0
GROUP BY t.number, t.name
ORDER BY t.number;
