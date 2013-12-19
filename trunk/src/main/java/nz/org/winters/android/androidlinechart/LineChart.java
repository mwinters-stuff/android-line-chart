/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 * 
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package nz.org.winters.android.androidlinechart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.Path.Direction;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

public class LineChart<XT, YT> extends View
{

  private       ArrayList<Line<XT, YT>> mLines     = new ArrayList<Line<XT, YT>>();
  private final Map<Float, String>      mXLabels   = new HashMap<Float, String>();
  private final ScorecardView           mScoreCard = new ScorecardView(getContext());

  private final Path   mPath   = new Path();
  private final Path   mPath2  = new Path();
  private final Canvas mCanvas = new Canvas();

  private XAxisFormatter<XT> mXAxisFormatter = null;
  private YAxisFormatter<YT> mYAxisFormatter = null;

  private float   mMinY           = Float.MAX_VALUE;//, mMinX = 0;
  private float   mMaxY           = Float.MIN_VALUE;//, mMaxX = 0;
  private float   mMinX           = Float.MAX_VALUE;//, mMinX = 0;
  private float   mMaxX           = Float.MIN_VALUE;//, mMaxX = 0;
//  private float   mMaxYNoRounding = Float.MIN_VALUE;
//  private float   mMinYNoRounding = Float.MAX_VALUE;
  private boolean mIsMaxYUserSet  = false;
  // --Commented out by Inspection (18/12/13 4:08 PM):private int     mLineToFill     = -1;
  private int     mIndexSelected  = -1;
  private OnPointClickedListener<XT, YT> mListener;
  private Bitmap                         mFullImage;

  private       boolean            mShouldUpdate    = false;
  // --Commented out by Inspection (18/12/13 4:08 PM):private       PorterDuffXfermode mPorterDuffClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
  private final Region             mRegion          = new Region();
  private final Rect               mRect            = new Rect();

  private int mYAxisLabels      = 4;
  private int mXAxisLabelsEvery = 1;

  private final Resources mResources = getResources();
  private float   mDensity;
  private float   mDotRadius;
  private float   mTextSize;
  // --Commented out by Inspection (18/12/13 4:08 PM):private int     mCurrentPosition;
  private boolean mSelectedPressed;


  public LineChart(Context context)
  {
    super(context);
  }

// --Commented out by Inspection START (18/12/13 4:08 PM):
//  public LineChart(Context context, AttributeSet attrs)
//  {
//    super(context, attrs);
//  }
// --Commented out by Inspection STOP (18/12/13 4:08 PM)

  // --Commented out by Inspection (18/12/13 4:09 PM):private final   Path  mBorder               = new Path();
  private final   Paint mBorderPaint          = new Paint(1);
  private final   Paint mBrightGridPaint      = new Paint(1);
  private final   Paint mBrightLabelTextPaint = new Paint(1);
  private final   Paint mChartPaint           = new Paint(1);
  // --Commented out by Inspection (18/12/13 4:09 PM):protected final Path  mChartPath            = new Path();
  private final   Paint mDotPaint             = new Paint(1);
  private final   Paint mDotShadowPaint       = new Paint(1);
  private final   Paint mGridPaint            = new Paint(1);
  private final   Paint mLabelPaint           = new Paint(1);
  private final   Paint mLabelPaintLeft       = new Paint(1);
  private final   Paint mScoreCardBorderPaint = new Paint(1);
  private final   Paint mScoreCardPaint       = new Paint(1);
  private final   Paint mTextPaint            = new Paint(1);
  private int mHoloLightBlue;


