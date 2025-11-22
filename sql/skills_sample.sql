-- Skills Sample Data
-- This script creates sample skills for categorizing question competencies and abilities

SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- Delete existing skills if needed (optional - remove if you want to keep existing data)
-- DELETE FROM skills WHERE 1=1;

-- ================================================================================
-- ENGLISH LANGUAGE SKILLS
-- ================================================================================

-- Listening Skills
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'LISTENING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'LISTENING', 'Listening Comprehension', 
            N'Ability to understand spoken English in various contexts including conversations, announcements, and presentations.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Reading Skills
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'READING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'READING', 'Reading Comprehension', 
            N'Ability to understand written English texts including articles, emails, notices, and academic passages.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Grammar
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'GRAMMAR')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'GRAMMAR', 'Grammar & Structure', 
            N'Understanding of English grammatical rules, sentence structure, verb tenses, and syntax.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Vocabulary
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'VOCABULARY')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'VOCABULARY', 'Vocabulary & Word Choice', 
            N'Knowledge of English words, phrases, idioms, and appropriate word usage in context.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Speaking (for reference)
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'SPEAKING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'SPEAKING', 'Speaking & Pronunciation', 
            N'Ability to communicate verbally in English with proper pronunciation and fluency.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Writing
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'WRITING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'WRITING', 'Writing Skills', 
            N'Ability to compose clear, coherent written English texts with proper structure and conventions.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- COGNITIVE & REASONING SKILLS
-- ================================================================================

-- Critical Thinking
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'CRITICAL_THINKING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'CRITICAL_THINKING', 'Critical Thinking', 
            N'Ability to analyze information objectively, evaluate arguments, and make reasoned judgments.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Problem Solving
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'PROBLEM_SOLVING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'PROBLEM_SOLVING', 'Problem Solving', 
            N'Capacity to identify issues, develop solutions, and apply logical approaches to challenges.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Logical Reasoning
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'LOGICAL_REASONING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'LOGICAL_REASONING', 'Logical Reasoning', 
            N'Ability to use logic, identify patterns, and draw valid conclusions from given information.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Analytical Skills
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'ANALYTICAL')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'ANALYTICAL', 'Analytical Skills', 
            N'Capability to break down complex information, examine relationships, and synthesize insights.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- MATHEMATICAL SKILLS
-- ================================================================================

-- Arithmetic
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'ARITHMETIC')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'ARITHMETIC', 'Arithmetic & Computation', 
            N'Basic mathematical operations including addition, subtraction, multiplication, and division.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Algebra
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'ALGEBRA')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'ALGEBRA', 'Algebraic Reasoning', 
            N'Understanding and manipulation of algebraic expressions, equations, and functions.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Geometry
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'GEOMETRY')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'GEOMETRY', 'Geometric Reasoning', 
            N'Understanding of shapes, spatial relationships, measurements, and geometric properties.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Statistics
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'STATISTICS')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'STATISTICS', 'Statistical Analysis', 
            N'Ability to collect, analyze, interpret, and present numerical data using statistical methods.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Calculus
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'CALCULUS')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'CALCULUS', 'Calculus', 
            N'Understanding of limits, derivatives, integrals, and applications of calculus.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- SCIENTIFIC SKILLS
-- ================================================================================

-- Scientific Method
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'SCIENTIFIC_METHOD')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'SCIENTIFIC_METHOD', 'Scientific Methodology', 
            N'Understanding of hypothesis formation, experimentation, observation, and scientific analysis.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Data Interpretation
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'DATA_INTERPRETATION')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'DATA_INTERPRETATION', 'Data Interpretation', 
            N'Ability to read, analyze, and draw conclusions from charts, graphs, tables, and scientific data.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Physics Concepts
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'PHYSICS')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'PHYSICS', 'Physics Knowledge', 
            N'Understanding of physical principles, laws of motion, energy, forces, and natural phenomena.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Chemistry Concepts
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'CHEMISTRY')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'CHEMISTRY', 'Chemistry Knowledge', 
            N'Understanding of chemical properties, reactions, molecular structure, and chemical principles.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Biology Concepts
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'BIOLOGY')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'BIOLOGY', 'Biology Knowledge', 
            N'Understanding of living organisms, biological processes, anatomy, and life sciences.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- TECHNICAL & COMPUTER SKILLS
-- ================================================================================

