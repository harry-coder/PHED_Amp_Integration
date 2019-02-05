package com.fedco.mbc.felhr.deviceids;

public class CH34xIds {
    private static final ConcreteDevice[] ch34xDevices = new ConcreteDevice[]{new ConcreteDevice(17224, 21795), new ConcreteDevice(6790, 29987), new ConcreteDevice(6790, 21795)};

    private static class ConcreteDevice {
        public int productId;
        public int vendorId;

        public ConcreteDevice(int vendorId, int productId) {
            this.vendorId = vendorId;
            this.productId = productId;
        }
    }

    private CH34xIds() {
    }

    public static boolean isDeviceSupported(int vendorId, int productId) {
        int i = 0;
        while (i <= ch34xDevices.length - 1) {
            if (ch34xDevices[i].vendorId == vendorId && ch34xDevices[i].productId == productId) {
                return true;
            }
            i++;
        }
        return false;
    }
}
