package org.weixin.framework.database.core.query;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

import java.util.Collection;
import java.util.Objects;

public class LambdaQueryWrapperX<T> extends LambdaQueryWrapper<T> {

    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        if (StrUtil.isNotBlank(val)) {
            return (LambdaQueryWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> inIfNotEmpty(SFunction<T, ?> column, Collection<?> values) {
        if (CollectionUtil.isNotEmpty(values)) {
            return (LambdaQueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> inIfNotEmpty(SFunction<T, ?> column, Object... values) {
        if (values != null && values.length > 0) {
            return (LambdaQueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (Objects.nonNull(val1) && Objects.nonNull(val2)) {
            return (LambdaQueryWrapperX<T>) super.between(column, val1, val2);
        }
        if (Objects.nonNull(val1)) {
            return (LambdaQueryWrapperX<T>) super.ge(column, val1);
        }
        if (Objects.nonNull(val2)) {
            return (LambdaQueryWrapperX<T>) super.le(column, val2);
        }
        return this;
    }


    private String getColumnName(SFunction<T, ?> column) {
        return super.columnsToString(column);
    }


    public static void main(String[] args) {
        String expectedSQLStr = "SELECT 1 FROM dual t WHERE a = b";
        // Step 1: generate the Java Object Hierarchy for
        Table table = new Table().withName("dual").withAlias(new Alias("t", false));
        Column columnA = new Column().withColumnName("a");
        Column columnB = new Column().withColumnName("b");
        Expression whereExpression =
                new EqualsTo().withLeftExpression(columnA).withRightExpression(columnB);
        //
        JsonOperator jsonOperator = new JsonOperator("->")
                .withLeftExpression(new Column("data"))
                .withRightExpression(new StringValue("$.name"));
        //
        Function function = new Function()
                .withName("JSON_EXTRACT")
                .withParameters(new ExpressionList(new Column("data"), new StringValue("$.name")));
        //
        PlainSelect select = new PlainSelect().addSelectItems(new SelectExpressionItem(function))
                .withFromItem(table).withWhere(whereExpression);

        System.out.println(select.toString());

    }

}
