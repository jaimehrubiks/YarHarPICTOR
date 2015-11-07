/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yarharpictor.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;

/**
 *
 * @author Jaime
 */
public class UserSettings {
    
    public static File          configFile = new File("./config.properties");
    public static Properties    configProps;
    
    static{
        
    }
    
    public static void run(){
        
    }
    
    public static synchronized void saveProperties(){
           try {
            OutputStream outputStream = new FileOutputStream(configFile, false);
            configProps.storeToXML(outputStream, "YAR HAR PICTOR SETTINGS FILE");
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(UserSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void loadDefaults(){
 
        Properties defaultProps = new Properties();
        defaultProps.setProperty("FilesFolder"      , "./"                                               );
        defaultProps.setProperty("FilesDepth"       , "1"                                                ); 
        defaultProps.setProperty("SerialPort"       , "Not Selected"                                     ); 
        defaultProps.setProperty("BaudRate"         , String.valueOf(SerialPort.BAUDRATE_9600)           );
        defaultProps.setProperty("dataBits"         , String.valueOf(SerialPort.DATABITS_8)              );
        defaultProps.setProperty("StopBits"         , String.valueOf(SerialPort.STOPBITS_1)              );
        defaultProps.setProperty("Parity"           , String.valueOf(SerialPort.PARITY_NONE)             );
        
        defaultProps.setProperty("Sound"            , "false"                                             );
        defaultProps.setProperty("Disclaimer"       , "false"                                             );
        
       
        configProps = defaultProps;
    }
    
    public static void loadProperties(){

        if (!configFile.exists()) {
            loadDefaults();

        } else {

            try {
                configProps = new Properties();
                InputStream inputStream = new FileInputStream(configFile);
                configProps.loadFromXML(inputStream);
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(UserSettings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
