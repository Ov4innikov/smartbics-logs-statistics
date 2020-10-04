package ru.ov4innikov.util;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import ru.ov4innikov.manager.FileParserManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class StatisticsFileWriterTest {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void writeStatisticsMinutes() throws InterruptedException, IOException {
        File file = new File("src/test/resources/");
        File fileResult = new File("src/test/resources/minutes/Statistics");
        FileParserManager fileParserManager = new FileParserManager(executorService);
        Map<String, Number> stringMap = fileParserManager.calculateStats(file);
        log.trace(stringMap.toString());
        StatisticsFileWriter statisticsFileWriter = new StatisticsFileWriter();
        statisticsFileWriter.writeStatistics(stringMap, fileResult);
        String actual = new String(Files.readAllBytes(fileResult.toPath()), "UTF-8");
        File file1 = new File("src/test/resources/minutes/StatisticsExpected");
        String expected = new String(Files.readAllBytes(file1.toPath()), "UTF-8");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void writeStatisticsHours() throws InterruptedException, IOException {
        File file = new File("src/test/resources");
        File fileResult = new File("src/test/resources/hours/Statistics");
        FileParserManager fileParserManager = new FileParserManager(executorService);
        Map<String, Number> stringMap = fileParserManager.calculateStats(file);
        log.trace(stringMap.toString());
        StatisticsFileWriter statisticsFileWriter = new StatisticsFileWriter();
        statisticsFileWriter.writeStatistics(stringMap, fileResult, "hours");
        String actual = new String(Files.readAllBytes(fileResult.toPath()), "UTF-8");
        File file1 = new File("src/test/resources/hours/StatisticsExpected");
        String expected = new String(Files.readAllBytes(file1.toPath()), "UTF-8");
        Assert.assertEquals(expected, actual);
    }
}