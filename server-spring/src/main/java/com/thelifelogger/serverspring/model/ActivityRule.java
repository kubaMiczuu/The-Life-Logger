package com.thelifelogger.serverspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="activity_rules")
public class ActivityRule {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="pattern", unique = true, nullable = false)
    private String pattern;

    @Enumerated(EnumType.STRING)
    @Column(name="rule_type")
    private RuleType ruleType;

    @Column(name="category")
    private String category;

    @Column(name="domain")
    private String domain;
}
