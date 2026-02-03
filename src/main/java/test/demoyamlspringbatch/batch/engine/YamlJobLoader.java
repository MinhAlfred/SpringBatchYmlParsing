package test.demoyamlspringbatch.batch.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.model.JobDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class YamlJobLoader {

    private final ObjectMapper mapper;

    public YamlJobLoader() {
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    public JobDefinition load(String path) throws IOException {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            JsonNode root = mapper.readTree(is);
            JsonNode jobNode = root.get("job");

            if (jobNode == null) {
                throw new IllegalArgumentException("No 'job' node found in YAML file: " + path);
            }

            return mapper.treeToValue(jobNode, JobDefinition.class);
        }
    }
    public List<JobDefinition> loadAll(String path) throws IOException {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            JsonNode root = mapper.readTree(is);
            JsonNode jobsNode = root.get("jobs");

            if (jobsNode == null || !jobsNode.isArray()) {
                throw new IllegalArgumentException("No 'jobs' array found");
            }

            List<JobDefinition> jobs = new ArrayList<>();
            for (JsonNode node : jobsNode) {
                jobs.add(mapper.treeToValue(node, JobDefinition.class));
            }
            return jobs;
        }
    }
}