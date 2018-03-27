package com.jacky.sams.dao;

import com.jacky.sams.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityRepository extends JpaRepository<Activity, String>,JpaSpecificationExecutor<Activity> {
}
