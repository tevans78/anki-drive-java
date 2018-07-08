package de.adesso.anki.tinyb;

import java.util.List;

import de.adesso.anki.MessageListener;
import de.adesso.anki.Vehicle;
import de.adesso.anki.messages.LightsPatternMessage;
import de.adesso.anki.messages.LightsPatternMessage.LightChannel;
import de.adesso.anki.messages.LightsPatternMessage.LightConfig;
import de.adesso.anki.messages.LightsPatternMessage.LightEffect;
import de.adesso.anki.messages.Message;
import de.adesso.anki.messages.SetSpeedMessage;

public class TinyBTest implements MessageListener<Message> {

    public void test() throws InterruptedException {
        AnkiConnectorTinyB anki = new AnkiConnectorTinyB();
        try {
            List<Vehicle> vehicles = anki.findVehicles();
            for (Vehicle v : vehicles) {
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

    public static void main(String[] args) throws InterruptedException {
        new TinyBTest().test();
    }
}
