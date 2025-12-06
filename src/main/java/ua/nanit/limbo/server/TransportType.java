package ua.nanit.limbo.server;

import io.netty.channel.ChannelFactory;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.uring.IoUring;
import io.netty.channel.uring.IoUringIoHandler;
import io.netty.channel.uring.IoUringServerSocketChannel;
import lombok.NonNull;

public enum TransportType {

    NIO {
        @NonNull
        @Override
        public ChannelFactory<? extends ServerChannel> getChannelFactory() {
            return NioServerSocketChannel::new;
        }

        @NonNull
        @Override
        public IoHandlerFactory getIoHandlerFactory() {
            return NioIoHandler.newFactory();
        }

        public boolean isAvailable() {
            return true;
        }
    },
    EPOLL {
        @NonNull
        @Override
        public ChannelFactory<? extends ServerChannel> getChannelFactory() {
            return EpollServerSocketChannel::new;
        }

        @NonNull
        @Override
        public IoHandlerFactory getIoHandlerFactory() {
            return EpollIoHandler.newFactory();
        }

        public boolean isAvailable() {
            return Epoll.isAvailable();
        }
    },
    IO_URING {
        @NonNull
        @Override
        public ChannelFactory<? extends ServerChannel> getChannelFactory() {
            return IoUringServerSocketChannel::new;
        }

        @NonNull
        @Override
        public IoHandlerFactory getIoHandlerFactory() {
            return IoUringIoHandler.newFactory();
        }

        public boolean isAvailable() {
            return IoUring.isAvailable();
        }
    },
    KQUEUE {
        @NonNull
        @Override
        public ChannelFactory<? extends ServerChannel> getChannelFactory() {
            return KQueueServerSocketChannel::new;
        }

        @NonNull
        @Override
        public IoHandlerFactory getIoHandlerFactory() {
            return KQueueIoHandler.newFactory();
        }

        public boolean isAvailable() {
            return KQueue.isAvailable();
        }
    };

    @NonNull
    public abstract ChannelFactory<? extends ServerChannel> getChannelFactory();

    @NonNull
    public abstract IoHandlerFactory getIoHandlerFactory();

    public abstract boolean isAvailable();
}
