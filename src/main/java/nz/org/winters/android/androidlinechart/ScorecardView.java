package nz.org.winters.android.androidlinechart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScorecardView extends View
{
  public enum ScorecardOrientation
  {
    ORIENTATION_DOWN,
    ORIENTATION_RIGHT,
    ORIENTATION_LEFT,
    ORIENTATION_UP

  }
  
  private ScorecardOrientation[] mAvailableOrientations = ScorecardOrientation.values();
  
  private Paint mBackgroundPaint;
  private float mBottomEdge;
  private float mBottomMargin = 300.0F;
  private float mDensity = getResources().getDisplayMetrics().density;
  private Point mFingerPosition = new Point(150, 150);
  private final float mLabelPadding = 5.0F * mDensity;
  private float mLeftEdge;
  private float mLeftMargin = 0.0F;
  private float mMaxTextWidth;
  
  private ScorecardOrientation mOrientation = null;
  
  private float mRightEdge;
  private float mRightMargin = 600.0F;
  private final float mScorecardPointGap = 10.0F * mDensity;
  private Paint mStrokePaint;
  private String[] mTextLines;
  private Paint mTextPaint;
  private float mTopEdge;
  private float mTopMargin = 0.0F;

  public ScorecardView(Context paramContext)
  {
    super(paramContext);
  }

  private Path createPath()
  {
    float f1 = mTextLines.length * mTextPaint.getTextSize() / 2.0F + mLabelPadding;
    float f2 = mMaxTextWidth / 2.0F + mLabelPadding;
    mLeftEdge = (mFingerPosition.x - f2);
    mTopEdge = (mFingerPosition.y - f1);
    mRightEdge = (f2 + mFingerPosition.x);
    mBottomEdge = (f1 + mFingerPosition.y);
    Path localPath = new Path();
    List localList = computeOrientation();
    if (localList.size() == 0)
    {
      mOrientation = mAvailableOrientations[0];
      Log.d("CHART","Oirentation " + mOrientation.name());
      switch (mOrientation)
      {
        case ORIENTATION_LEFT:
        mLeftEdge += f2 + mLabelPadding + mScorecardPointGap;
        mRightEdge += f2 + mLabelPadding + mScorecardPointGap;
        localPath.moveTo(mLeftEdge, mTopEdge);
        localPath.lineTo(mLeftEdge, mFingerPosition.y - mLabelPadding);
        localPath.lineTo(mLeftEdge - mLabelPadding, mFingerPosition.y);
        localPath.lineTo(mLeftEdge, mFingerPosition.y + mLabelPadding);
        localPath.lineTo(mLeftEdge, mBottomEdge);
        localPath.lineTo(mRightEdge, mBottomEdge);
        localPath.lineTo(mRightEdge, mTopEdge);
        break;
        case ORIENTATION_UP:
        offsetHorizontally(f2);
        mTopEdge += f1 + mLabelPadding + mScorecardPointGap;
        mBottomEdge += f1 + mLabelPadding + mScorecardPointGap;
        localPath.moveTo(mLeftEdge, mTopEdge);
        localPath.lineTo(mLeftEdge, mBottomEdge);
        localPath.lineTo(mRightEdge, mBottomEdge);
        localPath.lineTo(mRightEdge, mTopEdge);
        localPath.lineTo(mFingerPosition.x + mLabelPadding, mTopEdge);
        localPath.lineTo(mFingerPosition.x, mTopEdge - mLabelPadding);
        localPath.lineTo(mFingerPosition.x - mLabelPadding, mTopEdge);
        break;
        case ORIENTATION_DOWN:
        offsetHorizontally(f2);
        mTopEdge -= f1 + mLabelPadding + mScorecardPointGap;
        mBottomEdge -= f1 + mLabelPadding + mScorecardPointGap;
        localPath.moveTo(mLeftEdge, mTopEdge);
        localPath.lineTo(mLeftEdge, mBottomEdge);
        localPath.lineTo(mFingerPosition.x - mLabelPadding, mBottomEdge);
        localPath.lineTo(mFingerPosition.x, mBottomEdge + mLabelPadding);
        localPath.lineTo(mFingerPosition.x + mLabelPadding, mBottomEdge);
        localPath.lineTo(mRightEdge, mBottomEdge);
        localPath.lineTo(mRightEdge, mTopEdge);
        break;
        case ORIENTATION_RIGHT:
        mLeftEdge -= f2 + mLabelPadding + mScorecardPointGap;
        mRightEdge -= f2 + mLabelPadding + mScorecardPointGap;
        localPath.moveTo(mLeftEdge, mTopEdge);
        localPath.lineTo(mLeftEdge, mBottomEdge);
        localPath.lineTo(mRightEdge, mBottomEdge);
        localPath.lineTo(mRightEdge, mFingerPosition.y + mLabelPadding);
        localPath.lineTo(mRightEdge + mLabelPadding, mFingerPosition.y);
        localPath.lineTo(mRightEdge, mFingerPosition.y - mLabelPadding);
        localPath.lineTo(mRightEdge, mTopEdge);
        break;
        default:
          Log.e(getClass().getName(), "Invalid orientation");
          break;
      }
    }
    localPath.close();
    return localPath;
  }

  private void offsetHorizontally(float paramFloat)
  {
    if (mRightEdge > mRightMargin)
    {
      float f2 = Math.min(mRightEdge - mRightMargin, paramFloat - mLabelPadding);
      mLeftEdge -= f2;
      mRightEdge -= f2;
    }
    if (mLeftEdge < mLeftMargin)
    {
      float f1 = Math.min(mLeftMargin - mLeftEdge, paramFloat - mLabelPadding);
      mLeftEdge = (f1 + mLeftEdge);
      mRightEdge = (f1 + mRightEdge);
    }
  }

  protected List<ScorecardOrientation> computeOrientation()
  {
    float f1 = mTextLines.length * mTextPaint.getTextSize() / 2.0F + mLabelPadding;
    float f2 = mMaxTextWidth / 2.0F + mLabelPadding;
    ArrayList localArrayList = new ArrayList(Arrays.asList(mAvailableOrientations));
    if (mFingerPosition.x - f2 * 2.0F - (mLabelPadding + mScorecardPointGap) < mLeftMargin)
      localArrayList.remove(ScorecardOrientation.ORIENTATION_RIGHT);
    if (mFingerPosition.x + f2 * 2.0F + (mLabelPadding + mScorecardPointGap) > mRightMargin)
      localArrayList.remove(ScorecardOrientation.ORIENTATION_LEFT);
    if (mFingerPosition.y + f1 * 2.0F + (mLabelPadding + mScorecardPointGap) > mBottomMargin)
      localArrayList.remove(ScorecardOrientation.ORIENTATION_UP);
    if (mFingerPosition.y - f1 * 2.0F - (mLabelPadding + mScorecardPointGap) < mTopMargin)
      localArrayList.remove(ScorecardOrientation.ORIENTATION_DOWN);
    if (localArrayList.size() > 2)
    {
      if (mFingerPosition.y - f1 >= mTopMargin)
      {
        localArrayList.remove(ScorecardOrientation.ORIENTATION_DOWN);
        localArrayList.remove(ScorecardOrientation.ORIENTATION_UP);
      }
      if (f1 + mFingerPosition.y > mBottomMargin)
      {
        localArrayList.remove(ScorecardOrientation.ORIENTATION_DOWN);
        localArrayList.remove(ScorecardOrientation.ORIENTATION_UP);
      }
      if (mFingerPosition.x - f2 < mLeftMargin)
      {
        localArrayList.remove(ScorecardOrientation.ORIENTATION_RIGHT);
        localArrayList.remove(ScorecardOrientation.ORIENTATION_LEFT);
      }
      if(f2 + mFingerPosition.x <= mRightMargin)
      {
        localArrayList.remove(ScorecardOrientation.ORIENTATION_RIGHT);
        localArrayList.remove(ScorecardOrientation.ORIENTATION_LEFT);
      }
    }
    return localArrayList;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    Path localPath = createPath();
    paramCanvas.drawPath(localPath, mBackgroundPaint);
    paramCanvas.drawPath(localPath, mStrokePaint);
    for (int i = 0; i < mTextLines.length; i++)
      paramCanvas.drawText(mTextLines[i], (mLeftEdge + mRightEdge) / 2.0F, mTopEdge + mLabelPadding + (i + 1) * mTextPaint.getTextSize(), mTextPaint);
    super.onDraw(paramCanvas);
  }

  public void setAvailableOrientations(ScorecardOrientation[] paramArrayOfScorecardOrientation)
  {
    mAvailableOrientations = paramArrayOfScorecardOrientation;
  }

  public void setAvailableSpace(float leftMargin, float topMargin, float rightMargin, float bottomMargin)
  {
    mLeftMargin = leftMargin;
    mTopMargin = topMargin;
    mRightMargin = rightMargin;
    mBottomMargin = bottomMargin;
  }

  public void setFingerPosition(Point paramPoint)
  {
    mFingerPosition = paramPoint;
  }

  public void setPaints(Paint paramPaint1, Paint paramPaint2, Paint paramPaint3)
  {
    mTextPaint = paramPaint1;
    mBackgroundPaint = paramPaint2;
    mStrokePaint = paramPaint3;
  }

  public void setTextLines(String[] paramArrayOfString)
  {
    mTextLines = paramArrayOfString;
    mMaxTextWidth = 0.0F;
    for (String str : mTextLines)
      mMaxTextWidth = Math.max(mMaxTextWidth, mTextPaint.measureText(str));
  }

}

/* Location:           C:\temp\ads\classes-dex2jar.jar
 * Qualified Name:     com.google.android.apps.ads.publisher.ui.chart.ScorecardView
 * JD-Core Version:    0.6.2
 */