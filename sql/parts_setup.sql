-- Parts Setup Script
-- This script creates the parts table and inserts sample data for TOEIC test parts

SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- ================================================================================
-- CREATE PARTS TABLE
-- ================================================================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'parts')
BEGIN
    CREATE TABLE parts (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        number INT NOT NULL UNIQUE IDENTITY(1,1),
        name NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX),
        inserted_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        is_deleted BIT NOT NULL DEFAULT 0,
        inserted_by_id UNIQUEIDENTIFIER,
        updated_by_id UNIQUEIDENTIFIER
    );
    PRINT 'Created parts table successfully';
END
ELSE
BEGIN
    PRINT 'Parts table already exists';
END

-- ================================================================================
-- CREATE TEST_PARTS TABLE (Junction table for tests and parts)
-- ================================================================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'test_parts')
BEGIN
    CREATE TABLE test_parts (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        test_id UNIQUEIDENTIFIER NOT NULL,
        part_id UNIQUEIDENTIFIER NOT NULL,
        part_order INT,
        inserted_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        is_deleted BIT NOT NULL DEFAULT 0,
        inserted_by_id UNIQUEIDENTIFIER,
        updated_by_id UNIQUEIDENTIFIER,
        CONSTRAINT FK_test_parts_test FOREIGN KEY (test_id) REFERENCES tests(id),
        CONSTRAINT FK_test_parts_part FOREIGN KEY (part_id) REFERENCES parts(id)
    );
    PRINT 'Created test_parts table successfully';
END
ELSE
BEGIN
    PRINT 'Test_parts table already exists';
END

-- ================================================================================
-- INSERT TOEIC PARTS DATA
-- ================================================================================

-- Delete existing parts if needed (optional - remove if you want to keep existing data)
-- DELETE FROM parts WHERE 1=1;

-- Part 1: Photographs
IF NOT EXISTS (SELECT 1 FROM parts WHERE name = 'Photographs')
BEGIN
    INSERT INTO parts (id, number, name, description, inserted_at, updated_at, is_deleted)
    VALUES (
        NEWID(),
        1,
        'Photographs',
        N'Listening comprehension test where test takers view a photograph and hear four statements. They must select the statement that best describes what they see in the picture. This part contains 6 questions.',
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Part 1: Photographs';
END

-- Part 2: Question-Response
IF NOT EXISTS (SELECT 1 FROM parts WHERE name = 'Question-Response')
BEGIN
    INSERT INTO parts (id, number, name, description, inserted_at, updated_at, is_deleted)
    VALUES (
        NEWID(),
        2,
        'Question-Response',
        N'Test takers hear a question or statement followed by three responses. They must select the best response to the question or statement. This part contains 25 questions.',
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Part 2: Question-Response';
END

-- Part 3: Conversations
IF NOT EXISTS (SELECT 1 FROM parts WHERE name = 'Conversations')
BEGIN
    INSERT INTO parts (id, number, name, description, inserted_at, updated_at, is_deleted)
    VALUES (
        NEWID(),
        3,
        'Conversations',
        N'Test takers hear a conversation between two or more people and answer questions about what they heard. Each conversation has 3 questions. This part contains 39 questions (13 conversations).',
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Part 3: Conversations';
END

-- Part 4: Talks
IF NOT EXISTS (SELECT 1 FROM parts WHERE name = 'Talks')
BEGIN
    INSERT INTO parts (id, number, name, description, inserted_at, updated_at, is_deleted)
    VALUES (
        NEWID(),
        4,
        'Talks',
        N'Test takers hear a short talk given by a single speaker and answer questions about what they heard. Each talk has 3 questions. This part contains 30 questions (10 talks).',
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Part 4: Talks';
END

-- Part 5: Incomplete Sentences
IF NOT EXISTS (SELECT 1 FROM parts WHERE name = 'Incomplete Sentences')
BEGIN
    INSERT INTO parts (id, number, name, description, inserted_at, updated_at, is_deleted)
    VALUES (
        NEWID(),
        5,
        'Incomplete Sentences',
        N'Test takers read a sentence with a missing word or phrase and select the best option to complete the sentence. Tests grammar and vocabulary. This part contains 30 questions.',
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Part 5: Incomplete Sentences';
END

-- Part 6: Text Completion
IF NOT EXISTS (SELECT 1 FROM parts WHERE name = 'Text Completion')
BEGIN
    INSERT INTO parts (id, number, name, description, inserted_at, updated_at, is_deleted)
    VALUES (
        NEWID(),
        6,
        'Text Completion',
        N'Test takers read a text with missing words or phrases and select the best option to complete the text. Tests reading comprehension and context understanding. This part contains 16 questions (4 texts).',
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Part 6: Text Completion';
END

-- Part 7: Reading Comprehension
IF NOT EXISTS (SELECT 1 FROM parts WHERE name = 'Reading Comprehension')
BEGIN
    INSERT INTO parts (id, number, name, description, inserted_at, updated_at, is_deleted)
    VALUES (
        NEWID(),
        7,
        'Reading Comprehension',
        N'Test takers read various texts (emails, articles, advertisements, etc.) and answer comprehension questions. Includes single, double, and triple passage sets. This part contains 54 questions.',
        SYSUTCDATETIME(),
        SYSUTCDATETIME(),
        0
    );
    PRINT 'Inserted Part 7: Reading Comprehension';
END

COMMIT TRANSACTION;

-- ================================================================================
-- VERIFICATION
-- ================================================================================

PRINT '';
PRINT 'Setup completed successfully!';
PRINT '';
PRINT '=== Parts Summary ===';
SELECT 
    number as [Part Number],
    name as [Part Name],
    LEFT(description, 80) + '...' as [Description Preview]
FROM parts
WHERE is_deleted = 0
ORDER BY number;

PRINT '';
PRINT 'Total Parts: 7 (TOEIC Standard Format)';
PRINT 'Listening Section: Parts 1-4 (100 questions)';
PRINT 'Reading Section: Parts 5-7 (100 questions)';
