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
public class YarDump {

    /**
     * @param args the command line arguments
     */
    
    private  int lineDelay    = 100;
    
    private  int baudRate     = SerialPort.BAUDRATE_9600;
    private  int dataBits     = SerialPort.DATABITS_8;
    private  int stopBits     = SerialPort.STOPBITS_1;
    private  int parity       = SerialPort.PARITY_NONE;
    
    private  SerialPort   serialPort;    
    private  String       port    ="COM1";
    
    private YarHarPictor ref = null;
    
    private BufferedReader reader;
    
    public YarDump(YarHarPictor ref){
         this.ref = ref;
    }  
    
    public YarDump(int b, int d, int s, int p, String port){
        baudRate = b;
        dataBits = d;
        stopBits = s;
        parity   = p;
        this.port = port;
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
                
            ref.setBarConfig(Color.BLACK, null, null, null, Color.ORANGE, "READY. PRESS PICTOR'S RESET OR PLUG IT IN");

            if(ref!=null) updateValues();
            if(serialPort==null) serialPort = new SerialPort(port);
            
            serialPort.openPort();                                                          //Open serial port
            serialPort.setParams(baudRate, dataBits, stopBits, parity);                     //Set params
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;  //Prepare mask
            serialPort.setEventsMask(mask);                                                 //Set mask
            serialPort.addEventListener(new SerialPortReader());   
//            try {
//                serialPort.writeBytes("\r".getBytes());
//            }catch (SerialPortException ex){
//                ;
//            }
        } catch (SerialPortException ex) {
            System.out.println(ex);
            ref.setBarConfig(Color.RED, null, null, null,StaticStrings.DEFBAR_PROG_TOPCOLOR, "ERROR. Serial port not available.");
            disconnect();
            ref.enableComponents();
        }
            
    }
    
    public void disconnect(){
        try {
            System.out.println("dis in:" +serialPort.isOpened());
            if (serialPort != null && serialPort.isOpened()) {
                serialPort.purgePort(SerialPort.PURGE_TXCLEAR);
                serialPort.purgePort(SerialPort.PURGE_RXCLEAR);                
                serialPort.purgePort(SerialPort.PURGE_RXABORT);
                serialPort.purgePort(SerialPort.PURGE_TXABORT);
                serialPort.closePort();
                System.out.println("Test");
            }
        } catch (SerialPortException ex) {
            Logger.getLogger(YarDump.class.getName()).log(Level.SEVERE, null, ex);
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
        Boolean finish  = false;
                
        @Override
        public void serialEvent(SerialPortEvent event) {
            
            
            if (event.isRXCHAR()) {                     //If data is available
                if (event.getEventValue() > 0) {        //Check bytes count in the input buffer
                                                        //Read data, if 10 bytes available 
                    
                    if(!begin){
                        rx="";
                        ref.setDisconnect(false);
                        ref.setBarConfig(Color.WHITE, Color.BLACK, Color.RED, Color.RED, Color.BLACK, "CONNECTING. DO NOT DISCONNECT!!!");
                        begin = true;
                        try {
                            Thread.sleep(1000);
                            ref.setBarConfig(null,null,null,null,null, "CONNECTING. DO NOT DISCONNECT!!! Reading Data...");
                            
                            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(YarDump.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    
                    try {
                        byte buffer[] = serialPort.readBytes(event.getEventValue());
                        tmp = new String(buffer);
                        rx += tmp;
                        //ref.setSerialOutput(rx);
                        
                        if(ref!=null) ref.setSerialOutput(rx);
                        
                        System.out.print(tmp);
                        
                        if(rx.contains("Error") ){
                            disconnect();
                            ref.setBarConfig(Color.RED,StaticStrings.DEFBAR_BOTCOLOR,StaticStrings.DEFBAR_TOPCOLOR
                                    ,Color.BLACK,Color.BLACK,
                                    "ERROR. KudeaPIC command error. Please debug using HyperTerminal mode.");
                        }
                        
                        else if(rx.contains("PIC>") && !connected ){
                            connected=true;
                            ayuda=true;
                            serialPort.writeBytes("\r".getBytes());
                            Thread.sleep(lineDelay);
                        }
                        
                        else if(rx.contains("#") && !connected ){
                            serialPort.writeBytes("\r".getBytes());
                            Thread.sleep(lineDelay);
                            
                            connected = true;
                        } 
                        
                        else if(rx.contains("ayuda") && !ayuda ){
                            //serialPort.writeBytes("c\r".getBytes());
                            Thread.sleep(lineDelay);
                            serialPort.writeBytes("\r".getBytes());
                            Thread.sleep(lineDelay);
                            ayuda = true;
                        }
                        
                        else if(ayuda==true && end==false){
                       
                            for (int i = 0; i < 16; i++) {
                                System.out.println("0" + Integer.toHexString(i));
                                serialPort.writeBytes(
                                        (   ("D "+"0"+Integer.toHexString(i)+"\r").toUpperCase()    ).getBytes());
                                ref.setBarValue(i, 127);
                                Thread.sleep(25);
                            }
                            for (int i = 16; i < 128; i++) { //128
                                System.out.println(Integer.toHexString(i));
                                serialPort.writeBytes(
                                        (   ("D "+Integer.toHexString(i)+"\r").toUpperCase()    ).getBytes());
                                ref.setBarValue(i, 127);
                                Thread.sleep(25);
                            }
                            end = true;
                            
                            
                        }else if( rx.contains("7F = ") && end == true && !finish){
                            finish = true;
                            //serialPort.writeBytes("s\r".getBytes());
                            //Thread.sleep(lineDelay);
                            System.out.println("End, sent.");
                            disconnect();
                            ref.setBarConfig(Color.BLACK,StaticStrings.DEFBAR_BOTCOLOR,StaticStrings.DEFBAR_TOPCOLOR
                                    ,StaticStrings.DEFBAR_PROG_BOTCOLOR,Color.ORANGE,
                                    "YARRR! \"Your memories arrr ready! Loading Data...\" ");
                            ref.setDisconnect(true);
                            ref.enableComponents();
                            new DumpTable(rx).setVisible(true);
                        }
                        
                        
                        
                       

                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                            ref.setBarConfig(Color.RED,StaticStrings.DEFBAR_BOTCOLOR,StaticStrings.DEFBAR_TOPCOLOR
                                    ,Color.BLACK,Color.BLACK,
                                    "ERROR. Problem during connection");
                        ref.enableComponents();
                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(YarDump.class.getName()).log(Level.SEVERE, null, ex);
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
        
        for( int i = 0 ; i < 16 ; i++ ){
            System.out.println("0"+Integer.toHexString(i));
        }
        for( int i = 16 ; i < 128 ; i++ ){
            System.out.println(Integer.toHexString(i));
        }
    }
    
}
