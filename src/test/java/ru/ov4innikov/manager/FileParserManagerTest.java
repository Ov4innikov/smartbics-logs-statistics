package ru.ov4innikov.manager;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class FileParserManagerTest {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void testGetFiles() {
        File file = new File("src/test/resources/");
        FileParserManager fileParserManager = new FileParserManager(executorService);
        List<File> actualResult = fileParserManager.getFiles(file);
        Assert.assertEquals(2, actualResult.size());
    }

    @Test
    public void testCalculateStats() throws InterruptedException {
        File file = new File("src/test/resources/");
        FileParserManager fileParserManager = new FileParserManager(executorService);
        Map<String, ? extends Number> stringMap = fileParserManager.calculateStats(file);
        log.trace(stringMap.toString());
        Assert.assertEquals(12, stringMap.keySet().size());
    }
}