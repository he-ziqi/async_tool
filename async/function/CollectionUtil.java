import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author hzq
 * @version 1.0
 * @description collection function handler
 * @date 2025/3/25 13:14
 */
public interface CollectionUtil {

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

    static <K,V,R,M extends Map<? super K,? super V>> R ifPresent(M map, Function<? super M, ? extends R> handler, Supplier<? extends R> other){
        if(isNotEmpty(map)) return handler.apply(map);
        return Objects.nonNull(other) ? other.get() : null;
    }

    static <K,V,M extends Map<? super K,? super V>> void ifPresent(M map, Consumer<? super M> handler) {
        if(isNotEmpty(map)) handler.accept(map);
    }

    static <T,E extends Throwable> T[] requireNonNull(T[] arr, Supplier<? extends E> ex) throws E {
        if(isNotEmpty(arr)) return arr;
        if(Objects.nonNull(ex)) throw ex.get();
        throw new NullPointerException("array is empty");
    }

    static <T> T[] requireNonNull(T[] arr, String message) {
        return requireNonNull(arr, () -> new NullPointerException(message));
    }

    static <T> T[] requireNonNullElse(T[] arr, T[] defaultValue){
        return isNotEmpty(arr) ? arr : requireNonNull(defaultValue,"default array is null");
    }

    static <T> T[] requireNonNullElse(T[] arr, Supplier<? extends T[]> defaultValueSupplier){
        return isNotEmpty(arr) ? arr : requireNonNull(Objects.requireNonNull(defaultValueSupplier,"default value supplier is null").get(), "default value supplier is null");
    }

    static <T> T[] orElse(T[] arr, T[] defaultValue){
        return isNotEmpty(arr) ? arr : defaultValue;
    }

    static <T> T[] orElseGet(T[] arr, Supplier<? extends T[]> defaultValueSupplier){
        return isNotEmpty(arr) ? arr : (Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null);
    }

    static <T,C extends Collection<? super T>,E extends Throwable> C requireNonNull(C c, Supplier<? extends E> ex) throws E {
        if(isNotEmpty(c)) return c;
        if(Objects.nonNull(ex)) throw ex.get();
        throw new NullPointerException("collection is empty");
    }

    static <T,C extends Collection<? super T>> C requireNonNull(C c, String message){
        return requireNonNull(c, () -> new NullPointerException(message));
    }

    static <T,C extends Collection<? super T>> C requireNonNullElse(C c, C defaultValue){
        return isNotEmpty(c) ? c : requireNonNull(defaultValue,"default collection is empty");
    }

    static <T,C extends Collection<? super T>> C requireNonNullElseGet(C c, Supplier<? extends C> defaultValueSupplier){
        return isNotEmpty(c) ? c : requireNonNull(Objects.requireNonNull(defaultValueSupplier,"default value supplier is null").get(),"default value supplier is null");
    }

    static <T,C extends Collection<? super T>> C orElse(C c, C defaultValue){
        return isNotEmpty(c) ? c : defaultValue;
    }

    static <T,C extends Collection<? super T>> C orElseGet(C c, Supplier<? extends C> defaultValueSupplier){
        return isNotEmpty(c) ? c : (Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null);
    }

    static <K,V,M extends Map<? super K,? super V>,E extends Throwable> M requireNonNull(M m, Supplier<? extends E> ex) throws E {
        if(isNotEmpty(m)) return m;
        if(Objects.nonNull(ex)) throw ex.get();
        throw new NullPointerException("map is empty");
    }

    static <K,V,M extends Map<K,V>> M requireNonNull(M m, String message){
        return requireNonNull(m, () -> new NullPointerException(message));
    }

    static <K,V,M extends Map<K,V>> M requireNonNullElse(M m, M defaultValue){
        return isNotEmpty(m) ? m : requireNonNull(defaultValue,"default map value is null");
    }

    static <K,V,M extends Map<K,V>> M requireNonNullElseGet(M m, Supplier<? extends M> defaultValueSupplier){
        return isNotEmpty(m) ? m : requireNonNull(Objects.requireNonNull(defaultValueSupplier,"default value supplier is null").get(), "default value supplier is null");
    }

    static <K,V,M extends Map<K,V>> M orElse(M m, M defaultValue){
        return isNotEmpty(m) ? m : defaultValue;
    }

    static <K,V,M extends Map<K,V>> M orElseGet(M m, Supplier<? extends M> defaultValueSupplier){
        return isNotEmpty(m) ? m : (Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null);
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

    static <K,V,M extends Map<? super K,? super V>> boolean isEmpty(M map){
        return Objects.isNull(map) || map.isEmpty();
    }

    static <K,V,M extends Map<? super K,? super V>> boolean isNotEmpty(M map){
        return !isEmpty(map);
    }
}
