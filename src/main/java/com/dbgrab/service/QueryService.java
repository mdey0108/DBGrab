package com.dbgrab.service;

import java.nio.file.Path;
import java.util.Collections;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Log4j2 // Changed from @Slf4j to @Log4j2
public class QueryService {

    private final JdbcTemplate jdbcTemplate;
    private final TaskExecutor taskExecutor;

    @Value("${app.query.file}")
    private String queryFilePath;

    @Value("${app.input.file}")
    private String inputFilePath;

    @Value("${app.output.file}")
    private String outputFilePath;

    private String sqlQuery;

    @PostConstruct
    public void init() throws IOException {
        // Read SQL query from file
        sqlQuery = new String(Files.readAllBytes(Paths.get(queryFilePath)));
        log.info("Loaded SQL query: {}", sqlQuery);

        // Ensure output directory exists
        Files.createDirectories(Paths.get(outputFilePath).getParent());

        // Start processing
        log.debug("Executing query....");
        processQuery();
    }

    int corePoolSize;

    public void processQuery() {
        try {
            // Read input CSV - now returns List<List<String>> instead of List<String>
            List<List<String>> inputValues = readInputCsv();

            // Better batch size calculation with null check
            // int corePoolSize = 1;

            if (taskExecutor instanceof org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) {
                corePoolSize = ((org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) taskExecutor)
                        .getCorePoolSize();
            }
            int batchSize = Math.max(1, inputValues.size() / corePoolSize);

            // Create batches of input rows
            List<List<List<String>>> batches = new ArrayList<>();
            for (int i = 0; i < inputValues.size(); i += batchSize) {
                batches.add(inputValues.subList(i, Math.min(i + batchSize, inputValues.size())));
            }

            List<CompletableFuture<List<String[]>>> futures = new ArrayList<>();
            for (List<List<String>> batch : batches) {
                futures.add(processQueryBatch(batch));
            }

            // Wait for all futures to complete in parallel
            CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allDone.get(); // Wait for all

            // Combine results after all batches are done
            List<String[]> allResults = new ArrayList<>();
            for (CompletableFuture<List<String[]>> future : futures) {
                allResults.addAll(future.get());
            }

            writeToCsv(allResults);

            log.info("Query processing completed successfully. Results written to {}", outputFilePath);
        } catch (Exception e) {
            log.error("Error processing query", e);
        }
    }

    @Async
    public CompletableFuture<List<String[]>> processQueryBatch(List<List<String>> inputRows) {
        List<String[]> allResults = new ArrayList<>();

        for (List<String> row : inputRows) {
            // Prepare query with proper parameterization for each condition
            String formattedQuery = formatQueryWithConditions(sqlQuery, row.size());

            log.debug("Executing query with {} parameters", row.size());

            jdbcTemplate.query(
                    formattedQuery,
                    row.toArray(),
                    rs -> {
                        int columnCount = rs.getMetaData().getColumnCount();

                        // Add header row if this is the first result
                        if (allResults.isEmpty()) {
                            String[] headers = new String[columnCount];
                            for (int i = 1; i <= columnCount; i++) {
                                headers[i - 1] = rs.getMetaData().getColumnName(i);
                            }
                            allResults.add(headers);
                        }

                        // Add data row
                        String[] dataRow = new String[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            dataRow[i - 1] = rs.getString(i);
                        }
                        allResults.add(dataRow);
                    });
        }

        return CompletableFuture.completedFuture(allResults);
    }

    private String formatQueryWithConditions(String originalQuery, int paramCount) {
        // Replace :1, :2, :3 etc. with ? for prepared statement
        String formattedQuery = originalQuery;
        for (int i = 1; i <= paramCount; i++) {
            formattedQuery = formattedQuery.replace(":" + i, "?");
        }
        return formattedQuery;
    }

    private List<List<String>> readInputCsv() throws IOException {
        List<List<String>> values = new ArrayList<>();
        Path path = Paths.get(inputFilePath);
        if (!Files.exists(path)) {
            throw new IOException("Input file not found: " + inputFilePath);
        }

        try (CSVReader reader = new CSVReader(Files.newBufferedReader(path))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                values.add(Arrays.asList(line));
            }
        } catch (com.opencsv.exceptions.CsvValidationException e) {
            throw new IOException("Error reading CSV file", e);
        }
        return values;
    }

    private void writeToCsv(List<String[]> data) throws IOException {
        Path path = Paths.get(outputFilePath);
        try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(path))) {
            writer.writeAll(data);
        }
    }
}