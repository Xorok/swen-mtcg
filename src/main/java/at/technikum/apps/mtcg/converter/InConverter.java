package at.technikum.apps.mtcg.converter;

interface InConverter<T, R> {
    R convert(T obj) throws Exception;
}