-- Programming
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'PROGRAMMING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'PROGRAMMING', 'Programming & Coding', 
            N'Ability to write, understand, and debug computer code in various programming languages.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Algorithms
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'ALGORITHMS')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'ALGORITHMS', 'Algorithm Design', 
            N'Understanding of algorithmic thinking, computational complexity, and efficient problem-solving strategies.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Database Management
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'DATABASE')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'DATABASE', 'Database Management', 
            N'Knowledge of database design, SQL queries, data modeling, and database administration.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Networking
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'NETWORKING')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'NETWORKING', 'Networking & Communication', 
            N'Understanding of computer networks, protocols, network security, and data communication.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Cybersecurity
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'CYBERSECURITY')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'CYBERSECURITY', 'Cybersecurity', 
            N'Knowledge of security principles, threat detection, encryption, and information protection.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- BUSINESS & PROFESSIONAL SKILLS
-- ================================================================================

-- Financial Literacy
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'FINANCIAL')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'FINANCIAL', 'Financial Literacy', 
            N'Understanding of financial concepts, accounting principles, budgeting, and economic analysis.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Project Management
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'PROJECT_MGMT')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'PROJECT_MGMT', 'Project Management', 
            N'Ability to plan, organize, execute, and monitor projects using established methodologies.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Business Communication
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'BUSINESS_COMM')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'BUSINESS_COMM', 'Business Communication', 
            N'Professional communication skills for workplace contexts including emails, reports, and presentations.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Leadership
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'LEADERSHIP')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'LEADERSHIP', 'Leadership & Management', 
            N'Ability to guide teams, make decisions, motivate others, and manage organizational resources.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- GENERAL ACADEMIC SKILLS
-- ================================================================================

-- Research Skills
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'RESEARCH')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'RESEARCH', 'Research & Information Literacy', 
            N'Ability to locate, evaluate, synthesize, and cite information from various sources.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Time Management
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'TIME_MGMT')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'TIME_MGMT', 'Time Management', 
            N'Ability to prioritize tasks, manage schedules efficiently, and meet deadlines.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Attention to Detail
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'ATTENTION_DETAIL')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'ATTENTION_DETAIL', 'Attention to Detail', 
            N'Ability to notice and process fine details, maintain accuracy, and identify errors.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Memory & Recall
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'MEMORY')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'MEMORY', 'Memory & Recall', 
            N'Ability to retain, store, and retrieve information effectively.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Comprehension Speed
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'COMPREHENSION_SPEED')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'COMPREHENSION_SPEED', 'Comprehension Speed', 
            N'Ability to quickly understand and process information from various sources.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- ================================================================================
-- SOFT SKILLS
-- ================================================================================

-- Collaboration
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'COLLABORATION')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'COLLABORATION', 'Collaboration & Teamwork', 
            N'Ability to work effectively with others, share ideas, and contribute to group goals.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Adaptability
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'ADAPTABILITY')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'ADAPTABILITY', 'Adaptability & Flexibility', 
            N'Ability to adjust to new situations, handle change, and learn new skills quickly.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Creativity
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'CREATIVITY')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'CREATIVITY', 'Creativity & Innovation', 
            N'Ability to generate original ideas, think outside the box, and develop innovative solutions.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

-- Emotional Intelligence
IF NOT EXISTS (SELECT 1 FROM skills WHERE code = 'EMOTIONAL_INTEL')
    INSERT INTO skills (id, code, name, description, inserted_at, updated_at, is_deleted)
    VALUES (NEWID(), 'EMOTIONAL_INTEL', 'Emotional Intelligence', 
            N'Ability to recognize, understand, and manage one''s own emotions and those of others.',
            SYSUTCDATETIME(), SYSUTCDATETIME(), 0);

COMMIT TRANSACTION;

PRINT 'Successfully created skills!';
PRINT 'Total skills: 40';
PRINT '';
PRINT 'Skill Categories:';
PRINT '- English Language: 6 skills (Listening, Reading, Grammar, Vocabulary, Speaking, Writing)';
PRINT '- Cognitive & Reasoning: 4 skills (Critical Thinking, Problem Solving, Logical Reasoning, Analytical)';
PRINT '- Mathematical: 5 skills (Arithmetic, Algebra, Geometry, Statistics, Calculus)';
PRINT '- Scientific: 5 skills (Scientific Method, Data Interpretation, Physics, Chemistry, Biology)';
PRINT '- Technical & Computer: 5 skills (Programming, Algorithms, Database, Networking, Cybersecurity)';
PRINT '- Business & Professional: 4 skills (Financial, Project Management, Business Communication, Leadership)';
PRINT '- General Academic: 5 skills (Research, Time Management, Attention to Detail, Memory, Comprehension Speed)';
PRINT '- Soft Skills: 4 skills (Collaboration, Adaptability, Creativity, Emotional Intelligence)';
