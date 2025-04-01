package async.task;

/**
 * @author hzq
 * @version 1.0
 * @description 任务接口
 * @date 2024/6/7 16:50
 */
public interface Task {

    String getTaskName();

    <W> W getTask();

    boolean isCompleted();
}
