package net.morher.alarmus.plugins.wiz;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.messages.AbstractMessager;

public class WizLightPlugin extends AbstractMessager<ServerMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WizLightPlugin.class);
    private final Map<String, LampGroup> lampGroups = new HashMap<>();
    DatagramSocket socket;

    public WizLightPlugin() {

        registerGroup(new LampGroup("familyRoomWall")
                .addBulb("BulbFamilyRoomWallN")
                .addBulb("BulbFamilyRoomWallS")
                .putStyle("warmwhite", WizBulbParam.warmwhite().dimmed(100))
                .putStyle("greenBright", WizBulbParam.rgb(0, 255, 3).dimmed(100))
                .putStyle("redBright", WizBulbParam.rgb(255, 0, 0).dimmed(100))
                .putStyle("redDark", WizBulbParam.rgb(255, 0, 0).dimmed(10)));

        registerGroup(new LampGroup("diningTable")
                .addBulb("BulbDining0")
                .addBulb("BulbDining1")
                .addBulb("BulbDining2")
                .addBulb("BulbDining3")
                .putStyle("warmwhite", WizBulbParam.warmwhite().dimmed(80))
                .putStyle("greenBright", WizBulbParam.rgb(0, 255, 3).dimmed(100))
                .putStyle("redBright", WizBulbParam.rgb(255, 0, 0).dimmed(100))
                .putStyle("redDark", WizBulbParam.rgb(255, 0, 0).dimmed(10)));

        registerGroup(new LampGroup("hallways")
                .addBulb("BulbGarage")
                .addBulb("BulbTerrace")
                .putStyle("warmwhite", WizBulbParam.warmwhite().dimmed(30))
                .putStyle("greenBright", WizBulbParam.rgb(0, 255, 3).dimmed(100))
                .putStyle("redBright", WizBulbParam.rgb(255, 0, 0).dimmed(100))
                .putStyle("redDark", WizBulbParam.rgb(255, 0, 0).dimmed(10)));

        registerGroup(new LampGroup("otherLights")
                .addBulb("BulbExtra1")
                .putStyle("off", WizBulbParam.off())
                .putStyle("greenBright", WizBulbParam.rgb(0, 255, 3).dimmed(100))
                .putStyle("redBright", WizBulbParam.rgb(255, 0, 0).dimmed(100))
                .putStyle("redDark", WizBulbParam.rgb(255, 0, 0).dimmed(10)));

        try {
            socket = new DatagramSocket();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
