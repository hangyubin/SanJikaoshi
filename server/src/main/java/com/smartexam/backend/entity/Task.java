package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    private String assignBy;
    private Long assignById;
    private String receiveBy;
    private Long receiveById;
    private Integer type;
    private Integer status;
    private Long startTime;
    private Long endTime;
    private Long createTime;
    private Long updateTime;
    private String result;
    
    // 与Subject实体的关联
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    // 考试时长（分钟）
    private Integer duration;
    
    // 参与人数
    private Integer participantCount;
    
    // 考试说明
    private String instructions;
    
    // 与ExamPaper实体的关联
    @ManyToOne
    @JoinColumn(name = "paper_id")
    private ExamPaper examPaper;
    
    // 题型分配比例
    private String questionTypeDistribution;
    
    // 试卷总题数
    private Integer totalQuestions;
}


