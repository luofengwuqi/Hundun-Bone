package com.createpen.fifteenhundun.framework.interception;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * SQL拦截器：捕获正在执行的SQL，存入ThreadLocal
 */
@Slf4j
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
@Component
public class CurrentSqlInterceptor implements Interceptor {
    private static final ThreadLocal<String> CURRENT_SQL = new ThreadLocal<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentSql() {
        return CURRENT_SQL.get();
    }

    public static void clear() {
        CURRENT_SQL.remove();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Configuration configuration = (Configuration) metaObject.getValue("delegate.configuration");
        String originalSql = boundSql.getSql().trim().replaceAll("\\s+", " ");

        // 替换参数占位符
        String realSql = replaceParameters(originalSql, boundSql, configuration);
        CURRENT_SQL.set(realSql);

        try {
            return invocation.proceed();
        } finally {
            log.info("SQL: {}", realSql);
            // 在事务结束后清理，或在调用方手动清理
        }
    }

    /**
     * 替换SQL中的参数占位符
     */
    private String replaceParameters(String sql, BoundSql boundSql, Configuration configuration) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        if (parameterMappings == null || parameterMappings.isEmpty()) {
            return sql;
        }

        String currentSql = sql;
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        for (ParameterMapping parameterMapping : parameterMappings) {
            String propertyName = parameterMapping.getProperty();
            Object value;

            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                MetaObject metaParam = configuration.newMetaObject(parameterObject);
                value = metaParam.hasGetter(propertyName) ?
                    metaParam.getValue(propertyName) :
                    boundSql.getAdditionalParameter(propertyName);
            }

            String valueString = formatValue(value);
            // 替换第一个出现的?为实际值
            int firstIndex = currentSql.indexOf("?");
            if (firstIndex != -1) {
                currentSql = currentSql.substring(0, firstIndex) +
                           valueString +
                           currentSql.substring(firstIndex + 1);
            }
        }

        return currentSql;
    }

    /**
     * 格式化参数值
     */
    private String formatValue(Object value) {
        if (value == null) return "NULL";
        if (value instanceof String) {
            return "'" + value.toString().replace("'", "''") + "'";
        }
        if (value instanceof Date) {
            return "'" + DATE_FORMAT.format(value) + "'";
        }
        if (value instanceof java.time.LocalDateTime) {
            return "'" + value.toString() + "'";
        }
        return value.toString();
    }

    @Override
    public Object plugin(Object target) {
        return target instanceof StatementHandler ? Plugin.wrap(target, this) : target;
    }

    @Override
    public void setProperties(Properties properties) {}
}
