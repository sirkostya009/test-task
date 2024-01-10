package ua.sirkostya009.csvstatstesttask.validator;

public interface Validator<T> {
    boolean validate(T t);
}
