package at.technikum.apps.mtcg.util;

import java.sql.SQLException;

public interface SQLCloseable extends AutoCloseable {
    @Override
    public void close() throws SQLException;
}