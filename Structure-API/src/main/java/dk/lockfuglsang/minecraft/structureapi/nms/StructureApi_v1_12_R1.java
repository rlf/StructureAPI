package dk.lockfuglsang.minecraft.structureapi.nms;

import dk.lockfuglsang.minecraft.reflection.ReflectionUtil;
import dk.lockfuglsang.minecraft.structureapi.Structure;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class StructureApi_v1_12_R1 extends StructureApiCommon {

    private static Method saveMethod;
    private final Method readStructureMethod;
    private final Method createStructureMethod;

    public StructureApi_v1_12_R1() throws Exception {

        // Fields
        structureVoidBlock = Blocks.getField("dj").get(null);

        createStructureMethod = DefinedStructureManager.getMethod("a", MinecraftServer, MinecraftKey);
        readStructureMethod = DefinedStructureManager.getMethod("b", MinecraftServer, MinecraftKey);

        saveMethod = ReflectionUtil.findMethod(DefinedStructureManager, Boolean.TYPE, MinecraftServer, MinecraftKey);
        loadMethod = DefinedStructure.getMethod("a", World, BlockPosition, DefinedStructureInfo);
    }

    @Override
    public Structure readStructure(org.bukkit.World bukkitWorld, String name) {
        try {
            Object world = getHandleMethod.invoke(CraftWorld.cast(bukkitWorld));
            Object server = getMinecraftServerMethod.invoke(world);
            Object structureManager = getStructureManagerMethod.invoke(world);
            Object key = minecraftKeyConstructor.newInstance(name);
            Object structure = readStructureMethod.invoke(structureManager, server, key);
            return new StructureNms(name, structure, BlockPosition);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Unable to read structure", e);
        }
        return null;
    }

    @Override
    public Structure copy(Location start, Vector size, String name, String author, boolean includeEntities) {
        try {
            Object startPos = blockPositionConstructor.newInstance(start.getBlockX(), start.getBlockY(), start.getBlockZ());
            Object sizePos = blockPositionConstructor.newInstance(size.getBlockX(), size.getBlockY(), size.getBlockZ());
            Object world = getHandleMethod.invoke(CraftWorld.cast(start.getWorld()));
            Object structureManager = getStructureManagerMethod.invoke(world);
            Object key = minecraftKeyConstructor.newInstance(name);
            Object server = getMinecraftServerMethod.invoke(world);
            Object structure = createStructureMethod.invoke(structureManager, server, key);
            setPosMethod.invoke(structure, world, startPos, sizePos, includeEntities, structureVoidBlock);
            setAuthorMethod.invoke(structure, author);
            return new StructureNms(name, structure, BlockPosition);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Unable to copy structure", e);
        }
        return null;
    }

    @Override
    public Structure readStructure(org.bukkit.World world, String name, InputStream nbtStream) {
        throw new UnsupportedOperationException("v1.12 doesn't support reading structures directly from stream");
    }

    @Override
    public boolean saveStructure(org.bukkit.World bukkitWorld, Structure structure) {
        try {
            Object world = getHandleMethod.invoke(CraftWorld.cast(bukkitWorld));
            Object structureManager = getStructureManagerMethod.invoke(world);
            Object key = minecraftKeyConstructor.newInstance(structure.getName());
            Object server = getMinecraftServerMethod.invoke(world);
            return (boolean) saveMethod.invoke(structureManager, server, key);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Unable to save structure", e);
        }
        return false;
    }
}
