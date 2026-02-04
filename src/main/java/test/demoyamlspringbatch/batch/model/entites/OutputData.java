package test.demoyamlspringbatch.batch.model.entites;

import java.time.LocalDateTime;

public record OutputData(String name, LocalDateTime processedAt) {}
