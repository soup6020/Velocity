package com.velocitypowered.proxy.protocol.packet;

import com.google.common.collect.ImmutableSet;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class TeamPacket implements MinecraftPacket {

  public enum Action {
    CREATE_TEAM,
    REMOVE_TEAM,
    UPDATE_INFO,
    ADD_ENTITY,
    REMOVE_ENTITY;
  }

  private String teamName;
  private Action action;
  private Component displayName;
  private Component prefix, suffix;
  private byte friendlyFire;
  private String nameTagVisibility;
  private String collisionRule;
  private int color;
  private String[] entites; // Can either be a player name or an UUID

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public Component getDisplayName() {
    return displayName;
  }

  public void setDisplayName(Component displayName) {
    this.displayName = displayName;
  }

  public Component getPrefix() {
    return prefix;
  }

  public void setPrefix(Component prefix) {
    this.prefix = prefix;
  }

  public Component getSuffix() {
    return suffix;
  }

  public void setSuffix(Component suffix) {
    this.suffix = suffix;
  }

  public byte getFriendlyFire() {
    return friendlyFire;
  }

  public void setFriendlyFire(byte friendlyFire) {
    this.friendlyFire = friendlyFire;
  }

  public String getNameTagVisibility() {
    return nameTagVisibility;
  }

  public void setNameTagVisibility(String nameTagVisibility) {
    this.nameTagVisibility = nameTagVisibility;
  }

  public String getCollisionRule() {
    return collisionRule;
  }

  public void setCollisionRule(String collisionRule) {
    this.collisionRule = collisionRule;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public Set<String> getEntites() {
    return ImmutableSet.copyOf(entites);
  }

  public void setEntites(Collection<String> entites) {
    this.entites = entites.toArray(new String[entites.size()]);
  }

  @Override
  public String toString() {
    return "TeamPacket{" +
        "teamName='" + teamName + '\'' +
        ", action=" + action +
        ", displayName=" + displayName +
        ", prefix=" + prefix +
        ", suffix=" + suffix +
        ", friendlyFire=" + friendlyFire +
        ", nameTagVisibility='" + nameTagVisibility + '\'' +
        ", collisionRule='" + collisionRule + '\'' +
        ", color=" + color +
        ", entites=" + Arrays.toString(entites) +
        '}';
  }

  @Override
  public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    teamName = ProtocolUtils.readString(buf, 16);
    action = Action.values()[buf.readByte()];
	if (action == Action.CREATE_TEAM || action == Action.UPDATE_INFO) {
      displayName = ProtocolUtils.readGenericComponent(buf, version);
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_13) < 0) {
        prefix = ProtocolUtils.readGenericComponent(buf, version, 16);
        suffix = ProtocolUtils.readGenericComponent(buf, version, 16);
      }
      friendlyFire = buf.readByte();
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_8) >= 0) {
        nameTagVisibility = ProtocolUtils.readString(buf, 40);
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_9) >= 0) {
          collisionRule = ProtocolUtils.readString(buf, 40);
        }
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_13) >= 0) {
          color = ProtocolUtils.readVarInt(buf);
          prefix = ProtocolUtils.readGenericComponent(buf, version);
          suffix = ProtocolUtils.readGenericComponent(buf, version);
        } else {
          color = buf.readByte();
        }
      }
    }
    if (action == Action.CREATE_TEAM || action == Action.ADD_ENTITY
			|| action == Action.REMOVE_ENTITY) {
      entites = version.compareTo(ProtocolVersion.MINECRAFT_1_8) >= 0
              ? ProtocolUtils.readStringArray(buf) : ProtocolUtils.readStringArray17(buf);
	}
  }

  @Override
  public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    ProtocolUtils.writeString(buf, teamName);
    buf.writeByte(action.ordinal());
    if (action == Action.CREATE_TEAM || action == Action.UPDATE_INFO) {
      ProtocolUtils.writeGenericComponent(buf, displayName, version);
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_13) < 0) {
        ProtocolUtils.writeGenericComponent(buf, prefix, version, 16);
        ProtocolUtils.writeGenericComponent(buf, suffix, version, 16);
      }
      buf.writeByte(friendlyFire);
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_8) >= 0) {
        ProtocolUtils.writeString(buf, nameTagVisibility);
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_9) >= 0) {
          ProtocolUtils.writeString(buf, collisionRule);
        }
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_13) >= 0) {
          ProtocolUtils.writeVarInt(buf, color);
          ProtocolUtils.writeGenericComponent(buf, prefix, version);
          ProtocolUtils.writeGenericComponent(buf, suffix, version);
        } else {
          buf.writeByte(color);
        }
      }
    }
    if (action == Action.CREATE_TEAM || action == Action.ADD_ENTITY
			|| action == Action.REMOVE_ENTITY) {
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_8) >= 0) {
        ProtocolUtils.writeStringArray(buf, entites);
      } else {
        ProtocolUtils.writeStringArray17(buf, entites);
      }
    }
  }

  @Override
  public boolean handle(MinecraftSessionHandler handler) {
    return handler.handle(this);
  }
}