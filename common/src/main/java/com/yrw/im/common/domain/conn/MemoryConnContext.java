package com.yrw.im.common.domain.conn;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 使用内存存储连接
 * Date: 2019-05-04
 * Time: 12:52
 *
 * @author yrw
 */
public class MemoryConnContext<C extends Conn> implements ConnContext<C> {
    private static final Logger logger = LoggerFactory.getLogger(MemoryConnContext.class);

    protected ConcurrentMap<Serializable, C> connMap;

    public MemoryConnContext() {
        this.connMap = new ConcurrentHashMap<>();
    }

    @Override
    public C getConn(ChannelHandlerContext ctx) {
        Serializable netId = ctx.channel().attr(Conn.NET_ID).get();

        C conn = connMap.get(netId);
        if (conn == null) {
            logger.warn("ClientConn not found, netId: {}", netId);
        }
        return conn;
    }

    @Override
    public C getConn(Serializable netId) {
        C conn = connMap.get(netId);
        if (conn == null) {
            logger.warn("ClientConn not found, netId: {}", netId);
        }
        return conn;
    }

    @Override
    public void addConn(C conn) {
        connMap.putIfAbsent(conn.getNetId(), conn);
    }

    @Override
    public void removeConn(Serializable netId) {
        connMap.remove(netId);
    }

    @Override
    public void removeConn(ChannelHandlerContext ctx) {
        Serializable netId = ctx.channel().attr(Conn.NET_ID).get();
        if (netId == null) {
            logger.warn("[MemoryConnContext] channel id is null");
        }
        connMap.remove(netId);
    }
}