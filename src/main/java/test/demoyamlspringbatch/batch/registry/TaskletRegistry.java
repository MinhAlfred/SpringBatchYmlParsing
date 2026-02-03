package test.demoyamlspringbatch.batch.registry;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class TaskletRegistry {

    private final ApplicationContext context;

    public TaskletRegistry(ApplicationContext context) {
        this.context = context;
    }

    public Tasklet get(String beanName) {
        return context.getBean(beanName, Tasklet.class);
    }
}