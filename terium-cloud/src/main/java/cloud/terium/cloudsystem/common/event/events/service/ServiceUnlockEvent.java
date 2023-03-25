package cloud.terium.cloudsystem.common.event.events.service;

import cloud.terium.cloudsystem.cluster.ClusterStartup;
import cloud.terium.networking.packet.service.PacketPlayOutServiceUnlock;
import cloud.terium.teriumapi.TeriumAPI;
import cloud.terium.teriumapi.event.Event;
import lombok.Getter;

@Getter
public class ServiceUnlockEvent extends Event {

    private final String cloudService;

    public ServiceUnlockEvent(String cloudService) {
        this.cloudService = cloudService;
        if (ClusterStartup.getCluster() != null)
            TeriumAPI.getTeriumAPI().getProvider().getTeriumNetworking().sendPacket(new PacketPlayOutServiceUnlock(cloudService));
    }
}