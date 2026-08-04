# 技术决策

## Java 21

选择长期支持版本 Java 21，而不是本机同时安装的 Java 26，以贴近企业后端项目并降低依赖兼容风险。

## Spring Boot 4.1.0

使用当前稳定版本，并确认其支持 Java 21。项目采用 Spring MVC REST API。

## 暂不连接 MySQL

阶段 0 只验证工程、Spring 容器和 HTTP 链路。数据库与 MyBatis 在阶段 1 首个持久化功能中加入，确保每个依赖都有明确用途。

## 包结构

启动类位于 `com.pp.library` 根包，健康控制器位于 `com.pp.library.health` 子包，使 Spring Boot 的组件扫描可以覆盖业务子包。
