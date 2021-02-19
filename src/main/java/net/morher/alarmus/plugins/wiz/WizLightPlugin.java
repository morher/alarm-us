package net.morher.alarmus.plugins.wiz;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.messages.AbstractMessager;
import net.morher.alarmus.messages.MessageBroker;
import net.morher.alarmus.plugins.wiz.WizLightConfig.BulbGroupDef;
import net.morher.alarmus.plugins.wiz.WizLightConfig.StyleDef;

public class WizLightPlugin extends AbstractMessager<ServerMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WizLightPlugin.class);
    private final Map<String, LampGroup> lampGroups = new HashMap<>();
    DatagramSocket socket;

    public WizLightPlugin() {
        try {
            socket = new DatagramSocket();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WizLightPlugin configure(WizLightConfig config) {
        for (Entry<String, BulbGroupDef> group : config.groups.entrySet()) {
            registerGroup(createGroup(group.getKey(), group.getValue(), config.commonStyles));
        }
        return this;
    }

    private static LampGroup createGroup(String name, BulbGroupDef def, Map<String, StyleDef> commonStyles) {
        LampGroup group = new LampGroup(name);
        for (String bulb : def.bulbs) {
            group.addBulb(bulb);
        }
        for (Entry<String, StyleDef> style : commonStyles.entrySet()) {
            group.putStyle(style.getKey(), createBulbParam(style.getValue()));
        }
        for (Entry<String, StyleDef> style : def.styles.entrySet()) {
            group.putStyle(style.getKey(), createBulbParam(style.getValue()));
        }
        return group;
    }

    private static WizBulbParam createBulbParam(StyleDef def) {
        if (def.rgb != null) {
            return WizBulbParam
                    .rgb(def.rgb[0], def.rgb[1], def.rgb[2])
                    .dimmed(def.dimmed);

        } else if (def.scene != null) {
            return WizBulbParam
                    .scene(def.scene)
                    .dimmed(def.dimmed);
        }

        return WizBulbParam.off();
    }

    public WizLightPlugin registerGroup(LampGroup group) {
        lampGroups.put(group.getName(), group);
        return this;
    }

    @Override
    public void onMessage(ServerMessage message) {
        if (message.hasAttributes()) {
            for (Map.Entry<String, String> attribute : message.getAttributes().entrySet()) {
                LampGroup group = lampGroups.get(attribute.getKey());
                if (group != null) {
                    handleLampGroup(group, attribute.getValue());
                }
            }
        }
    }

    private void handleLampGroup(LampGroup group, String styleName) {
        String styleParams = group.getStyleParams(styleName);
        if (styleParams != null) {
            for (InetAddress bulb : group.getBulbs()) {
                sendWizBulbCommand(bulb, styleParams);
            }
        }
    }

    private void sendWizBulbCommand(InetAddress address, String params) {
        try {
            String msg = "{\"method\":\"setPilot\",\"params\":" + params + "}";

            byte[] buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 38899);
            socket.send(packet);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public WizLightPlugin connectAndStart(MessageBroker<ServerMessage> broker) {
        connect(broker);
        return this;
    }
}
