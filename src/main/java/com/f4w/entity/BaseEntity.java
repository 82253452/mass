package com.f4w.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author admin
 */
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(insertable = false, updatable = false)
    private Date ctime;
    @Column(insertable = false, updatable = false)
    private Date mtime;
    @Column(insertable = false, updatable = false)
    private Integer delete;
}
