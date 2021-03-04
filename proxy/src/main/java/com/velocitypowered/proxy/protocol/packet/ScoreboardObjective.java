package com.velocitypowered.proxy.protocol.packet;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;

public class ScoreboardObjective implements MinecraftPacket {

  public enum RenderType {
    INTEGER,
    HEARTS
  }

  private String objectiveName;
  private Component displayName;
  private RenderType renderType;
  private int method; // 0: create or update | 1: remove | 2: change objective


  @Override
  public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    objectiveName = ProtocolUtils.readString(buf, 16);
    method = buf.readByte();
    if (method != 0 && method != 2) {
      displayName = Component.empty();
      renderType = RenderType.INTEGER;
    } else {
      displayName = ProtocolUtils.readGenericComponent(buf, version);
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_13) >= 0) {
        renderType = RenderType.values()[ProtocolUtils.readVarInt(buf)];
      } else {
        renderType = RenderType.valueOf(ProtocolUtils.readString(buf).toUpperCase());
      }
    }
  }

  @Override
  public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    ProtocolUtils.writeString(buf, objectiveName, 16);
    buf.writeByte(method);
    if (method == 0 || method == 2) {
      ProtocolUtils.writeGenericComponent(buf, displayName, version);
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_13) >= 0) {
        ProtocolUtils.writeVarInt(buf, renderType.ordinal());
      } else {
        ProtocolUtils.writeString(buf, renderType.toString().toLowerCase());
      }
    }
  }

  @Override
  public boolean handle(MinecraftSessionHandler handler) {
    return handler.handle(this);
  }
}
