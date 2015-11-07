/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yarharpictor.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FilesTools {
    
    private int         depth;
    private File        file;
    ArrayList<File>     files;
    
    public FilesTools(File f, int d){
        file = f;
        depth = d;
        files = new ArrayList<>();
        findHEXFiles(f,d);
    }
    
    //public void fin
    
    public ArrayList<File> returnFiles(){
        return files;
    }
    

 
    private void findHEXFiles(File root , int depth) {
        
        if(depth > 0){
        
            File[] listOfFiles = root.listFiles();
            
            if ( listOfFiles != null){
                if(listOfFiles.length > 0){
            
                        for (int i = 0; i < listOfFiles.length; i++) {
                            String iName = listOfFiles[i].getName();
                            if (listOfFiles[i].isFile()) {
                                if ( iName.toLowerCase().endsWith(".hex") && !iName.equals("code.hex")) {
            //                        for (int j = 0; j < depth; j++) {
            //                            System.out.print("\t");
            //                        }
            //                        System.out.println("File: " + listOfFiles[i].getAbsolutePath());
                                    files.add(  new File(  listOfFiles[i].getAbsolutePath() )   );
                                }
                            } else if (listOfFiles[i].isDirectory()) {
            //                    for (int j = 0; j < depth; j++) {
            //                        System.out.print("\t");
            //                    }
            //                    System.out.println("Directory: " + iName);
                                findHEXFiles( listOfFiles[i] , depth - 1);
                            }
                        }
                
                
                }
            }
        
        }
        
    }
    
    public static void copyFile( File from, File to ) throws IOException {
        Files.copy( from.toPath(), to.toPath() , StandardCopyOption.REPLACE_EXISTING );
    }
    
        public static void main(String args[]) {
        FilesTools fl = new FilesTools(new File("."), 3);
        ArrayList<File> files = fl.files;
        for(File f : files)
                System.out.println("->>>"+f.getPath());
//        
//        ArrayList<File> files = FilesTools.findHEXFiles(new File("D:\\JAIMEDATA\\Dropbox\\Informatica y Hacking\\MIS PROYECTOS\\JAVA\\YarHarPiktor"), 0);
//        for(File f : files){
//            System.out.println("---------->");
//            System.out.println(f.getAbsoluteFile());
//            System.out.println(".");
//        }
//        System.out.println("sssss---"+files.get(0).getPath());
    }
}