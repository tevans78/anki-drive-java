package io.openliberty.anki.cdi;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import de.adesso.anki.MessageListener;
import de.adesso.anki.Vehicle;
import de.adesso.anki.messages.LightsPatternMessage;
import de.adesso.anki.messages.LightsPatternMessage.LightChannel;
import de.adesso.anki.messages.LightsPatternMessage.LightConfig;
import de.adesso.anki.messages.LightsPatternMessage.LightEffect;
import de.adesso.anki.messages.Message;
import de.adesso.anki.messages.MessageMap;
import de.adesso.anki.messages.SetSpeedMessage;
import de.adesso.anki.tinyb.AnkiConnectorTinyB;

@ApplicationScoped
public class Anki implements MessageListener<Message> {

	private AnkiConnectorTinyB anki = new AnkiConnectorTinyB();
	private Map<String,Vehicle> vehicles = new HashMap<>();

	public Anki() {
		for(Vehicle v:anki.findVehicles()) {
			vehicles.put(v.getAddress(), v);
		}
	}
	
	public void test() throws InterruptedException {

		try {
			for (Vehicle v : vehicles.values()) {
				if (v.getAddress().equals("EE:09:1D:49:59:FC")) {
					v.connect();
					v.addMessageListener(Message.class, this);

					for (int i = 0; i < 1; i++) {
						LightsPatternMessage lights = new LightsPatternMessage();
						lights.add(new LightConfig(LightChannel.FRONT_RED, LightEffect.STROBE, 0, 1, 2));
						v.sendMessage(lights);

						SetSpeedMessage speed = new SetSpeedMessage(200, 1000);
						v.sendMessage(speed);

						Thread.sleep(5000);

						speed = new SetSpeedMessage(0, 1000);
						v.sendMessage(speed);
					}

					v.disconnect();
				}
			}

		} finally {
			anki.close();
		}
	}

	@Override
	public void messageReceived(Message message) {
		System.out.println("Message! " + message);
	}

	public List<Vehicle> listVehicles() {
		return anki.findVehicles();
	}

	public void connect(String address) {
		vehicles.get(address).connect();
	}
	
	public void disconnect(String address) {
		vehicles.get(address).disconnect();
	}

}
