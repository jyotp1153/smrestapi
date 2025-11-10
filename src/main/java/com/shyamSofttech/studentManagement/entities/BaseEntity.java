package com.shyamSofttech.studentManagement.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.shyamSofttech.studentManagement.authUtils.JwtHelper;
import com.shyamSofttech.studentManagement.configs.TokenContext;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    protected LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date", nullable = false)
    protected LocalDateTime lastModifiedDate;
    protected Long createdBy;
    protected Long modifiedBy;

    @PrePersist
    private void updateCreatedAndModifiedBy(){
        if(TokenContext.getToken() == null){
            setCreatedBy(0L);
            setModifiedBy(0L);
        }else {
            setCreatedBy(JwtHelper.getUserIdFromToken(TokenContext.getToken()));
            setModifiedBy(JwtHelper.getUserIdFromToken(TokenContext.getToken()));
        }
    }

    @PreUpdate
    private void updateModifiedBy(){
        if(TokenContext.getToken() == null){
            setModifiedBy(0L);
        }else {
            setModifiedBy(JwtHelper.getUserIdFromToken(TokenContext.getToken()));
        }
    }
}
