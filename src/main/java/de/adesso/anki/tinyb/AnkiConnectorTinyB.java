package de.adesso.anki.tinyb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import de.adesso.anki.AnkiConnector;
import de.adesso.anki.MessageListener;
import de.adesso.anki.Vehicle;
import de.adesso.anki.messages.Message;
import tinyb.BluetoothDevice;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

public class AnkiConnectorTinyB implements AnkiConnector {

    private static final String ANKI_UUID = "be15beef-6186-407e-8381-0bd89c4d8df4";
    private static final Short ANKI_MANUFACTURER_DATA_ID = -4162;

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
    }

    @Override
    public List<Vehicle> findVehicles() {
        List<Vehicle> foundVehicles = new ArrayList<>();
        try {
            Map<String, BluetoothDevice> ankiDevices = discoverAnkiDevices(10);
            for (BluetoothDevice dev : ankiDevices.values()) {
                String address = dev.getAddress();
                String manufacturerData = DatatypeConverter
                        .printHexBinary(dev.getManufacturerData().get(ANKI_MANUFACTURER_DATA_ID));
                String localName = dev.getName();

                foundVehicles.add(new Vehicle(this, address, manufacturerData, localName));
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return foundVehicles;
    }

    private Map<String, BluetoothDevice> discoverAnkiDevices(int loops) throws InterruptedException {

        // remove old devices
        List<BluetoothDevice> existing = this.manager.getDevices();
        for (BluetoothDevice dev : existing) {
            dev.remove();
        }

        Map<String, BluetoothDevice> devices = new HashMap<>();

        boolean started = manager.startDiscovery();
        System.out.println("Discovery started: " + started);
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.print(".");
                Map<String, BluetoothDevice> newDevices = findAnkiDevices();
                for (String address : newDevices.keySet()) {
                    BluetoothDevice newDev = newDevices.get(address);
                    System.out.println("");
                    System.out.println("New: " + address + " = " + newDev.getUUIDs()[0]);
                    if (!devices.containsKey(address)) {
                        displayAnkiDevice(newDev);
                        devices.put(address, newDev);
                    }
                }

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
            System.out.println("Addr: " + dev.getAddress());
            String[] uuids = dev.getUUIDs();
            for (String u : uuids) {
                System.out.println("UUID: " + u);
                if (ANKI_UUID.equals(u)) {
                    devices.put(dev.getAddress(), dev);
                    break;
                }
            }
        }
        return devices;
    }

    public void displayAnkiDevice(BluetoothDevice dev) throws InterruptedException {
        System.out.println("Device: " + dev.getAddress());
        System.out.println("\tName: " + dev.getName());
        System.out.println("\tAlias: " + dev.getAlias());

        boolean connected = dev.connect();
        System.out.println("Connected: " + connected);
        try {

            Map<Short, byte[]> mdata = dev.getManufacturerData();
            for (Map.Entry<Short, byte[]> d : mdata.entrySet()) {
                System.out.println("\t\tManufacturer Data: " + d.getKey() + " = "
                        + DatatypeConverter.printHexBinary(d.getValue()));
            }
            System.out.print("...looking for services");
            List<BluetoothGattService> services = dev.getServices();
            long time = 0;
            while (time < 10000 && !dev.getServicesResolved()) {
                Thread.sleep(100);
                System.out.print(".");
                time = time + 100;
            }
            System.out.println("");
            Map<String, byte[]> sdata = dev.getServiceData();
            for (Map.Entry<String, byte[]> d : sdata.entrySet()) {
                System.out.println(
                        "\t\tService Data: " + d.getKey() + " = " + DatatypeConverter.printHexBinary(d.getValue()));
            }
            for (BluetoothGattService service : services) {
                String service_uuid = service.getUUID();
                if (ANKI_UUID.equals(service_uuid)) {
                    System.out.println("\t\tService: " + service_uuid);
                    List<BluetoothGattCharacteristic> chars = service.getCharacteristics();
                    for (BluetoothGattCharacteristic bchar : chars) {
                        boolean isWrite = false;
                        System.out.println("\t\t\tChar: " + bchar.getUUID());
                        String[] flags = bchar.getFlags();
                        for (String flag : flags) {
                            System.out.println("\t\t\t\tFlag: " + flag);
                            isWrite = isWrite || "write".equals(flag);
                        }
                        if (isWrite) {
                            // boolean written = bchar.writeValue(toHex().getBytes());
                            // System.out.println("\t\t\tWritten: " + written);
                            byte[] data = bchar.readValue();
                            System.out.println("\t\t\tData: " + DatatypeConverter.printHexBinary(data));
                        } else {
                            byte[] data = bchar.readValue();
                            System.out.println("\t\t\tData: " + DatatypeConverter.printHexBinary(data));
                        }
                        // List<BluetoothGattDescriptor> descs = bchar.getDescriptors();
                        // for(BluetoothGattDescriptor desc:descs) {
                        // System.out.println("\t\t\t\tDesc: " + desc.getUUID());
                        // System.out.println("\t\t\t\t\tValue: " +
                        // DatatypeConverter.printHexBinary(desc.readValue()));
                        // }
                    }
                }
            }
        } finally {
            if (connected) {
                dev.disconnect();
            }
            System.out.println("...done...");
        }
    }

    @Override
    public void connect(Vehicle vehicle) throws InterruptedException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendMessage(Vehicle vehicle, Message message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addMessageListener(Vehicle vehicle, MessageListener<? extends Message> listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeMessageListener(Vehicle vehicle, MessageListener<? extends Message> listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fireMessageReceived(Vehicle vehicle, Message message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnect(Vehicle vehicle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public AnkiConnector duplicate() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
