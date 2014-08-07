package com.example.practica6;

import java.io.Serializable;

public class Device implements Serializable{

   private static final long serialVersionUID = 1L;

   private String deviceName;
   private String deviceMacAddress;

       // getters & setters....

   public String getDevice() {
	   
       return deviceName + "_" + deviceMacAddress;
   }  
   
   public void setName(String name)
   {
	   deviceName = name;
   }
   
   public String getName()
   {
	   return deviceName;
   }
   
   public void setMacAddress(String macAddress)
   {
	   deviceMacAddress = macAddress;
   }
   
   public String getMacAddress()
   {
	   return deviceMacAddress;
   }
   
}