# Stage 0 Spring Boot Bootstrap Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build and verify a minimal Java 21 Spring Boot REST application, establish version control, and create learning records that explain the project rather than hiding its structure behind generated code.

**Architecture:** A single Maven module contains one Spring Boot application and one HTTP health endpoint. The endpoint is tested through Spring MVC's `MockMvc`; no database connection or business feature is added in Stage 0.

**Tech Stack:** Java 21.0.12, Spring Boot 4.1.0, Maven 3.9.16, JUnit 5, Spring Boot Test, Git 2.53.0

## Global Constraints

- Use Java 21 for compilation and execution even while JDK 26 remains installed.
- Use Spring Boot REST API without a command-line application or frontend.
- Use Maven as the build tool.
- Do not connect to MySQL or introduce MyBatis until the first persistence task in Stage 1.
- Do not use Lombok, code generation, Spring Security, Redis, Docker, or MyBatis-Plus in Stage 0.
- The learner writes and understands the application class, controller, and test; AI may review or explain them.

---

## File Map

- `.gitignore`: prevents IDE, Maven output, logs, and local secrets from entering version control.
- `pom.xml`: fixes Java and Spring Boot versions and declares only web and test dependencies.
- `src/main/java/com/pp/library/LibraryManagementApplication.java`: application entry point and root component-scan package.
- `src/main/java/com/pp/library/health/HealthController.java`: exposes the Stage 0 REST endpoint.
- `src/main/resources/application.yml`: assigns the application name and HTTP port.
- `src/test/java/com/pp/library/health/HealthControllerTest.java`: verifies the HTTP contract without starting a real server.
- `docs/learning/roadmap.md`: records the six-stage learning sequence.
- `docs/learning/progress.md`: records verified work and commands.
- `docs/learning/weak-points.md`: records concepts that need review.
- `docs/learning/decisions.md`: records version and architecture decisions.

### Task 1: Establish the Maven application baseline

**Files:**
- Create: `.gitignore`
- Create: `pom.xml`
- Create: `src/main/java/com/pp/library/LibraryManagementApplication.java`
- Create: `src/main/resources/application.yml`
- Test: `src/test/java/com/pp/library/LibraryManagementApplicationTests.java`

**Interfaces:**
- Consumes: JDK at `C:\Program Files\Java\jdk-21.0.12`, Maven 3.9.16.
- Produces: executable `com.pp.library.LibraryManagementApplication` and a loadable Spring application context.

- [ ] **Step 1: Initialize Git and create `.gitignore`**

Run:

```powershell
git init
```

Create `.gitignore` with:

```gitignore
target/
.idea/
*.iml
.vscode/
*.log
.env
application-local.yml
```

- [ ] **Step 2: Create the Maven build**

Create `pom.xml` with:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>

    <groupId>com.pp</groupId>
    <artifactId>library-management-system</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>library-management-system</name>
    <description>Spring Boot library management learning project</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: Write a failing application-context test**

Create `src/test/java/com/pp/library/LibraryManagementApplicationTests.java`:

```java
package com.pp.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LibraryManagementApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 4: Run the test and verify the expected failure**

Run:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn -Dtest=LibraryManagementApplicationTests test
```

Expected: FAIL because `LibraryManagementApplication` does not exist and Spring cannot find a `@SpringBootConfiguration`.

- [ ] **Step 5: Implement the application entry point and configuration**

Create `src/main/java/com/pp/library/LibraryManagementApplication.java`:

```java
package com.pp.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }
}
```

Create `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: library-management-system

server:
  port: 8080
```

- [ ] **Step 6: Run the context test with Java 21**

Run:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
mvn -Dtest=LibraryManagementApplicationTests test
```

Expected: `java version "21.0.12"` and Maven reports `BUILD SUCCESS` with one passing test.

- [ ] **Step 7: Commit the baseline**

```powershell
git add .gitignore pom.xml src docs/superpowers/specs docs/superpowers/plans
git commit -m "chore: bootstrap Spring Boot learning project"
```

### Task 2: Add the first REST endpoint test-first

**Files:**
- Create: `src/main/java/com/pp/library/health/HealthController.java`
- Create: `src/test/java/com/pp/library/health/HealthControllerTest.java`

**Interfaces:**
- Consumes: Spring MVC auto-configuration from `LibraryManagementApplication`.
- Produces: `GET /api/health` returning HTTP 200 and JSON `{"status":"UP"}`.

- [ ] **Step 1: Write the failing MVC test**

Create `src/test/java/com/pp/library/health/HealthControllerTest.java`:

```java
package com.pp.library.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnUpStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
```

- [ ] **Step 2: Run the endpoint test and verify it fails**

Run:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn -Dtest=HealthControllerTest test
```

