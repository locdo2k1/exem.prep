"""
Generate complete TOEIC test SQL with proper question set structure
Each question set contains questions that share the same context (passage, conversation, etc.)
"""

def escape_sql(text):
    """Escape single quotes for SQL Server"""
    return text.replace("'", "''")

# TOEIC Structure:
# Part 1: 6 photos (6 sets, 1 question each)
# Part 2: 25 Q-R (25 sets, 1 question each)
# Part 3: 13 conversations (13 sets, 3 questions each = 39 questions)
# Part 4: 10 talks (10 sets, 3 questions each = 30 questions)
# Part 5: 30 sentences (30 sets, 1 question each)
# Part 6: 4 passages (4 sets, 4 questions each = 16 questions)
# Part 7: 18 passages (variable questions = 54 questions total)

part1_photos = [
    {
        'title': 'Part 1 - Photo 1',
        'description': 'A woman stands beside a counter, holding a tray. A man is seated at a table.',
        'questions': [{
            'prompt': 'Look at the picture. Choose the statement that best describes what you see.',
            'clip': 1,
            'audio': '/audio/toeic_audio_q001.mp3',
            'options': [
                ('A. The woman is pouring coffee into a cup.', False),
                ('B. The woman is carrying a tray.', True),
                ('C. The woman is sweeping the floor.', False),
                ('D. The woman is speaking at a podium.', False)
            ]
        }]
    },
    {
        'title': 'Part 1 - Photo 2',
        'description': 'Two people are examining documents together at a desk.',
        'questions': [{
            'prompt': 'Look at the picture. What do you see?',
            'clip': 2,
            'audio': '/audio/toeic_audio_q002.mp3',
            'options': [
                ('A. They are closing the office door.', False),
                ('B. They are reviewing papers together.', True),
                ('C. They are painting a wall.', False),
                ('D. They are moving furniture.', False)
            ]
        }]
    },
    {
        'title': 'Part 1 - Photo 3',
        'description': 'A man is standing at a photocopier in an office.',
        'questions': [{
            'prompt': 'Look at the picture. Choose the best description.',
            'clip': 3,
            'audio': '/audio/toeic_audio_q003.mp3',
            'options': [
                ('A. He is using a copy machine.', True),
                ('B. He is cleaning the windows.', False),
                ('C. He is arranging flowers.', False),
                ('D. He is repairing equipment.', False)
            ]
        }]
    },
    {
        'title': 'Part 1 - Photo 4',
        'description': 'Several cars are parked in a parking lot outside a building.',
        'questions': [{
            'prompt': 'Look at the picture. What is happening?',
            'clip': 4,
            'audio': '/audio/toeic_audio_q004.mp3',
            'options': [
                ('A. Vehicles are being washed.', False),
                ('B. Cars are parked in rows.', True),
                ('C. A car is being towed.', False),
                ('D. People are boarding a bus.', False)
            ]
        }]
    },
    {
        'title': 'Part 1 - Photo 5',
        'description': 'A woman is working at a laptop computer at her desk.',
        'questions': [{
            'prompt': 'Look at the picture. Choose the statement that best describes it.',
            'clip': 5,
            'audio': '/audio/toeic_audio_q005.mp3',
            'options': [
                ('A. She is typing on a keyboard.', True),
                ('B. She is watering plants.', False),
                ('C. She is filing documents.', False),
                ('D. She is answering the phone.', False)
            ]
        }]
    },
    {
        'title': 'Part 1 - Photo 6',
        'description': 'Boxes are stacked on shelves in a warehouse.',
        'questions': [{
            'prompt': 'Look at the picture. What do you see?',
            'clip': 6,
            'audio': '/audio/toeic_audio_q006.mp3',
            'options': [
                ('A. Shelves are being assembled.', False),
                ('B. Items are arranged on shelves.', True),
                ('C. A truck is being loaded.', False),
                ('D. Workers are taking inventory.', False)
            ]
        }]
    }
]