  @SuppressWarnings("ConstantConditions")
  void initializePaint()
  {
    mHoloLightBlue = mResources.getColor(R.color.holo_blue_light);


    Typeface localTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");
    mTextPaint.setColor(mResources.getColor(R.color.widget_dark_grey));
    mTextPaint.setStrokeWidth(3.0F * mDensity);
    mTextPaint.setTextSize(mTextSize);
    mTextPaint.setTextAlign(Paint.Align.CENTER);

    mBorderPaint.setColor(mResources.getColor(R.color.border_color));
    mBorderPaint.setStyle(Paint.Style.STROKE);
    mBorderPaint.setStrokeWidth(1.0F * mDensity);

    mLabelPaint.setTypeface(localTypeface);
    mLabelPaint.setColor(mResources.getColor(R.color.label_color));
    mLabelPaint.setStrokeWidth(3.0F * mDensity);
    mLabelPaint.setTextSize(mTextSize);
    mLabelPaint.setTextAlign(Paint.Align.CENTER);
    mLabelPaint.setAntiAlias(true);
    mLabelPaint.setShadowLayer(2.0F, 1.0F, 1.0F, mResources.getColor(R.color.text_shadow_white));

    mBrightLabelTextPaint.setColor(mResources.getColor(R.color.bright_text_color));
    mBrightLabelTextPaint.setStrokeWidth(3.0F * mDensity);
    mBrightLabelTextPaint.setTextSize(mTextSize);
    mBrightLabelTextPaint.setTextAlign(Paint.Align.CENTER);
    mBrightLabelTextPaint.setAntiAlias(true);

    mLabelPaintLeft.setColor(mResources.getColor(R.color.label_color));
    mLabelPaintLeft.setTypeface(localTypeface);
    mLabelPaintLeft.setTextSize(mTextSize);
    mLabelPaintLeft.setTextAlign(Paint.Align.LEFT);
    mLabelPaintLeft.setAntiAlias(true);
    mLabelPaintLeft.setStrokeWidth(3.0F * mDensity);
    mLabelPaintLeft.setShadowLayer(2.0F, 1.0F, 1.0F, mResources.getColor(R.color.text_shadow_white));

    Paint mLabelPaintLeftStroke = new Paint(mLabelPaintLeft);
    mLabelPaintLeftStroke.setStyle(Paint.Style.FILL_AND_STROKE);
    mLabelPaintLeftStroke.setStrokeWidth(2.0F * mDensity);
    mLabelPaintLeftStroke.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

//    mChartPaint.setColor(mChartColor);

    mChartPaint.setStrokeWidth(2.0F * mDensity);
    mChartPaint.setDither(true);
    mChartPaint.setStyle(Paint.Style.STROKE);
    mChartPaint.setStrokeJoin(Paint.Join.ROUND);
    mChartPaint.setStrokeCap(Paint.Cap.ROUND);
    mChartPaint.setPathEffect(new CornerPathEffect(3.0F));
    mChartPaint.setAntiAlias(true);

//    mDotPaint.setColor(mChartColor);

    mDotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    mDotPaint.setStrokeWidth(2.0F * mDensity);
    mDotShadowPaint.setStyle(Paint.Style.FILL);
    mDotShadowPaint.setColor(getResources().getColor(R.color.shadow_color));
    mGridPaint.setColor(mResources.getColor(R.color.grid_color));
    mGridPaint.setStrokeWidth(0.5F * mDensity);
    mBrightGridPaint.setColor(mResources.getColor(R.color.light_grid_color));
    mBrightGridPaint.setStrokeWidth(0.5F * mDensity);
    mScoreCardBorderPaint.setStyle(Paint.Style.STROKE);
    mScoreCardBorderPaint.setStrokeWidth(1.0F * mDensity);
    mScoreCardBorderPaint.setColor(mResources.getColor(R.color.scorecard_border_color));
    mScoreCardPaint.setStyle(Paint.Style.FILL);
    mScoreCardPaint.setColor(mResources.getColor(R.color.scorecard_color));
    mScoreCardPaint.setShadowLayer(2.0F * mDensity, 0.0F, 2.0F * mDensity, mResources.getColor(R.color.scorecard_border_shadow_color));

    mScoreCard.setPaints(mLabelPaint, mScoreCardPaint, mScoreCardBorderPaint);
  }


  public void removeAllLines()
  {
    while (mLines.size() > 0)
    {
      mLines.remove(0);
    }
  }

