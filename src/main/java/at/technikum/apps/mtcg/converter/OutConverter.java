package at.technikum.apps.mtcg.converter;

interface OutConverter<T, R> {
    R convert(T obj);
}