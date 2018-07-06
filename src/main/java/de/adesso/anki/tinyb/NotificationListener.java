package de.adesso.anki.tinyb;

import javax.xml.bind.DatatypeConverter;

import de.adesso.anki.AnkiConnector;
import de.adesso.anki.Vehicle;
import de.adesso.anki.messages.Message;
import tinyb.BluetoothNotification;

public class NotificationListener implements BluetoothNotification<byte[]> {

    private Vehicle vehicle;
    private AnkiConnector anki;

    public NotificationListener(Vehicle vehicle, AnkiConnector anki) {
        this.vehicle = vehicle;
        this.anki = anki;
    }

    @Override
    public void run(byte[] arg0) {
        Message message = Message.parse(DatatypeConverter.printHexBinary(arg0));
        System.out.println("Notification: " + message);
    }

}