  private void resetMaxMin()
  {
    if (!mIsMaxYUserSet)
    {
      mMinY = Float.MAX_VALUE;//, mMinX = 0;
      mMaxY = Float.MIN_VALUE;//, mMaxX = 0;
      mMinX = Float.MAX_VALUE;//, mMinX = 0;
      mMaxX = Float.MIN_VALUE;//, mMaxX = 0;
   //   mMaxYNoRounding = Float.MIN_VALUE;
   //   mMinYNoRounding = Float.MAX_VALUE;//, mMinX = 0;
    }
  }

  public void addLine(Line<XT, YT> line)
  {
    mLines.add(line);
  }

  public ArrayList<Line<XT, YT>> getLines()
  {
    return mLines;
  }

  public void setLines(ArrayList<Line<XT, YT>> lines)
  {
    mLines = lines;
  }

  public void draw()
  {
    mDensity = mResources.getDisplayMetrics().density;
    float mFontScale = mResources.getConfiguration().fontScale;
   // float mLabelPadding = (6.0F * mDensity);
    mDotRadius = (3.0F * mDensity);
    mTextSize = (12.0F * mDensity * mFontScale);

    initializePaint();

    mShouldUpdate = true;
    resetMaxMin();
    formatAxisPoints();
    postInvalidate();

  }


  public Line<XT, YT> getLine(int index)
  {
    return mLines.get(index);
  }

  public int getSize()
  {
    return mLines.size();
  }

  public void setRangeY(float min, float max)
  {
    float xMinY = roundDown(min);
    float xMaxY = roundUp(max);

    mMaxY = xMaxY + ((xMaxY + xMinY) / mYAxisLabels);

    if (xMinY != 0)
    {
      mMinY = xMinY - ((xMaxY + xMinY) / mYAxisLabels);
    }

   // mMaxYNoRounding = max;
    mIsMaxYUserSet = true;
  }

// --Commented out by Inspection START (18/12/13 4:19 PM):
//  float getMaxYNoRounding()
//  {
//    if (!mIsMaxYUserSet && (mLines.size() > 0 && mMaxYNoRounding == Float.MIN_VALUE))
//    {
//      mMaxYNoRounding = Collections.max(mLines, new Comparator<Line>()
//      {
//        @Override
//        public int compare(Line lhs, Line rhs)
//        {
//          return Float.compare(lhs.getMaxY(), rhs.getMaxY());
//        }
//      }).getMaxY();
//    }
//    return mMaxYNoRounding == Float.MIN_VALUE ? 0 : mMaxYNoRounding;
//  }
// --Commented out by Inspection STOP (18/12/13 4:19 PM)


// --Commented out by Inspection START (18/12/13 4:19 PM):
//  float getMinYNoRounding()
//  {
//    if (!mIsMaxYUserSet && (mLines.size() > 0 && mMinYNoRounding == Float.MAX_VALUE))
//    {
//      mMinYNoRounding = Collections.min(mLines, new Comparator<Line>()
//      {
//        @Override
//        public int compare(Line lhs, Line rhs)
//        {
//          return Float.compare(lhs.getMinY(), rhs.getMinY());
//        }
//      }).getMinY();
//    }
//    return mMinYNoRounding == Float.MIN_VALUE ? 0 : mMinYNoRounding;
//  }
// --Commented out by Inspection STOP (18/12/13 4:19 PM)

  float getMaxY()
  {
    float miny = getMinY(); // ensures calculated first
    if (!mIsMaxYUserSet && (mLines.size() > 0 && mMaxY == Float.MIN_VALUE))
    {
      mMaxY = Collections.max(mLines, new Comparator<Line>()
      {
        @Override
        public int compare(Line lhs, Line rhs)
        {
          return Float.compare(lhs.getMaxY(), rhs.getMaxY());
        }
      }).getMaxY();

      mMaxY = roundUp(mMaxY);
      mMaxY = mMaxY + ((mMaxY - miny) / mYAxisLabels);


    }
    return mMaxY == Float.MIN_VALUE ? 0 : mMaxY;
  }

  float getMinY()
  {
    if (!mIsMaxYUserSet && (mLines.size() > 0 && mMinY == Float.MAX_VALUE))
    {
      mMinY = Collections.min(mLines, new Comparator<Line>()
      {
        @Override
        public int compare(Line lhs, Line rhs)
        {
          return Float.compare(lhs.getMinY(), rhs.getMinY());
        }
      }).getMinY();


      mMinY = roundDown(mMinY);
    }
    return mMinY == Float.MAX_VALUE ? 0 : mMinY;
  }

