package com.thelifelogger.serverspring.repository;

import com.thelifelogger.serverspring.model.ActivityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRuleRepository extends JpaRepository<ActivityRule, Long> {
}
