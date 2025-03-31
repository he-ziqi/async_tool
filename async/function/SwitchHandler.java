package async.function;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    static void established(boolean express, Runnable handler){
        if(express){
            handler.run();
        }
    }

    static <T> void ifPresent(T t, Consumer<T> handler){
        if(Objects.nonNull(t)) handler.accept(t);
    }

    static <T,E extends Throwable> T requireNonNull(T t, Supplier<? extends E> ex) throws E {
        if(Objects.nonNull(t)) return t;
        if(Objects.isNull(ex) || Objects.isNull(ex.get())) throw new NullPointerException();
        throw ex.get();
    }
}
