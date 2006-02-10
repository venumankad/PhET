/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display2d;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display.GUIUtils;
import org.opensourcephysics.display.Measurable;

/**
 * DataRaster maps (x,y) data onto an image.
 *
 * The image has the same size as the data panel.
 * Every data point renders itself as one pixel.
 *
 * @author Wolfgang Christian
 * @version 1.0
 */
public class DataRaster implements Measurable {
  static Color[] colors = {
    Color.red, Color.green, Color.blue, Color.yellow, Color.cyan, Color.magenta, Color.orange
  };

  /**
   * The drawing panel  that determines the image size.
   */
  public DrawingPanel primaryDrawingPanel = null;
  Color backgroundColor;
  ArrayList imageDatasets = new ArrayList();
  boolean visible = true;
  protected double xmin = -1;
  protected double xmax = 1;
  protected double ymin = -1;
  protected double ymax = 1;
  protected int alpha = 255;         // the alpha value for the image
  protected BufferedImage image;
  protected int maxPoints = 0x2ffff; // the maximum number of points that will be saved as image data.

  /**
   * Constructs a DataRaster object that maps (x,y) data to image pixels.
   *
   * @param dp  the drawing panel that will be used to calculate the image size
   * @param _xmin  the mininum x value that can be mapped
   * @param _xmax  the maximum x value that can be mapped
   * @param _ymin  the mininum y value that can be mapped
   * @param _ymax  the maximum y value that can be mapped
   */
  public DataRaster(DrawingPanel dp, double _xmin, double _xmax, double _ymin, double _ymax) {
    primaryDrawingPanel = dp;
    if(primaryDrawingPanel!=null) {
      primaryDrawingPanel.setPixelScale();
    }
    xmin = _xmin;
    xmax = _xmax;
    ymin = _ymin;
    ymax = _ymax;
    image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); // make a 1x1 image to start
    backgroundColor = new Color(image.getRGB(0, 0));
  }

  /**
   *  Appends an (x,y) datum to the image.
   *
   * @param  x
   * @param  y
   * @param  dataIndex  Description of Parameter
   */
  public void append(int dataIndex, double x, double y) {
    checkIndex(dataIndex).append(x, y);
  }

  /**
   *  Sets the data point marker color.
   *
   * @param  dataIndex
   * @param  color
   */
  public void setColor(int dataIndex, Color color) {
    checkIndex(dataIndex).setColor(color);
  }

  /**
   *  Clears all data from all Datasets.
   */
  public void clear() {
    for(int i = 0, n = imageDatasets.size();i<n;i++) {
      ((ImageData) (imageDatasets.get(i))).clear();
    }
    render();
  }

  /**
   *  Ensures that the image data exists
   *
   * @param  dataIndex
   */
  protected ImageData checkIndex(int dataIndex) {
    while(dataIndex>=imageDatasets.size()) {
      ImageData d = null;
      if(dataIndex<colors.length-1) {
        d = new ImageData(colors[dataIndex]);      // use specified colors
      } else {
        d = new ImageData(GUIUtils.randomColor()); // use random color
      }
      imageDatasets.add(d);
    }
    return(ImageData) imageDatasets.get(dataIndex);
  }

  /**
 * Paints a new image using the existing data.
 *
 * returns the image buffer
 */
  public synchronized BufferedImage render() {
    if(primaryDrawingPanel==null) {
      return null;
    }
    int xrange = primaryDrawingPanel.xToPix(xmax)-primaryDrawingPanel.xToPix(xmin);
    int yrange = primaryDrawingPanel.yToPix(ymin)-primaryDrawingPanel.yToPix(ymax);
    xrange = Math.min(xrange, primaryDrawingPanel.getWidth());
    yrange = Math.min(yrange, primaryDrawingPanel.getHeight());
    image = new BufferedImage(Math.abs(xrange), Math.abs(yrange), BufferedImage.TYPE_INT_ARGB);
    backgroundColor = new Color(image.getRGB(0, 0));
    for(int i = 0, n = imageDatasets.size();i<n;i++) {
      ((ImageData) (imageDatasets.get(i))).render();
    }
    return image;
  }

  /**
   * Draw the image containing the dataset pixels.
   *
   * @param panel  the world in which the arrow is viewed
   * @param g  the graphics context upon which to draw
   */
  public void draw(DrawingPanel panel, Graphics g) {
    if(!visible) {
      return;
    }
    if(primaryDrawingPanel!=panel) {
      return; // only draw on one panel for now.
    }
    int xrange = panel.xToPix(xmax)-panel.xToPix(xmin);
    int yrange = panel.yToPix(ymin)-panel.yToPix(ymax);
    xrange = Math.min(xrange, panel.getWidth());
    yrange = Math.min(yrange, panel.getHeight());
    if(xrange==0||xrange==0) {
      return;
    }
    if((Math.abs(xrange)!=image.getWidth())|| // the range does not match the image size
            (Math.abs(yrange)!=image.getHeight())) {
      render();
    }
    if(image!=null&&image.getWidth()>1) {
      g.drawImage(image, panel.xToPix(xmin), panel.yToPix(ymax), panel); // blast the image into the panel
    }
  }

  public boolean isMeasured() {
    return true;
  }

  public void setXMin(double _value) {
    xmin = _value;
  }

  public void setXMax(double _value) {
    xmax = _value;
  }

  public void setYMin(double _value) {
    ymin = _value;
  }

  public void setYMax(double _value) {
    ymax = _value;
  }

  public void setMinMax(double _minx, double _maxx, double _miny, double _maxy) {
    xmin = _minx;
    xmax = _maxx;
    ymin = _miny;
    ymax = _maxy;
  }

  public double getXMin() {
    return xmin;
  }

  public double getXMax() {
    return xmax;
  }

  public double getYMin() {
    return ymin;
  }

  public double getYMax() {
    return ymax;
  }

  public double getWidth() {
    return image.getRaster().getWidth();
  }

  public double getHeight() {
    return image.getRaster().getHeight();
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  int xToPix(int x) {
    return(int) (image.getRaster().getWidth()*(x-xmin)/(xmax-xmin));
  }

  int yToPix(int y) {
    return(int) (image.getRaster().getHeight()*(ymax-y)/(ymax-ymin));
  }

  public Color getPixColor(int xpix, int ypix) {
    return new Color(image.getRGB(xpix, ypix));
  }

  /**
   * Sets the visibility of the contour plot.
   * Drawing will be disabled if visible is false.
   *
   * @param isVisible
   */
  public void setVisible(boolean isVisible) {
    visible = isVisible;
  }

  class ImageData {
    private int[] color;
    float[][] data;
    int nextPoint = 0;

    ImageData(Color c) {
      setColor(c);
      data = new float[2][64]; // create an array to hold 64 data points
    }

    void setColor(Color c) {
      color = new int[4];
      color[0] = c.getRed();
      color[1] = c.getGreen();
      color[2] = c.getBlue();
      color[3] = alpha;
    }

    private synchronized void increaseCapacity(int size) {
      size = Math.min(size, maxPoints); // do not let the number of data points exceed maxPoints
      float[][] newData = new float[2][size];
      int newNext = Math.min(nextPoint, (3*size)/4); // drop 1/4 of the old data if the size is no longer increasing
      System.arraycopy(data[0], nextPoint-newNext, newData[0], 0, newNext);
      System.arraycopy(data[1], nextPoint-newNext, newData[1], 0, newNext);
      nextPoint = newNext;
      data = newData;
    }

    synchronized void clear() {
      data = new float[2][64]; // create an array to hold 64 data points
      nextPoint = 0;
    }

    void append(double x, double y) {
      if(Double.isNaN(x)||Double.isInfinite(x)||Double.isNaN(y)||Double.isInfinite(y)) {
        return; // do not append bad data
      }
      if(nextPoint>=data[0].length) {
        increaseCapacity(data[0].length*2);
      }
      data[0][nextPoint] = (float) x; // save the values for later use
      data[1][nextPoint] = (float) y;
      nextPoint++;
      WritableRaster raster = image.getRaster();
      if(raster.getWidth()<2) {
        return; // image is too small
      }
      int i = (int) (raster.getWidth()*(x-xmin)/(xmax-xmin));
      int j = (int) (raster.getHeight()*(ymax-y)/(ymax-ymin));
      if(i<0||j<0||i>=raster.getWidth()||j>=raster.getHeight()) {
        return; // outside the image
      }
      try {
        raster.setPixel(i, j, color); // set the image pixel
      } catch(Exception ex) {
        System.out.println("Error setting raster in ImageData append.");
      }
    }

    void render() {
      WritableRaster raster = image.getRaster();
      for(int c = 0;c<nextPoint;c++) {
        int i = (int) (raster.getWidth()*(data[0][c]-xmin)/(xmax-xmin));
        int j = (int) (raster.getHeight()*(ymax-data[1][c])/(ymax-ymin));
        if(i<0||j<0||i>=raster.getWidth()||j>=raster.getHeight()) {
          return;                       // outside the image raster
        }
        try {
          raster.setPixel(i, j, color); // set the image
        } catch(Exception ex) {
          System.out.println("Error setting raster in ImageData render.");
        }
      }
    }
  } // end of ImageData inner class
}

/*
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
