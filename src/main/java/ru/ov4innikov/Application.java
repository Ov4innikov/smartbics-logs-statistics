package ru.ov4innikov;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.ov4innikov.manager.FileParserManager;
import ru.ov4innikov.util.StatisticsFileWriter;

import java.io.File;
import java.util.Map;

@Log4j2
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Value("${mode}")
    private String mode;

    @Value("${logs.path}")
    private String logsPath;

    @Autowired
    private StatisticsFileWriter statisticsFileWriter;

    @Autowired
    private FileParserManager fileParserManager;

    @Override
    public void run(String... args) throws Exception {
        long l1 = System.currentTimeMillis();
        log.info("Время запуска сервиса! = {}", l1);
        if (mode == null || logsPath == null) {
            log.warn("java -jar smartbics-logs-statistics-0.0.1.jar --mode=hours --logs.path=...");
            log.warn("Поддерживается два режима, для статистики по часам(hours) и по минутам(minutes). Задаётся параметром mode.");
            log.warn("Параметр logs.path определяет путь до анализируемых логов.");
            return;
        }
        File file = new File(logsPath);
        Map<String, Number> stringNumberMap = fileParserManager.calculateStats(file);
        statisticsFileWriter.writeStatistics(stringNumberMap, file);
        long l2 = System.currentTimeMillis();
        log.info("Время остановки сервиса! = {}, время выполнения в миллисекундах = {}", l2, l2 - l1);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
