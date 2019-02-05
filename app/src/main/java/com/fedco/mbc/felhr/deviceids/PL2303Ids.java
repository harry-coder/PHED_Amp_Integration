package com.fedco.mbc.felhr.deviceids;

public class PL2303Ids {
    private static final ConcreteDevice[] pl2303Devices = new ConcreteDevice[]{new ConcreteDevice(1189, 16423), new ConcreteDevice(1659, 8963), new ConcreteDevice(1659, 1211), new ConcreteDevice(1659, 4660), new ConcreteDevice(1659, 43680), new ConcreteDevice(1659, 43682), new ConcreteDevice(1659, 1553), new ConcreteDevice(1659, 1554), new ConcreteDevice(1659, 1545), new ConcreteDevice(1659, 13082), new ConcreteDevice(1659, 775), new ConcreteDevice(1367, 8200), new ConcreteDevice(1351, 8200), new ConcreteDevice(1211, 2563), new ConcreteDevice(1211, 2574), new ConcreteDevice(1390, 20483), new ConcreteDevice(1390, 20484), new ConcreteDevice(3770, 4224), new ConcreteDevice(3770, 8320), new ConcreteDevice(3575, 1568), new ConcreteDevice(1412, 45056), new ConcreteDevice(9336, 8200), new ConcreteDevice(5203, 16422), new ConcreteDevice(1841, 1320), new ConcreteDevice(24969, 8296), new ConcreteDevice(4599, 735), new ConcreteDevice(1256, 32769), new ConcreteDevice(4597, 1), new ConcreteDevice(4597, 3), new ConcreteDevice(4597, 4), new ConcreteDevice(4597, 5), new ConcreteDevice(1861, 1), new ConcreteDevice(1931, 4660), new ConcreteDevice(4277, 44144), new ConcreteDevice(1947, 39), new ConcreteDevice(1043, 8449), new ConcreteDevice(3669, 4363), new ConcreteDevice(1841, 8195), new ConcreteDevice(1293, 599), new ConcreteDevice(1423, 38688), new ConcreteDevice(4598, 8193), new ConcreteDevice(1962, 42), new ConcreteDevice(1453, 4026), new ConcreteDevice(21362, 8963), new ConcreteDevice(1008, 2873), new ConcreteDevice(1008, 12601), new ConcreteDevice(1008, 12857), new ConcreteDevice(1008, 13604), new ConcreteDevice(1208, 1313), new ConcreteDevice(1208, 1314), new ConcreteDevice(1356, 1079), new ConcreteDevice(4525, 1), new ConcreteDevice(2915, 25904), new ConcreteDevice(2956, 8963)};

    private static class ConcreteDevice {
        public int productId;
        public int vendorId;

        public ConcreteDevice(int vendorId, int productId) {
            this.vendorId = vendorId;
            this.productId = productId;
        }
    }

    private PL2303Ids() {
    }

    public static boolean isDeviceSupported(int vendorId, int productId) {
        int i = 0;
        while (i <= pl2303Devices.length - 1) {
            if (pl2303Devices[i].vendorId == vendorId && pl2303Devices[i].productId == productId) {
                return true;
            }
            i++;
        }
        return false;
    }
}
