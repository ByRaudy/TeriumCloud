package cloud.terium.cloudsystem.pipe;

import cloud.terium.cloudsystem.TeriumCloud;
import cloud.terium.cloudsystem.event.events.player.CloudPlayerConnectEvent;
import cloud.terium.cloudsystem.event.events.player.CloudPlayerConnectedToServiceEvent;
import cloud.terium.cloudsystem.event.events.player.CloudPlayerJoinEvent;
import cloud.terium.cloudsystem.event.events.player.CloudPlayerQuitEvent;
import cloud.terium.cloudsystem.event.events.service.*;
import cloud.terium.networking.packet.PacketPlayOutCloudPlayerConnect;
import cloud.terium.networking.packet.PacketPlayOutCloudPlayerConnectedService;
import cloud.terium.networking.packet.PacketPlayOutCloudPlayerJoin;
import cloud.terium.networking.packet.PacketPlayOutCloudPlayerQuit;
import cloud.terium.networking.packet.service.*;
import cloud.terium.teriumapi.network.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter

public class TeriumServer {

    private final ServerBootstrap serverBootstrap;
    private final Channel channel;
    private final ChannelFuture channelFuture;
    private final List<Channel> channels;

    public TeriumServer(String host, int port) {
        EventLoopGroup eventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            this.serverBootstrap = new ServerBootstrap()
                    .group(eventLoopGroup)
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            channel.pipeline()
                                    .addLast("packet-decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())))
                                    .addLast("packet-encoder", new ObjectEncoder())
                                    .addLast(new SimpleChannelInboundHandler<Packet>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
                                            try {
                                                // service packets
                                                if(packet instanceof PacketPlayOutServiceAdd newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceAddEvent(newPacket.cloudService()));
                                                if(packet instanceof PacketPlayOutCreateService newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceCreateEvent(newPacket.name(), newPacket.serviceGroup(), newPacket.templates(), newPacket.port(), newPacket.maxPlayers(), newPacket.memory(), newPacket.serviceId(), newPacket.cloudServiceType()));
                                                if(packet instanceof PacketPlayOutServiceForceShutdown newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceForceStopEvent(newPacket.cloudService()));
                                                if(packet instanceof PacketPlayOutServiceLock newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceLockEvent(newPacket.cloudService()));
                                                if(packet instanceof PacketPlayOutSuccessfullyServiceStarted newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceLoggedInEvent(newPacket.cloudService(), newPacket.cloudService().getServiceGroup().getServiceGroupNode()));
                                                if(packet instanceof PacketPlayOutServiceRemove newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceRemoveEvent(newPacket.cloudService()));
                                                if(packet instanceof PacketPlayOutServiceRestart newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceRestartEvent(newPacket.cloudService()));
                                                if(packet instanceof PacketPlayOutServiceStart newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceStartEvent(newPacket.cloudService(), newPacket.node()));
                                                if(packet instanceof PacketPlayOutServiceShutdown newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceStopEvent(newPacket.cloudService(), newPacket.cloudService().getServiceGroup().getServiceGroupNode()));
                                                if(packet instanceof PacketPlayOutServiceUnlock newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceUnlockEvent(newPacket.cloudService()));
                                                if(packet instanceof PacketPlayOutUpdateService newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new ServiceUpdateEvent(newPacket.cloudService(), newPacket.serviceState(), newPacket.locked(), newPacket.usedMemory(), newPacket.onlinePlayers()));

                                                // player packets
                                                if(packet instanceof PacketPlayOutCloudPlayerConnectedService newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new CloudPlayerConnectedToServiceEvent(newPacket.cloudPlayer(), newPacket.cloudService()));                                                if(packet instanceof PacketPlayOutCloudPlayerConnectedService newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new CloudPlayerConnectedToServiceEvent(newPacket.cloudPlayer(), newPacket.cloudService()));
                                                if(packet instanceof PacketPlayOutCloudPlayerConnect newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new CloudPlayerConnectEvent(newPacket.cloudPlayer(), newPacket.cloudService()));
                                                if(packet instanceof PacketPlayOutCloudPlayerJoin newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new CloudPlayerJoinEvent(newPacket.cloudPlayer()));
                                                if(packet instanceof PacketPlayOutCloudPlayerQuit newPacket) TeriumCloud.getTerium().getEventProvider().callEvent(new CloudPlayerQuitEvent(newPacket.cloudPlayer()));

                                            } catch (Exception exception) {
                                                channels.forEach(targetChannel -> {
                                                    if (targetChannel != channelHandlerContext.channel()) {
                                                        targetChannel.writeAndFlush(packet);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void channelRegistered(ChannelHandlerContext channelHandlerContext) {
                                            channels.add(channelHandlerContext.channel());
                                        }

                                        @Override
                                        public void channelUnregistered(ChannelHandlerContext channelHandlerContext) {
                                            channels.remove(channelHandlerContext.channel());
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                        }
                                    });
                        }
                    });
            this.channelFuture = this.serverBootstrap.bind(host, port).sync();
            this.channel = this.channelFuture.channel();
            this.channels = new ArrayList<>();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to start terium-server", exception);
        }
    }
}
