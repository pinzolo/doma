package org.seasar.doma.internal.jdbc.mock;

import static org.seasar.doma.internal.util.Assertions.*;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.seasar.doma.internal.util.Assertions;


/**
 * 
 * @author taedium
 * 
 */
public class MockPreparedStatement extends MockStatement implements
        PreparedStatement {

    public MockResultSet resultSet = new MockResultSet();

    public List<BindValue> bindValues = new ArrayList<BindValue>();

    public String sql;

    public MockPreparedStatement() {
    }

    public MockPreparedStatement(MockResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public void addBatch() throws SQLException {
        addBatchCount++;
    }

    @Override
    public void clearParameters() throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public boolean execute() throws SQLException {
        return false;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        assertTrue(!closed);
        if (resultSet == null) {
            resultSet = new MockResultSet();
        }
        return resultSet;
    }

    @Override
    public int executeUpdate() throws SQLException {
        assertTrue(!closed);
        return updatedRows;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        Assertions.notYetImplemented();
        return null;
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        Assertions.notYetImplemented();
        return null;
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x)
            throws SQLException {
        assertTrue(!closed);
        bindValues.add(new BindValue("BigDecimal", parameterIndex, x));
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader,
            long length) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        assertTrue(!closed);
        bindValues.add(new BindValue("Int", parameterIndex, x));
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        assertTrue(!closed);
        bindValues.add(new BindValue("Long", parameterIndex, x));
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value,
            long length) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setNString(int parameterIndex, String value)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        assertTrue(!closed);
        bindValues.add(new BindValue(sqlType, parameterIndex));
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType,
            int scaleOrLength) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        assertTrue(!closed);
        bindValues.add(new BindValue("String", parameterIndex, x));
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @SuppressWarnings("deprecation")
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        Assertions.notYetImplemented();

    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        Assertions.notYetImplemented();

    }

}