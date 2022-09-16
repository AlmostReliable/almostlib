package com.almostreliable.almostlib.kubejs;

import com.almostreliable.almostlib.util.AlmostUtils;
import com.almostreliable.almostlib.util.WeightedList;
import net.minecraft.advancements.critereon.MinMaxBounds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalDouble;

@SuppressWarnings("unused")
public class AlmostWrappers {

    public static MinMaxBounds.Doubles ofDoubles(Object o) {
        if (o instanceof List<?> list) {
            if (list.size() == 0) {
                return MinMaxBounds.Doubles.ANY;
            }

            if (list.size() == 1) {
                return ofDoubles(list.get(0));
            }

            if (list.size() == 2) {
                Object min = list.get(0);
                Object max = list.get(1);
                if (min instanceof Number minN && max instanceof Number maxN) {
                    return MinMaxBounds.Doubles.between(minN.doubleValue(), maxN.doubleValue());
                }
            }
        }

        if (o instanceof MinMaxBounds<? extends Number> mmb) {
            if (mmb.isAny()) return MinMaxBounds.Doubles.ANY;
            if (mmb.getMin() != null && mmb.getMax() != null) {
                return MinMaxBounds.Doubles.between(mmb.getMin().doubleValue(), mmb.getMax().doubleValue());
            }
            if (mmb.getMin() != null) {
                return MinMaxBounds.Doubles.atLeast(mmb.getMin().doubleValue());
            }
            if (mmb.getMax() != null) {
                return MinMaxBounds.Doubles.atMost(mmb.getMax().doubleValue());
            }
        }

        OptionalDouble d = AlmostUtils.parseDouble(o);
        if (d.isPresent()) {
            return MinMaxBounds.Doubles.atLeast(d.getAsDouble());
        }

        throw new IllegalArgumentException("Argument is not a MinMaxBound");
    }

    public static MinMaxBounds.Ints ofInt(Object o) {
        if (o instanceof MinMaxBounds.Ints) {
            return (MinMaxBounds.Ints) o;
        }

        MinMaxBounds.Doubles mmb = ofDoubles(o);
        if (mmb.getMin() != null && mmb.getMax() != null) {
            return MinMaxBounds.Ints.between(mmb.getMin().intValue(), mmb.getMax().intValue());
        }
        if (mmb.getMin() != null) {
            return MinMaxBounds.Ints.atLeast(mmb.getMin().intValue());
        }
        if (mmb.getMax() != null) {
            return MinMaxBounds.Ints.atMost(mmb.getMax().intValue());
        }
        return MinMaxBounds.Ints.ANY;
    }

    public static WeightedList.Builder<Object> weightedList() {
        return new WeightedList.Builder<>();
    }

    public static WeightedList<Object> ofWeightedList(@Nullable Object o) {
        if (o instanceof WeightedList.Builder b) {
            //noinspection unchecked
            return b.build();
        }

        if (o instanceof WeightedList) {
            return AlmostUtils.cast(o);
        }

        var builder = new WeightedList.Builder<>();

        for (Object entry : AlmostUtils.asList(o)) {
            List<Object> weightValue = AlmostUtils.asList(entry);
            if (weightValue.size() == 2) {
                builder.add(AlmostUtils.parseInt(weightValue.get(0)).orElse(1), weightValue.get(1));
            } else {
                builder.add(1, entry);
            }
        }
        return builder.build();
    }
}
