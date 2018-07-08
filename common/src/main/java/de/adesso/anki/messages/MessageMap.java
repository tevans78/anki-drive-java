package de.adesso.anki.messages;

import java.util.HashMap;
import java.util.Map;

public class MessageMap {

	public static final Map<Integer, Class<? extends Message>> MESSAGES = new HashMap<>();
	
	static {
		MESSAGES.put(BatteryLevelRequestMessage.TYPE, BatteryLevelRequestMessage.class);
        MESSAGES.put(BatteryLevelResponseMessage.TYPE, BatteryLevelResponseMessage.class);
        MESSAGES.put(CancelLaneChangeMessage.TYPE, CancelLaneChangeMessage.class);
        MESSAGES.put(ChangeLaneMessage.TYPE, ChangeLaneMessage.class);
        MESSAGES.put(LightsPatternMessage.TYPE,LightsPatternMessage.class);
        MESSAGES.put(LocalizationIntersectionUpdateMessage.TYPE,LocalizationIntersectionUpdateMessage.class);
        MESSAGES.put(LocalizationPositionUpdateMessage.TYPE,LocalizationPositionUpdateMessage.class);
        MESSAGES.put(LocalizationTransitionUpdateMessage.TYPE,LocalizationTransitionUpdateMessage.class);
        MESSAGES.put(OffsetFromRoadCenterUpdateMessage.TYPE,OffsetFromRoadCenterUpdateMessage.class);
        MESSAGES.put(PingRequestMessage.TYPE,PingRequestMessage.class);
        MESSAGES.put(PingResponseMessage.TYPE,PingResponseMessage.class);
        MESSAGES.put(SdkModeMessage.TYPE,SdkModeMessage.class);
        MESSAGES.put(SetConfigParamsMessage.TYPE,SetConfigParamsMessage.class);
        MESSAGES.put(SetLightsMessage.TYPE,SetLightsMessage.class);
        MESSAGES.put(SetOffsetFromRoadCenterMessage.TYPE,SetOffsetFromRoadCenterMessage.class);
        MESSAGES.put(SetSpeedMessage.TYPE,SetSpeedMessage.class);
        MESSAGES.put(TurnMessage.TYPE,TurnMessage.class);
        MESSAGES.put(VehicleDelocalizedMessage.TYPE,VehicleDelocalizedMessage.class);
        MESSAGES.put(VehicleInfoMessage.TYPE,VehicleInfoMessage.class);
        MESSAGES.put(VersionRequestMessage.TYPE,VersionRequestMessage.class);
        MESSAGES.put(VersionResponseMessage.TYPE,VersionResponseMessage.class);
    }
	
    public static Class<? extends Message> getMessageClass(int type) {
        return MESSAGES.get(type);
    }

}