  float getMaxX()
  {
    if (mLines.size() > 0 && mMaxX == Float.MIN_VALUE)
    {
      mMaxX = Collections.max(mLines, new Comparator<Line>()
      {
        @Override
        public int compare(Line lhs, Line rhs)
        {
          return Float.compare(lhs.getMaxX(), rhs.getMaxX());
        }
      }).getMaxX();
    }
    return mMaxX == Float.MIN_VALUE ? 0 : mMaxX;
  }

  float getMinX()
  {
    if (mLines.size() > 0 && mMinX == Float.MAX_VALUE)
    {
      mMinX = Collections.min(mLines, new Comparator<Line>()
      {
        @Override
        public int compare(Line lhs, Line rhs)
        {
          return Float.compare(lhs.getMinX(), rhs.getMinX());
        }
      }).getMinX();
    }
    return mMinX == Float.MAX_VALUE ? 0 : mMinX;
  }

  float getTextWidthY()
  {
    if (mLines.size() > 0)
    {
      return Collections.max(mLines, new Comparator<Line>()
      {
        @Override
        public int compare(Line lhs, Line rhs)
        {
          return Float.compare(lhs.getMaxTextWidthY(), rhs.getMaxTextWidthY());
        }
      }).getMaxTextWidthY();
    }
    return 0;
  }

  float getTextWidthX()
  {
    if (mLines.size() > 0)
    {
      return Collections.max(mLines, new Comparator<Line>()
      {
        @Override
        public int compare(Line lhs, Line rhs)
        {
          return Float.compare(lhs.getMaxTextWidthX(), rhs.getMaxTextWidthX());
        }
      }).getMaxTextWidthX();
    }
    return 0;
  }

  float getTextHeightY()
  {
    if (mLines.size() > 0)
    {
      return Collections.max(mLines, new Comparator<Line>()
      {
        @Override
        public int compare(Line lhs, Line rhs)
        {
          return Float.compare(lhs.getMaxTextHeightY(), rhs.getMaxTextHeightY());
        }
      }).getMaxTextHeightY();
    }
    return 0;
  }

  float getTextHeightX()
  {
    if (mLines.size() > 0)
    {
      return Collections.max(mLines, new Comparator<Line>()
      {
        @Override
        public int compare(Line lhs, Line rhs)
        {
          return Float.compare(lhs.getMaxTextHeightX(), rhs.getMaxTextHeightX());
        }
      }).getMaxTextHeightX();
    }
    return 0;
  }

  public void onDraw(Canvas ca)
  {
    if (isInEditMode())
    {
      return;
    }
    if ((mFullImage == null || mShouldUpdate) && (getWidth() > 0 && getHeight() > 0))
    {
      mFullImage = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
      mCanvas.setBitmap(mFullImage);


      float bottomPadding = getTextHeightX() + (mDensity * 3);
      float topPadding = mDensity;
      float leftPadding = (getTextWidthX() / 2) + (mDensity * 3);
      float rightPadding = (getTextWidthY() / 2) + (mDensity * 3);
      float usableHeight = getHeight() - bottomPadding - topPadding;
      float usableWidth = getWidth() - leftPadding - rightPadding;

      float f = 5.0F * this.mDensity;
      mScoreCard.setAvailableSpace(leftPadding + f, topPadding + f, getWidth() - leftPadding - f, getHeight() - bottomPadding - f);

      calculatePoints(bottomPadding, leftPadding, usableHeight, usableWidth);


      drawLines();

      drawDots();

      drawYAxisLabels(bottomPadding, usableHeight);
      drawXAxis(bottomPadding, leftPadding, rightPadding, usableWidth);

      mShouldUpdate = false;
    }

    if (mFullImage != null)
    {
      ca.drawBitmap(mFullImage, 0, 0, null);
    }


  }

