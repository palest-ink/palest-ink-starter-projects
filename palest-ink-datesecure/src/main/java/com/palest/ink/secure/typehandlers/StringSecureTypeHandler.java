package com.palest.ink.secure.typehandlers;

import com.palest.ink.secure.common.config.EncryptSwitchConfig;
import com.palest.ink.secure.common.constants.DataSecureConstants;
import com.palest.ink.secure.utils.SuperClassReflectionUtils;
import com.palest.ink.secure.utils.SymmetryUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类StringSecureTypeHander.java的实现描述：
 * 
 * @author wanghy 2017年5月22日 下午7:10:01
 */
public class StringSecureTypeHandler extends BaseTypeHandler<String> {
    /**
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.type.BaseTypeHandler#setNonNullParameter(java.sql.PreparedStatement,
     *      int, java.lang.Object, org.apache.ibatis.type.JdbcType)
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        String sqlstr = null;
        Object object = SuperClassReflectionUtils.getFieldValue(ps, "originalSQL");
        if (object != null) {
            sqlstr = object.toString().toLowerCase();
        }
        /** 因存量数据的影响及加密存储总开关的灵活性，必须保证数据库一行记录的一致性 **/
        /** 对加密某个字段进行修改时，必须带上全部需要加密的字段及is_encrypt相关handler，行记录必须保持一致性 **/
        if (StringUtils.isNotBlank(sqlstr)) {
            int wpos = sqlstr.indexOf("where");
            if (wpos > -1 && sqlstr.indexOf("insert") > -1) {//不支持这种复杂的sql
                throw new SQLException("不支持这种特殊的SQL语句!");
            } else if (sqlstr.indexOf("insert") > -1 && sqlstr.indexOf("is_encrypt") == -1) {
                throw new SQLException("插入加密字段时，把所有加密字段及is_encrypt标志设置相关handler，必须保证'行记录'的一致性!");
            } else if (wpos > -1 && getCharacterPosition(sqlstr, i) >= wpos + 5) {//查询或待修改条件
                ps.setString(i, SymmetryUtil.encryption(parameter));
            } else {//插入或待修改的值
                ps.setString(i, parameter);
                if (EncryptSwitchConfig.getEncryptFlag()) {
                    ps.setString(i, SymmetryUtil.encryption(parameter));
                }
            }
        } else {
            throw new SQLException("sqlstr isBlank!");
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.ResultSet,
     *      java.lang.String)
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String decryptVal = rs.getString(columnName);
        String isEncrypt = rs.getString(DataSecureConstants.IS_ENCRYPT);
        if (StringUtils.equals(isEncrypt, DataSecureConstants.IS_ENCRYPT_OPEN)) {
            decryptVal = SymmetryUtil.decryption(decryptVal);
        }
        return decryptVal;
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.ResultSet,
     *      int)
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String decryptVal = rs.getString(columnIndex);
        String isEncrypt = rs.getString(DataSecureConstants.IS_ENCRYPT);
        if (StringUtils.equals(isEncrypt, DataSecureConstants.IS_ENCRYPT_OPEN)) {
            decryptVal = SymmetryUtil.decryption(decryptVal);
        }
        return decryptVal;
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.CallableStatement,
     *      int)
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String decryptVal = cs.getString(columnIndex);
        String isEncrypt = cs.getString(DataSecureConstants.IS_ENCRYPT);
        if (StringUtils.equals(isEncrypt, DataSecureConstants.IS_ENCRYPT_OPEN)) {
            decryptVal = SymmetryUtil.decryption(decryptVal);
        }
        return decryptVal;
    }

    private static int getCharacterPosition(String sqlstr, int count) {
        Matcher slashMatcher = Pattern.compile("\\?").matcher(sqlstr);
        int mIdx = 0;
        while (slashMatcher.find()) {
            mIdx++;
            if (mIdx == count) {
                break;
            }
        }
        return slashMatcher.start();
    }

}
