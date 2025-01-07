package com.suni.api.jpprivateuserapi.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "profile_message")
    private String profileMessage;

    @Column(name = "personal_color")
    private String personalColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;

    @CreatedDate
    @Column(name = "insert_date", columnDefinition = "datetime(6)", updatable = false)
    private LocalDateTime insertDate;

    @CreatedBy
    @Column(name = "insert_id", columnDefinition = "varchar(64)", updatable = false)
    private String insertId;

    @LastModifiedDate
    @Column(name = "update_date", columnDefinition = "datetime(6)")
    private LocalDateTime updateDate;

    @LastModifiedBy
    @Column(name = "update_id", columnDefinition = "varchar(64)")
    private String updateId;

    @Column(name = "delete_date", columnDefinition = "datetime(6)")
    private LocalDateTime deleteDate;

    @Column(name = "delete_id", columnDefinition = "varchar(64)")
    private String deleteId;

    public User() {}
}
