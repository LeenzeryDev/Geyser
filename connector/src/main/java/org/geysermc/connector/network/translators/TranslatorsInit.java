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

package org.geysermc.connector.network.translators;

import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerDisplayScoreboardPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerScoreboardObjectivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerUpdateScorePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerNotifyClientPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.packet.AnimatePacket;
import com.nukkitx.protocol.bedrock.packet.CommandRequestPacket;
import com.nukkitx.protocol.bedrock.packet.TextPacket;
import lombok.Getter;
import org.geysermc.connector.network.translators.bedrock.BedrockAnimateTranslator;
import org.geysermc.connector.network.translators.bedrock.BedrockCommandRequestTranslator;
import org.geysermc.connector.network.translators.bedrock.BedrockTextTranslator;
import org.geysermc.connector.network.translators.inventory.GenericInventoryTranslator;
import org.geysermc.connector.network.translators.inventory.InventoryTranslator;
import org.geysermc.connector.network.translators.item.ItemTranslator;
import org.geysermc.connector.network.translators.java.JavaChatTranslator;
import org.geysermc.connector.network.translators.java.JavaJoinGameTranslator;
import org.geysermc.connector.network.translators.java.entity.JavaEntityDestroyTranslator;
import org.geysermc.connector.network.translators.java.entity.JavaEntityPositionRotationTranslator;
import org.geysermc.connector.network.translators.java.entity.JavaEntityPositionTranslator;
import org.geysermc.connector.network.translators.java.entity.JavaEntityTeleportTranslator;
import org.geysermc.connector.network.translators.java.entity.JavaEntityVelocityTranslator;
import org.geysermc.connector.network.translators.java.entity.spawn.JavaSpawnExpOrbTranslator;
import org.geysermc.connector.network.translators.java.scoreboard.JavaScoreboardDisplayTranslator;
import org.geysermc.connector.network.translators.java.scoreboard.JavaScoreboardObjectiveTranslator;
import org.geysermc.connector.network.translators.java.scoreboard.JavaUpdateScoreTranslator;
import org.geysermc.connector.network.translators.java.world.JavaNotifyClientTranslator;
import org.geysermc.connector.network.translators.java.window.JavaOpenWindowTranslator;
import org.geysermc.connector.network.translators.java.window.JavaSetSlotTranslator;
import org.geysermc.connector.network.translators.java.JavaTitleTranslator;
import org.geysermc.connector.network.translators.java.world.JavaUpdateTimeTranslator;
import org.geysermc.connector.network.translators.java.window.JavaWindowItemsTranslator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TranslatorsInit {

    @Getter
    private static ItemTranslator itemTranslator;

    @Getter
    private static Map<WindowType, InventoryTranslator> inventoryTranslators = new HashMap<WindowType, InventoryTranslator>();

    private static final CompoundTag EMPTY_TAG = CompoundTagBuilder.builder().buildRootTag();
    public static final byte[] EMPTY_LEVEL_CHUNK_DATA;

    static {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(new byte[258]); // Biomes + Border Size + Extra Data Size

            try (NBTOutputStream stream = NbtUtils.createNetworkWriter(outputStream)) {
                stream.write(EMPTY_TAG);
            }

            EMPTY_LEVEL_CHUNK_DATA = outputStream.toByteArray();
        }catch (IOException e) {
            throw new AssertionError("Unable to generate empty level chunk data");
        }
    }

    public static void start() {
        Registry.registerJava(ServerJoinGamePacket.class, new JavaJoinGameTranslator());
        Registry.registerJava(ServerChatPacket.class, new JavaChatTranslator());
        Registry.registerJava(ServerTitlePacket.class, new JavaTitleTranslator());
        Registry.registerJava(ServerUpdateTimePacket.class, new JavaUpdateTimeTranslator());
        Registry.registerJava(ServerEntityPositionPacket.class, new JavaEntityPositionTranslator());
        Registry.registerJava(ServerEntityPositionRotationPacket.class, new JavaEntityPositionRotationTranslator());
        Registry.registerJava(ServerEntityTeleportPacket.class, new JavaEntityTeleportTranslator());
        Registry.registerJava(ServerEntityVelocityPacket.class, new JavaEntityVelocityTranslator());
        Registry.registerJava(ServerNotifyClientPacket.class, new JavaNotifyClientTranslator());
        Registry.registerJava(ServerEntityDestroyPacket.class, new JavaEntityDestroyTranslator());
        Registry.registerJava(ServerSpawnExpOrbPacket.class, new JavaSpawnExpOrbTranslator());
        Registry.registerJava(ServerWindowItemsPacket.class, new JavaWindowItemsTranslator());
        Registry.registerJava(ServerOpenWindowPacket.class, new JavaOpenWindowTranslator());
        Registry.registerJava(ServerSetSlotPacket.class, new JavaSetSlotTranslator());
        Registry.registerJava(ServerScoreboardObjectivePacket.class, new JavaScoreboardObjectiveTranslator());
        Registry.registerJava(ServerDisplayScoreboardPacket.class, new JavaScoreboardDisplayTranslator());
        Registry.registerJava(ServerUpdateScorePacket.class, new JavaUpdateScoreTranslator());

        Registry.registerBedrock(AnimatePacket.class, new BedrockAnimateTranslator());
        Registry.registerBedrock(CommandRequestPacket.class, new BedrockCommandRequestTranslator());
        Registry.registerBedrock(TextPacket.class, new BedrockTextTranslator());

        itemTranslator = new ItemTranslator();

        registerInventoryTranslators();
    }

    private static void registerInventoryTranslators() {
        inventoryTranslators.put(WindowType.GENERIC_9X1, new GenericInventoryTranslator());
        inventoryTranslators.put(WindowType.GENERIC_9X2, new GenericInventoryTranslator());
        inventoryTranslators.put(WindowType.GENERIC_9X3, new GenericInventoryTranslator());
        inventoryTranslators.put(WindowType.GENERIC_9X4, new GenericInventoryTranslator());
        inventoryTranslators.put(WindowType.GENERIC_9X5, new GenericInventoryTranslator());
        inventoryTranslators.put(WindowType.GENERIC_9X6, new GenericInventoryTranslator());
    }
}