  private void formatAxisPoints()
  {
    if (mXAxisFormatter == null || mYAxisFormatter == null)
    {
      return;
    }

    for (Line<XT, YT> line : mLines)
    {
      line.format(mXAxisFormatter, mYAxisFormatter, mLabelPaint);
    }
  }

  private void drawDots()
  {
    int pointCount = 0;
    // draws dots..
    for (Line<XT, YT> line : mLines)
    {
      if (line.getSize() > 0 && line.isShowingPoints())
      {

        for (LinePoint<XT, YT> p : line.getPoints())
        {
          if (p.isYNotNull())
          {
            mDotPaint.setColor(line.getColor());
            mCanvas.drawCircle(p.getPointX(), p.getPointY(), mDotRadius + 1.0F * mDensity, mDotPaint);
            mCanvas.drawCircle(p.getPointX(), p.getPointY(), mDotRadius, mDotShadowPaint);

            mPath2.reset();
            float pathSize = mDotRadius * (5 * mDensity);
            mPath2.addCircle(p.getPointX(), p.getPointY(), pathSize, Direction.CW);
            p.setPath(mPath2);
            mRegion.set((int) (p.getPointX() - pathSize), (int) (p.getPointY() - pathSize), (int) (p.getPointX() + pathSize), (int) (p.getPointY() + pathSize));
            p.setRegion(mRegion);

            if (mIndexSelected == pointCount && mSelectedPressed)
            {
              mDotPaint.setColor(mHoloLightBlue);
              mDotPaint.setAlpha(100);
              mCanvas.drawPath(p.getPath(), mDotPaint);
              mDotPaint.setAlpha(255);

            }
            drawCursorLabel(mCanvas);

          }
          pointCount++;
        }
      }
    }
  }

  private void drawLines()
  {
    for (Line<XT, YT> line : mLines)
    {
      if (line.getSize() > 1)
      {
        int count = 0;

        mChartPaint.setColor(line.getColor());

        mPath.reset();
        for (LinePoint<XT, YT> p : line.getPoints())
        {
          if (p.isYNotNull())
          {
            if (count == 0)
            {
              mPath.moveTo(p.getPointX(), p.getPointY());
            } else
            {
              mPath.lineTo(p.getPointX(), p.getPointY());
            }
            count++;
          }
        }
        mCanvas.drawPath(mPath, mChartPaint);
      } else if (line.getSize() == 1)
      {
        LinePoint<XT, YT> p = line.getPoint(0);
        mChartPaint.setColor(line.getColor());
        mCanvas.drawCircle(p.getPointX(), p.getPointY(), mDotRadius + 1.0F * mDensity, mDotPaint);
        mCanvas.drawCircle(p.getPointX(), p.getPointY(), mDotRadius, mDotShadowPaint);
      }
    }
  }

  private void drawXAxis(float bottomPadding, float leftPadding, float rightPadding, float usableWidth)
  {
    mCanvas.drawLine(leftPadding, getHeight() - bottomPadding, getWidth() - rightPadding, getHeight() - bottomPadding, mGridPaint);
    if (mXLabels.size() > 0)
    {

      ArrayList<Pair<Float, String>> xAxisLabels = new ArrayList<Pair<Float, String>>();

      for (Float xPoint : mXLabels.keySet())
      {
        xAxisLabels.add(new Pair<Float, String>(xPoint, mXLabels.get(xPoint)));
      }

      Collections.sort(xAxisLabels, new Comparator<Pair<Float, String>>()
      {
        @Override
        public int compare(Pair<Float, String> lhs, Pair<Float, String> rhs)
        {
          return lhs.first.compareTo(rhs.first);
        }
      });

      float y = getHeight() - bottomPadding;
      float textHeight = getTextHeightX();
      float textWidth = getTextWidthX() + (mDensity * 4);
      float maxCanShow = (usableWidth / textWidth);
      int stepMaxCanShow = (int)((mXLabels.size()  / maxCanShow) + .5);
      if (mXAxisLabelsEvery > stepMaxCanShow)
      {
        stepMaxCanShow = mXAxisLabelsEvery;
      }


      float textWidthX = getTextWidthX();

      int i = 1;
      int index = 0;
      for (Pair<Float, String> point : xAxisLabels)
      {
        if (i >= stepMaxCanShow || index == 0 || index == xAxisLabels.size() - 1)
        {

          mLabelPaint.getTextBounds(point.second, 0, point.second.length(), mRect);
          float tx = point.first;//- mRect.centerX();
          float ty = y + (2 * mDensity) + textHeight;

          if ((tx + mRect.centerX() + textWidthX < leftPadding + usableWidth) || (index == mXLabels.size() - 1))
          {
            mCanvas.drawText(point.second, tx, ty, mLabelPaint);
          }
          mCanvas.drawLine(point.first, y, point.first, y + 5, mGridPaint);

          i = 1;
        } else
        {
          i++;
        }
        index++;
      }

    }
  }

