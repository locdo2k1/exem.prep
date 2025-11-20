package com.example.exam.prep.model.viewmodels;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TestAttemptInfoVM {
   private UUID id;
   private Instant takeDate;
   // Localized representation of takeDate based on requested timezone (e.g., tz=Asia/Ho_Chi_Minh)
   private String takeDateLocal;
   private List<String> parts;
   private List<UUID> partIds;
   private Boolean isPractice;
   private Integer correctAnswers;
   private Integer totalQuestions;
   private Instant startTime;
   private Instant endTime;
   private Integer durationSeconds;
}
