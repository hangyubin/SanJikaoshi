package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "exam_record")
public class ExamRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "paper_id")
    private ExamPaper examPaper;
    
    // 额外添加的字段以支持控制器中的方法调用
    private Integer score;
    private Long examTime;
    private Long duration;
    private String answers;
    private String correctAnswers;
    
    private Integer totalScore;
    private Integer obtainedScore;
    private Integer status; // 1:未开始 2:进行中 3:已完成 4:已过期
    private String answerJson;
    private String resultJson;
    
    private Long startTime;
    private Long endTime;
    private Long submitTime;
    
    private Long createTime;
    private Long updateTime;
}


