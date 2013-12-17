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

import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListenerProxy;

public class Line<XT, YT>
{
  private ArrayList<LinePoint<XT, YT>> points = new ArrayList<LinePoint<XT, YT>>();
  private int color;
  private boolean showPoints = true;
  private boolean xIsIndex   = false;

  public int getColor()
  {
    return color;
  }

  public void setColor(int color)
  {
    this.color = color;
  }

  public ArrayList<LinePoint<XT, YT>> getPoints()
  {
    return points;
  }

  public void setPoints(ArrayList<LinePoint<XT, YT>> points)
  {
    this.points = points;
  }

  public void addPoint(LinePoint<XT, YT> point)
  {
    points.add(point);
  }

  public void addPoint(XT x, YT y)
  {
    addPoint(new LinePoint<XT, YT>(x, y));
  }

  public LinePoint<XT, YT> getPoint(int index)
  {
    return points.get(index);
  }

  public int getSize()
  {
    return points.size();
  }

  public boolean isShowingPoints()
  {
    return showPoints;
  }

  public void setShowingPoints(boolean showPoints)
  {
    this.showPoints = showPoints;
  }


  public boolean isxIsIndex()
  {
    return xIsIndex;
  }

  public void setXIsIndex(boolean xIsIndex)
  {
    this.xIsIndex = xIsIndex;
  }

  public float getMinX()
  {
    LinePoint point = Collections.min(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
        return Float.compare(lhs.getX(), rhs.getX());
      }

    });
    return point == null ? 0 : point.getX();
  }

  public float getMaxX()
  {
    LinePoint point = Collections.max(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
        return Float.compare(lhs.getX(), rhs.getX());
      }

    });
    return point == null ? 0 : point.getX();
  }

  public float getMinY()
  {
    LinePoint point = Collections.min(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
				if(lhs.isYNull() || rhs.isYNull())
				{
					return 0;
				}
        return Float.compare(lhs.getY(), rhs.getY());
      }

    });
    return point == null ? 0 : point.getY();
  }

  public float getMaxY()
  {
    LinePoint point = Collections.max(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
				if(lhs.isYNull() || rhs.isYNull())
				{
					return 0;
				}
        return Float.compare(lhs.getY(), rhs.getY());
      }

    });
    return point == null ? 0 : point.getY();
  }

  public float getMaxTextWidthX()
  {
    LinePoint point = Collections.max(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
        return Float.compare(lhs.getXAxisLabelWidth(), rhs.getXAxisLabelWidth());
      }
    });
    return point == null ? 0 : point.getXAxisLabelWidth();
  }

  public float getMaxTextWidthY()
  {
    LinePoint point = Collections.max(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
				if(lhs.isYNull() || rhs.isYNull())
				{
					return 0;
				}
        return Float.compare(lhs.getYAxisLabelWidth(), rhs.getYAxisLabelWidth());
      }
    });
    return point == null ? 0 : point.getYAxisLabelWidth();
  }

  public float getMaxTextHeightX()
  {
    LinePoint point = Collections.max(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
        return Float.compare(lhs.getXAxisLabelHeight(), rhs.getXAxisLabelHeight());
      }
    });
    return point == null ? 0 : point.getXAxisLabelHeight();
  }

  public float getMaxTextHeightY()
  {
    LinePoint point = Collections.max(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
				if(lhs.isYNull() || rhs.isYNull())
				{
					return 0;
				}
        return Float.compare(lhs.getYAxisLabelHeight(), rhs.getYAxisLabelHeight());
      }
    });
    return point == null ? 0 : point.getYAxisLabelHeight();
  }

  public float getMaxPointY()
  {
    LinePoint point = Collections.max(points, new Comparator<LinePoint>()
    {
      @Override
      public int compare(LinePoint lhs, LinePoint rhs)
      {
				if(lhs.isYNull() || rhs.isYNull())
				{
					return 0;
				}
        return Float.compare(lhs.getPointY(), rhs.getPointY());
      }
    });
    return point == null ? 0 : point.getPointY();
  }

  @Override
  public String toString()
  {
    return "Line{" +
            ", color=" + color +
            ", points=" + points +
            '}';
  }

  public void format(XAxisFormatter<XT> xAxisFormatter, YAxisFormatter<YT> yAxisFormatter, Paint labelPaint)
  {
    for (LinePoint<XT, YT> p : points)
    {
      p.format(xAxisFormatter, yAxisFormatter, labelPaint);
    }
  }

	public LinePoint<XT,YT> findPointX(Integer value)
	{
		for (LinePoint<XT, YT> p : points)
		{
			if(p.getXValue().equals(value))
			{
				return p;
			}
		}
		return null;
	}
	public void fillXAxisPoints(int start, int end)
	{
		ArrayList<LinePoint<XT, YT>> filledpoints = new ArrayList<LinePoint<XT, YT>>();

		int pos = start;
		while(pos <= end)
		{

			LinePoint<XT,YT> point = findPointX(pos);
			if(point == null)
			{
				point = new LinePoint<XT,YT>(pos);
			}
			filledpoints.add(point);
			pos++;
		}
		points = filledpoints;

	}

	public void rotateToStartAt(int start)
	{
		ArrayList<LinePoint<XT, YT>> rotatedpoints = new ArrayList<LinePoint<XT, YT>>();
		LinePoint<XT,YT> firstPoint = null;
		for(LinePoint<XT,YT> point: points)
		{
			if(firstPoint == null)
			{
				if(point.getXValue().equals(Integer.valueOf(start)))
				{
					firstPoint = point;
					rotatedpoints.add(firstPoint);
				}
			}else
			{
				rotatedpoints.add(point);
			}
		}

		if(firstPoint == null)
		{
			return;
		}

		for(LinePoint<XT,YT> point: points)
		{
			if(point.equals(firstPoint))
			{
				break;
			}
			rotatedpoints.add(point);
		}
		points = rotatedpoints;

		xIsIndex = true;

	}

}
