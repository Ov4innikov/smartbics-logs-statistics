package ru.ov4innikov;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private Application application;

    @Value("${logs.path}")
    private String logsPath;

    @Test
    public void test() throws Exception {
        //application.run();
        File file = new File(logsPath);
        Assert.assertTrue(file.exists());
    }
}