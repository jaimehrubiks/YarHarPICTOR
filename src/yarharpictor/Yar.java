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
public class Yar {

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
    
    private YarHarPictor ref = null;
    
    private BufferedReader reader;
    
    public Yar(YarHarPictor ref){
         this.ref = ref;
    }  
    
    public Yar(int b, int d, int s, int p, String port, File f){
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
    
    
    public void connect(File f){
        
        try {
            if(hex.isFile()){
                if(!hex.delete()){
                    ref.enableComponents();
                    ref.setBarConfig(Color.RED, null, null, null, StaticStrings.DEFBAR_PROG_TOPCOLOR, "ERROR. Temporal HEX file in use");
                    return;
                }
            }
                
            ref.setBarConfig(Color.BLACK, null, null, null, Color.ORANGE, "READY. PRESS PICTOR'S RESET OR PLUG IT IN");
                        
            FilesTools.copyFile(f, hex);
            if(!Files.isReadable(hex.toPath())){
                ref.enableComponents();
                ref.setBarConfig(Color.RED, null, null, null, StaticStrings.DEFBAR_PROG_TOPCOLOR, "ERROR. Can't copy file. Permission denied.");
                return;
            }

            reader = new BufferedReader(new FileReader(hex));
            if(ref!=null) updateValues();
            if(serialPort==null) serialPort = new SerialPort(port);
            
            serialPort.openPort();                                                          //Open serial port
            serialPort.setParams(baudRate, dataBits, stopBits, parity);                     //Set params
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;  //Prepare mask
            serialPort.setEventsMask(mask);                                                 //Set mask
            serialPort.addEventListener(new SerialPortReader());   



        } catch (SerialPortException ex) {
            System.out.println(ex);
            ref.setBarConfig(Color.RED, null, null, null,StaticStrings.DEFBAR_PROG_TOPCOLOR, "ERROR. Serial port not available.");
            disconnect();
            ref.enableComponents();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Yar.class.getName()).log(Level.SEVERE, null, ex);
            ref.setBarConfig(Color.RED, null, null, null, StaticStrings.DEFBAR_PROG_TOPCOLOR, "ERROR. File not found.");
            ref.enableComponents();
            disconnect();
        } catch (IOException ex) {
            Logger.getLogger(Yar.class.getName()).log(Level.SEVERE, null, ex);
            ref.setBarConfig(Color.RED, null, null, null, StaticStrings.DEFBAR_PROG_TOPCOLOR, "ERROR. File not found.");
            ref.enableComponents();
            disconnect();
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
            reader.close();
        } catch (SerialPortException | IOException ex) {
            Logger.getLogger(Yar.class.getName()).log(Level.SEVERE, null, ex);
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
                    
                    if(!begin){
                        ref.setDisconnect(false);
                        ref.setBarConfig(Color.WHITE, Color.BLACK, Color.RED, Color.RED, Color.BLACK, "FLASHING. DO NOT DISCONNECT!!!");
                        if(ref.isSound()) SoundTools.playPirate();
                        begin = true;
                        try {
                            ref.setBarConfig(null,null,null,null,null, "FLASHING. DO NOT DISCONNECT!!! Starting in 3.");
                            Thread.sleep(1000);
                            ref.setBarConfig(null,null,null,null,null, "FLASHING. DO NOT DISCONNECT!!! Starting in 2..");
                            Thread.sleep(1000);
                            ref.setBarConfig(null,null,null,null,null, "FLASHING. DO NOT DISCONNECT!!! Starting in 1...");
                            Thread.sleep(1000);
                            ref.setBarConfig(null,null,null,null,null, "FLASHING. DO NOT DISCONNECT!!! Establishing connection");
                            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Yar.class.getName()).log(Level.SEVERE, null, ex);
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
                        
                        else if(rx.contains("#") && !connected ){
                            serialPort.writeBytes("\r".getBytes());
                            Thread.sleep(lineDelay);
                            connected = true;
                        } 
                        
                        else if(rx.contains("ayuda") && !ayuda ){
                            serialPort.writeBytes("c\r".getBytes());
                            Thread.sleep(lineDelay);
                            ayuda = true;
                        }
                        
                        else if (rx.contains("Seguro? [S/N]") && !really) {
                            ref.setBarConfig(null,null,null,null,null, "FLASHING. DO NOT DISCONNECT!!! Erasing data blocks...");
                            serialPort.writeBytes("s\r".getBytes());
                            Thread.sleep(lineDelay);
                            really = true;
                        }

                        else if (rx.contains("Preparado") && !preparado) {
                            
                            try {
                                float size = hex.length();
                                float val = 0;
                                //Files f = new Files(hex);
                                System.out.println("size: "+size);
                                ref.setBarConfig(null,null,null,null,null, "FLASHING. DO NOT DISCONNECT!!! Writing...");
                                while ((tmp = reader.readLine()) != null) {
                                    serialPort.writeBytes(tmp.getBytes());
                                    serialPort.writeBytes("\r".getBytes());
                                    
                                    System.out.print("\n>"+tmp+"<\n");
                                    ref.setBarValue(val+=tmp.length(), size);
                                    System.out.println("line length: "+tmp.length());
                                    
                                    Thread.sleep(50);
                                }
                                    serialPort.writeBytes("\r".getBytes());
                                    Thread.sleep(lineDelay);
                                    System.out.println("END");
                                    ref.setBarValue(size, size);
                                    preparado = true;
                                    reader.close();
                                    
                            } catch (IOException ex) {
                                Logger.getLogger(Yar.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        
                        else if( rx.contains("PIC>") && preparado && !end ){
                            end = true;
                            serialPort.writeBytes("s\r".getBytes());
                            Thread.sleep(lineDelay);
                            System.out.println("End, sent.");
                            disconnect();
                            ref.setBarConfig(Color.BLACK,StaticStrings.DEFBAR_BOTCOLOR,StaticStrings.DEFBAR_TOPCOLOR
                                    ,StaticStrings.DEFBAR_PROG_BOTCOLOR,Color.ORANGE,
                                    "YARRR! \"Enjoy your visit to the depths of the sea, landlubber!\" ");
                            ref.setDisconnect(true);
                            ref.enableComponents();
                        }

                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                            ref.setBarConfig(Color.RED,StaticStrings.DEFBAR_BOTCOLOR,StaticStrings.DEFBAR_TOPCOLOR
                                    ,Color.BLACK,Color.BLACK,
                                    "ERROR. Problem during connection");
                        ref.enableComponents();
                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Yar.class.getName()).log(Level.SEVERE, null, ex);
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
    
    class SerialPortReader_old implements SerialPortEventListener {

        String acum = "";
        Boolean c = false;
      
        
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {//If data is available
                if (event.getEventValue() > 0) {//Check bytes count in the input buffer
                    try {
                        byte buffer[] = serialPort.readBytes(event.getEventValue());
                        acum += new String(buffer);
                        System.out.print(new String(buffer));

                        if (acum.contains("Seguro? [S/N]")) {
                            serialPort.writeBytes("s\r".getBytes());
                            acum = "";
                        } else if (acum.contains("PIC>")) {
                            if (!c) {
                                serialPort.writeBytes("\r".getBytes());
                                Thread.sleep(lineDelay);
                                serialPort.writeBytes("c\r".getBytes());
                                Thread.sleep(lineDelay);
                                acum = "";
                                c = true;
                            } else {
                                serialPort.writeBytes("s\r".getBytes());
                                Thread.sleep(lineDelay);
                                serialPort.closePort();
                                reader.close();
                            }
                        } else if (acum.contains("Preparado")) {
                            serialPort.writeBytes(reader.readLine().getBytes());
                            serialPort.writeBytes("\r".getBytes());
                            Thread.sleep(lineDelay);
                            acum = "";
                        } else if (acum.contains("#") || acum.contains("*")) {
                            if (reader.ready()) {
                                serialPort.writeBytes(reader.readLine().getBytes());
                                serialPort.writeBytes("\r".getBytes());
                            } else {
                                serialPort.writeBytes("\r".getBytes());
                            }
                            Thread.sleep(lineDelay);
                            acum = "";
                        }
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
        
               
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames)
            System.out.println(portName);
        

        
        
        
//        SerialPort serialPort = new SerialPort("COM4");
//        try {
//            serialPort.openPort();//Open serial port
//            serialPort.setParams(SerialPort.BAUDRATE_9600, 
//                                 SerialPort.DATABITS_8,
//                                 SerialPort.STOPBITS_1,
//                                 SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
//            byte[] buffer;// = serialPort.readBytes(15);//Read 10 bytes from serial port
//            String st;
//            
//            
//            while( (buffer = serialPort.readBytes(1)).length > 0  ){
//                System.out.print(new String(buffer));
//            }
//            System.out.println("end");
//            
//            serialPort.closePort();//Close serial port
//        }
//        catch (SerialPortException ex) {
//            System.out.println(ex);
//        }
//        
    }
    
}
