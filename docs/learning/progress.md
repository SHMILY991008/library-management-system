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

## 阶段 1

- 已完成：MySQL `book` 表、MyBatis Entity/Mapper、Service、图书新增/查询/修改/逻辑删除/分页搜索接口及自动化测试。
- 暂缓：Postman 完整 CRUD 手动验证。此前新增接口出现过 HTTP 500，但尚未读取应用端完整堆栈定位；暂不把手动验证标记为通过。
- 下一步：阶段 2 统一参数校验、业务异常和错误响应。
