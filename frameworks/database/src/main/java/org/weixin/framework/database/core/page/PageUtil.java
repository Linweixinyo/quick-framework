package org.weixin.framework.database.core.page;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PageUtil {

    /**
     * 构建查询Page
     *
     * @param pageRequest 分页查询请求参数
     * @return page
     */
    public static <T> Page<T> convert(PageRequest pageRequest) {
        return convert(pageRequest.getCurrent(), pageRequest.getSize());
    }

    /**
     * 构建查询Page
     *
     * @param current 当前页
     * @param size    页大小
     * @return page
     */
    public static <T> Page<T> convert(long current, long size) {
        return Page.of(current, size);
    }

    /**
     * 构建分页查询响应
     *
     * @param iPage 分页查询结果
     * @return 分页查询响应
     */
    public static <T> PageResponse<T> convert(IPage<T> iPage) {
        return buildConventionPage(iPage);
    }

    /**
     * 构建分页查询响应
     *
     * @param iPage 分页查询结果
     * @return 分页查询响应
     */
    public static <T> PageResponse<T> buildConventionPage(IPage<T> iPage) {
        return new PageResponse<T>().setRecords(iPage.getRecords())
                .setCurrent(iPage.getCurrent())
                .setSize(iPage.getSize())
                .setTotal(iPage.getTotal());
    }

    /**
     * 将分页结果转为目标类型
     *
     * @param iPage       分页查询结果
     * @param targetClass 目标类
     * @param <TARGET>    目标数据类型
     * @param <ORIGINAL>  源数据类型
     * @return 分页查询响应
     */
    public static <TARGET, ORIGINAL> PageResponse<TARGET> convert(IPage<ORIGINAL> iPage, Class<TARGET> targetClass) {
        return buildConventionPage(iPage.convert(each -> BeanUtil.toBean(each, targetClass)));
    }

    /**
     * 将分页结果转为目标类型
     *
     * @param iPage      分页查询结果
     * @param mapper     自定义映射规则
     * @param <TARGET>   目标数据类型
     * @param <ORIGINAL> 源数据类型
     * @return 分页查询响应
     */
    public static <TARGET, ORIGINAL> PageResponse<TARGET> convert(IPage<ORIGINAL> iPage, Function<? super ORIGINAL, ? extends TARGET> mapper) {
        List<TARGET> targetDataList = iPage.getRecords().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new PageResponse<TARGET>()
                .setCurrent(iPage.getCurrent())
                .setSize(iPage.getSize())
                .setRecords(targetDataList)
                .setTotal(iPage.getTotal());
    }

}
