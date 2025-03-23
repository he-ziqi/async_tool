package async.config;

import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hzq
 * @version 1.0
 * @description 异步配置
 * @date 2024/6/6 19:11
 */
@Configuration
public class AsyncConfig implements Condition{

    private static final Integer maxWaitTasks = Integer.MAX_VALUE >> 4;
    private static final String threadPrefix = "async-task";

    @Bean
    @Conditional(AsyncConfig.class)
    public ExecutorService asyncExecutor(){
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() << 1,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(maxWaitTasks),
                new AsyncThreadFactory(threadPrefix,false),
                new AsyncThreadRejectedPolicy()
        );
    }

    @SuppressWarnings("all")
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            return Objects.isNull(context.getBeanFactory().getBean("asyncExecutor", ExecutorService.class));
        }catch (Exception e){
            return true;
        }
    }
}
