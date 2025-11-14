package com.juliusbaer.cmt.pat.financialInstrument.batch;

import com.juliusbaer.cmt.pat.financialInstrument.config.CsvProperties;
import com.juliusbaer.cmt.pat.financialInstrument.repository.FinancialInstrumentRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final CsvProperties csvProperties;
    private final FinancialInstrumentRepository repository;

    @Bean
    public Step checkFileExistsStep() {
        return new StepBuilder("checkFileExistsStep", jobRepository).tasklet((contribution, chunkContext) -> {
            File dir = new File(csvProperties.getFilePath());
            File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
            if (files == null || files.length == 0) {
                LOGGER.error("No CSV files found in directory: {}. Stopping import.", dir.getAbsolutePath());
                contribution.setExitStatus(ExitStatus.FAILED);
                return RepeatStatus.FINISHED;
            }
            LOGGER.info("CSV file found: {}", files[0].getName());
            return RepeatStatus.FINISHED;
        }, transactionManager).build();
    }

    /* ---------- reader ---------- */
    @Bean
    @StepScope
    public FlatFileItemReader<com.juliusbaer.cmt.pat.financialInstrument.batch.FinancialInstrumentCsv> reader() {
        File dir = new File(csvProperties.getFilePath());
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) {
            LOGGER.error("No CSV files found in directory: {}", dir.getAbsolutePath());
            return new FlatFileItemReaderBuilder<FinancialInstrumentCsv>().name("emptyReader").resource(new FileSystemResource("")).linesToSkip(1).delimited().delimiter(";").names(csvProperties.getColumnNames()).targetType(FinancialInstrumentCsv.class).build();
        }
        File csvFile = files[0];
        LOGGER.info("CSV file found in directory: {} with name : {}", dir.getAbsolutePath(), csvFile.getName());
        return new FlatFileItemReaderBuilder<com.juliusbaer.cmt.pat.financialInstrument.batch.FinancialInstrumentCsv>().name("csvReader").resource(new FileSystemResource(csvFile)).linesToSkip(1)                       // header
                .delimited().delimiter(";")          // ← semicolon, not comma
                .names(csvProperties.getColumnNames()).targetType(com.juliusbaer.cmt.pat.financialInstrument.batch.FinancialInstrumentCsv.class).build();
    }

    /* ---------- writer (JDBC batch) ---------- */
    @Bean
    public JdbcBatchItemWriter<com.juliusbaer.cmt.pat.financialInstrument.batch.FinancialInstrumentCsv> writer() {
        String tableName = csvProperties.getTableName();
        String[] columns = csvProperties.getTableColumnNames();
        String columnList = String.join(", ", columns);
        return new JdbcBatchItemWriterBuilder<com.juliusbaer.cmt.pat.financialInstrument.batch.FinancialInstrumentCsv>().dataSource(dataSource).sql(String.format("""
                INSERT INTO %s
                  (%s)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)
                """, tableName, columnList)).itemPreparedStatementSetter((csv, ps) -> {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, csv.getAssJbGlobalId());
            ps.setString(3, csv.getAssIsin());
            ps.setString(4, csv.getAssValorNo());
            ps.setString(5, csv.getAssMstSecId());
            ps.setString(6, csv.getAssBbgid());
            ps.setString(7, csv.getAssIssuerNo());
            ps.setString(8, csv.getAssBjbNameLongIssuer());
            ps.setString(9, csv.getAssBjbNameLongProduct());
            ps.setString(10, csv.getAssBjbNameShortProduct());
            ps.setString(11, csv.getAssFicIndustrySector());
            ps.setString(12, csv.getAssInstrumentGroup());
            ps.setString(13, csv.getAssFicProductType());
            ps.setString(14, csv.getAssFundEsgCoverage());
            ps.setString(15, csv.getAssSfdrCategoryType());
            ps.setString(16, csv.getAssStatus());
            ps.setString(17, csv.getAssCountry());
            ps.setString(18, csv.getAssFicFixIncomeSector());
            ps.setString(19, csv.getAssMstFundId());
        }).build();
    }


    @Bean
    public Step purgeStep(DataSource ds) {
        String tableName = csvProperties.getTableName();
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        return new StepBuilder("purgeStep", jobRepository).tasklet((contribution, chunkContext) -> {
            jdbc.execute("TRUNCATE TABLE " + tableName);
            LOGGER.info("Table {} truncated", tableName);
            return RepeatStatus.FINISHED;
        }, transactionManager).build();
    }

    /* ---------- step ---------- */
    @Bean
    public Step importStep() {
        LOGGER.debug("Configuring import step to read from CSV and write to database.");
        return new StepBuilder("importStep", jobRepository).<com.juliusbaer.cmt.pat.financialInstrument.batch.FinancialInstrumentCsv, FinancialInstrumentCsv>chunk(csvProperties.getChunk(), transactionManager).reader(reader()).writer(writer()).build();
    }

    @Bean
    public Step refreshInstrumentenGruppeMvStep(DataSource ds) {
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        return new StepBuilder("refreshInstrumentenGruppeMvStep", jobRepository).tasklet((contribution, chunkContext) -> {
            jdbc.execute("BEGIN DBMS_MVIEW.REFRESH('mv_instruments_instrumenten_equities'); END;");
            jdbc.execute("BEGIN DBMS_MVIEW.REFRESH('mv_instruments_instrumenten_equities_funds_digitalasset'); END;");
            jdbc.execute("BEGIN DBMS_MVIEW.REFRESH('mv_instruments_all_not_options'); END;");
            LOGGER.info("MV mv_instruments_instrumenten_gruppe refreshed");
            return RepeatStatus.FINISHED;
        }, transactionManager).build();
    }


    @Bean
    public Step deleteCsvFileStep() {
        return new StepBuilder("deleteCsvFileStep", jobRepository).tasklet((contribution, chunkContext) -> {
            File dir = new File(csvProperties.getFilePath());
            File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
            if (files != null && files.length > 0) {
                File csvFile = files[0];
                boolean deleted = csvFile.delete();
                if (deleted) {
                    LOGGER.info("Deleted CSV file: {}", csvFile.getAbsolutePath());
                } else {
                    LOGGER.error("Failed to delete CSV file: {}", csvFile.getAbsolutePath());
                }
            } else {
                LOGGER.warn("No CSV file found to delete in directory: {}", dir.getAbsolutePath());
            }
            return RepeatStatus.FINISHED;
        }, transactionManager).build();
    }


    /* ---------- job ---------- */
    @Bean
    public Job importJob() throws Exception {
        LOGGER.debug("Configuring import job with file check, purge, import, and cleanup steps.");
        return new JobBuilder("importFinancialInstrumentsJob", jobRepository).start(checkFileExistsStep()).on("FAILED").end().from(checkFileExistsStep()).on("*").to(purgeStep(dataSource)).next(importStep()).next(refreshInstrumentenGruppeMvStep(dataSource)).next(deleteCsvFileStep()).end().build();
    }

    /**
     * This bean configures ShedLock to use your existing database
     * as the store for the distributed locks.
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .usingDbTime()
                .build());
    }
}