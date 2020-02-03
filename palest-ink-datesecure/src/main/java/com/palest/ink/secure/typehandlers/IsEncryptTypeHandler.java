
package com.palest.ink.secure.typehandlers;

import com.palest.ink.secure.common.beans.EncryptType;
import com.palest.ink.secure.common.config.EncryptSwitchConfig;
import com.palest.ink.secure.common.constants.DataSecureConstants;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类IsEncryptTypeHandler.java的实现描述：
 * 
 * @author huanglifang Jun 2, 2017 5:45:30 PM
 */
public class IsEncryptTypeHandler extends BaseTypeHandler<EncryptType> {

    @Override
    public EncryptType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return new EncryptType(rs.getString(columnIndex));
    }

    @Override
    public EncryptType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return new EncryptType(rs.getString(columnName));
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, EncryptType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, DataSecureConstants.IS_ENCRYPT_CLOSE);
        if (EncryptSwitchConfig.getEncryptFlag()) {
            ps.setString(i, DataSecureConstants.IS_ENCRYPT_OPEN);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.
     * CallableStatement, int)
     */
    @Override
    public EncryptType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return new EncryptType(cs.getString(columnIndex));
    }

    /*
     * (non-Javadoc)
     * @see org.apache.ibatis.type.BaseTypeHandler#setNonNullParameter(java.sql.
     * PreparedStatement, int, java.lang.Object,
     * org.apache.ibatis.type.JdbcType)
     */
    @Override
    public void setNonNullParameter(PreparedStatement arg0, int arg1, EncryptType arg2, JdbcType arg3)
            throws SQLException {

    }
}
