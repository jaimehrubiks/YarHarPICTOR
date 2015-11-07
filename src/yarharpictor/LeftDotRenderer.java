/*
 * JAIME HIDALGO.
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yarharpictor;

/**
 *
 * @author Jaime Hidalgo García
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/*
 *  Simple text renderer that will display the text right justified with
 *  leading dots when the column width is not large enough to display the
 *  entire text.�
 */
class LeftDotRenderer extends DefaultTableCellRenderer
{
        @Override
	public Component getTableCellRendererComponent(
		JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		//  Determine the width available to render the text

		
                //String cellText = getText();
                

		//  Not enough space so start rendering from the end of the string
		//  until all the space is used up
                //int a = cellText.lastIndexOf("LABORATORIO");
                //cellText = cellText.substring(  a  );
                //setText(cellText);
                //String a = getText();
                //a=a.substring(3);
                //setText(a);
                //setText( getText().substring(  getText().lastIndexOf("LABORATORIO")  ) );


		return this;
	}
}