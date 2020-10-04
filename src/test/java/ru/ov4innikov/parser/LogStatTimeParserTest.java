package ru.ov4innikov.parser;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ov4innikov.AppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppConfiguration.class})
public class LogStatTimeParserTest {

//    @Autowired
//    private Deque<String> deque;

    @Test
    public void calculateAndWriteStat() throws ExecutionException, InterruptedException, FileNotFoundException {
            File file = new File("src/test/resources/test-log1.log");
        ConcurrentLinkedDeque deque = new ConcurrentLinkedDeque();
        LogStatTimeParserTask logStatTimeParser = new LogStatTimeParserTask(deque, new FileInputStream(file));
        RunnableFuture<Void> task = new FutureTask<>(logStatTimeParser, null);
        task.run();
        task.get();

        Deque<String> expectedObjects = new LinkedList<>();
        expectedObjects.add("2019-01-01T00:12:01.001");
        expectedObjects.add("2019-01-01T00:12:01.004");
        expectedObjects.add("2019-01-01T00:12:01.006");
        expectedObjects.add("2019-01-02T00:13:02.002");
        expectedObjects.add("2019-01-03T00:14:03.003");
        expectedObjects.add("2020-01-03T00:14:03.003");

        Assert.assertEquals(6, deque.size());
        Assert.assertArrayEquals(expectedObjects.toArray(), deque.toArray());
    }
}