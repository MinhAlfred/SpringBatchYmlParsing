# DemoYAMLSpringBatch

A Spring Boot Batch application that demonstrates YAML-based job configuration and dynamic job scheduling. This project enables defining batch jobs declaratively in YAML files, making batch job configuration flexible and maintainable without code changes.

## Features

- **YAML-based Job Configuration**: Define batch jobs using simple YAML files
- **Automatic Job Scheduling**: Built-in cron-based scheduling support
- **Dynamic Job Loading**: Jobs are loaded and scheduled automatically at startup
- **Multiple Step Types**: Support for both chunk-oriented and tasklet-based steps
- **Registry Pattern**: Extensible registries for readers, writers, processors, tasklets, and row mappers
- **JDBC Support**: Built-in JDBC reader with named parameters
- **MS SQL Server Integration**: Configured for Microsoft SQL Server database

## Technology Stack

- **Java**: 21
- **Spring Boot**: 4.0.2
- **Spring Batch**: Batch processing framework
- **MS SQL Server**: Database for batch metadata and data
- **Jackson YAML**: YAML parsing
- **Lombok**: Reducing boilerplate code
- **Maven**: Build and dependency management

## Project Structure

```
src/main/java/test/demoyamlspringbatch/
├── DemoYamlSpringBatchApplication.java  # Main application entry point
└── batch/
    ├── engine/                           # Core batch engine
    │   ├── BatchScheduler.java          # Schedules and executes jobs
    │   ├── SchedulerConfig.java         # Scheduler configuration
    │   ├── YamlJobFactory.java          # Creates Job instances from definitions
    │   └── YamlJobLoader.java           # Loads job definitions from YAML
    ├── factory/
    │   └── ReaderFactory.java           # Factory for creating readers
    ├── impl/                            # Component implementations
    │   ├── processor/                   # Item processors
    │   │   ├── InputProcessor.java
    │   │   ├── LowerCaseProcessor.java
    │   │   └── UpperCaseProcessor.java
    │   ├── reader/                      # Item readers
    │   │   ├── NamedParameterJdbcItemReader.java
    │   │   └── SimpleStringReader.java
    │   ├── rowmapper/
    │   │   └── InputDataRowMapper.java
    │   ├── tasklet/
    │   │   └── CleanTempFileTasklet.java
    │   └── writer/
    │       └── ConsoleWriter.java
    ├── model/                           # Data models and definitions
    │   ├── ChunkStepDefinition.java
    │   ├── JobDefinition.java
    │   ├── RefDefinition.java
    │   ├── ScheduleDefinition.java
    │   ├── StepDefinition.java
    │   ├── StepType.java
    │   ├── TaskletStepDefinition.java
    │   ├── entites/
    │   │   ├── InputData.java
    │   │   └── OutputData.java
    │   └── reader/
    │       ├── BeanReaderDefinition.java
    │       ├── JdbcReaderDefinition.java
    │       └── ReaderDefinition.java
    └── registry/                        # Component registries
        ├── ProcessorRegistry.java
        ├── ReaderRegistry.java
        ├── RowMapperRegistry.java
        ├── TaskletRegistry.java
        └── WriterRegistry.java
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- MS SQL Server instance running on `localhost:1433`
- Database named `batchdb`

### Database Setup

1. Create a SQL Server database named `batchdb`
2. Update credentials in `application.yml` if needed (default: sa/12345)
3. Spring Batch will automatically create required metadata tables on startup

### Configuration

The application configuration is in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=batchdb;encrypt=false
    username: sa
    password: 12345
  
  batch:
    jdbc:
      initialize-schema: always
    job:
      enabled: false   # Important: prevents auto-run on startup
```

