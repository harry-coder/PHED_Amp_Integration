package com.fedco.mbc.felhr.deviceids;

public class XdcVcpIds {
    private static final ConcreteDevice[] xdcvcpDevices = new ConcreteDevice[]{new ConcreteDevice(9805, 562), new ConcreteDevice(9805, 288)};

    private static class ConcreteDevice {
        public int productId;
        public int vendorId;

        public ConcreteDevice(int vendorId, int productId) {
            this.vendorId = vendorId;
            this.productId = productId;
        }
    }

    public static boolean isDeviceSupported(int vendorId, int productId) {
        int i = 0;
        while (i <= xdcvcpDevices.length - 1) {
            if (xdcvcpDevices[i].vendorId == vendorId && xdcvcpDevices[i].productId == productId) {
                return true;
            }
            i++;
        }
        return false;
    }
}
