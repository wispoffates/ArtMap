package me.Fupery.ArtMap.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.PacketType;
import me.Fupery.ArtMap.api.Utils.VersionHandler.BukkitVersion;

public class Reflection {

    public static final String NMS;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        NMS = version.replace("org.bukkit.craftbukkit", "net.minecraft.server");
    }

    public Channel getPlayerChannel(Player player) throws ReflectiveOperationException {
        return ArtMap.instance().getCompatManager().getReflectionHandler().getPlayerChannel(player);
    }

    public Object getField(Object obj, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldException(String.format("Field '%s' could not be found in '%s'. Fields found: {%s}",
                    fieldName, obj.getClass().getName(), Arrays.asList(obj.getClass().getDeclaredFields())));
        }
        return field.get(obj);
    }

    public Object getSuperField(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldException(String.format("Field '%s' could not be found in '%s'. Fields found: {%s}",
                    fieldName, obj.getClass().getName(), Arrays.asList(obj.getClass().getDeclaredFields())));
        }
        return field.get(obj);
    }

    public void setField(Object obj, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldException(String.format("Field '%s' could not be found in '%s'. Fields found: [%s]",
                    fieldName, obj.getClass().getName(), Arrays.asList(obj.getClass().getDeclaredFields())));
        }
        field.set(obj, value);
    }

    public Object invokeMethod(Object obj, String methodName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = obj.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException(String.format("Method '%s' could not be found in '%s'. Methods found: [%s]",
                    methodName, obj.getClass().getName(), Arrays.asList(obj.getClass().getDeclaredMethods())));
        }
        return method.invoke(obj);
    }

    public Object invokeMethod(Object obj, String methodName, Object... parameters)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method;
        Class<?>[] parameterTypes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = parameters[i].getClass();
        }
        try {
            method = obj.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException(String.format("Method '%s' could not be found in '%s'. Methods found: [%s]",
                    methodName, obj.getClass().getName(), Arrays.asList(obj.getClass().getDeclaredMethods())));
        }
        return method.invoke(obj, parameters);
    }

    public Object invokeStaticMethod(String className, String methodName, Object... params)
            throws Exception {
        Class<?> obj = Class.forName(NMS + "." + className);

        Class<?>[] paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) paramTypes[i] = params[i].getClass();

        Method method;
        try {
            method = obj.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new Exception(String.format("Method '%s' could not be found in '%s'. Methods found: [%s]",
                    methodName, obj.getName(), Arrays.asList(obj.getMethods())), e);
        }
        return method.invoke(null, params);
    }

    public ArtistPacket getArtistPacket(Object packet) {
        PacketType type = PacketType.getPacketType(packet);
        final BukkitVersion version = ArtMap.instance().getBukkitVersion().getVersion();

        if (type == null) {
            return null;
        }
        switch (type) {
            case LOOK:

                float yaw, pitch;
                try {
                    yaw = version.getYaw(packet);
                    pitch = version.getPitch(packet);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    ArtMap.instance().getLogger().log(Level.SEVERE, "Failure!", e);
                    return null;
                }
                return new ArtistPacket.PacketLook(yaw, pitch);

            case ARM_ANIMATION:
                return new ArtistPacket.PacketArmSwing();

            case INTERACT:

                Object packetInteractType;

                try {
                    packetInteractType = invokeMethod(packet, "a");
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    ArtMap.instance().getLogger().log(Level.SEVERE, "Failure!", e);
                    return null;
                }

                Class<?> enumEntityUseActionType = packetInteractType.getClass();
                Object[] enumValues = enumEntityUseActionType.getEnumConstants();
                int i;

                for (i = 0; i < enumValues.length; i++) {

                    if (packetInteractType == enumValues[i]) {
                        break;
                    }
                }

                ArtistPacket.PacketInteract.InteractType interactType = (i == 1) ?
                        ArtistPacket.PacketInteract.InteractType.ATTACK :
                        ArtistPacket.PacketInteract.InteractType.INTERACT;

                return new ArtistPacket.PacketInteract(interactType);

            default:
                break;
        }
        return null;
    }

    public byte[] getMap(MapView mapView) {
        byte colors[];

        try {
            Object worldMap = getField(mapView, "worldMap");
            try {
                colors = (byte[]) getField(worldMap, "colors");
            } catch (NoSuchFieldException e) {
                //Then we must be on 1.17
                colors = (byte[]) getField(worldMap, "g");
            }
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            colors = null;
        }
        if (colors == null) {
            return new byte[128 * 128];
        }
        return colors;
    }

    public void setWorldMap(MapView mapView, byte[] colors) throws NoSuchFieldException, IllegalAccessException {
            mapView.setCenterX(-999999);
            mapView.setCenterZ(-999999);
            
            Object worldMap = getField(mapView, "worldMap");
            try {
                setField(worldMap, "colors", colors);
            } catch (NoSuchFieldException e) {
                //Then we must be on 1.17
                setField(worldMap, "g", colors);
            }

            mapView.setScale(MapView.Scale.FARTHEST);
    }
}
