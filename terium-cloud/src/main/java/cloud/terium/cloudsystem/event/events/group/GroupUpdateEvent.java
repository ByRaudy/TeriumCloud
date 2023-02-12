package cloud.terium.cloudsystem.event.events.group;

import cloud.terium.cloudsystem.TeriumCloud;
import cloud.terium.networking.packet.group.PacketPlayOutGroupUpdate;
import cloud.terium.teriumapi.event.Event;
import lombok.Getter;

@Getter
public class GroupUpdateEvent extends Event {

    private final String cloudServiceGroup;

    public GroupUpdateEvent(String cloudServiceGroup) {
        this.cloudServiceGroup = cloudServiceGroup;
        TeriumCloud.getTerium().getNetworking().sendPacket(new PacketPlayOutGroupUpdate(cloudServiceGroup));
    }
}