package async.function;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author hzq
 * @version 1.0
 * @description collection function handler
 * @date 2025/3/23 16:25
 */
public interface CollectionHandler {

    static <T,R,C extends Collection<? super T>> R ifPresent(C c, Function<? super C, ? extends R> handler, Supplier<? extends R> other) {
        if(isNotEmpty(c)) return handler.apply(c);
        return Objects.nonNull(other) ? other.get() : null;
    }

    static <T,C extends Collection<? super T>> void ifPresent(C c, Consumer<? super C> handler) {
        if(isNotEmpty(c)) handler.accept(c);
    }

    static <T> void ifPresent(T[] array, Consumer<? super T[]> handler){
        if(isNotEmpty(array)) handler.accept(array);
    }

    static <T,R> R ifPresent(T[] array, Function<? super T[], ? extends R> handler, Supplier<? extends R> other){
        if(isNotEmpty(array)) return handler.apply(array);
        return Objects.nonNull(other) ? other.get() : null;
    }

    static <T,E extends Throwable> T[] requireNonNull(T[] arr,Supplier<? extends E> ex) throws E {
        if(isNotEmpty(arr)) return arr;
        if(Objects.nonNull(ex)) throw ex.get();
        throw new RuntimeException("array is empty");
    }

    static <T,C extends Collection<? super T>,E extends Throwable> C requireNonNull(C c, Supplier<? extends E> ex) throws E {
        if(isNotEmpty(c)) return c;
        if(Objects.nonNull(ex)) throw ex.get();
        throw new RuntimeException("collection is empty");
    }

    static <T> boolean isNotEmpty(T[] arr) {
        return !isEmpty(arr);
    }

    static <T,C extends Collection<? super T>> boolean isNotEmpty(C c) {
        return !isEmpty(c);
    }

    static <T,C extends Collection<? super T>> boolean isEmpty(C c) {
        return Objects.isNull(c) || c.isEmpty();
    }

    static <T> boolean isEmpty(T[] arr){
        return Objects.isNull(arr) || arr.length == 0;
    }
}
