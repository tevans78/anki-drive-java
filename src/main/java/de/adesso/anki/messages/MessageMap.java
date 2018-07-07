package de.adesso.anki.messages;

public class MessageMap {

    public static Class<? extends Message> getMessageClass(int type) {
        if (BatteryLevelRequestMessage.TYPE == type)
            return BatteryLevelRequestMessage.class;
        if (BatteryLevelResponseMessage.TYPE == type)
            return BatteryLevelResponseMessage.class;
        if (CancelLaneChangeMessage.TYPE == type)
            return CancelLaneChangeMessage.class;
        if (ChangeLaneMessage.TYPE == type)
            return ChangeLaneMessage.class;
        if (LightsPatternMessage.TYPE == type)
            return LightsPatternMessage.class;
        if (LocalizationIntersectionUpdateMessage.TYPE == type)
            return LocalizationIntersectionUpdateMessage.class;
        if (LocalizationPositionUpdateMessage.TYPE == type)
            return LocalizationPositionUpdateMessage.class;
        if (LocalizationTransitionUpdateMessage.TYPE == type)
            return LocalizationTransitionUpdateMessage.class;
        if (OffsetFromRoadCenterUpdateMessage.TYPE == type)
            return OffsetFromRoadCenterUpdateMessage.class;
        if (PingRequestMessage.TYPE == type)
            return PingRequestMessage.class;
        if (PingResponseMessage.TYPE == type)
            return PingResponseMessage.class;
        if (SdkModeMessage.TYPE == type)
            return SdkModeMessage.class;
        if (SetConfigParamsMessage.TYPE == type)
            return SetConfigParamsMessage.class;
        if (SetLightsMessage.TYPE == type)
            return SetLightsMessage.class;
        if (SetOffsetFromRoadCenterMessage.TYPE == type)
            return SetOffsetFromRoadCenterMessage.class;
        if (SetSpeedMessage.TYPE == type)
            return SetSpeedMessage.class;
        if (TurnMessage.TYPE == type)
            return TurnMessage.class;
        if (VehicleDelocalizedMessage.TYPE == type)
            return VehicleDelocalizedMessage.class;
        if (VehicleInfoMessage.TYPE == type)
            return VehicleInfoMessage.class;
        if (VersionRequestMessage.TYPE == type)
            return VersionRequestMessage.class;
        if (VersionResponseMessage.TYPE == type)
            return VersionResponseMessage.class;
        return null;
    }

}
