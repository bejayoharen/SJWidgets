/**
 *
 * This file is part of the SJWidget library.
 * (c) 2005-2012 Bjorn Roche
 * Development of this library has been supported by Indaba Media (http://www.indabamusic.com)
 * and XO Audio (http://www.xoaudio.com)
 *
 * for copyright and sharing permissions, please see the COPYING.txt file which you should
 * have recieved with this file.
 *
 */
package com.xowave.sjwidget.examples;

import com.xowave.sjwidget.*;
import javax.swing.*;

import java.io.IOException;


/**
 * This class demonstrates how you might use SJWidgets to design a table
 */
public class TableExample
{
   public static void main( String[] args ) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
      });
   }
   private static void createAndShowGUI() {
      try {
         WidgetUtil.loadDescriptions("resources/TableDescriptions.xml");
      } catch( IOException ioe ) {
         throw new RuntimeException( ioe );
      }

      JFrame frame = new JFrame("Table Example");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // we're going to create a column of labels, buttons and stuff.
      JTable table = new JTable();

      frame.getContentPane().add( table );
      frame.pack();
      frame.setVisible(true);
   }
}
