package cloud.terium.module.dockerizedservices;

import cloud.terium.module.dockerizedservices.config.ConfigLoader;
import cloud.terium.module.dockerizedservices.config.DockerizedConfig;
import cloud.terium.module.dockerizedservices.service.DockerizedService;
import cloud.terium.module.dockerizedservices.service.DockerizedServiceFactory;
import cloud.terium.teriumapi.TeriumAPI;
import cloud.terium.teriumapi.module.IModule;
import cloud.terium.teriumapi.module.ModuleType;
import cloud.terium.teriumapi.module.annotation.Module;
import lombok.Getter;

@Module(name = "dockerized-services-module", author = "Jxnnik(ByRaudy)", version = "1.6-OXYGEN", description = "", reloadable = true, moduleType = ModuleType.CLOUD_ONLY)
@Getter
public class TeriumDockerizedServices implements IModule {

    private static TeriumDockerizedServices instance;
    private DockerizedServiceFactory serviceFactory;
    private ConfigLoader configLoader;
    private DockerizedConfig dockerizedConfig;

    public static TeriumDockerizedServices getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        serviceFactory = new DockerizedServiceFactory();
        configLoader = new ConfigLoader();
        dockerizedConfig = new DockerizedConfig();

        new DockerizedService(TeriumAPI.getTeriumAPI().getProvider().getServiceGroupProvider().getServiceGroupByName("Lobby").get()).start();
    }

    @Override
    public void onDisable() {
    }

    public void reload() {
    }
}
