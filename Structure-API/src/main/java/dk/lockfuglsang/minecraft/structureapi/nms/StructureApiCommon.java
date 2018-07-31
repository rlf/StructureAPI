package dk.lockfuglsang.minecraft.structureapi.nms;

import dk.lockfuglsang.minecraft.reflection.ReflectionUtil;
import dk.lockfuglsang.minecraft.structureapi.Structure;
import dk.lockfuglsang.minecraft.structureapi.StructureApi;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class StructureApiCommon implements StructureApi {
    public static Logger logger = Logger.getLogger("StructureAPI");
    private static String version = ReflectionUtil.getCraftBukkitVersion();

    final Class MinecraftServer;
    private final Class WorldServer;
    final Class CraftWorld;
    final Class World;
    final Class BlockPosition;
    final Class DefinedStructureManager;
    final Class DefinedStructure;
    final Class MinecraftKey;
    final Class Blocks;
    final Class Block;
    final Class DefinedStructureInfo;
    final Class EnumBlockMirror;
    final Class EnumBlockRotation;
    final Constructor blockPositionConstructor;
    final Constructor minecraftKeyConstructor;
    final Constructor definedStructureInfoConstructor;

    Method getMinecraftServerMethod;
    Method getHandleMethod;
    Method getStructureManagerMethod;
    Object structureVoidBlock;
    Method enumBlockMirrorValueOfMethod;
    Method enumBlockRotationValueOfMethod;
    Method mirrorMethod;
    Method rotationMethod;
    Method ignoreEntitiesMethod;
    Method loadMethod;

    Method setAuthorMethod;
    Method setPosMethod;

    StructureApiCommon() throws ClassNotFoundException, NoSuchMethodException {
        // Classes
        BlockPosition = loadNMSClass("BlockPosition");
        WorldServer = loadNMSClass("WorldServer");
        CraftWorld = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
        MinecraftServer = loadNMSClass("MinecraftServer");
        DefinedStructureManager = loadNMSClass("DefinedStructureManager");
        DefinedStructure = loadNMSClass("DefinedStructure");
        MinecraftKey = loadNMSClass("MinecraftKey");
        Blocks = loadNMSClass("Blocks");
        Block = loadNMSClass("Block");
        World = loadNMSClass("World");
        DefinedStructureInfo = loadNMSClass("DefinedStructureInfo");
        EnumBlockMirror = loadNMSClass("EnumBlockMirror");
        EnumBlockRotation = loadNMSClass("EnumBlockRotation");

        // Constructors
        blockPositionConstructor = BlockPosition.getConstructor(int.class, int.class, int.class);
        minecraftKeyConstructor = MinecraftKey.getConstructor(String.class);
        definedStructureInfoConstructor = DefinedStructureInfo.getConstructor();

        // Getters
        getHandleMethod = CraftWorld.getMethod("getHandle");
        getMinecraftServerMethod = WorldServer.getMethod("getMinecraftServer");
        getStructureManagerMethod = ReflectionUtil.findMethod(WorldServer, DefinedStructureManager);

        // Enums valueOf
        enumBlockMirrorValueOfMethod = EnumBlockMirror.getMethod("valueOf", String.class);
        enumBlockRotationValueOfMethod = EnumBlockRotation.getMethod("valueOf", String.class);

        mirrorMethod = DefinedStructureInfo.getMethod("a", EnumBlockMirror);
        rotationMethod = DefinedStructureInfo.getMethod("a", EnumBlockRotation);
        ignoreEntitiesMethod = DefinedStructureInfo.getMethod("a", boolean.class);

        // Setters
        setAuthorMethod = DefinedStructure.getMethod("a", String.class);
        setPosMethod = DefinedStructure.getMethod("a", World, BlockPosition, BlockPosition, boolean.class, Block);
    }

    static Class<?> loadNMSClass(String className) {
        try {
            String nms = "net.minecraft.server." + version + ".";
            return Class.forName(nms + className);
        } catch (Exception ignored) {
            // we ignore it, we might not need it (depends on version)
        }
        return null;
    }

    @Override
    public Structure copy(Location center, int radius, String name, String author, boolean includeEntities) {
        Location start = center.clone().subtract(new Vector(radius,radius,radius));
        return copy(start, new Vector(radius*2, radius*2, radius*2), name, author, includeEntities);
    }

    @Override
    public boolean paste(Location origin, Structure structure, String mirror, String rotate, boolean includeEntities) {
        try {
            if (structure == null) {
                return false;
            } else {
                Object originPos = blockPositionConstructor.newInstance(
                        origin.getBlockX() - structure.getSize().getBlockX() / 2,
                        origin.getBlockY() - structure.getSize().getBlockY() / 2,
                        origin.getBlockZ() - structure.getSize().getBlockZ() / 2);
                Object world = getHandleMethod.invoke(CraftWorld.cast(origin.getWorld()));
                Object structureInfo = definedStructureInfoConstructor.newInstance();
                mirrorMethod.invoke(structureInfo, enumBlockMirrorValueOfMethod.invoke(null, mirror));
                rotationMethod.invoke(structureInfo, enumBlockRotationValueOfMethod.invoke(null, rotate));
                ignoreEntitiesMethod.invoke(structureInfo, includeEntities);
                loadMethod.invoke(structure.getNmsObject(), world, originPos, structureInfo);
                return true;
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Unable to paste structure", e);
        }
        return false;
    }
}
