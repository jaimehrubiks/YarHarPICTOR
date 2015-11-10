/*
 * JAIME HIDALGO.
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yarharpictor.tools;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jaime Hidalgo García
 */
public class EquFetcher {
        
    public  String[] getNames(String fileName){
        String[] names = new String[128];
        for( int i = 0 ; i < 128 ; i++ )
            names[i]="";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String tmp;
            String[] pair = new String[2];
            try {
                while(   (tmp=br.readLine())!=null     ) {
                    
                    
                    if(tmp.toLowerCase().contains("equ")){
                        pair = catchName(tmp);
                        //System.out.println(pair[0]+" "+pair[1]);
                        try{
                            names[ Integer.parseInt(pair[1], 16)] = names[ Integer.parseInt(pair[1], 16)] + "/"+pair[0];
                        }catch(Exception e){}
                    }
                    
                    
                }
            } catch (IOException ex) {
                Logger.getLogger(EquFetcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EquFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return names;
    }
    
    public  String[] getDefNames(){
        String[] names = null;
        try {
            InputStream in = getClass().getResourceAsStream("/resources/pic.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String tmp;
            names = new String[32];
            int i=0;
            while(   (tmp=reader.readLine())!=null     ) {
                names[i++] = tmp.split(" ")[0];
            }
        } catch (IOException ex) {
            Logger.getLogger(EquFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return names;
    }
    
    public  String[] catchName(String str){
        String[] pair = new String[2];
        String[] line = str.split("\\s+");
        //System.out.println("");
        for (int i = 0; i < line.length; i++) {
            //System.out.print(line[i]+"->");
            if (line[i].toLowerCase().equals("equ")) {
                pair[0] = line[i - 1];
                pair[1] = line[i + 1];
                System.out.println(pair[0]+"--"+pair[1]);
            }
        }
        if (pair[0] != null && pair[1] != null) {
            if (pair[1].toLowerCase().contains("h")) {
                pair[1] = pair[0].replaceAll("h", "");
                pair[1] = pair[0].replaceAll("H", "");
                pair[1] = pair[0].replaceAll("'", "");
            }
            char a;
            if ((a = pair[1].toLowerCase().charAt(0)) == 'd' || a == 'a' || a == 'c' || a == 'b') {
                pair[0] = "";
                pair[1] = "";
            }

        }

        return pair;
        
        
    }
    
    public static void main(String[] argv){
        EquFetcher e = new EquFetcher();
        String[] a = e.getDefNames();
        for (String i : a)
            System.out.println(i);
    }
    
    
}