Expected: FAIL at test compilation because `HealthController` does not exist.

- [ ] **Step 3: Implement the minimal controller**

Create `src/main/java/com/pp/library/health/HealthController.java`:

```java
package com.pp.library.health;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
```

- [ ] **Step 4: Run all automated tests**

Run:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn test
```

Expected: `BUILD SUCCESS` with both context and health-controller tests passing.

- [ ] **Step 5: Start the application and verify the real HTTP response**

Run in one PowerShell window:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn spring-boot:run
```

Run in a second PowerShell window:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

Expected response:

```text
status
------
UP
```

- [ ] **Step 6: Commit the endpoint**

```powershell
git add src/main/java/com/pp/library/health src/test/java/com/pp/library/health
git commit -m "feat: add health check endpoint"
```

### Task 3: Establish the learning journal

**Files:**
- Create: `docs/learning/roadmap.md`
- Create: `docs/learning/progress.md`
- Create: `docs/learning/weak-points.md`
- Create: `docs/learning/decisions.md`

**Interfaces:**
- Consumes: verified Stage 0 environment and test results from Tasks 1 and 2.
- Produces: stable Markdown locations for future progress, weaknesses, and technical decisions.

- [ ] **Step 1: Create `roadmap.md`**

```markdown
# 学习路线

- [x] 阶段 0：环境、Spring Boot 工程和健康检查接口
- [ ] 阶段 1：图书 CRUD
- [ ] 阶段 2：参数校验与统一异常处理
- [ ] 阶段 3：读者和借阅业务
- [ ] 阶段 4：事务与并发库存控制
- [ ] 阶段 5：自动化测试体系
- [ ] 阶段 6：认证授权与工程化
```

- [ ] **Step 2: Create `progress.md`**

```markdown
# 学习进度

## 阶段 0

- Java：21.0.12
- Maven：3.9.16
- MySQL：8.0.46
- Spring Boot：4.1.0
- 已完成：Maven 工程、Spring 上下文测试、健康检查接口与 MVC 测试。
- 验证命令：`mvn test`
- HTTP 验证：`GET /api/health` 返回 `{"status":"UP"}`。
```

- [ ] **Step 3: Create `weak-points.md`**

```markdown
# 薄弱点记录

## 待自测

- 能否解释 `@SpringBootApplication` 组合了哪些核心能力？
- 能否解释为什么启动类放在根包 `com.pp.library`？
- 能否解释 Maven 的 `test` 与 `spring-boot:run` 分别做什么？
- 能否解释 `@WebMvcTest` 为什么不加载完整应用？
```

- [ ] **Step 4: Create `decisions.md`**

```markdown
# 技术决策

## Java 21

选择长期支持版本 Java 21，而不是本机同时安装的 Java 26，以贴近企业后端项目并降低依赖兼容风险。

## Spring Boot 4.1.0

使用当前稳定版本，并确认其支持 Java 21。项目采用 Spring MVC REST API。

## 暂不连接 MySQL

阶段 0 只验证工程、Spring 容器和 HTTP 链路。数据库与 MyBatis 在阶段 1 首个持久化功能中加入，确保每个依赖都有明确用途。
```

- [ ] **Step 5: Verify documentation and repository state**

Run:

```powershell
Get-ChildItem docs/learning
git status --short
```

Expected: four learning Markdown files are listed; Git status shows only those new documentation files.

- [ ] **Step 6: Commit the learning journal**

```powershell
git add docs/learning
git commit -m "docs: add learning progress journal"
```

### Task 4: Stage 0 completion gate

**Files:**
- Modify: `docs/learning/progress.md`

**Interfaces:**
- Consumes: passing Maven test suite and working `GET /api/health` endpoint.
- Produces: evidence that Stage 0 is complete and a clean handoff to Stage 1.

- [ ] **Step 1: Run the complete verification suite with Java 21**

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
mvn --version
mvn clean test
git status --short
```

Expected:

- Java and Maven both report Java 21.0.12.
- Maven reports `BUILD SUCCESS`.
- Tests include the application-context and health-controller tests.
- Git status is clean before recording the final evidence.

- [ ] **Step 2: Record actual verification evidence**

Append to `docs/learning/progress.md` using the real test count and date from Step 1:

```markdown

### 验证结果

- 验证日期：2026-08-04
- `mvn clean test`：通过
- 应用上下文测试：通过
- `GET /api/health` MVC 测试：通过
- 阶段结论：阶段 0 完成，可以进入图书 CRUD。
```

- [ ] **Step 3: Commit completion evidence**

```powershell
git add docs/learning/progress.md
git commit -m "docs: record stage zero verification"
git status --short
```

Expected: commit succeeds and `git status --short` has no output.