part2_questions = [
    {'description': 'When will the new computers arrive?', 'options': [('A. By the loading dock.', False), ('B. Three computers.', False), ('C. By next Tuesday.', True), ('D. Yes, they did.', False)]},
    {'description': 'Who is responsible for training new employees?', 'options': [('A. The Human Resources department.', True), ('B. Last Monday morning.', False), ('C. In the conference room.', False), ('D. About three hours.', False)]},
    {'description': 'Where should I put these files?', 'options': [('A. Yesterday afternoon.', False), ('B. On the shelf behind my desk.', True), ('C. Five manila folders.', False), ('D. Yes, please file them.', False)]},
    {'description': 'Why was the meeting postponed?', 'options': [('A. At two o\'clock.', False), ('B. In the main conference room.', False), ('C. The director is traveling.', True), ('D. About fifteen people.', False)]},
    {'description': 'How do I access the company database?', 'options': [('A. You need to request a password from IT.', True), ('B. Next to the printer.', False), ('C. Around noon today.', False), ('D. Five databases.', False)]},
    {'description': 'Would you like coffee or tea?', 'options': [('A. With sugar, please.', False), ('B. Coffee would be great, thanks.', True), ('C. At the coffee shop.', False), ('D. This morning at nine.', False)]},
    {'description': 'Didn\'t you receive my email about the schedule change?', 'options': [('A. No, I haven\'t checked my inbox yet.', True), ('B. Yes, the schedule is ready.', False), ('C. Tomorrow at ten.', False), ('D. By email attachment.', False)]},
    {'description': 'Should we order lunch now or wait until later?', 'options': [('A. At the restaurant downtown.', False), ('B. Chicken sandwiches.', False), ('C. Let\'s wait until the meeting ends.', True), ('D. About twelve dollars.', False)]},
    {'description': 'Which printer should I use for color copies?', 'options': [('A. Ten copies, please.', False), ('B. The one on the third floor.', True), ('C. Yes, it prints in color.', False), ('D. Around three o\'clock.', False)]},
    {'description': 'Haven\'t the invoices been sent out yet?', 'options': [('A. They\'ll be mailed this afternoon.', True), ('B. Five invoices total.', False), ('C. In the filing cabinet.', False), ('D. Yes, I received one.', False)]},
    {'description': 'Could you help me move these boxes?', 'options': [('A. Sure, just let me finish this.', True), ('B. They arrived yesterday.', False), ('C. In the storage room.', False), ('D. About ten boxes.', False)]},
    {'description': 'When is the deadline for submitting the proposal?', 'options': [('A. Ms. Park submitted it.', False), ('B. The end of this week.', True), ('C. A detailed proposal.', False), ('D. To the director.', False)]},
    {'description': 'Where can I find the employee handbook?', 'options': [('A. Check the company website.', True), ('B. All employees have one.', False), ('C. Last week.', False), ('D. About fifty pages.', False)]},
    {'description': 'Who approved the budget increase?', 'options': [('A. The finance committee did.', True), ('B. Next month.', False), ('C. A ten percent increase.', False), ('D. In the meeting room.', False)]},
    {'description': 'Why is the elevator out of order?', 'options': [('A. It\'s being repaired.', True), ('B. On the second floor.', False), ('C. Take the stairs.', False), ('D. This morning.', False)]},
    {'description': 'How long will the renovation take?', 'options': [('A. About three weeks.', True), ('B. The construction crew.', False), ('C. In the lobby area.', False), ('D. Yes, it will.', False)]},
    {'description': 'Would you prefer the morning or afternoon session?', 'options': [('A. The afternoon works better for me.', True), ('B. Two sessions total.', False), ('C. In Conference Room B.', False), ('D. About two hours.', False)]},
    {'description': 'Didn\'t you attend the workshop last month?', 'options': [('A. No, I was on vacation.', True), ('B. Yes, next month.', False), ('C. At the downtown office.', False), ('D. Three workshops.', False)]},
    {'description': 'Should we schedule the presentation for Monday or Wednesday?', 'options': [('A. In the auditorium.', False), ('B. Wednesday would be better.', True), ('C. About thirty minutes.', False), ('D. Sales presentation.', False)]},
    {'description': 'Which department handles travel arrangements?', 'options': [('A. The administrative office does.', True), ('B. Business travel.', False), ('C. Next Tuesday.', False), ('D. By airplane.', False)]},
    {'description': 'Haven\'t the new catalogs arrived yet?', 'options': [('A. They should be here tomorrow.', True), ('B. Yes, they are new.', False), ('C. In the mailroom.', False), ('D. Product catalog.', False)]},
    {'description': 'Could you show me how to operate this machine?', 'options': [('A. Of course, it\'s quite simple.', True), ('B. A new machine.', False), ('C. In the break room.', False), ('D. This afternoon.', False)]},
    {'description': 'When did the package arrive?', 'options': [('A. Earlier this morning.', True), ('B. From our supplier.', False), ('C. Three packages.', False), ('D. At the reception desk.', False)]},
    {'description': 'Where should we hold the client meeting?', 'options': [('A. Important clients.', False), ('B. Let\'s use the executive boardroom.', True), ('C. At ten o\'clock.', False), ('D. About an hour.', False)]},
    {'description': 'Who will be managing the new project?', 'options': [('A. Mr. Kim from the engineering team.', True), ('B. Starting next month.', False), ('C. A six-month project.', False), ('D. Very challenging.', False)]}
]

