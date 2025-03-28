import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public interface Tools {

    interface CollectionTool {

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

    interface TimeTool {

        String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

        ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

        /**
         * 将LocalDateTime格式化为字符串
         * @param localDateTime 日期时间
         * @param pattern       日期格式
         * @return 格式化后的字符串
         */
        static String format(LocalDateTime localDateTime, String pattern) {
            return Objects.isNull(localDateTime) ? null : localDateTime.format(DateTimeFormatter.ofPattern(pattern));
        }

        /**
         * 将LocalDateTime格式化为默认格式的字符串
         * @param localDateTime 日期时间
         * @return 格式化后的字符串
         */
        static String format(LocalDateTime localDateTime) {
            return format(localDateTime, DEFAULT_PATTERN);
        }

        /**
         * 将字符串解析为LocalDateTime
         * @param dateTime 日期时间字符串
         * @param pattern  格式
         * @return LocalDateTime对象
         */
        static LocalDateTime parse(String dateTime, String pattern) {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
        }

        /**
         * 将字符串解析为LocalDateTime（使用默认格式）
         * @param dateTime 日期时间字符串
         * @return LocalDateTime对象
         */
        static LocalDateTime parse(String dateTime) {
            return parse(dateTime, DEFAULT_PATTERN);
        }

        /**
         * 增加时间
         * @param localDateTime 日期时间
         * @param amount        数量
         * @param unit          时间单位
         * @return 增加后的日期时间
         */
        static LocalDateTime plus(LocalDateTime localDateTime, long amount, ChronoUnit unit) {
            return localDateTime.plus(amount, unit);
        }

        /**
         * 减少时间
         * @param localDateTime 日期时间
         * @param amount        数量
         * @param unit          时间单位
         * @return 减少后的日期时间
         */
        static LocalDateTime minus(LocalDateTime localDateTime, long amount, ChronoUnit unit) {
            return localDateTime.minus(amount, unit);
        }

        /**
         * 比较两个LocalDateTime的大小
         * @param dateTime1 日期时间1
         * @param dateTime2 日期时间2
         * @return 如果dateTime1在dateTime2之后，返回true；否则返回false
         */
        static boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
            return dateTime1.isAfter(dateTime2);
        }

        /**
         * 比较两个LocalDateTime的大小
         * @param dateTime1 日期时间1
         * @param dateTime2 日期时间2
         * @return 如果dateTime1在dateTime2之前，返回true；否则返回false
         */
        static boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
            return dateTime1.isBefore(dateTime2);
        }

        /**
         * 计算两个LocalDateTime之间的时间差
         * @param start 开始时间
         * @param end   结束时间
         * @param unit  时间单位
         * @return 时间差
         */
        static long difference(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
            return unit.between(start, end);
        }

        /**
         * time时间是否在start与end之内
         * @param start 开始时间
         * @param end   结束时间
         * @param time  目标时间
         * @return time在[start,end]中返回true 否则返回false
         */
        static boolean between(LocalDateTime start, LocalDateTime end, LocalDateTime time) {
            return !Objects.isNull(start) && !Objects.isNull(end) && !Objects.isNull(time) && !time.isBefore(start) && !time.isAfter(end);
        }

        static LocalDateTime now(ZoneId zoneId) {
            return LocalDateTime.now(zoneId);
        }

        static LocalDateTime now(){
            return now(DEFAULT_ZONE);
        }

        static LocalDateTime with(int year, int month, int day, int hour, int minute, int second) {
            return LocalDateTime.of(year, month, day, hour, minute, second);
        }

        static LocalDateTime convert(long millis) {
            return convert(millis, DEFAULT_ZONE);
        }

        static LocalDateTime convert(long millis, ZoneId zoneId) {
            return Instant.ofEpochMilli(millis)
                    .atZone(zoneId)
                    .toLocalDateTime();
        }

        static long convertToMillis(LocalDateTime time) {
            return convertToMillis(time, DEFAULT_ZONE);
        }

        static long convertToMillis(LocalDateTime time, ZoneId zoneId) {
            return time.atZone(zoneId)
                    .toInstant()
                    .toEpochMilli();
        }

        static Date convert(LocalDateTime time) {
            return convert(time, DEFAULT_ZONE);
        }

        static Date convert(LocalDateTime time, ZoneId zone) {
            return Date.from(Objects.requireNonNull(time,"LocalDateTime转换Date时入参为空").atZone(Objects.requireNonNull(zone,"LocalDateTime转换Date时时区为空")).toInstant());
        }

        static LocalDateTime convert(Date date) {
            return convert(date, DEFAULT_ZONE);
        }

        static LocalDateTime convert(Date date, ZoneId zone) {
            return Objects.requireNonNull(date,"Date转换为LocalDateTime时入参为空").toInstant().atZone(Objects.requireNonNull(zone,"Date转换为LocalDateTime时时区为空")).toLocalDateTime();
        }

        static <R> R ifPresent(LocalDateTime time, Function<LocalDateTime, R> handler, Supplier<R> other) {
            return Objects.nonNull(time) ? handler.apply(time) : (Objects.nonNull(other) ? other.get() : null);
        }

        static void ifPresent(LocalDateTime time, Consumer<LocalDateTime> handler) {
            if (Objects.nonNull(time)) handler.accept(time);
        }

        static LocalDateTime orElse(LocalDateTime time, LocalDateTime defaultValue) {
            return Objects.nonNull(time) ? time : defaultValue;
        }

        static LocalDateTime orElseGet(LocalDateTime time, Supplier<LocalDateTime> defaultValueSupplier) {
            return Objects.nonNull(time) ? time : (Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null);
        }

        static LocalDateTime requireNonNull(LocalDateTime time, String message) {
            if (Objects.isNull(time)) throw new NullPointerException(message);
            return time;
        }

        static LocalDateTime requireNonNullElse(LocalDateTime time, LocalDateTime defaultValue) {
            return Objects.nonNull(time) ? time : requireNonNull(defaultValue, "默认值不能为空");
        }

        static LocalDateTime requireNonNullElseGet(LocalDateTime time, Supplier<LocalDateTime> defaultValueSupplier) {
            return Objects.nonNull(time) ? time : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, "默认值提供函数不能为空");
        }

        static <E extends Throwable> LocalDateTime requireNonNull(LocalDateTime time, Supplier<E> exceptionSupplier) throws E {
            if (Objects.nonNull(time)) return time;
            if (Objects.nonNull(exceptionSupplier)) throw exceptionSupplier.get();
            throw new NullPointerException("time is null");
        }

        static <E extends Throwable> LocalDateTime requireNonNullElse(LocalDateTime time, LocalDateTime defaultValue, Supplier<E> exceptionSupplier) throws E {
            return Objects.nonNull(time) ? time : requireNonNull(defaultValue, exceptionSupplier);
        }

        static <E extends Throwable> LocalDateTime requireNonNullElseGet(LocalDateTime time, Supplier<LocalDateTime> defaultValueSupplier, Supplier<E> exceptionSupplier) throws E {
            return Objects.nonNull(time) ? time : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, exceptionSupplier);
        }
    }

    interface StringTool {

        static boolean isEmpty(String str) {
            return Objects.isNull(str) || str.isEmpty();
        }

        static boolean isNotEmpty(String str) {
            return !isEmpty(str);
        }

        static boolean isBlank(String str) {
            return Objects.isNull(str) || str.trim().isEmpty();
        }

        static boolean isNotBlank(String str) {
            return !isBlank(str);
        }

        static boolean equals(String str1, String str2) {
            return Objects.nonNull(str1) && str1.equals(str2);
        }

        /**
         * 反转字符串
         */
        static String reverse(String str) {
            return isEmpty(str) ? str : new StringBuilder(str).reverse().toString();
        }

        /**
         * 将字符串的首字母大写
         */
        static String capitalizeFirstLetter(String str) {
            return isEmpty(str) ? str : str.substring(0, 1).toUpperCase() + str.substring(1);
        }

        static String upperCase(String str) {
            return isEmpty(str) ? str : str.toUpperCase();
        }

        static String lowerCase(String str) {
            return isEmpty(str) ? str : str.toLowerCase();
        }

        /**
         * 将字符串首字母小写
         */
        static String uncapitalizeFirstLetter(String str) {
            return isEmpty(str) ? str : str.substring(0, 1).toLowerCase() + str.substring(1);
        }

        /**
         * 将蛇形命名字符串转换为大驼峰命名法
         */
        static String toUpperCamelCase(String str) {
            if (isEmpty(str)) {
                return str;
            }
            String[] parts = str.split("_");
            StringBuilder camelCase = new StringBuilder();
            for (String part : parts) {
                camelCase.append(capitalizeFirstLetter(part));
            }
            return camelCase.toString();
        }

        /**
         * 将蛇形命名字符串转换为小驼峰命名法
         */
        static String toLowerCamelCase(String str) {
            if (isEmpty(str)) {
                return str;
            }
            String[] parts = str.split("_");
            StringBuilder camelCase = new StringBuilder(uncapitalizeFirstLetter(parts[0]));
            for (int i = 1; i < parts.length; i++) {
                camelCase.append(capitalizeFirstLetter(parts[i]));
            }
            return camelCase.toString();
        }

        /**
         * 去除字符串中的所有空格
         */
        static String removeAllSpaces(String str) {
            if (isEmpty(str)) {
                return str;
            }
            return str.replaceAll("\\s+", "");
        }

        /**
         * 截取字符串的前n个字符
         */
        static String truncate(String str, int n) {
            return (isEmpty(str) || n <= 0) ? str : (str.length() > n ? str.substring(0, n) : str);
        }

        /**
         * 判断字符串是否包含子字符串
         */
        static boolean contains(String str, String substr) {
            return isNotEmpty(str) && isNotEmpty(substr) && str.contains(substr);
        }

        /**
         * 将字符串重复指定次数
         */
        static String repeat(String str, int times) {
            return (isEmpty(str) || times <= 0) ? str : new String(new char[times]).replace("\0", str);
        }

        /**
         * 检查字符串是否匹配正则表达式
         */
        static boolean matches(String input, String regex) {
            return isNotEmpty(input) && Pattern.compile(regex).matcher(input).matches();
        }

        /**
         * 匹配数字
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isNumber(String input) {
            return matches(input, "\\d+");
        }

        /**
         * 匹配字母
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isLetter(String input) {
            return matches(input, "[a-zA-Z]+");
        }

        /**
         * 匹配字母和数字
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isAlphanumeric(String input) {
            return matches(input, "[a-zA-Z0-9]+");
        }

        /**
         * 匹配邮箱地址
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isEmail(String input) {
            return matches(input, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        }

        /**
         * 匹配URL
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isUrl(String input) {
            return matches(input, "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$");
        }

        /**
         * 匹配日期（YYYY-MM-DD）
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isDate(String input) {
            return matches(input, "\\d{4}-\\d{2}-\\d{2}");
        }

        /**
         * 匹配时间（HH:MM:SS）
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isTime(String input) {
            return matches(input, "\\d{2}:\\d{2}:\\d{2}");
        }

        /**
         * 匹配IP地址
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isIpAddress(String input) {
            return matches(input, "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
        }

        /**
         * 匹配中文
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isChinese(String input) {
            return matches(input, "[\\u4e00-\\u9fa5]+");
        }

        /**
         * 匹配手机号码（中国大陆）
         * @param input 输入字符串
         * @return 是否匹配
         */
        static boolean isPhoneNumber(String input) {
            return matches(input, "^1[3-9]\\d{9}$");
        }

        static <R> R ifPresent(String str, Function<String, R> handler, Supplier<R> other) {
            return isNotEmpty(str) ? handler.apply(str) : (Objects.nonNull(other) ? other.get() : null);
        }

        static void ifPresent(String str, Consumer<String> handler) {
            if (isNotEmpty(str)) handler.accept(str);
        }

        static String orElse(String str, String defaultValue) {
            return isNotEmpty(str) ? str : defaultValue;
        }

        static String orElseGet(String str, Supplier<String> defaultValueSupplier) {
            return isNotEmpty(str) ? str : (Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null);
        }

        static String requireNonNull(String str, String message) {
            if (Objects.isNull(str)) throw new NullPointerException(message);
            return str;
        }

        static String requireNonNullElse(String str, String defaultValue) {
            return isNotEmpty(str) ? str : requireNonNull(defaultValue, "默认值不能为空");
        }

        static String requireNonNullElseGet(String str, Supplier<String> defaultValueSupplier) {
            return isNotEmpty(str) ? str : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, "默认值提供函数不能为空");
        }

        static <E extends Throwable> String requireNonNull(String str, Supplier<E> exceptionSupplier) throws E {
            if (isEmpty(str)) return str;
            if (Objects.nonNull(exceptionSupplier)) throw exceptionSupplier.get();
            throw new NullPointerException("str is null");
        }

        static <E extends Throwable> String requireNonNullElse(String str, String defaultValue, Supplier<E> exceptionSupplier) throws E {
            return isNotEmpty(str) ? str : requireNonNull(defaultValue, exceptionSupplier);
        }

        static <E extends Throwable> String requireNonNullElseGet(String str, Supplier<String> defaultValueSupplier, Supplier<E> exceptionSupplier) throws E {
            return isNotEmpty(str) ? str : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, exceptionSupplier);
        }
    }

    interface CryptoTool {
        
        Charset CHARTSET = StandardCharsets.UTF_8;

        // AES加密
        static String aesEncrypt(String data, String key) throws Exception {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CHARTSET), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(CHARTSET));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        // AES解密
        static String aesDecrypt(String encryptedData, String key) throws Exception {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CHARTSET), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, CHARTSET);
        }

        // MD5加密
        static String md5(String data) throws Exception {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes(CHARTSET));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }

        // SHA-256加密
        static String sha256(String data) throws Exception {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data.getBytes(CHARTSET));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }

        // Base64编码
        static String base64Encode(String data) {
            return Base64.getEncoder().encodeToString(data.getBytes(CHARTSET));
        }

        // Base64解码
        static String base64Decode(String encodedData) {
            return new String(Base64.getDecoder().decode(encodedData), CHARTSET);
        }
    }

    interface UUIDTool {

        // 生成一个随机的 UUID
        static String generateUUID() {
            return UUID.randomUUID().toString();
        }

        // 生成不带连字符的 UUID
        static String generateUUIDWithoutHyphens() {
            return UUID.randomUUID().toString().replace("-", "");
        }

        // 生成指定版本的 UUID
        static String generateUUID(int version) {
            if (version == 1) {
                return UUID.nameUUIDFromBytes(new byte[0]).toString();
            } else if (version == 4) {
                return UUID.randomUUID().toString();
            } else {
                throw new IllegalArgumentException("Unsupported UUID version: " + version);
            }
        }

        // 从字符串生成 UUID 对象
        static UUID fromString(String uuid) {
            return UUID.fromString(uuid);
        }

        // 生成短 UUID（基于 Base62 编码）
        static String generateShortUUID() {
            UUID uuid = UUID.randomUUID();
            byte[] bytes = toBytes(uuid);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        }

        // 将 UUID 转换为字节数组
        private static byte[] toBytes(UUID uuid) {
            long msb = uuid.getMostSignificantBits();
            long lsb = uuid.getLeastSignificantBits();
            byte[] buffer = new byte[16];
            for (int i = 0; i < 8; i++) {
                buffer[i] = (byte) (msb >>> 8 * (7 - i));
            }
            for (int i = 8; i < 16; i++) {
                buffer[i] = (byte) (lsb >>> 8 * (15 - i));
            }
            return buffer;
        }

        // 校验字符串是否为有效的 UUID 格式
        static boolean isValidUUID(String uuid) {
            if (uuid == null) {
                return false;
            }
            // UUID 的正则表达式
            String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
            return StringTool.matches(uuid,regex);
        }

        // 比较两个 UUID 的顺序
        static int compareUUIDs(UUID uuid1, UUID uuid2) {
            return uuid1.compareTo(uuid2);
        }

        static <R> R ifPresent(UUID uuid, Function<UUID, R> handler, Supplier<R> other) {
            return Objects.nonNull(uuid) ? handler.apply(uuid) : (Objects.nonNull(other) ? other.get() : null);
        }

        static void ifPresent(UUID uuid, Consumer<UUID> handler) {
            if (Objects.nonNull(uuid)) handler.accept(uuid);
        }

        static UUID orElse(UUID uuid, UUID defaultValue) {
            return Objects.nonNull(uuid) ? uuid : defaultValue;
        }

        static UUID orElseGet(UUID uuid, Supplier<UUID> defaultValueSupplier) {
            return Objects.nonNull(uuid) ? uuid : (Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null);
        }

        static UUID requireNonNull(UUID uuid, String message) {
            if (Objects.isNull(uuid)) throw new NullPointerException(message);
            return uuid;
        }

        static UUID requireNonNullElse(UUID uuid, UUID defaultValue) {
            return Objects.nonNull(uuid) ? uuid : requireNonNull(defaultValue, "默认值不能为空");
        }

        static UUID requireNonNullElseGet(UUID uuid, Supplier<UUID> defaultValueSupplier) {
            return Objects.nonNull(uuid) ? uuid : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, "默认值提供函数不能为空");
        }

        static <E extends Throwable> UUID requireNonNull(UUID uuid, Supplier<E> exceptionSupplier) throws E {
            if (Objects.nonNull(uuid)) return uuid;
            if (Objects.nonNull(exceptionSupplier)) throw exceptionSupplier.get();
            throw new NullPointerException("uuid is null");
        }

        static <E extends Throwable> UUID requireNonNullElse(UUID uuid, UUID defaultValue, Supplier<E> exceptionSupplier) throws E {
            return Objects.nonNull(uuid) ? uuid : requireNonNull(defaultValue, exceptionSupplier);
        }

        static <E extends Throwable> UUID requireNonNullElseGet(UUID uuid, Supplier<UUID> defaultValueSupplier, Supplier<E> exceptionSupplier) throws E {
            return Objects.nonNull(uuid) ? uuid : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, exceptionSupplier);
        }
    }
    
    interface Base64Tool {
        Charset CHARTSET = StandardCharsets.UTF_8;
        
        // 标准 Base64 编码
        static String encode(String input) {
            return Base64.getEncoder().encodeToString(input.getBytes(CHARTSET));
        }

        // 标准 Base64 解码
        static String decode(String input) {
            byte[] decodedBytes = Base64.getDecoder().decode(input);
            return new String(decodedBytes, CHARTSET);
        }

        // URL 安全的 Base64 编码
        static String encodeUrlSafe(String input) {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes(CHARTSET));
        }

        // URL 安全的 Base64 解码
        static String decodeUrlSafe(String input) {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(input);
            return new String(decodedBytes, CHARTSET);
        }

        // MIME 格式的 Base64 编码
        static String encodeMime(String input) {
            return Base64.getMimeEncoder().encodeToString(input.getBytes(CHARTSET));
        }

        // MIME 格式的 Base64 解码
        static String decodeMime(String input) {
            byte[] decodedBytes = Base64.getMimeDecoder().decode(input);
            return new String(decodedBytes, CHARTSET);
        }

        // 将文件编码为 Base64 字符串
        static String encodeFileToBase64(File file) throws IOException {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] fileBytes = new byte[(int) file.length()];
                fileInputStream.read(fileBytes);
                return Base64.getEncoder().encodeToString(fileBytes);
            }
        }

        // 将 Base64 字符串解码为文件
        static void decodeBase64ToFile(String base64, File outputFile) throws IOException {
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                fileOutputStream.write(decodedBytes);
            }
        }
        
    }
    
    interface ByteTool {

        // 将字节数组转换为十六进制字符串
        static String bytesToHex(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }

        // 将十六进制字符串转换为字节数组
        static byte[] hexToBytes(String hex) {
            int len = hex.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                        + Character.digit(hex.charAt(i + 1), 16));
            }
            return data;
        }

        // 将字符串转换为字节数组（默认使用 UTF-8 编码）
        static byte[] stringToBytes(String str) {
            return str.getBytes(StandardCharsets.UTF_8);
        }

        // 将字节数组转换为字符串（默认使用 UTF-8 编码）
        static String bytesToString(byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }

        // 将 int 转换为字节数组
        static byte[] intToBytes(int value) {
            return ByteBuffer.allocate(4).putInt(value).array();
        }

        // 将字节数组转换为 int
        static int bytesToInt(byte[] bytes) {
            return ByteBuffer.wrap(bytes).getInt();
        }

        // 将 long 转换为字节数组
        static byte[] longToBytes(long value) {
            return ByteBuffer.allocate(8).putLong(value).array();
        }

        // 将字节数组转换为 long
        static long bytesToLong(byte[] bytes) {
            return ByteBuffer.wrap(bytes).getLong();
        }

        // 将 float 转换为字节数组
        static byte[] floatToBytes(float value) {
            return ByteBuffer.allocate(4).putFloat(value).array();
        }

        // 将字节数组转换为 float
        static float bytesToFloat(byte[] bytes) {
            return ByteBuffer.wrap(bytes).getFloat();
        }

        // 将 double 转换为字节数组
        static byte[] doubleToBytes(double value) {
            return ByteBuffer.allocate(8).putDouble(value).array();
        }

        // 将字节数组转换为 double
        static double bytesToDouble(byte[] bytes) {
            return ByteBuffer.wrap(bytes).getDouble();
        }

        // 拼接多个字节数组
        static byte[] concatBytes(byte[]... arrays) {
            int totalLength = 0;
            for (byte[] array : arrays) {
                totalLength += array.length;
            }
            byte[] result = new byte[totalLength];
            int offset = 0;
            for (byte[] array : arrays) {
                System.arraycopy(array, 0, result, offset, array.length);
                offset += array.length;
            }
            return result;
        }

        // 截取字节数组
        static byte[] subBytes(byte[] src, int start, int length) {
            byte[] result = new byte[length];
            System.arraycopy(src, start, result, 0, length);
            return result;
        }

        // 比较两个字节数组是否相等
        static boolean compareBytes(byte[] a, byte[] b) {
            if (a.length != b.length) {
                return false;
            }
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]) {
                    return false;
                }
            }
            return true;
        }
        
        static boolean isEmpty(byte[] bytes) {
            return Objects.isNull(bytes) || bytes.length == 0;
        }
        
        static boolean isNotEmpty(byte[] bytes) {
            return !isEmpty(bytes);
        }

        static <R> R ifPresent(byte[] bytes, Function<byte[], R> handler, Supplier<R> other) {
            return isNotEmpty(bytes) && bytes.length > 0 ? handler.apply(bytes) : (Objects.nonNull(other) ? other.get() : null);
        }

        static void ifPresent(byte[] bytes, Consumer<byte[]> handler) {
            if (isNotEmpty(bytes)) handler.accept(bytes);
        }

        static byte[] orElse(byte[] bytes, byte[] defaultValue) {
            return isNotEmpty(bytes) ? bytes : defaultValue;
        }

        static byte[] orElseGet(byte[] bytes, Supplier<byte[]> defaultValueSupplier) {
            return isNotEmpty(bytes) ? bytes : (Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null);
        }

        static byte[] requireNonNull(byte[] bytes, String message) {
            if (isEmpty(bytes)) throw new NullPointerException(message);
            return bytes;
        }

        static <E extends Throwable> byte[] requireNonNull(byte[] bytes, Supplier<? extends E> exceptionSupplier) throws E{
            if (isNotEmpty(bytes)) return bytes;
            if (Objects.nonNull(exceptionSupplier)) throw exceptionSupplier.get();
            throw new NullPointerException("bytes is null");
        }

        static byte[] requireNonNullElse(byte[] bytes, byte[] defaultValue) {
            return isNotEmpty(bytes) ? bytes : requireNonNull(defaultValue, "默认值不能为空");
        }

        static byte[] requireNonNullElseGet(byte[] bytes, Supplier<byte[]> defaultValueSupplier) {
            return isNotEmpty(bytes) ? bytes : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, "默认值提供函数不能为空");
        }

        static <E extends Throwable> byte[] requireNonNullElse(byte[] bytes, byte[] defaultValue, Supplier<E> exceptionSupplier) throws E {
            return isNotEmpty(bytes) ? bytes : requireNonNull(defaultValue, exceptionSupplier);
        }

        static <E extends Throwable> byte[] requireNonNullElseGet(byte[] bytes, Supplier<byte[]> defaultValueSupplier, Supplier<E> exceptionSupplier) throws E {
            return isNotEmpty(bytes) ? bytes : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, exceptionSupplier);
        }
    }
    
    interface FileTool {

        // 读取文件内容（一次性读取）
        static String readFile(String filePath) throws IOException {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }

        // 按行读取文件内容
        static List<String> readFileByLines(String filePath) throws IOException {
            return Files.readAllLines(Paths.get(filePath));
        }

        // 写入文件内容（覆盖）
        static void writeFile(String filePath, String content) throws IOException {
            Files.write(Paths.get(filePath), content.getBytes());
        }

        // 写入文件内容（追加）
        static void appendFile(String filePath, String content) throws IOException {
            Files.write(Paths.get(filePath), content.getBytes(), StandardOpenOption.APPEND);
        }

        // 复制文件
        static void copyFile(String sourcePath, String targetPath) throws IOException {
            Files.copy(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
        }

        // 删除文件
        static void deleteFile(String filePath) throws IOException {
            Files.deleteIfExists(Paths.get(filePath));
        }

        // 删除目录（递归删除）
        @SuppressWarnings("all")
        static void deleteDirectory(String dirPath) throws IOException {
            Path path = Paths.get(dirPath);
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted((p1, p2) -> -p1.compareTo(p2)) // 从子文件开始删除
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }

        // 重命名文件
        static void renameFile(String oldPath, String newPath) throws IOException {
            Files.move(Paths.get(oldPath), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
        }

        // 获取文件大小
        static long getFileSize(String filePath) throws IOException {
            return Files.size(Paths.get(filePath));
        }

        // 获取文件最后修改时间
        static long getLastModifiedTime(String filePath) throws IOException {
            return Files.getLastModifiedTime(Paths.get(filePath)).toMillis();
        }

        // 创建目录
        static void createDirectory(String dirPath) throws IOException {
            Files.createDirectories(Paths.get(dirPath));
        }

        // 列出目录下的所有文件
        static List<String> listFiles(String dirPath) throws IOException {
            List<String> fileList = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dirPath))) {
                for (Path path : stream) {
                    fileList.add(path.toString());
                }
            }
            return fileList;
        }

        static <R> R ifPresent(File file, Function<File, R> handler, Supplier<R> other) {
            return Objects.nonNull(file) ? handler.apply(file) : (Objects.nonNull(other) ? other.get() : null);
        }

        static void ifPresent(File file, Consumer<File> handler) {
            if (Objects.nonNull(file)) handler.accept(file);
        }
        
        // orElse 方法
        static File orElse(File file, File defaultValue) {
            return Objects.nonNull(file) ? file : defaultValue;
        }

        // orElseGet 方法
        static File orElseGet(File file, Supplier<File> defaultValueSupplier) {
            return Objects.nonNull(file) ? file : (Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null);
        }

        // requireNonNull 方法
        static File requireNonNull(File file, String message) {
            if (Objects.isNull(file)) throw new NullPointerException(message);
            return file;
        }

        static <E extends Throwable> File requireNonNull(File file, Supplier<? extends E> exceptionSupplier) throws E{
            if (Objects.nonNull(file)) return file;
            if (Objects.nonNull(exceptionSupplier)) throw exceptionSupplier.get();
            throw new NullPointerException("file is null");
        }

        // requireNonNullElse 方法
        static File requireNonNullElse(File file, File defaultValue) {
            return Objects.nonNull(file) ? file : requireNonNull(defaultValue, "默认值不能为空");
        }

        // requireNonNullElseGet 方法
        static File requireNonNullElseGet(File file, Supplier<File> defaultValueSupplier) {
            return Objects.nonNull(file) ? file : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, "默认值提供函数不能为空");
        }
        
        // requireNonNullElse 方法（带异常 Supplier）
        static <E extends Throwable> File requireNonNullElse(File file, File defaultValue, Supplier<E> exceptionSupplier) throws E {
            return Objects.nonNull(file) ? file : requireNonNull(defaultValue, exceptionSupplier);
        }
        
        // requireNonNullElseGet 方法（带异常 Supplier）
        static <E extends Throwable> File requireNonNullElseGet(File file, Supplier<File> defaultValueSupplier, Supplier<E> exceptionSupplier) throws E {
            return Objects.nonNull(file) ? file : requireNonNull(Objects.nonNull(defaultValueSupplier) ? defaultValueSupplier.get() : null, exceptionSupplier);
        }
    }
    
}
