package com.palest.ink.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 将受检异常吃掉
 *
 * @author WeiHui
 * @version v1.0.0
 * @date 2019/4/8 9:42
 * @since JDK 1.8
 */
@Slf4j
@UtilityClass
public class Try {

	public static <T> Consumer<T> ofConsumer(UncheckedConsumer<T> action) {
		Objects.requireNonNull(action);
		return t -> {
			try {
				action.accept(t);
			} catch (Exception ex) {
				log.error("exception happened", ex);
			}
		};
	}

	public static <T> Supplier<T> ofSupplier(UncheckedSupplier<T> supplier, T defaultR) {
		Objects.requireNonNull(supplier);
		return () -> {
			try {
				return supplier.get();
			} catch (Exception ex) {
				log.error("exception happened", ex);
				return defaultR;
			}
		};
	}

	public static <T, R> Function<T, R> ofFunction(UncheckedFunction<T, R> mapper) {
		Objects.requireNonNull(mapper);
		return t -> {
			try {
				return mapper.apply(t);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}

	public static <T, R> Function<T, R> ofFunction(UncheckedFunction<T, R> mapper, R defaultR) {
		Objects.requireNonNull(mapper);
		return t -> {
			try {
				return mapper.apply(t);
			} catch (Exception ex) {
				log.error("exception happened", ex);
				return defaultR;
			}
		};
	}

	@FunctionalInterface
	public interface UncheckedConsumer<T> {

		/**
		 * Performs this operation on the given argument.
		 *
		 * @param t the input argument
		 */
		void accept(T t) throws Exception;

	}

	@FunctionalInterface
	public interface UncheckedFunction<T, R> {

		/**
		 * Applies this function to the given argument.
		 *
		 * @param t the function argument
		 * @return the function result
		 */
		R apply(T t) throws Exception;

	}

	@FunctionalInterface
	public interface UncheckedSupplier<T> {

		/**
		 * Gets a result.
		 *
		 * @return a result
		 */
		T get() throws Exception;

	}

}
