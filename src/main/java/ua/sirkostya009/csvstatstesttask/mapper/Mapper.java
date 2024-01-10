package ua.sirkostya009.csvstatstesttask.mapper;

import org.apache.commons.csv.CSVRecord;

import java.util.function.Function;

public interface Mapper<T> extends Function<CSVRecord, T> {
}
