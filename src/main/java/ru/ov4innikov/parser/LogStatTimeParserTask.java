package ru.ov4innikov.parser;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Deque;

@Log4j2
public class LogStatTimeParserTask implements Runnable {

    private final Deque<String> statistics;
    private final InputStream inputStream;

    public LogStatTimeParserTask(Deque<String> statistics, InputStream inputStream) {
        this.statistics = statistics;
        this.inputStream = inputStream;
    }

    private void parseAndWriteStat(InputStream inputStream) {
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            int i;
            boolean nextCharLogType = false; // флаг указывающий что следующий символ это тип лога
            boolean isReadingTime = true; // флаг указывающий что сейчас читаем время
            StringBuilder stringBuilder = new StringBuilder();
            while((i = bufferedInputStream.read())!= -1) {
                char ch = (char) i;
                if (!nextCharLogType && !isReadingTime && (ch != '\n')) {
                    //тип лога и время прочитали
                    continue;
                } else if (ch == ';') {
                    nextCharLogType = true;
                    isReadingTime = false;
                } else if (nextCharLogType) {
                    //проверяем тип лога
                    nextCharLogType = false;
                    if (ch == 'E') {
                        statistics.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        continue;
                    }
                    stringBuilder = new StringBuilder();
                } else if (isReadingTime) {
                    stringBuilder.append(ch);
                } else if (ch == '\n') {
                    //дальше новая строка, сбрасываем значения флагов
                    nextCharLogType = false;
                    isReadingTime = true;
                }
            }
        } catch (IOException ex) {
            log.warn("IOException", ex);
            return;
        }
    }

    @Override
    public void run() {
        parseAndWriteStat(inputStream);
    }
}
