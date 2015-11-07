/*
 * JAIME HIDALGO.
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yarharpictor;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jaime Hidalgo García
 */
public class DumpTable extends javax.swing.JFrame {

    private String rx = "00 = 3F\r1A = 22";
    private Map<String,String>  map = new HashMap<>();
    
    private TableModel          model;
    /**
     * Creates new form DumpTable
     * @param s
     */
    public DumpTable(String s) {
        rx = s;
        String[] columnNames = new String[]{ "Address [HEX]" , "Value [HEX]" , "Value [DEC]" , "Value [BIN]"};
        model = new TableModel(columnNames,0);
        
        
        initComponents();
        
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        
        stringToMap();
        populateTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jTable1.setModel(model);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DumpTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DumpTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DumpTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DumpTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new DumpTable().setVisible(true);
                
            }
        });
        
    }

    private void stringToMap(){
        
        String[] lines = rx.split("\r");
        System.out.println("->"+rx.length());
        System.out.println("->"+lines.length);
        
        String part[];
        String tmp;
        for( String line : lines){
            //System.out.println(line);
            if ( line.contains( " = ")){
                part = line.split(" = ");
                map.put(part[0].substring(1),part[1]);
                //System.out.println("_"+part[0].substring(1)+"_"+"<->"+"_"+part[1]+"_");
            }
        }
        
//        for (int i = 0; i < 16; i++) {
//            tmp = ( "0" + Integer.toHexString(i).toUpperCase()   );
//            if(map.containsKey(tmp))
//                System.out.println(map.get(tmp));
//        }
//        for (int i = 16; i < 128; i++) { //128
//            tmp = ( Integer.toHexString(i).toUpperCase()   );
//            if(map.containsKey(tmp))
//                System.out.println(map.get(tmp));
//        }
    }
    
    private void populateTable(){
        String tmp;
        String hex;
        String[] rowData;
        for (int i = 0; i < 16; i++) {
            tmp = ( "0" + Integer.toHexString(i).toUpperCase()   );
            if(map.containsKey(tmp)){
                rowData = new String[4];
                hex = map.get(tmp);
                rowData[0] = tmp;
                rowData[1] = hex;
                rowData[2] = String.valueOf( Integer.parseInt(hex, 16) );
                rowData[3] = Integer.toBinaryString(Integer.parseInt(hex, 16));
                model.addRow(rowData);
            }
        }
        for (int i = 16; i < 128; i++) { //128
            tmp = ( Integer.toHexString(i).toUpperCase()   );
            if(map.containsKey(tmp)){
                rowData = new String[4];
                hex = map.get(tmp);
                rowData[0] = tmp;
                rowData[1] = hex;
                rowData[2] = String.valueOf( Integer.parseInt(hex, 16) );
                rowData[3] = Integer.toBinaryString(Integer.parseInt(hex, 16));
                model.addRow(rowData);
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}