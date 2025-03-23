package async;

import async.function.CollectionHandler;
import async.task.CallableTask;
import async.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author hzq
 * @version 1.0
 * @description 异步操作处理
 * @date 2024/6/7 10:20
 */
@SuppressWarnings("all")
public interface AsyncHandler extends AsyncService {

    Logger log = LoggerFactory.getLogger(AsyncHandler.class);

    /**
     * 默认异常处理逻辑
     * @param handler 异常处理handler
     * @param ex 异常对象
     * @param result 线程执行结果
     * @param defaultValue 异常后默认返回值
     * @param name 任务名称
     * @param <E> 异常泛型
     * @param <R> 结果泛型
     * @return 有异常返回默认值,无异常返回执行结果
     */
    default <E extends Throwable,R> Object exHandler(Function<? super E,? extends R> handler, E ex, R result, R defaultValue, String name){
        //任务存在异常处理handler 交由handler处理
        if(Objects.nonNull(ex)){
            return Objects.nonNull(handler) ? handler.apply(ex) : defaultExHandler(ex,defaultValue,name);
        }
        if(result instanceof Supplier<?>){
            try {
                return ((Supplier<?>) result).get();
            }catch (Throwable e){
                return Objects.nonNull(handler) ? handler.apply((E)e) : defaultExHandler(e,defaultValue,name);
            }
        }
        return result;
    }

    /**
     * 线程任务异常 默认处理handler
     * @param e 异常对象
     * @param defaultValue 默认值
     * @param taskName 任务名称
     * @param <E> 异常类型
     * @param <R> 默认值类型
     * @return 默认值
     */
    default <E extends Throwable,R> R defaultExHandler(E e, R defaultValue, String taskName){
        log.error("任务[{}]执行时出现异常,异常信息:",taskName,e);
        return defaultValue;
    }

    /**
     * 任务列表校验
     */
    default Task[] tasksCheck(Task... tasks){
        return CollectionHandler.ifPresent(tasks,t -> {
            List<Task> validTasks = new ArrayList<>(t.length);
            for (int i = 0; i < t.length; i++) {
                if(!illegalTask(t[i])) validTasks.add(tasks[i]);
            }
            CollectionHandler.requireNonNull(validTasks,() -> new RuntimeException("异步操作至少要有一个有效任务"));
            return validTasks.toArray(new Task[validTasks.size()]);
        }, () -> null);
    }

    /**
     * 是否非法任务
     * @param task 任务对象
     * @return true是非法任务 false 不是
     */
    default boolean illegalTask(Task task){
        return Objects.isNull(task) || Objects.isNull(task.getTask());
    }

    /**
     * 任务获取结果异常处理
     * @param task 任务对象
     * @param e 异常对象
     */
    default void resultExHandler(CallableTask task, Exception e){
        if(e instanceof TimeoutException){
            task.setResult(task.getExDefaultValue());
        }
    }
}
