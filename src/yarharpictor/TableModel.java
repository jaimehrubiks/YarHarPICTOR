package yarharpictor;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jaime Hidalgo García
 */
    public class TableModel extends DefaultTableModel {
        
        
    public TableModel(Object[] columnNames, int emptyRows){
        super(columnNames,emptyRows);
    }

    
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    @Override
    public Class getColumnClass(int c) {
        if ( getValueAt(0, c) == null ) return Object.class;
        else
        return getValueAt(0, c).getClass();
    }
    
    }
    

