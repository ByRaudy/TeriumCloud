package cloud.terium.cloudsystem.node;

import cloud.terium.cloudsystem.TeriumCloud;
import cloud.terium.cloudsystem.utils.logger.Logger;
import cloud.terium.networking.client.TeriumClient;
import cloud.terium.teriumapi.console.LogType;
import cloud.terium.teriumapi.node.INode;
import cloud.terium.teriumapi.node.INodeProvider;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Getter
public class NodeProvider implements INodeProvider {

    private final HashMap<String, INode> nodes;
    private final HashMap<INode, TeriumClient> nodeClients;

    public NodeProvider() {
        Logger.log("Loaded node-provider", LogType.INFO);
        this.nodes = new LinkedHashMap<>();
        this.nodeClients = new HashMap<>();
    }

    public void registerNode(INode node) {
        nodes.put(node.getName(), node);
    }

    public void registerNodes() {
        Logger.log("Trying to load all nodes from config.", LogType.INFO);
        registerNode(TeriumCloud.getTerium().getThisNode());
        JsonObject jsonObject = TeriumCloud.getTerium().getCloudConfig().nodes();

        TeriumCloud.getTerium().getCloudConfig().nodes().entrySet().forEach(jsonElement -> registerNode(new Node(jsonElement.getValue().getAsJsonObject().get("name").getAsString(), jsonElement.getValue().getAsJsonObject().get("key").getAsString(),
                new InetSocketAddress(jsonElement.getValue().getAsJsonObject().get("address").getAsString(), jsonElement.getValue().getAsJsonObject().get("port").getAsInt()))));
    }

    @Override
    public List<INode> getAllNodes() {
        return nodes.values().stream().toList();
    }

    @Override
    public Optional<INode> getNodeByName(String name) {
        return Optional.ofNullable(nodes.get(name));
    }

    public void addClientToNode(INode node, TeriumClient client) {
        nodeClients.put(node, client);
    }

    public TeriumClient getClientFromNode(INode node) {
        return nodeClients.get(node);
    }
}