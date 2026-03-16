package com.ansim.map.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/public-data")
@RequiredArgsConstructor
public class AdminPublicDataController {

    private final JobLauncher jobLauncher;
    private final List<Job> publicDataJobs; // 핵심: 빈(Bean)으로 등록된 모든 Job을 리스트로 주입받음

    @PostMapping("/{dataType}/upload") // {dataType}을 경로 변수로 받음 (cctv, lamp 등)
    public ResponseEntity<String> uploadData(
            @PathVariable String dataType,
            @RequestParam MultipartFile file) {

        Path tempFile = null;
        try {
            // 1. 임시 파일 생성
            tempFile = Files.createTempFile(dataType + "_", ".csv");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("[{}] 임시 파일 생성 완료: {}", dataType, tempFile);

            // 2. 요청받은 dataType에 맞는 Job 찾기
            // 예: /cctv/upload 호출 시 이름이 'cctv'로 시작하는 Job을 찾음
            Job targetJob = publicDataJobs.stream()
                    .filter(job -> job.getName().toLowerCase().startsWith(dataType.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 데이터 타입입니다: " + dataType));

            // 3. Job 파라미터 설정
            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", tempFile.toString())
                    .addString("fileName", file.getOriginalFilename())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // 4. Job 실행
            jobLauncher.run(targetJob, params);

            return ResponseEntity.ok(dataType.toUpperCase() + " 데이터 적재 성공");

        } catch (Exception e) {
            log.error("[{} 업로드 실패]", dataType, e);
            return ResponseEntity.internalServerError().body("적재 실패: " + e.getMessage());
        } finally {
            // 5. 자동 임시 파일 삭제
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    log.info("[{}] 임시 파일 삭제 완료", dataType);
                } catch (Exception ex) {
                    log.warn("[{}] 임시 파일 삭제 실패: {}", dataType, ex.getMessage());
                }
            }
        }
    }
}