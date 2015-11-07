/*
 * JAIME HIDALGO.
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yarharpictor;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.ColorUIResource;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import yarharpictor.tools.FilesTools;
import yarharpictor.tools.SoundTools;
import yarharpictor.tools.StaticStrings;
import yarharpictor.tools.UserSettings;

/**
 *
 * @author Jaime Hidalgo García
 */
public class YarPicTerminalEngine {

    /**
     * @param args the command line arguments
     */
    
    private  int lineDelay    = 100;
    
    private  int baudRate     = SerialPort.BAUDRATE_9600;
    private  int dataBits     = SerialPort.DATABITS_8;
    private  int stopBits     = SerialPort.STOPBITS_1;
    private  int parity       = SerialPort.PARITY_NONE;
    
    private  SerialPort   serialPort;    
    private  File         hex = new File("./code.hex");
    private  String       port    ="COM1";
    
    private YarPicTerminal ref = null;
    
    private BufferedReader reader;
    
    public YarPicTerminalEngine(YarPicTerminal ref){
         this.ref = ref;
    }  
    
    public YarPicTerminalEngine(int b, int d, int s, int p, String port, File f){
        baudRate = b;
        dataBits = d;
        stopBits = s;
        parity   = p;
        this.port = port;
        this.hex = f;
    }
    
    public void updateValues(){
        
        port = (String) UserSettings.configProps.getProperty("SerialPort");
        
        baudRate = Integer.parseInt( UserSettings.configProps.getProperty("BaudRate") );
        dataBits = Integer.parseInt( UserSettings.configProps.getProperty("dataBits") );
        stopBits = Integer.parseInt( UserSettings.configProps.getProperty("StopBits") );
        parity   = Integer.parseInt( UserSettings.configProps.getProperty("Parity") );
        
    }
    
    
    public void connect(){

        try {
            updateValues();
            serialPort = new SerialPort(port);
            serialPort.openPort();                                                          //Open serial port
            serialPort.setParams(baudRate, dataBits, stopBits, parity);                     //Set params
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;  //Prepare mask
            serialPort.setEventsMask(mask);                                                 //Set mask
            serialPort.addEventListener(new SerialPortReader());   
            ref.setLabelText("Connected.");
        } catch (SerialPortException ex) {
            Logger.getLogger(YarPicTerminalEngine.class.getName()).log(Level.SEVERE, null, ex);
            ref.setLabelText("Error. Port busy");
        }

    }
    
    public void disconnect(){
        try {
            if (serialPort != null && serialPort.isOpened()) {
                serialPort.purgePort(SerialPort.PURGE_TXCLEAR);
                serialPort.purgePort(SerialPort.PURGE_RXCLEAR);                
                serialPort.purgePort(SerialPort.PURGE_RXABORT);
                serialPort.purgePort(SerialPort.PURGE_TXABORT);
                serialPort.closePort();
                ref.setLabelText("Disconnected.");
                System.out.println("Test");
            }
            //reader.close();
        } catch (SerialPortException ex) {
            Logger.getLogger(YarPicTerminalEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendText(String text){
        
        try {
            serialPort.writeBytes(text.getBytes());
            serialPort.writeBytes("\r".getBytes());
        } catch (SerialPortException ex) {
            Logger.getLogger(YarPicTerminalEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void sendFile(File f){
        
        try {
            reader = new BufferedReader(new FileReader(f));
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                
                try {
                    serialPort.writeBytes(tmp.getBytes());
                    serialPort.writeBytes("\r".getBytes());
                    Thread.sleep(50);
                } catch (SerialPortException | InterruptedException ex) {
                    Logger.getLogger(YarPicTerminalEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        } catch (IOException ex) {            
            Logger.getLogger(YarPicTerminalEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    class SerialPortReader implements SerialPortEventListener {

        String      rx   = "";
        String      tmp  = null;

        Boolean     connected = false;
        Boolean     ayuda     = false;
        Boolean     really    = false;
        Boolean     preparado = false;
        Boolean     end       = false;
        
        Boolean     begin     = false;
                
        @Override
        public void serialEvent(SerialPortEvent event) {
            
            
            if (event.isRXCHAR()) {                     //If data is available
                if (event.getEventValue() > 0) {        //Check bytes count in the input buffer
                                                        //Read data, if 10 bytes available 
                   
                    
                    try {
                        byte buffer[] = serialPort.readBytes(event.getEventValue());
                        tmp = new String(buffer);
                        rx += tmp;
                        if(ref!=null) ref.setInput(rx);
                        
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            } else if (event.isCTS()) {//If CTS line has changed state
                if (event.getEventValue() == 1) {//If line is ON
                    System.out.println("CTS - ON");
                } else {
                    System.out.println("CTS - OFF");
                }
            } else if (event.isDSR()) {///If DSR line has changed state
                if (event.getEventValue() == 1) {//If line is ON
                    System.out.println("DSR - ON");
                } else {
                    System.out.println("DSR - OFF");
                }
            }
        }
    }
    
    public ArrayList<String> getPorts(){
        ArrayList<String> ar = new ArrayList<>();
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames)
            ar.add(portName);
        return ar;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        
               
       
    }
    
}