  private float roundDown(float value)
  {
    if (value % 10 != 0)
    {
      float value2 = ((int) value / 10) * 10;
      if (value2 != 0)
      {
        return value2 - 10;
      }
      return value2;
    }
    return value;
  }

  private float roundUp(float value)
  {
    if (value % 10 != 0)
    {
      float value2 = ((int) value / 10) * 10;
      if (value2 != 0)
      {
        return value2 + 10;
      }
      return value2;
    }
    return value;
  }

  private void drawYAxisLabels(float bottomPadding, float usableHeight)
  {
    float maxY = getMaxY();
    float minY = getMinY();

    int textHeight = (int) getTextHeightY();
    int yStep = mYAxisLabels + 1;
    while (textHeight * yStep > usableHeight)
    {
      yStep -= 5;
    }

    float step = (maxY - minY) / (yStep - 1);
    float value = minY;

    for (int i = 0; i <= yStep - 2; i++)
    {
      float yPercent = ((int) (mYAxisFormatter.roundAxisValue(value - minY))) / (maxY - minY);
      float y = getHeight() - bottomPadding - (usableHeight * yPercent);

      mCanvas.drawLine(this.mDensity, y, getWidth() - this.mDensity, y, this.mGridPaint);

      String str = mYAxisFormatter.format(value);

      float tx = mDensity;
      float ty = y - mDensity;

      mCanvas.drawText(str, tx, ty, mLabelPaintLeft);

      value += step;
    }
  }

  private void calculatePoints(float bottomPadding, float leftPadding, float usableHeight, float usableWidth)
  {
    mXLabels.clear();
    float maxY = getMaxY();
    float minY = getMinY();
    float maxX = getMaxX();
    float minX = getMinX();

    // calculate points.
    for (Line<XT, YT> line : mLines)
    {
      if (line.getSize() > 0)
      {

        int count = 0;
        for (LinePoint<XT, YT> p : line.getPoints())
        {
          Log.d("POINT", p.toString());
          float yPercent = (p.getY() - minY) / (maxY - minY);
          float px = line.isxIsIndex() ? line.getPoints().indexOf(p) + minX : p.getX();

          float xPercent = px == 0 ? 0 : (px - minX) / (maxX - minX);
          float lastXPixels = leftPadding + (xPercent * usableWidth);
          float lastYPixels = getHeight() - bottomPadding - (usableHeight * yPercent);
          p.setPoints(lastXPixels, lastYPixels);

          if (!mXLabels.containsKey(p.getPointX()))
          {
            mXLabels.put(p.getPointX(), p.getXAxisLabel());
          }

          count++;
        }
      }
    }
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public boolean onTouchEvent(MotionEvent event)
  {

    Point point = new Point();
    point.x = (int) event.getX();
    point.y = (int) event.getY();


    int count = 0;
    int lineCount = 0;
    int pointCount;
    boolean selected = false;

    Region r = new Region();
    for (Line<XT, YT> line : mLines)
    {
      if(line.isShowingPoints())
      {
        pointCount = 0;
        for (LinePoint<XT, YT> p : line.getPoints())
        {

          if (p.getPath() != null && p.getRegion() != null)
          {
            r.setPath(p.getPath(), p.getRegion());
            if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_DOWN)
            {
              mIndexSelected = count;

              mFingerPositionX = p.getPointX();
              mFingerPositionY = p.getPointY();

              mLabelTextLine1 = p.getXPopoutValue();
							mLabelTextLine2 = p.getYPopoutValue();
							//mMaxLabelTextWidth = Math.max(p.getXAxisLabelWidth(),p.getYAxisLabelWidth());

							mSelectedPressed = true;
              selected = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP)
            {
              if (r.contains(point.x,point.y))
              {
                if(mListener != null)
                {
                  mListener.onClick(p,lineCount, pointCount);
                }
                mSelectedPressed = false;
                selected = true;
              }
            }
          }

          pointCount++;
          count++;
        }
      }
      lineCount++;
    }

    if(!selected)
    {
      mIndexSelected = -1;
    }

    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP)
    {
      mShouldUpdate = true;
      postInvalidate();
    }


