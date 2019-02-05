/*
 * To change Genus license header, choose License Headers in Project Properties.
 * To change Genus template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fedco.mbc.felhr.constant;

import android.util.Log;

import com.fedco.mbc.felhr.connectivityservices.UsbService;
import com.fedco.mbc.felhr.droidterm.utilities.HexData;

import static android.content.ContentValues.TAG;

/**
 *
 * @author CYJ
 */
public class Genus {


    private static int FindASC(int ch) {
        if ((ch >= 0) && (ch <= 9)) {
            return (int) (ch + 0x30);
        }
        if ((ch >= 10) && (ch <= 15)) {
            return (int) (ch + 0x37);
        }
        return  0x30;
    }

    private static int FindBCD(int ch) {
        int num = 0x30;
        int num2 = 0x37;
        int num3 = 0x57;
        if ((ch >= 0x30) && (ch <= 0x39)) {
            return (int) (ch - num);
        }
        if ((ch >= 0x41) && (ch <= 70)) {
            return (int) (ch - num2);
        }
        if ((ch >= 0x61) && (ch <= 0x66)) {
            return (int) (ch - num3);
        }
        return 0x00;
    }

    private static int calculate_checksum(int[] data, int startbyte, int endbyte) {
        int index = 0;
        int num2 = 0;
        for (index = startbyte; index <= endbyte; index++) {
            num2 = (int) (num2 + (data[index]));
        }
        return num2;
    }

    public static byte[] signOnMeter1(String token1) {
        byte[] usbReceivedRawData = UsbService.rawData;
        int num5 = 0;
        int ch = 0;
        int num7 = 0;
        int num = 0;
        int num2 = 3;
        int num3 = 0;
        int[] buffer8 = new int[8];
        int[] buffer6 = buffer8;
        buffer8 = new int[8];
        int[] buffer7 = buffer8;
        Boolean flag;
        byte[] buffer2 = new byte[0];
        int i=0;
        byte[] data = new byte[]{0x01, 0x54, 0x32, 0x02, 0x28, 0, 0, 0, 0, 0, 0, 0, 0, 0x29, 0x03, 0};
        int index = 0;
        int[] data1 = new int[16];


        Log.e(TAG,"GENUS BYTE-HEX 1 : "+HexData.hexToString(usbReceivedRawData));

        for (index = 1; (index < HexData.hexToString(usbReceivedRawData).length()); index++) {

            Log.e(TAG,"&&&&&&&&&&  : "+HexData.hexToString(usbReceivedRawData).charAt(index));
//            buffer2[index] = HexData.hexToString(usbReceivedRawData)[index];
            //  Console.WriteLine("Token1 (Buffer6): " + String.format("0x%20x", buffer6[index]));
        }



        for(i =0 ;i < 16; i++)
            data1[i]= unsignedByteToInt(data[i]);
        buffer2 = new byte [16];
        //Console.WriteLine(TAG,"GENUS Response: "+token);
        //buffer2 = new int[]{0x31, 0x36, 0x21, 0x0c, 0x67, 0xef, 0xbf, 0xbd};
//        buffer2 = new byte[]{ (byte)0x31,(byte)0x36, (byte)0x51, (byte)0x22, (byte)0x67, (byte)0xdc, (byte)0x03, (byte)0x41 };

        Log.e(TAG,"GENUS BYTE-HEX 2 : "+bytesToHex(usbReceivedRawData));
//        Log.e(TAG,"GENUS Response 2: "+ HexData.stringTobytes(bytesToHex(usbReceivedRawData)));




//        buffer2=token1.getBytes();
        buffer2=usbReceivedRawData;


        int[] buf2 = new int[16];
        for(i =0 ;i < 8; i++)
            buf2[i]= unsignedByteToInt(buffer2[i]);
        // buffer2 = token.getBytes();

        // Array.
        index = 0;
        // buffer2 = Genus.buffer;
        for (index = 0; (index < 8); index++) {
            buffer6[index] = buf2[(index)];
            //  Console.WriteLine("Token1 (Buffer6): " + String.format("0x%20x", buffer6[index]));
        }

        for (index = 0; (index < 8); index += 2) {
            ch = Genus.FindBCD(buffer6[index]);
            // System.out.println("ch : " + ch);

            num7 = ((int) ((Genus.FindBCD(buffer6[(index + 1)]) * 0x10)));
            // System.out.println("num7 : " + (num7));

            num5 = ((int) ((ch + num7)));
            // System.out.println("num5 : " + num5);

            num5 = unsignedToBytes(((int) ((num5 - 0x18))));
            // System.out.println("num5 Second : " + (num5));

            ch = ((int) ((num5 & 15)));
            // System.out.println("ch2: " + (ch));

            num7 = unsignedToBytes(((int) ((num5 >> 4))));
            // System.out.println("num7 : " + num7);

            buffer7[index] = Genus.FindASC(ch);
            // System.out.println(" buffer7[index] : " + (buffer7[index]));

            buffer7[(index + 1)] = Genus.FindASC(num7);
            // System.out.println(" buffer7[(index + 1)] : " +(buffer7[(index + 1)]));
        }

        for (index = 0; (index < 8); index++) {
            data1[(index + 5)] = buffer7[index];
            // System.out.println("token2 index" + index + 5 + ": " + unsignedToBytes(data1[index + 5]));
        }

        data1[15] = Genus.calculate_checksum(data1, 0, 14);
        // System.out.println("token2 index 15 : " + unsignedToBytes(data1[15]));
        num3 = 0;
        flag = true;
        boolean minus;
        byte result [] = new byte [16];
        for(i = 0; i<= 15; i++)
        {
            //data1[i] = unsignedToBytes(data1[i]);
            data[i] = intToBytes(data1[i]);
            // data[i] = (byte) unsignedToBytes(data[i]);
            //minus = (boolean)(data[i]  <<  1);
            //if((data[i] << 1)) data[i] =  data[i]; 
            Log.e(TAG," " +unsignedToBytes(data[i]) );
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        Log.e(TAG, "hhh"+sb.toString());

        return data;
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }

        Log.e(TAG,"HEX IN GENUS "+builder.toString());
        return builder.toString();
    }

    public static int unsignedToBytes(int a) {
        int b = a & 0xFF;
        return b;
    }
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
    public static byte intToBytes(int data){

        byte b = (byte) (data & 0xFF);
        return b;
    }

}
