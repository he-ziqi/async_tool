package async.function;

/**
 * @author hzq
 * @version 1.0
 * @description 函数式分支处理
 * @date 2024/5/28 12:02
 */
@FunctionalInterface
public interface SwitchHandler {

    void execute(Runnable trueHandler,Runnable falseHandler);

    static SwitchHandler handler(boolean express){
        return (trueHandler,falseHandler) -> {
            if (express) {
                trueHandler.run();
            } else {
                falseHandler.run();
            }
        };
    }
}
