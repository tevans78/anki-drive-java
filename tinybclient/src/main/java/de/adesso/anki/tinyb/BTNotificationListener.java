package de.adesso.anki.tinyb;

import javax.xml.bind.DatatypeConverter;

import de.adesso.anki.AnkiConnector;
import de.adesso.anki.Vehicle;
import de.adesso.anki.messages.Message;
import tinyb.BluetoothNotification;

public class BTNotificationListener implements BluetoothNotification<byte[]> {

    private Vehicle vehicle;
    private AnkiConnector ankiConnector;

    public BTNotificationListener(Vehicle vehicle, AnkiConnector ankiConnector) {
        this.vehicle = vehicle;
        this.ankiConnector = ankiConnector;
    }

    @Override
    public void run(byte[] arg0) {
        System.out.println("notification: +" + vehicle.getAddress());
        Message message = Message.parse(DatatypeConverter.printHexBinary(arg0));
        System.out.println(message.getClass());
        ankiConnector.fireMessageReceived(vehicle, message);
    }

}
