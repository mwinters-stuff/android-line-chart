package nz.org.winters.android.androidlinechart;

/**
 * Created by mathew on 7/12/13.
 */
public interface XAxisFormatter<XT>
{
  public String format(XT value);

  public float toFloat(XT value);
}
