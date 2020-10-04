package ru.ov4innikov;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ov4innikov.manager.FileParserManager;
import ru.ov4innikov.util.StatisticsFileWriter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfiguration {

    @Bean(destroyMethod = "stop")
    public FileParserManager fileParserManager(ExecutorService executorService) {
        return new FileParserManager(executorService);
    }

    @Bean
    public StatisticsFileWriter statisticsFileWriter() {
        return new StatisticsFileWriter();
    }

    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(15);
    }
}
