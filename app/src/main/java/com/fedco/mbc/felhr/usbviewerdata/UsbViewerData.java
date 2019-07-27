package com.fedco.mbc.felhr.usbviewerdata;

import com.fedco.mbc.felhr.droidterm.utilities.HexData;
import java.util.ArrayList;
import java.util.List;

public class UsbViewerData {
    private Interface currentInterface;
    public List<Interface> interfaces = new ArrayList();
    public String productId;
    public String productName;
    public String vendorId;
    public String vendorName;

    public class Endpoint {
        public String attribute;
        public String interval;
        public String packSize;
        public String type;

        public Endpoint(String attribute, String type, String interval, String packSize) {
            this.attribute = attribute;
            this.type = type;
            this.interval = interval;
            this.packSize = packSize;
        }
    }

    public class Interface {
        public String classInterface;
        public List<Endpoint> endpoints = new ArrayList();

        public Interface(String classInterface) {
            this.classInterface = classInterface;
        }

        public String getClassInterface() {
            return this.classInterface;
        }
    }

    public static class UsbViewerDataUtils {
        private UsbViewerDataUtils() {
        }

        public static String getInterfaceClass(int interfaceClass) {
            if (interfaceClass == 254) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Application Specific USB Class";
            }
            if (interfaceClass == 1) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Audio Class";
            }
            if (interfaceClass == 10) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Communications Device Class (CDC)";
            }
            if (interfaceClass == 2) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Communication Class";
            }
            if (interfaceClass == 13) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Content Security Devices Class";
            }
            if (interfaceClass == 11) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Content Smart Card Devices Class";
            }
            if (interfaceClass == 3) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Human Interface Devices Class";
            }
            if (interfaceClass == 9) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Usb Hubs Class";
            }
            if (interfaceClass == 8) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Mass Storage Class";
            }
            if (interfaceClass == 239) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Wireless Miscellaneous Devices";
            }
            if (interfaceClass == 5) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Physical Devices Class";
            }
            if (interfaceClass == 7) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Printer Devices Class";
            }
            if (interfaceClass == 6) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Digital Cameras Class";
            }
            if (interfaceClass == 255) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Vendor Specific Class";
            }
            if (interfaceClass == 14) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Video Devices Class";
            }
            if (interfaceClass == 224) {
                return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Wireless Controller Devices Class";
            }
            return "0x" + HexData.hex4digits(Integer.toHexString(interfaceClass)) + " Class";
        }

        public static String getAttributes(int attributes, int direction) {
            if (direction == 128) {
                return "0x" + HexData.hex4digits(Integer.toHexString(attributes)) + " IN";
            }
            if (direction == 0) {
                return "0x" + HexData.hex4digits(Integer.toHexString(attributes)) + " OUT";
            }
            return "0x" + HexData.hex4digits(Integer.toHexString(attributes));
        }

        public static String getType(int type) {
            if (type == 0) {
                return "0x" + HexData.hex4digits(Integer.toHexString(type)) + " CONTROL";
            }
            if (type == 2) {
                return "0x" + HexData.hex4digits(Integer.toHexString(type)) + " BULK";
            }
            if (type == 1) {
                return "0x" + HexData.hex4digits(Integer.toHexString(type)) + " ISO";
            }
            if (type == 3) {
                return "0x" + HexData.hex4digits(Integer.toHexString(type)) + " INT";
            }
            return "0x" + HexData.hex4digits(Integer.toHexString(type));
        }

        public static String getInterval(int interval) {
            return "0x" + HexData.hex4digits(Integer.toHexString(interval));
        }

        public static String getPackSize(int packSize) {
            return "0x" + HexData.hex4digits(Integer.toHexString(packSize)) + " (" + packSize + ")";
        }
    }

    public void addInterface(String classInterface) {
        if (this.currentInterface != null) {
            this.interfaces.add(this.currentInterface);
        }
        this.currentInterface = new Interface(classInterface);
    }

    public void addEndpoint(String attribute, String type, String interval, String packSize) {
        this.currentInterface.endpoints.add(new Endpoint(attribute, type, interval, packSize));
    }

    public void addLastInterface() {
        this.interfaces.add(this.currentInterface);
    }
}
