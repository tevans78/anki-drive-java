package de.adesso.anki;

import java.io.IOException;
import java.util.List;

import de.adesso.anki.messages.Message;

/**
 * Interface to a Bluetooth LE connection
 */
public interface AnkiConnector {

    public List<Vehicle> findVehicles();

    public void connect(Vehicle vehicle) throws InterruptedException;

    public void sendMessage(Vehicle vehicle, Message message);

    public void addMessageListener(Vehicle vehicle, MessageListener<? extends Message> listener);

    public void removeMessageListener(Vehicle vehicle, MessageListener<? extends Message> listener);

    public void fireMessageReceived(Vehicle vehicle, Message message);

    public void disconnect(Vehicle vehicle);

    public void close();

    public AnkiConnector duplicate() throws IOException;
}
