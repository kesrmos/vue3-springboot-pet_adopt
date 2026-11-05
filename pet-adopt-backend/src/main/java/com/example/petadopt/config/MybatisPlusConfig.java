package com.example.petadopt.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置：分页插件
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最多 100 条，防止恶意传参
        pagination.setMaxLimit(100L);
        // 超过最大页后返回空数据而不是回到第一页
        pagination.setOverflow(false);
        // 联表分页时优化 join（申请列表使用 XML 联表查询）
        pagination.setOptimizeJoin(true);
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
