package at.technikum.apps.mtcg.converter;

interface Converter<T, R> {
    R convert(T obj) throws Exception;
}