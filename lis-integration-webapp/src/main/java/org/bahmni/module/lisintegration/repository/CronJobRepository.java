package org.bahmni.module.lisintegration.repository;

import org.bahmni.module.lisintegration.model.QuartzCronScheduler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobRepository extends JpaRepository<QuartzCronScheduler, Integer> {
}
