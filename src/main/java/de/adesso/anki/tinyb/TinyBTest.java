package de.adesso.anki.tinyb;

import java.util.List;

import de.adesso.anki.AdvertisementData;
import de.adesso.anki.Vehicle;
import de.adesso.anki.messages.LightsPatternMessage;
import de.adesso.anki.messages.LightsPatternMessage.LightChannel;
import de.adesso.anki.messages.LightsPatternMessage.LightConfig;
import de.adesso.anki.messages.LightsPatternMessage.LightEffect;

public class TinyBTest {

    public static void main(String[] args) {
        AnkiConnectorTinyB anki = new AnkiConnectorTinyB();
        try {
            List<Vehicle> vehicles = anki.findVehicles();
            for (Vehicle v : vehicles) {
                System.out.println("===================");
                System.out.println(v.getAddress());
                System.out.println(v.getColor());
                AdvertisementData ad = v.getAdvertisement();
                System.out.println(ad.getIdentifier());
                System.out.println(ad.getModelId());
                System.out.println(ad.getProductId());
                System.out.println(ad.getModel().getColor());
                System.out.println(ad.getModel().name());

                LightsPatternMessage message = new LightsPatternMessage();
                message.add(new LightConfig(LightChannel.FRONT_RED, LightEffect.STROBE, 0, 1, 20));
                v.connect();
                v.sendMessage(message);
                v.disconnect();
                System.out.println("===================");
            }
        } finally {
            anki.close();
        }
    }

}
