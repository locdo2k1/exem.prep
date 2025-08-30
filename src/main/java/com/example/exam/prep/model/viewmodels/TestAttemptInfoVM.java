package com.example.exam.prep.model.viewmodels;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class TestAttemptInfoVM {
   private Instant takeDate;
   // Localized representation of takeDate based on requested timezone (e.g., tz=Asia/Ho_Chi_Minh)
   private String takeDateLocal;
   private List<String> parts;
   private Boolean isPractice;
   private Integer correctAnswers;
   private Integer totalQuestions;
   private Instant startTime;
   private Instant endTime;
   private Integer durationSeconds;
}
