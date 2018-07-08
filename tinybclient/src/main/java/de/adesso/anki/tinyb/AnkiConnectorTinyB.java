package de.adesso.anki.tinyb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.adesso.anki.AnkiConnector;
import de.adesso.anki.MessageListener;
import de.adesso.anki.Vehicle;
import de.adesso.anki.messages.Message;
import de.adesso.anki.messages.SdkModeMessage;
import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

public class AnkiConnectorTinyB implements AnkiConnector {

    private static final String ANKI_UUID = "be15beef-6186-407e-8381-0bd89c4d8df4";
    private static final short ANKI_MANUFACTURER_DATA_ID = -4162;

    private Map<String, BluetoothDevice> devices = new HashMap<>();
    private Map<String, Vehicle> vehicles = new HashMap<>();
    private Multimap<Vehicle, MessageListener<?>> messageListeners = ArrayListMultimap.create();
    private BluetoothManager manager;

    public AnkiConnectorTinyB() {
        this.manager = BluetoothManager.getBluetoothManager();
        reset();
    }

    private void reset() {
        List<BluetoothDevice> existing = this.manager.getDevices();
        for (BluetoothDevice dev : existing) {
            dev.remove();
        }
        devices.clear();
        vehicles.clear();
    }

    @Override
    public List<Vehicle> findVehicles() {
        List<Vehicle> foundVehicles = new ArrayList<>();
        try {
            this.devices = discoverAnkiDevices(3);
            for (BluetoothDevice dev : this.devices.values()) {
                String address = dev.getAddress();
                ByteBuffer buffer = ByteBuffer.allocate(8);
                buffer.putShort(ANKI_MANUFACTURER_DATA_ID);
                buffer.put(dev.getManufacturerData().get(ANKI_MANUFACTURER_DATA_ID));

                String manufacturerData = DatatypeConverter.printHexBinary(buffer.array());
                String localName = DatatypeConverter.printHexBinary(dev.getName().getBytes());

                Vehicle vehicle = new Vehicle(this, address, manufacturerData, localName);
                vehicle.connect();
                SdkModeMessage sdk = new SdkModeMessage(true, (byte) 1);
                vehicle.sendMessage(sdk);
                vehicle.disconnect();
                vehicles.put(address, vehicle);
                foundVehicles.add(vehicle);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return foundVehicles;
    }

    private Map<String, BluetoothDevice> discoverAnkiDevices(int loops) throws InterruptedException {

        boolean started = manager.startDiscovery();
        System.out.println("Discovery started: " + started);
        try {
            for (int i = 0; i < loops; i++) {
                Thread.sleep(2000);
                // System.out.print(".");
                Map<String, BluetoothDevice> newDevices = findAnkiDevices();
                for (String address : newDevices.keySet()) {
                    BluetoothDevice newDev = newDevices.get(address);
                    if (!devices.containsKey(address)) {
                        System.out.println("Anki: " + address);
                        devices.put(address, newDev);
                    }
                }

            }
            for (BluetoothDevice ankiDev : this.devices.values()) {
                System.out.println("Connecting: " + ankiDev.getAddress());
                connect(ankiDev);
                BluetoothGattService ankiService = getAnkiService(ankiDev);
                if (ankiService == null) {
                    throw new RuntimeException("Could not find Anki Service: " + ankiDev.getAddress());
                }
                disconnect(ankiDev);

            }
        } finally {
            if (started) {
                manager.stopDiscovery();
            }
            System.out.println("Discovery stopped");
        }
        return devices;
    }

    private Map<String, BluetoothDevice> findAnkiDevices() {
        Map<String, BluetoothDevice> devices = new HashMap<>();
        List<BluetoothDevice> list = manager.getDevices();
        for (BluetoothDevice dev : list) {
            String[] uuids = dev.getUUIDs();
            for (String u : uuids) {
                if (ANKI_UUID.equals(u)) {
                    devices.put(dev.getAddress(), dev);
                    break;
                }
            }
        }
        return devices;
    }

    private void displayAnkiDevice(BluetoothDevice dev) throws InterruptedException {
        System.out.println("Device: " + dev.getAddress());
        System.out.println("\tName: " + dev.getName());
        System.out.println("\tAlias: " + dev.getAlias());

        // boolean connected = dev.connect();
        // System.out.println("Connected: " + connected);
        try {

            Map<Short, byte[]> mdata = dev.getManufacturerData();
            for (Map.Entry<Short, byte[]> d : mdata.entrySet()) {
                byte[] data = d.getValue();
                System.out.println("\t\tManufacturer Data: " + data.length);

            }
            // System.out.print("...looking for services");
            // List<BluetoothGattService> services = dev.getServices();
            // long time = 0;
            // while (time < 10000 && !dev.getServicesResolved()) {
            // Thread.sleep(100);
            // System.out.print(".");
            // time = time + 100;
            // }
            // System.out.println("");
            // Map<String, byte[]> sdata = dev.getServiceData();
            // for (Map.Entry<String, byte[]> d : sdata.entrySet()) {
            // System.out.println(
            // "\t\tService Data: " + d.getKey() + " = " +
            // DatatypeConverter.printHexBinary(d.getValue()));
            // }
            // for (BluetoothGattService service : services) {
            // String service_uuid = service.getUUID();
            // if (ANKI_UUID.equals(service_uuid)) {
            // System.out.println("\t\tService: " + service_uuid);
            // List<BluetoothGattCharacteristic> chars = service.getCharacteristics();
            // for (BluetoothGattCharacteristic bchar : chars) {
            // boolean isWrite = false;
            // System.out.println("\t\t\tChar: " + bchar.getUUID());
            // String[] flags = bchar.getFlags();
            // for (String flag : flags) {
            // System.out.println("\t\t\t\tFlag: " + flag);
            // isWrite = isWrite || "write".equals(flag);
            // }
            // if (isWrite) {
            // // boolean written = bchar.writeValue(toHex().getBytes());
            // // System.out.println("\t\t\tWritten: " + written);
            // byte[] data = bchar.readValue();
            // System.out.println("\t\t\tData: " + DatatypeConverter.printHexBinary(data));
            // } else {
            // byte[] data = bchar.readValue();
            // System.out.println("\t\t\tData: " + DatatypeConverter.printHexBinary(data));
            // }
            // // List<BluetoothGattDescriptor> descs = bchar.getDescriptors();
            // // for(BluetoothGattDescriptor desc:descs) {
            // // System.out.println("\t\t\t\tDesc: " + desc.getUUID());
            // // System.out.println("\t\t\t\t\tValue: " +
            // // DatatypeConverter.printHexBinary(desc.readValue()));
            // // }
            // }
            // }
            // }
        } finally {
            // if (connected) {
            // dev.disconnect();
            // }
            System.out.println("...done...");
        }
    }

    @Override
    public void connect(Vehicle vehicle) throws InterruptedException {
        BluetoothDevice device = getDevice(vehicle);
        connect(device);
        BluetoothGattService ankiService = getAnkiService(device);
        BluetoothGattCharacteristic readChar = getReadChar(ankiService);
        readChar.enableValueNotifications(new BTNotificationListener(vehicle, this));
    }

    private void connect(BluetoothDevice device) throws InterruptedException {
        long time = 0;
        try {
            while (!device.getConnected() && time < 15000) {// this might be overkill
                boolean connected = device.connect();
                if (!connected) {
                    Thread.sleep(100);
                    time = time + 100;
                }
            }

        } catch (BluetoothException ble) {
            ble.printStackTrace();
        }
        if (!device.getConnected()) {
            System.out.println("CONNECTION FAILED!");
        }
    }

    private BluetoothDevice getDevice(Vehicle vehicle) {
        String address = vehicle.getAddress();
        BluetoothDevice device = this.devices.get(address);
        return device;
    }

    @Override
    public void sendMessage(Vehicle vehicle, Message message) {
        BluetoothDevice device = getDevice(vehicle);
        BluetoothGattService ankiService = getAnkiService(device);
        BluetoothGattCharacteristic writeChar = getWriteChar(ankiService);

        writeChar.writeValue(DatatypeConverter.parseHexBinary(message.toHex()));
    }

    private BluetoothGattService getAnkiService(BluetoothDevice device) {
        BluetoothGattService ankiService = null;
        try {
            long time = 0;
            while (time < 10000 && !device.getServicesResolved()) {
                Thread.sleep(100);
                // System.out.print("s");
                time = time + 100;
            }

            List<BluetoothGattService> services = device.getServices();
            for (BluetoothGattService service : services) {
                String service_uuid = service.getUUID();

                if (ANKI_UUID.equals(service_uuid)) {
                    ankiService = service;
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ankiService;
    }

    private BluetoothGattCharacteristic getWriteChar(BluetoothGattService ankiService) {
        BluetoothGattCharacteristic writeChar = null;
        List<BluetoothGattCharacteristic> chars = ankiService.getCharacteristics();
        for (BluetoothGattCharacteristic bchar : chars) {
            boolean isWrite = false;
            // System.out.println("\t\t\tChar: " + bchar.getUUID());
            String[] flags = bchar.getFlags();
            for (String flag : flags) {
                // System.out.println("\t\t\t\tFlag: " + flag);
                isWrite = "write".equals(flag);
                if (isWrite) {
                    writeChar = bchar;
                    break;
                }
            }
            if (isWrite) {
                break;
            }
        }
        return writeChar;
    }

    private BluetoothGattCharacteristic getReadChar(BluetoothGattService ankiService) {
        BluetoothGattCharacteristic readChar = null;
        List<BluetoothGattCharacteristic> chars = ankiService.getCharacteristics();
        for (BluetoothGattCharacteristic bchar : chars) {
            boolean isWrite = false;
            // System.out.println("\t\t\tChar: " + bchar.getUUID());
            String[] flags = bchar.getFlags();
            for (String flag : flags) {
                // System.out.println("\t\t\t\tFlag: " + flag);
                isWrite = "write".equals(flag);
                if (isWrite) {
                    break;
                }
            }
            if (!isWrite) {
                readChar = bchar;
                break;
            }
        }
        return readChar;
    }

    public void addMessageListener(Vehicle vehicle, MessageListener<? extends Message> listener) {
        messageListeners.put(vehicle, listener);
    }

    public void removeMessageListener(Vehicle vehicle, MessageListener<? extends Message> listener) {
        messageListeners.remove(vehicle, listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fireMessageReceived(Vehicle vehicle, Message message) {
        for (MessageListener l : messageListeners.get(vehicle)) {
            l.messageReceived(message);
        }
    }

    @Override
    public void disconnect(Vehicle vehicle) {
        BluetoothDevice device = getDevice(vehicle);
        BluetoothGattService ankiService = getAnkiService(device);
        BluetoothGattCharacteristic readChar = getReadChar(ankiService);
        if (readChar.getNotifying()) {
            readChar.disableValueNotifications();
        }
        disconnect(device);
    }

    private void disconnect(BluetoothDevice device) {
        long time = 0;
        try {
            while (device.getConnected() && time < 5000) {// this might be overkill
                boolean disconnected = device.disconnect();
                if (!disconnected) {
                    Thread.sleep(100);
                    time = time + 100;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        for (Vehicle vehicle : vehicles.values()) {
            disconnect(vehicle);
        }
        reset();
    }

    @Override
    public AnkiConnector duplicate() throws IOException {
        return this;
    }

}
