package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "practice_record")
public class PracticeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private Question question;
    
    private String answer; // 用户答案
    private String correctAnswer; // 正确答案
    private Boolean isCorrect; // 是否正确
    private Integer difficulty; // 题目难度
    private Integer type; // 题目类型
    private Integer subjectId; // 科目ID
    private Long practiceTime; // 练习时间
    private Long createTime; // 创建时间
    private Long updateTime; // 更新时间
}


