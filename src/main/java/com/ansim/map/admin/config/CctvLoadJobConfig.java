package com.ansim.map.admin.config;

import com.ansim.map.admin.dto.CctvCsvDto;
import com.ansim.map.admin.listener.CctvJobListener;
import com.ansim.map.admin.processor.CctvItemProcessor;
import com.ansim.map.admin.writer.CctvItemWriter;
import com.ansim.map.domain.entity.CctvInfrastructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CctvLoadJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CctvItemProcessor cctvItemProcessor;
    private final CctvItemWriter cctvItemWriter;
    private final CctvJobListener cctvJobListener;
    private final JdbcTemplate jdbcTemplate;

    private static final int CHUNK_SIZE = 5000;

    @Bean
    public Job cctvLoadJob() {
        return new JobBuilder("cctvLoadJob", jobRepository)
                .listener(cctvJobListener)
                .start(truncateStep())
                .next(cctvLoadStep())
                .build();
    }

    // Step 1. TRUNCATE
    @Bean
    public Step truncateStep() {
        return new StepBuilder("truncateStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("[CCTV] TRUNCATE 시작");
                    //jdbcTemplate.update("DELETE FROM safety_infrastructure WHERE category = 'CCTV'");
                    jdbcTemplate.execute("TRUNCATE TABLE cctv_infrastructure");
                    log.info("[CCTV] TRUNCATE 완료");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // Step 2. CSV Read → Process → Write
    @Bean
    public Step cctvLoadStep() {
        return new StepBuilder("cctvLoadStep", jobRepository)
                .<CctvCsvDto, CctvInfrastructure>chunk(CHUNK_SIZE, transactionManager)
                .reader(cctvItemReader(null))
                .processor(cctvItemProcessor)
                .writer(cctvItemWriter)
                .faultTolerant()                              // 추가
                .skip(FlatFileParseException.class)           // 추가
                .skipLimit(1000)                              // 추가 (최대 1000건 skip 허용)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CctvCsvDto> cctvItemReader(
            @Value("#{jobParameters['filePath']}") String filePath) {

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setQuoteCharacter('"');  // 쌍따옴표 처리
        tokenizer.setNames(
                "openLocalCode", "mngNo", "managementOrg",
                "roadAddress", "lotAddress", "installPurpose",
                "cameraCount", "cameraPixels", "shootingDirection",
                "storageDays", "installYearMonth", "managerTel",
                "latitude", "longitude", "dataStandardDate", "lastModified"
        );

        DefaultLineMapper<CctvCsvDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(CctvCsvDto.class);
        }});

        FlatFileItemReader<CctvCsvDto> reader = new FlatFileItemReader<>();
        reader.setName("cctvItemReader");
        reader.setResource(new FileSystemResource(filePath));
        reader.setEncoding("EUC-KR");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);
        return reader;
    }
}