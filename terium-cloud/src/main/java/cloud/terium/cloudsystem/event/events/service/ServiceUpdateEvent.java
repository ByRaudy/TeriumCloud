package cloud.terium.cloudsystem.event.events.service;

import cloud.terium.teriumapi.event.Event;
import cloud.terium.teriumapi.node.INode;
import cloud.terium.teriumapi.service.ICloudService;
import cloud.terium.teriumapi.service.ServiceState;
import lombok.Getter;

@Getter
public class ServiceUpdateEvent extends Event {

    private final ICloudService cloudService;
    private final INode node;
    private final ServiceState serviceState;
    private final boolean locked;
    private final long memory;
    private final int onlinePlayers;

    public ServiceUpdateEvent(ICloudService cloudService, INode node, ServiceState serviceState, boolean locked, long memory, int onlinePlayers) {
        this.cloudService = cloudService;
        this.node = node;
        this.serviceState = serviceState;
        this.locked = locked;
        this.memory = memory;
        this.onlinePlayers = onlinePlayers;
    }
}