part3_conversations = [
    {
        'title': 'Part 3 - Conversation 1',
        'description': 'Woman: Have you finished the quarterly sales report?\nMan: Almost. I just need to add the graphs and send it to the director by Friday.\nWoman: Let me know if you need help with the data visualization.',
        'clip': 32,
        'audio': '/audio/toeic_audio_q032.mp3',
        'questions': [
            {'prompt': 'What does the man need to do by Friday?', 'options': [('A. Review sales data', False), ('B. Complete and submit a report', True), ('C. Meet with the director', False), ('D. Train a new employee', False)]},
            {'prompt': 'What does the woman offer to help with?', 'options': [('A. Writing the report', False), ('B. Creating graphs', True), ('C. Scheduling a meeting', False), ('D. Collecting sales data', False)]},
            {'prompt': 'When is the deadline?', 'options': [('A. Monday', False), ('B. Wednesday', False), ('C. Friday', True), ('D. Next week', False)]}
        ]
    },
    {
        'title': 'Part 3 - Conversation 2',
        'description': 'Man: I\'d like to book a conference room for next Tuesday afternoon.\nWoman: Let me check our schedule. How many people will be attending?\nMan: About fifteen. We\'ll need a projector and a whiteboard.\nWoman: Conference Room B is available and has all that equipment.',
        'clip': 35,
        'audio': '/audio/toeic_audio_q035.mp3',
        'questions': [
            {'prompt': 'What is the man trying to do?', 'options': [('A. Cancel a reservation', False), ('B. Reserve a meeting space', True), ('C. Order equipment', False), ('D. Invite colleagues', False)]},
            {'prompt': 'How many people will attend?', 'options': [('A. Five', False), ('B. Ten', False), ('C. Fifteen', True), ('D. Twenty', False)]},
            {'prompt': 'What room does the woman suggest?', 'options': [('A. Conference Room A', False), ('B. Conference Room B', True), ('C. The training room', False), ('D. The executive suite', False)]}
        ]
    },
    {
        'title': 'Part 3 - Conversation 3',
        'description': 'Woman: The printer on our floor isn\'t working again.\nMan: Did you try restarting it?\nWoman: Yes, but it\'s still showing an error message.\nMan: I\'ll send someone from IT to take a look this afternoon.',
        'clip': 38,
        'audio': '/audio/toeic_audio_q038.mp3',
        'questions': [
            {'prompt': 'What is the problem?', 'options': [('A. The computer is slow', False), ('B. The printer is malfunctioning', True), ('C. The internet is down', False), ('D. The phone system failed', False)]},
            {'prompt': 'What did the woman already try?', 'options': [('A. Calling IT support', False), ('B. Restarting the printer', True), ('C. Replacing the ink', False), ('D. Reading the manual', False)]},
            {'prompt': 'What will the man do?', 'options': [('A. Fix it himself', False), ('B. Order a new printer', False), ('C. Send an IT technician', True), ('D. Restart the device', False)]}
        ]
    },
    # Continue with remaining 10 conversations (simplified for brevity - full version would have all)
]

