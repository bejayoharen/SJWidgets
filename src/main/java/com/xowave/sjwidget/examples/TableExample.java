/**
 *
 * This file is part of the SJWidget library.
 * (c) 2005-2012 Bjorn Roche
 * Development of this library has been supported by Indaba Media (http://www.indabamusic.com)
 * and XO Audio (http://www.xoaudio.com)
 *
 * for copyright and sharing permissions, please see the COPYING.txt file which you should
 * have received with this file.
 *
 */
package com.xowave.sjwidget.examples;

import com.xowave.sjwidget.*;
import com.xowave.sjwidget.util.WidgetUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.io.IOException;


/**
 * This class demonstrates how you might use SJWidgets to change the look and feel of a JTable.
 * A similar approach can be used for JList.
 */
public class TableExample
{
   public static void main( String[] args ) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                createAndShowGUI();
            }
      });
   }
   private static void createAndShowGUI() {
      try {
         WidgetUtil.loadDescriptions("examples/TableDescriptions.xml");
      } catch( IOException ioe ) {
         throw new RuntimeException( ioe );
      }

      JFrame frame = new JFrame("Table Example");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      DefaultTableModel dtm = new DefaultTableModel();
      dtm.setRowCount(5);
      dtm.setColumnCount(5);
      for( int i=0; i<5; ++i )
    	  for( int j=0; j<5; ++j )
    		  dtm.setValueAt(i + " x " + j + " = " + ( i*j ), i, j);
      JTable table = new JTable(dtm);
      table.setDefaultRenderer(Object.class, new MyStringRenderer());
      table.setDefaultEditor(Object.class, new MyStringEditor());

      frame.getContentPane().add( table );
      frame.pack();
      frame.setResizable(false);
      frame.setVisible(true);
   }
}

@SuppressWarnings("serial")
class MyStringRenderer extends SJLabel implements TableCellRenderer {
	MyStringRenderer() {
		super( null );
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setText( value.toString() );
		setWidgetID("table cell");
		
		return this;
	}
}

@SuppressWarnings("serial")
class MyStringEditor extends DefaultCellEditor {
	private String orig;
	
	MyStringEditor() {
		super( new SJTextField("table editor") );
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		SJTextField ret = (SJTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
		orig = value.toString();
		ret.setText(orig);
		return ret;
	}
}
