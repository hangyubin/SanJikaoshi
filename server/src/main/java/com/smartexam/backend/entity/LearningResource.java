package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "exam_learning_resource")
public class LearningResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private Integer type; // 1:文档 2:视频 3:音频 4:图片
    private String url;
    private String fileType;
    private Long fileSize;
    private Integer status;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    @ManyToOne
    @JoinColumn(name = "create_by")
    private User createBy;
    
    private Long createTime;
    private Long updateTime;
}