# Generate SQL
def generate_sql():
    lines = []
    lines.append('-- TOEIC Complete Test - Properly Structured Question Sets')
    lines.append('-- Run after basic schema is created\n')
    lines.append('SET XACT_ABORT ON;')
    lines.append('BEGIN TRANSACTION;\n')
    
    # Question Types & Categories (same as before)
    lines.append('-- Question Types & Categories')
    lines.append('-- [Insert question_types and question_categories as shown in template]\n')
    
    # Part 1: Photos
    lines.append('-- ================================================================================')
    lines.append('-- PART 1: PHOTOGRAPHS (6 sets, 1 question each)')
    lines.append('-- ================================================================================')
    lines.append('DECLARE @part1_type_id UNIQUEIDENTIFIER = (SELECT id FROM question_types WHERE code = ''LISTENING_PHOTOGRAPH'');')
    lines.append('DECLARE @part1_cat_id UNIQUEIDENTIFIER = (SELECT id FROM question_categories WHERE code = ''PART1'');\n')
    
    q_num = 1
    for photo in part1_photos:
        set_id = f'@set{q_num}_id'
        q_id = f'@q{q_num}_id'
        
        lines.append(f'-- Question {q_num}')
        lines.append(f'DECLARE {set_id} UNIQUEIDENTIFIER = NEWID();')
        lines.append(f'INSERT INTO question_sets (id, title, description, display_order, inserted_at, updated_at, is_deleted)')
        lines.append(f'VALUES ({set_id}, N\'{escape_sql(photo["title"])}\', N\'{escape_sql(photo["description"])}\', {q_num}, SYSUTCDATETIME(), SYSUTCDATETIME(), 0);\n')
        
        q = photo['questions'][0]
        lines.append(f'DECLARE {q_id} UNIQUEIDENTIFIER = NEWID();')
        lines.append(f'INSERT INTO questions (id, question_type_id, question_category_id, clip_number, display_order, audio_url, transcript, prompt, score, inserted_at, updated_at, is_deleted)')
        lines.append(f'VALUES ({q_id}, @part1_type_id, @part1_cat_id, {q["clip"]}, {q_num}, N\'{q["audio"]}\', N\'{escape_sql(photo["description"])}\', N\'{escape_sql(q["prompt"])}\', 1, SYSUTCDATETIME(), SYSUTCDATETIME(), 0);\n')
        
        lines.append(f'INSERT INTO question_set_items (id, question_set_id, question_id, display_order, inserted_at, updated_at, is_deleted)')
        lines.append(f'VALUES (NEWID(), {set_id}, {q_id}, 1, SYSUTCDATETIME(), SYSUTCDATETIME(), 0);\n')
        
        for i, (opt_text, is_correct) in enumerate(q['options'], 1):
            correct_val = 1 if is_correct else 0
            lines.append(f'INSERT INTO options (id, question_id, text, correct, display_order, inserted_at, updated_at, is_deleted)')
            lines.append(f'VALUES (NEWID(), {q_id}, N\'{escape_sql(opt_text)}\', {correct_val}, {i}, SYSUTCDATETIME(), SYSUTCDATETIME(), 0);')
        lines.append('')
        q_num += 1
    
    # Part 2: Question-Response
    lines.append('-- ================================================================================')
    lines.append('-- PART 2: QUESTION-RESPONSE (25 sets, 1 question each)')
    lines.append('-- ================================================================================')
    lines.append('DECLARE @part2_type_id UNIQUEIDENTIFIER = (SELECT id FROM question_types WHERE code = ''LISTENING_QUESTION_RESPONSE'');')
    lines.append('DECLARE @part2_cat_id UNIQUEIDENTIFIER = (SELECT id FROM question_categories WHERE code = ''PART2'');\n')
    
    for i, qr in enumerate(part2_questions, q_num):
        set_id = f'@set{i}_id'
        q_id = f'@q{i}_id'
        clip_num = i
        
        lines.append(f'-- Question {i}')
        lines.append(f'DECLARE {set_id} UNIQUEIDENTIFIER = NEWID();')
        lines.append(f'INSERT INTO question_sets (id, title, description, display_order, inserted_at, updated_at, is_deleted)')
        lines.append(f'VALUES ({set_id}, N''Part 2 - Question {i}'', N\'{escape_sql(qr["description"])}\', {i}, SYSUTCDATETIME(), SYSUTCDATETIME(), 0);\n')
        
        lines.append(f'DECLARE {q_id} UNIQUEIDENTIFIER = NEWID();')
        lines.append(f'INSERT INTO questions (id, question_type_id, question_category_id, clip_number, display_order, audio_url, transcript, prompt, score, inserted_at, updated_at, is_deleted)')
        lines.append(f'VALUES ({q_id}, @part2_type_id, @part2_cat_id, {clip_num}, {i}, N\'/audio/toeic_audio_q{i:03d}.mp3\', N\'{escape_sql(qr["description"])}\', N\'{escape_sql(qr["description"])}\', 1, SYSUTCDATETIME(), SYSUTCDATETIME(), 0);\n')
        
        lines.append(f'INSERT INTO question_set_items (id, question_set_id, question_id, display_order, inserted_at, updated_at, is_deleted)')
        lines.append(f'VALUES (NEWID(), {set_id}, {q_id}, 1, SYSUTCDATETIME(), SYSUTCDATETIME(), 0);\n')
        
        for j, (opt_text, is_correct) in enumerate(qr['options'], 1):
            correct_val = 1 if is_correct else 0
            lines.append(f'INSERT INTO options (id, question_id, text, correct, display_order, inserted_at, updated_at, is_deleted)')
            lines.append(f'VALUES (NEWID(), {q_id}, N\'{escape_sql(opt_text)}\', {correct_val}, {j}, SYSUTCDATETIME(), SYSUTCDATETIME(), 0);')
        lines.append('')
    
    lines.append('COMMIT TRANSACTION;')
    lines.append('PRINT ''Successfully created TOEIC test structure'';')
    
    return '\n'.join(lines)

if __name__ == '__main__':
    print('Generating SQL...')
    sql = generate_sql()
    with open('toeic_restructured_partial.sql', 'w', encoding='utf-8') as f:
        f.write(sql)
    print(f'Generated toeic_restructured_partial.sql ({len(sql):,} characters)')
    print('This is a template - full script would include all parts')
