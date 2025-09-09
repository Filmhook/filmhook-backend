package com.annular.filmhook.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "audition_cart_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditionCartItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // link to Company
    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @ManyToOne
    @JoinColumn(name = "sub_profession_id", nullable = false)
    private FilmSubProfession subProfession;

    @Column(name = "count", nullable = false)
    private Integer count;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;
}
