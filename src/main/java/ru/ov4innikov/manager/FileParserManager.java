package ru.ov4innikov.manager;

import lombok.extern.log4j.Log4j2;
import ru.ov4innikov.parser.LogStatTimeParserTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Log4j2
public class FileParserManager {

    private final ExecutorService executorService;

    public FileParserManager(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Map<String, Number> calculateStats(File file) throws InterruptedException {
        Map<String, Number> statistics = new ConcurrentHashMap<>();
        List<File> files = getFiles(file);
        log.debug("Количество файлов = {}", files.size());
        Deque<String> deque = new ConcurrentLinkedDeque<>();
        files.forEach(f -> {
            try {
                LogStatTimeParserTask logStatTimeParserTask = new LogStatTimeParserTask(deque, new FileInputStream(f));
                log.warn("File = {}", f.getAbsoluteFile());
                executorService.submit(logStatTimeParserTask);
            } catch (FileNotFoundException e) {
                log.warn("File not found = {}", f.getAbsoluteFile());
            }
        });
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS) || !deque.isEmpty()) shovelQue(statistics, deque);

        return statistics;
    }

    private void shovelQue(Map<String, Number> statistics, Deque<String> deque) {
        while (!deque.isEmpty()) {
            String s = deque.pollFirst();
            String key = s.substring(0, s.indexOf('.') - 3);
            AtomicLong number = (AtomicLong) statistics.get(key);
            if (number != null) {
                number.incrementAndGet();
            } else {
                log.trace(key);
                statistics.put(key, new AtomicLong(1));
            }
        }
    }

    protected List<File> getFiles(File file) {
        List<File> files;
        if (file.isDirectory()) {
            try {
                files = Files.walk(file.toPath())
                        .filter(path -> !path.toFile().isDirectory() && path.toString().endsWith(".log"))
                        .map(path -> path.toFile()).collect(Collectors.toList());
            } catch (IOException ex) {
                files = Collections.EMPTY_LIST;
                log.error("Files read error: ", ex);
            }
        } else {
            files = Arrays.asList(file);
        }
        return files;
    }

    public void stop() {
//        log.info("Stopping Manager!");
//        executorService.shutdownNow();
    }
}