### Building the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/DemoYAMLSpringBatch-0.0.1-SNAPSHOT.jar
```

## YAML Job Configuration

Jobs are defined in `src/main/resources/batch-jobs/demo-job.yml`. The application supports multiple jobs in a single YAML file.

### Example Job Definition

```yaml
jobs:
  - name: jobA
    parameters:
      inputFile: classpath:data/input.txt
    schedule:
      type: cron
      expression: "0 */1 * * * *"  # Every minute
    
    steps:
      - name: step1
        type: chunk
        chunkSize: 2
        
        reader:
          type: bean
          bean: simpleStringReader
        
        processor:
          bean: upperCaseProcessor
        
        writer:
          bean: consoleWriter
      
      - name: step2
        type: chunk
        chunkSize: 2
        
        reader:
          type: jdbc
          sql: select id, name, status from input_data where status = :status
          params:
            status: NEW
          rowMapper: inputDataRowMapper
        
        processor:
          bean: inputProcessor
        
        writer:
          bean: consoleWriter
  
  - name: jobB
    schedule:
      type: cron
      expression: "30 */1 * * * *"  # Every minute at 30 seconds
    
    steps:
      - name: step1
        type: tasklet
        tasklet: cleanTempFileTasklet
        params:
          path: /tmp
```

### Step Types

#### 1. Chunk-Oriented Steps

Process items in chunks for better performance:

```yaml
- name: chunkStep
  type: chunk
  chunkSize: 10
  reader:
    type: bean
    bean: myReader
  processor:
    bean: myProcessor
  writer:
    bean: myWriter
```

#### 2. Tasklet Steps

Execute single-task operations:

```yaml
- name: taskletStep
  type: tasklet
  tasklet: myTasklet
  params:
    key1: value1
```

### Reader Types

#### Bean Reader

Use a Spring bean as a reader:

```yaml
reader:
  type: bean
  bean: simpleStringReader
```

#### JDBC Reader

Read from database with named parameters:

```yaml
reader:
  type: jdbc
  sql: select * from table where status = :status
  params:
    status: ACTIVE
  rowMapper: myRowMapper
```

## Extending the Application

### Adding a New Processor

1. Create a new processor class:

```java
@Component
@StepScope
public class MyProcessor implements ItemProcessor<Input, Output> {
    @Override
    public Output process(Input item) {
        // Transform logic
        return transformedItem;
    }
}
```

2. Reference it in your YAML:

```yaml
processor:
  bean: myProcessor
```

### Adding a New Reader

1. Create a new reader class:

```java
@Component
@StepScope
public class MyReader implements ItemReader<MyData> {
    @Override
    public MyData read() {
        // Reading logic
        return data;
    }
}
```

2. Use it in your YAML:

```yaml
reader:
  type: bean
  bean: myReader
```

### Adding a New Tasklet

1. Create a new tasklet class:

```java
@Component
public class MyTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, 
                                ChunkContext chunkContext) {
        // Task logic
        return RepeatStatus.FINISHED;
    }
}
```

2. Register and use it:

```yaml
steps:
  - name: myTask
    type: tasklet
    tasklet: myTasklet
```

## Architecture Highlights

### Registry Pattern

The application uses registry components to manage batch processing components:

- **ReaderRegistry**: Manages ItemReader beans
- **WriterRegistry**: Manages ItemWriter beans
- **ProcessorRegistry**: Manages ItemProcessor beans
- **TaskletRegistry**: Manages Tasklet beans
- **RowMapperRegistry**: Manages RowMapper beans

This pattern enables:
- Loose coupling between configuration and implementation
- Easy component registration and lookup
- Dynamic component resolution at runtime

### Job Lifecycle

1. **Startup**: `BatchScheduler` initializes
2. **Load**: `YamlJobLoader` reads YAML files from `batch-jobs/` directory
3. **Build**: `YamlJobFactory` creates Spring Batch Job instances
4. **Schedule**: Jobs are scheduled based on cron expressions
5. **Execute**: Jobs run automatically according to their schedules

## Cron Expression Examples

```
"0 */1 * * * *"     # Every minute
"0 0 */2 * * *"     # Every 2 hours
"0 0 0 * * *"       # Daily at midnight
"0 0 9 * * MON-FRI" # Weekdays at 9 AM
```

## Troubleshooting

### Jobs Not Running

- Check that `spring.batch.job.enabled=false` in application.yml
- Verify cron expressions are valid
- Check logs for scheduling errors

### Database Connection Issues

- Ensure SQL Server is running on localhost:1433
- Verify database `batchdb` exists
- Check username/password in application.yml
- Ensure SQL Server accepts TCP/IP connections

### YAML Parsing Errors

- Validate YAML syntax
- Ensure all required fields are present
- Check that bean names match Spring component names

## License

This project is a demonstration/learning project.

## Contributing

Feel free to submit issues and enhancement requests!

## Contact

For questions or issues, please create an issue in the repository.
