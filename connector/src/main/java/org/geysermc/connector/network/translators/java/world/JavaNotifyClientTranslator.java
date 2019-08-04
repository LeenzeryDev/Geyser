/*
 * Copyright (c) 2019 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.connector.network.translators.java.world;

import com.flowpowered.math.vector.Vector3f;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerNotifyClientPacket;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;
import com.nukkitx.protocol.bedrock.packet.SetPlayerGameTypePacket;
import org.geysermc.connector.network.session.GeyserSession;
import org.geysermc.connector.network.translators.PacketTranslator;

import java.util.concurrent.ThreadLocalRandom;

public class JavaNotifyClientTranslator extends PacketTranslator<ServerNotifyClientPacket> {

    @Override
    public void translate(ServerNotifyClientPacket packet, GeyserSession session) {
        switch (packet.getNotification()) {
            case START_RAIN:
                LevelEventPacket startRainPacket = new LevelEventPacket();
                startRainPacket.setEvent(LevelEventPacket.Event.START_RAIN);
                startRainPacket.setData(ThreadLocalRandom.current().nextInt(50000) + 10000);
                startRainPacket.setPosition(new Vector3f(0, 0, 0));

                session.getUpstream().sendPacket(startRainPacket);
                break;
            case STOP_RAIN:
                LevelEventPacket stopRainPacket = new LevelEventPacket();
                stopRainPacket.setEvent(LevelEventPacket.Event.STOP_RAIN);
                stopRainPacket.setData(ThreadLocalRandom.current().nextInt(50000) + 10000);
                stopRainPacket.setPosition(new Vector3f(0, 0, 0));

                session.getUpstream().sendPacket(stopRainPacket);
                break;
            case CHANGE_GAMEMODE:
                int gamemode = 0;
                if (packet.getValue().equals(GameMode.CREATIVE)) {
                    gamemode = 1;
                } else if (packet.getValue().equals(GameMode.ADVENTURE)) {
                    gamemode = 2;
                } else if (packet.getValue().equals(GameMode.SPECTATOR)) {
                    gamemode = 3;
                }
                SetPlayerGameTypePacket playerGameTypePacket = new SetPlayerGameTypePacket();
                playerGameTypePacket.setGamemode(gamemode);
                break;
            case ENTER_CREDITS:
                // ShowCreditsPacket showCreditsPacket = new ShowCreditsPacket();
                // showCreditsPacket.setStatus(ShowCreditsPacket.Status.START_CREDITS);
                // showCreditsPacket.setRuntimeEntityId(runtimeEntityId);
                // session.getUpstream().sendPacket(showCreditsPacket);
                break;
            default:
                break;
        }
    }
}
