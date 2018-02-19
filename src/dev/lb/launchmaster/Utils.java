package dev.lb.launchmaster;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Enumeration;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import javax.swing.SpringLayout;
import javax.swing.Spring;
import java.awt.Component;

/**
 * Methods for various tasks. Most are copied from the internet.
 *
 * @author Lars Bündgen, others
 * @version 1.0
 */
public final class Utils
{
    private Utils(){}

    public static <T extends JComponent> T setSize(T comp, int width, int height){
        comp.setSize(new Dimension(width, height));
        comp.setPreferredSize(new Dimension(width, height));
        return comp;
    }

    
    /**
     * Aus SpringUtilites.java kopiert
     * 
     * Aligns the first <code>rows</code> * <code>cols</code>
     * components of <code>parent</code> in
     * a grid. Each component in a column is as wide as the maximum
     * preferred width of the components in that column;
     * height is similarly determined for each row.
     * The parent is made just big enough to fit them all.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param initialX x location to start the grid at
     * @param initialY y location to start the grid at
     * @param xPad x padding between cells
     * @param yPad y padding between cells
     */
    public static void makeCompactGrid(JComponent parent,
                                       int rows, int cols,
                                       int initialX, int initialY,
                                       int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout)parent.getLayout();
        } catch (ClassCastException exc) {
            layout = new SpringLayout();
            parent.setLayout(layout);
        }
 
        //Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width,
                                   getConstraintsForCell(r, c, parent, cols).
                                       getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }
 
        //Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height,
                                    getConstraintsForCell(r, c, parent, cols).
                                        getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }
 
        //Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }

    
    /**
     *  Aus SpringUtilites.java kopiert
     */
    private static SpringLayout.Constraints getConstraintsForCell(
                                                int row, int col,
                                                JComponent parent,
                                                int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }
    
    
  /**
   * Das ist von StackOverflow geklaut. ClassLoader sind schei*e.
   * 
   * Attempts to list all the classes in the specified package as determined
   * by the context class loader, recursively, avoiding anonymous classes
   * 
   * @param pckgname
   *            the package name to search
   * @deprecated Doesn't really work on all IDEs / Compiled
   * @return a list of classes that exist within that package
   * @throws ClassNotFoundException
   *             if something went wrong
   */
  @Deprecated
  public static List<Class<?>> getClassesForPackage(String pckgname, ClassLoader cl) throws ClassNotFoundException {
      // This will hold a list of directories matching the pckgname. There may be more than one if a package is split over multiple jars/paths
      ArrayList<File> directories = new ArrayList<File>();
      String packageToPath = pckgname.replace('.', '/');
      try {
          // Ask for all resources for the packageToPath
          Enumeration<URL> resources = cl.getResources(packageToPath);
          while (resources.hasMoreElements()) {
              directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
          }
      } catch (NullPointerException x) {
          throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
      } catch (UnsupportedEncodingException encex) {
          throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
      } catch (IOException ioex) {
          throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
      }

      ArrayList<Class<?>> classes = new ArrayList<>();
      // For every directoryFile identified capture all the .class files
      while (!directories.isEmpty()){
          File directoryFile  = directories.remove(0);             
          if (directoryFile.exists()) {
              // Get the list of the files contained in the package
              File[] files = directoryFile.listFiles();

              for (File file : files) {
                  // we are only interested in .class files
                  if ((file.getName().endsWith(".class")) && (!file.getName().contains("$"))) {
                      // removes the .class extension
                      int index = directoryFile.getPath().indexOf(packageToPath);
                      String packagePrefix = directoryFile.getPath().substring(index).replace('/', '.');;                          
                    try {                  
                      String className = packagePrefix + '.' + file.getName().substring(0, file.getName().length() - 6);                            
                      classes.add(Class.forName(className));                                
                    } catch (NoClassDefFoundError e)
                    {
                      // do nothing. this class hasn't been found by the loader, and we don't care.
                    }
                  } else if (file.isDirectory()){ // If we got to a subdirectory
                      directories.add(new File(file.getPath()));                          
                  }
              }
          } else {
              throw new ClassNotFoundException(pckgname + " (" + directoryFile.getPath() + ") does not appear to be a valid package");
          }
      }
      return classes;
  }  
}
