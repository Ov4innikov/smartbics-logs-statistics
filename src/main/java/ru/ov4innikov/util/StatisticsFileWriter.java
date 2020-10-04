package ru.ov4innikov.util;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Log4j2
public class StatisticsFileWriter {

    public static final String NAME = "/Statistics";
    public static final DateTimeFormatter formatterMinutes = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    public static final DateTimeFormatter formatterHours = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");

    public void writeStatistics(Map<String, Number> map, File file) {
        writeStatistics(map, file, null);
    }

    public void writeStatistics(Map<String, Number> map, File file, String mode) {
        DateTimeFormatter formatter;
        Map<String, Number> statMap;
        if (mode != null && mode.equals("hours")) {
            formatter = formatterHours;
            statMap = new HashMap<>();
            for (String key : map.keySet()) {
                Number number = map.get(key);
                String subKey = key.substring(0, key.length() - 3);
                if (!statMap.containsKey(subKey)) {
                    statMap.put(subKey, number);
                } else {
                    Number number1 = statMap.get(subKey);
                    long newNumber = number.longValue() + number1.longValue();
                    statMap.put(subKey, newNumber);
                }
            }
        } else {
            formatter = formatterMinutes;
            statMap = map;
        }
        File resultFile;
        List<LocalDateTime> keys = statMap.keySet().stream().map(e -> LocalDateTime.parse(e, formatter)).collect(Collectors.toList());
        TreeSet<LocalDateTime> zonedDateTimes = new TreeSet<>(keys);

        if (file.exists()) {
            if (file.isDirectory()) {
                resultFile = new File(file.getAbsoluteFile() + NAME);
            } else {
                resultFile = new File(file.getParent() + NAME);
            }
        } else {
            log.error("Path not found");
            return;
        }

        try (FileWriter fileWriter = new FileWriter(resultFile, StandardCharsets.UTF_8)) {
            for (LocalDateTime dateTime : zonedDateTimes) {
                String key = dateTime.format(formatter);
                Number number = statMap.get(key);
                log.trace("Key = {}", key);
                fileWriter.write(key + " Количество ошибок: " + number + "\n");
            }
            fileWriter.flush();
        } catch (IOException e) {
            log.error("IOException ", e);
        }
    }
}
