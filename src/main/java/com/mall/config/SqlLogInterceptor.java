package com.mall.config;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SqlLogInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(SqlLogInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = showSql(boundSql, parameter);
        logger.info("Executing SQL: {}", sql);
        return invocation.proceed();
    }

    private String showSql(BoundSql boundSql, Object parameterObject) {
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value = null;
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject != null) {
                    if (parameterObject instanceof Map) {
                        value = ((Map<?, ?>) parameterObject).get(propertyName);
                    } else {
                        try {
                            Field field = parameterObject.getClass().getDeclaredField(propertyName);
                            field.setAccessible(true);
                            value = field.get(parameterObject);
                        } catch (Exception e) {
                            // Ignore
                        }
                    }
                }
                sql = sql.replaceFirst("\\?", value == null ? "null" : "'" + value.toString() + "'");
            }
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        // No properties needed
    }
}