    return true;
  }

  public void setOnPointClickedListener(OnPointClickedListener<XT,YT> listener)
  {
    mListener = listener;
  }


  public interface OnPointClickedListener<XT,YT>
  {
    abstract void onClick(LinePoint<XT,YT> point, int lineIndex, int pointIndex);
  }

  public int getXAxisLabelsEvery()
  {
    return mXAxisLabelsEvery;
  }

  public void setXAxisLabelsEvery(int xAxisLabelsEvery)
  {
    mXAxisLabelsEvery = xAxisLabelsEvery;
  }

  public int getYAxisLabels()
  {
    return mYAxisLabels;
  }

  public void setYAxisLabels(int yAxisLabels)
  {
    mYAxisLabels = yAxisLabels;
  }

  public void setXAxisFormatter(XAxisFormatter<XT> axisFormatter)
  {
    mXAxisFormatter = axisFormatter;
  }

  public void setYAxisFormatter(YAxisFormatter<YT> axisFormatter)
  {
    mYAxisFormatter = axisFormatter;
  }

 // private float mLabelPositionY = 0;
  //  protected Point  mFingerPosition;
  private float  mFingerPositionX;
  private float  mFingerPositionY;
  //private float  mMaxLabelTextWidth;
  private String mLabelTextLine1;
  private String mLabelTextLine2;


  void drawCursorLabel(Canvas paramCanvas)
  {
    if (mIndexSelected < 0)
    {
      return;
    }
 //   float mLabelPositionX = mFingerPositionX;
  //  mLabelPositionY = mFingerPositionY;
   // mMaxLabelTextWidth = Math.max(mLabelPaint.measureText(mLabelTextLine1), mLabelPaint.measureText(mLabelTextLine2));
  //  float mOffset = 0;
 //   float mLabelRightMargin = (mLabelPositionX + mMaxLabelTextWidth / 2.0F + mOffset);
 //   float mLabelLeftMargin = (mLabelPositionX - mMaxLabelTextWidth / 2.0F - mOffset);
 //   if (mLabelRightMargin > getWidth())
//    {
    //  mLabelLeftMargin -= mLabelRightMargin - getWidth();
     // mLabelPositionX -= mLabelRightMargin - getWidth();
     // mLabelRightMargin = getWidth();
 //   }
 //   if (mLabelLeftMargin < 0.0F)
//    {
    //  mLabelRightMargin += -mLabelLeftMargin;
     // mLabelPositionX += -mLabelLeftMargin;
      //mLabelLeftMargin = 0.0F;
//    }
    mScoreCard.setFingerPosition(new Point((int) mFingerPositionX, (int) mFingerPositionY));
    ScorecardView localScorecardView = mScoreCard;
    String[] arrayOfString = new String[2];
    arrayOfString[0] = mLabelTextLine1;
    arrayOfString[1] = mLabelTextLine2;
    localScorecardView.setTextLines(arrayOfString);
    mScoreCard.draw(paramCanvas);
  }


  public void fillXAxisPoints(int start, int end)
  {
    for (Line<XT, YT> line : mLines)
    {
      line.fillXAxisPoints(start, end);
    }
  }

  public void rotateToStartAt(int start)
  {
    for (Line<XT, YT> line : mLines)
    {
      line.rotateToStartAt(start);
    }
  }

}
