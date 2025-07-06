package com.example.exam.prep.repository;

import com.example.exam.prep.model.Test;
import com.example.exam.prep.vm.test.TestVM;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITestRepository extends GenericRepository<Test> {
    
    @Query(value = """
        WITH test_data AS (
            SELECT 
                t.id as test_id,
                t.name as test_title,
                tc.id as category_id,
                tc.name as category_name,
                t.is_active as is_active,
                
                -- Test Parts
                (
                    SELECT JSON_ARRAYAGG(
                        JSON_OBJECT(
                            'id', tp.id,
                            'title', p.name,
                            'description', p.description,
                            'order', tp.order_index,
                            'questions', (
                                SELECT COALESCE(JSON_ARRAYAGG(
                                    JSON_OBJECT(
                                        'id', q.id,
                                        'prompt', q.prompt,
                                        'score', q.score,
                                        'order', q.display_order,
                                        'questionType', JSON_OBJECT(
                                            'id', qt.id,
                                            'name', qt.name
                                        ),
                                        'options', (
                                            SELECT COALESCE(JSON_ARRAYAGG(
                                                JSON_OBJECT(
                                                    'id', o.id,
                                                    'text', o.text,
                                                    'isCorrect', o.is_correct
                                                )
                                            ), JSON_ARRAY()) 
                                            FROM options o 
                                            WHERE o.question_id = q.id
                                        ),
                                        'questionAnswers', (
                                            SELECT COALESCE(JSON_ARRAYAGG(fba.answer_text), JSON_ARRAY())
                                            FROM fill_blank_answers fba 
                                            WHERE fba.question_id = q.id
                                        ),
                                        'questionAudios', (
                                            SELECT COALESCE(JSON_ARRAYAGG(
                                                JSON_OBJECT(
                                                    'id', fi.id,
                                                    'fileName', fi.file_name,
                                                    'fileUrl', fi.url,
                                                    'fileType', fi.file_type,
                                                    'fileSize', fi.file_size
                                                )
                                            ), JSON_ARRAY())
                                            FROM file_infos fi 
                                            WHERE fi.question_id = q.id
                                        )
                                    )
                                ), JSON_ARRAY())
                                FROM questions q
                                JOIN test_question tq ON tq.question_id = q.id
                                JOIN question_types qt ON q.question_type_id = qt.id
                                WHERE tq.test_part_id = tp.id
                            ),
                            'questionSets', (
                                SELECT COALESCE(JSON_ARRAYAGG(
                                    JSON_OBJECT(
                                        'id', qs.id,
                                        'title', qs.title,
                                        'description', qs.description,
                                        'order', qs.display_order,
                                        'imageUrl', qs.image_url,
                                        'questions', (
                                            SELECT COALESCE(JSON_ARRAYAGG(
                                                JSON_OBJECT(
                                                    'id', q2.id,
                                                    'prompt', q2.prompt,
                                                    'score', COALESCE(qsi2.custom_score, q2.score),
                                                    'order', COALESCE(qsi2.display_order, 0),
                                                    'questionType', JSON_OBJECT(
                                                        'id', qt2.id,
                                                        'name', qt2.name
                                                    ),
                                                    'options', (
                                                        SELECT COALESCE(JSON_ARRAYAGG(
                                                            JSON_OBJECT(
                                                                'id', o2.id,
                                                                'text', o2.text,
                                                                'isCorrect', o2.is_correct
                                                            )
                                                        ), JSON_ARRAY())
                                                        FROM options o2 
                                                        WHERE o2.question_id = q2.id
                                                    ),
                                                    'questionAnswers', (
                                                        SELECT COALESCE(JSON_ARRAYAGG(fba2.answer_text), JSON_ARRAY())
                                                        FROM fill_blank_answers fba2 
                                                        WHERE fba2.question_id = q2.id
                                                    ),
                                                    'questionAudios', (
                                                        SELECT COALESCE(JSON_ARRAYAGG(
                                                            JSON_OBJECT(
                                                                'id', fi2.id,
                                                                'fileName', fi2.file_name,
                                                                'fileUrl', fi2.url,
                                                                'fileType', fi2.file_type,
                                                                'fileSize', fi2.file_size
                                                            )
                                                        ), JSON_ARRAY())
                                                        FROM file_infos fi2 
                                                        WHERE fi2.question_id = q2.id
                                                    )
                                                )
                                            ), JSON_ARRAY())
                                            FROM question_set_items qsi2
                                            JOIN questions q2 ON qsi2.question_id = q2.id
                                            JOIN question_types qt2 ON q2.question_type_id = qt2.id
                                            WHERE qsi2.question_set_id = qs.id
                                            AND qsi2.is_active = true
                                        )
                                    )
                                ), JSON_ARRAY())
                                FROM question_sets qs
                                JOIN test_question_set tqs ON tqs.question_set_id = qs.id
                                WHERE tqs.test_part_id = tp.id
                            )
                        )
                    )
                    FROM test_parts tp
                    JOIN parts p ON p.id = tp.part_id
                    WHERE tp.test_id = t.id
                    ORDER BY tp.order_index
                ) as parts,
                
                -- Skills
                (
                    SELECT JSON_ARRAYAGG(
                        JSON_OBJECT(
                            'id', s.id,
                            'name', s.name
                        )
                    )
                    FROM test_skills ts
                    JOIN skills s ON s.id = ts.skill_id
                    WHERE ts.test_id = t.id
                ) as skills
                
            FROM tests t
            LEFT JOIN test_categories tc ON t.test_category_id = tc.id
            WHERE t.id = :testId
            GROUP BY t.id, tc.id
        )
        SELECT 
            test_id as id,
            test_title as title,
            JSON_OBJECT(
                'id', category_id,
                'name', category_name
            ) as testCategory,
            is_active as active,
            parts as listPart,
            skills as listSkill
        FROM test_data
    """, nativeQuery = true)
    Optional<TestVM> findTestVMById(@Param("testId") UUID testId);
    
    @Query("""
        SELECT DISTINCT t FROM Test t
        LEFT JOIN FETCH t.testParts tp
        LEFT JOIN FETCH tp.part p
        LEFT JOIN FETCH tp.questions q
        LEFT JOIN FETCH q.options o
        LEFT JOIN FETCH q.fillBlankAnswers fba
        LEFT JOIN FETCH q.fileInfos fi
        LEFT JOIN FETCH q.questionType qt
        LEFT JOIN FETCH q.category qc
        LEFT JOIN FETCH tp.questionSets qs
        LEFT JOIN FETCH qs.questionSetItems qsi
        LEFT JOIN FETCH qsi.question qsiq
        LEFT JOIN FETCH qsiq.options qsio
        LEFT JOIN FETCH qsiq.fillBlankAnswers qsifba
        LEFT JOIN FETCH qsiq.fileInfos qsifi
        LEFT JOIN FETCH qsiq.questionType qsiqt
        LEFT JOIN FETCH qsiq.category qsic
        WHERE t.id = :testId
        ORDER BY tp.orderIndex, qs.order, qsi.order
    """)
    Optional<Test> findByIdWithDetails(@Param("testId") UUID testId);
}
