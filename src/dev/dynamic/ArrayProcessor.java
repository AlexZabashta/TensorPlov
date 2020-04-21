package dev.dynamic;

public interface ArrayProcessor<T> {

	void add(T src, int srcPos, T dest, int destPos, int length);

	default T alloc(double[] src, int srcPos, int length) {
		T dest = alloc(length, 0.0);
		set(src, srcPos, dest, 0, length);
		return dest;
	}

	default T alloc(int length) {
		return alloc(length, 0.0);
	}

	T alloc(int length, double initialValue);

	default T apply(T src, int srcPos, int length, Object function) {
		T dest = alloc(length);
		applyAdd(src, srcPos, dest, 0, length, function);
		return dest;
	}

	default void apply(T src, int srcPos, T dest, int destPos, int length, Object function) {
		fill(dest, destPos, length, 0.0);
		applyAdd(src, srcPos, dest, destPos, length, function);
	}

	void applyAdd(T src, int srcPos, T dest, int destPos, int length, Object function);

	default T clone(T src, int srcPos, int length) {
		T dest = alloc(length);
		move(src, srcPos, dest, 0, length);
		return dest;
	}

	void fill(T array, int offset, int length, double value);

	void free(T array);

	void get(T src, int srcPos, double[] dest, int destPos, int length);

	default double[] get(T src, int srcPos, int length) {
		double[] dest = new double[length];
		get(src, srcPos, dest, 0, length);
		return dest;
	}

	void hadp(T src, int srcPos, T dest, int destPos, int length);

	void move(T src, int srcPos, T dest, int destPos, int length);

	default T mul(T lft, int lftPos, T rht, int rhtPos, int l, int m, int r) {
		T res = alloc(l * r);
		mulAdd(lft, lftPos, rht, rhtPos, res, 0, l, m, r);
		return res;
	}

	default void mul(T lft, int lftPos, T rht, int rhtPos, T res, int resPos, int l, int m, int r) {
		fill(res, resPos, l * r, 0.0);
		mulAdd(lft, lftPos, rht, rhtPos, res, resPos, l, m, r);
	}

	void mulAdd(T lft, int lftPos, T rht, int rhtPos, T res, int resPos, int l, int m, int r);

	void set(double[] src, int srcPos, T dest, int destPos, int length);

	T[] typeArray(int length);

}
