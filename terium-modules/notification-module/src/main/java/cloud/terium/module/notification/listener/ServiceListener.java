package cloud.terium.module.notification.listener;

import cloud.terium.module.notification.bungeecord.NotificationBungeeCordStartup;
import cloud.terium.module.notification.manager.ConfigManager;
import cloud.terium.module.notification.velocity.NotificationVelocityStartup;
import cloud.terium.teriumapi.event.Listener;
import cloud.terium.teriumapi.event.Subscribe;
import cloud.terium.teriumapi.events.config.ReloadConfigEvent;
import cloud.terium.teriumapi.events.service.CloudServiceStartedEvent;
import cloud.terium.teriumapi.events.service.CloudServiceStartingEvent;
import cloud.terium.teriumapi.events.service.CloudServiceStoppedEvent;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ServiceListener implements Listener {

    @Subscribe
    public void handleCloudServiceStarting(CloudServiceStartingEvent event) {
        if(NotificationVelocityStartup.getInstance() == null) {
            NotificationBungeeCordStartup.getInstance().getProxy().getPlayers().stream().filter(player -> player.hasPermission("terium.notification")).forEach(player ->
                    BungeeAudiences.create(NotificationBungeeCordStartup.getInstance()).player(player).sendMessage(MiniMessage.miniMessage().deserialize(NotificationVelocityStartup.getInstance().getConfigManager().getJson().get("starting").getAsString().replace("%service%", event.getCloudService().getServiceName()).replace("%node%", event.getCloudService().getServiceNode().getName()))));
            return;
        }

        NotificationVelocityStartup.getInstance().getProxyServer().getAllPlayers().stream().filter(player -> player.hasPermission("terium.notification")).forEach(player ->
                player.sendMessage(MiniMessage.miniMessage().deserialize(NotificationVelocityStartup.getInstance().getConfigManager().getJson().get("starting").getAsString().replace("%service%", event.getCloudService().getServiceName()).replace("%node%", event.getCloudService().getServiceNode().getName()))));
    }

    @Subscribe
    public void handleCloudServiceStarting(CloudServiceStartedEvent event) {
        if(NotificationVelocityStartup.getInstance() == null) {
            NotificationBungeeCordStartup.getInstance().getProxy().getPlayers().stream().filter(player -> player.hasPermission("terium.notification")).forEach(player ->
                    BungeeAudiences.create(NotificationBungeeCordStartup.getInstance()).player(player).sendMessage(MiniMessage.miniMessage().deserialize(NotificationVelocityStartup.getInstance().getConfigManager().getJson().get("started").getAsString().replace("%service%", event.getCloudService().getServiceName()).replace("%node%", event.getCloudService().getServiceNode().getName()))));
            return;
        }

        NotificationVelocityStartup.getInstance().getProxyServer().getAllPlayers().stream().filter(player -> player.hasPermission("terium.notification")).forEach(player ->
                player.sendMessage(MiniMessage.miniMessage().deserialize(NotificationVelocityStartup.getInstance().getConfigManager().getJson().get("started").getAsString().replace("%service%", event.getCloudService().getServiceName()).replace("%node%", event.getCloudService().getServiceNode().getName())).hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§fClick to connect"))).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + event.getCloudService().getServiceName()))));
    }

    @Subscribe
    public void handleCloudServiceStarting(CloudServiceStoppedEvent event) {
        if(NotificationVelocityStartup.getInstance() == null) {
            NotificationBungeeCordStartup.getInstance().getProxy().getPlayers().stream().filter(player -> player.hasPermission("terium.notification")).forEach(player ->
                    BungeeAudiences.create(NotificationBungeeCordStartup.getInstance()).player(player).sendMessage(MiniMessage.miniMessage().deserialize(NotificationVelocityStartup.getInstance().getConfigManager().getJson().get("stopped").getAsString().replace("%service%", event.getCloudService().getServiceName()).replace("%node%", event.getCloudService().getServiceNode().getName()))));
            return;
        }

        NotificationVelocityStartup.getInstance().getProxyServer().getAllPlayers().stream().filter(player -> player.hasPermission("terium.notification")).forEach(player ->
                player.sendMessage(MiniMessage.miniMessage().deserialize(NotificationVelocityStartup.getInstance().getConfigManager().getJson().get("stopped").getAsString().replace("%service%", event.getCloudService().getServiceName()).replace("%node%", event.getCloudService().getServiceNode().getName()))));
    }

    @Subscribe
    public void handleReloadConfig(ReloadConfigEvent event) {
        NotificationVelocityStartup.getInstance().setConfigManager(new ConfigManager());
    }
}