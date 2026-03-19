package com.annular.filmhook.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.*;

@Entity
@Table(name = "security_questions")
@Builder
@Data
@NoArgsConstructor        
@AllArgsConstructor
public class UserSecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question_text", nullable = false)
    private String questionText;
    
    @Column(name = "status")
    private Boolean status = true;
}
