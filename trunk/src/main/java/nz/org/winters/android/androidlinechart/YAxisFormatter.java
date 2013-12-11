package nz.org.winters.android.androidlinechart;

/**
 * Created by mathew on 7/12/13.
 */
public interface YAxisFormatter<YT>
{
  public String format(YT value);
  public String format(Float value);
  public float toFloat(YT value);

  public float roundAxisValue(float value);
}
