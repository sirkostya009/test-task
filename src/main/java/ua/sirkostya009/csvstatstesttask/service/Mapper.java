package ua.sirkostya009.csvstatstesttask.service;

import org.apache.commons.csv.CSVRecord;

import java.util.function.Function;

public interface Mapper<T> extends Function<CSVRecord, T> {
}
