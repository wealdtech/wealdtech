package test.com.wealdtech;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.wealdtech.Continuum;
import com.wealdtech.TwoTuple;

/**
 */
public class IntegerContinuum extends Continuum<Integer>
{
  public static final ImmutableList<TwoTuple<Range<Integer>, String>> ranges = ImmutableList.of(new TwoTuple<>(Range.<Integer>openClosed(0, 100), "Low"),
                                                                                                new TwoTuple<>(Range.<Integer>openClosed(100, 200), "Medium"),
                                                                                                new TwoTuple<>(Range.<Integer>atLeast(200), "High"));

  public IntegerContinuum(final Integer level)
  {
    super(ranges, level);
  }
}
