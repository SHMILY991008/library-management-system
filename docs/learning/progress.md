# 学习进度

## 阶段 0

- Java：21.0.12
- Maven：3.9.16
- MySQL：8.0.46
- Spring Boot：4.1.0
- 已完成：Maven 工程、Spring 上下文测试、健康检查接口与 MVC 测试。
- TDD 结果：先观察到控制器缺失导致测试失败，再补充最小实现并通过。
- 验证命令：`mvn test`
- 自动化结果：2 个测试通过，0 个失败。
- HTTP 契约：`GET /api/health` 返回 HTTP 200 和 `{"status":"UP"}`。

### 验证结果

- 验证日期：2026-08-04
- `mvn test`：通过
- 应用上下文测试：通过
- `GET /api/health` MVC 测试：通过
- `GET /api/health` 本地真实 HTTP：通过
- 阶段结论：阶段 0 完成，可以进入图书 CRUD。
