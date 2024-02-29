package cn.monkey.commons.data;

public class DoubleRange extends Range<Double> {
    public static DoubleRange of(double start, double end) {
        return of(start, end, DoubleRange.class);
    }
}
