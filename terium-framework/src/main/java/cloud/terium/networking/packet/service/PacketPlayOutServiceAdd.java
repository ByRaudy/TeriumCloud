package cloud.terium.networking.packet.service;

import cloud.terium.teriumapi.network.Packet;
import cloud.terium.teriumapi.node.INode;
import cloud.terium.teriumapi.service.ICloudService;
import cloud.terium.teriumapi.service.ServiceState;
import cloud.terium.teriumapi.service.group.ICloudServiceGroup;
import cloud.terium.teriumapi.template.ITemplate;

import java.util.HashMap;
import java.util.List;

public record PacketPlayOutCreateAdd(String serviceName, int serviceId, int port, int maxPlayers, int memory, String node, String serviceGroup, List<String> templates, HashMap<String, Object> propertyCache) implements Packet {

    public Optional<ICloudServiceGroup> parsedServiceGroup() {
        return TeriumAPI.getTeriumAPI().getProvider().getServiceGroupProvider().getServiceGroupByName(serviceGroup);
    }

    public Optional<INode> parsedNode() {
        return TeriumAPI.getTeriumAPI().getProvider().getNodeProvider().getNodeByName(node);
    }

    public List<ITemplate> parsedTemplates() {
        return TeriumAPI.getTeriumAPI().getProvider().getTemplateProvider().getAllTemplates();
    }
}