package com.ansim.map.admin.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CctvJobListener implements JobExecutionListener {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
        boolean success = jobExecution.getStatus() == BatchStatus.COMPLETED;
        String fileName = jobExecution.getJobParameters().getString("fileName");
        long writeCount = jobExecution.getStepExecutions().stream()
                .mapToLong(s -> s.getWriteCount())
                .sum();

        String message = success ? null :
                jobExecution.getAllFailureExceptions().stream()
                        .map(Throwable::getMessage)
                        .findFirst()
                        .orElse("알 수 없는 오류");

        jdbcTemplate.update("""
            INSERT INTO public_data_load_history
              (data_type, file_name, total_count, status, message, loaded_at)
            VALUES (?, ?, ?, ?, ?, NOW())
            """,
                "CCTV", fileName, (int) writeCount,
                success ? "SUCCESS" : "FAIL", message
        );

        log.info("[CCTV Job] 완료: status={}, count={}", jobExecution.getStatus(), writeCount);
    }
}