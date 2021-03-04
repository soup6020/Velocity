package com.velocitypowered.proxy.protocol.packet;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;

public class ScoreboardDisplayObjective implements MinecraftPacket {

  public enum Position {
    LIST,
    SIDEBAR,
    BELOW_NAME,
    SIDEBAR_TEAM_BLACK,
    SIDEBAR_TEAM_DARK_BLUE,
    SIDEBAR_TEAM_DARK_GREEN,
    SIDEBAR_TEAM_DARK_AQUA,
    SIDEBAR_TEAM_DARK_RED,
    SIDEBAR_TEAM_DARK_PURPLE,
    SIDEBAR_TEAM_GOLD,
    SIDEBAR_TEAM_GRAY,
    SIDEBAR_TEAM_DARK_GRAY,
    SIDEBAR_TEAM_BLUE,
    SIDEBAR_TEAM_GREEN,
    SIDEBAR_TEAM_AQUA,
    SIDEBAR_TEAM_RED,
    SIDEBAR_TEAM_LIGHT_PURPLE,
    SIDEBAR_TEAM_YELLOW,
    SIDEBAR_TEAM_WHITE
  }

  private Position position;
  private String objectiveName;

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public String getObjectiveName() {
    return objectiveName;
  }

  public void setObjectiveName(String objectiveName) {
    this.objectiveName = objectiveName;
  }

  @Override
  public String toString() {
    return "ScoreboardDisplay{" +
        "position=" + position +
        ", objectiveName='" + objectiveName + '\'' +
        '}';
  }

  @Override
  public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    position = Position.values()[buf.readByte()];
    objectiveName = ProtocolUtils.readString(buf, 16);
  }

  @Override
  public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    buf.writeByte(position.ordinal());
    ProtocolUtils.writeString(buf, objectiveName, 16);
  }

  @Override
  public boolean handle(MinecraftSessionHandler handler) {
    return handler.handle(this);
  }
}
