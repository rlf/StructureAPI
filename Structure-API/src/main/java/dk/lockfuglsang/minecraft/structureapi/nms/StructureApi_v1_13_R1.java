package dk.lockfuglsang.minecraft.structureapi.nms;

import dk.lockfuglsang.minecraft.structureapi.Structure;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class StructureApi_v1_13_R1 extends StructureApiCommon {
    private Class GeneratorAccess;

    private final Method readStructureMethod;
    private final Method readStructureFromStream;
    private final Method createStructureMethod;
    private final Method saveMethod;

    public StructureApi_v1_13_R1() throws Exception {
        GeneratorAccess = loadNMSClass("GeneratorAccess");

        // Fields
        structureVoidBlock = Blocks.getField("STRUCTURE_VOID").get(null);

        saveMethod = DefinedStructureManager.getMethod("c", MinecraftKey);
        createStructureMethod = DefinedStructureManager.getMethod("a", MinecraftKey);
        readStructureMethod = DefinedStructureManager.getMethod("b", MinecraftKey);
        readStructureFromStream = DefinedStructureManager.getDeclaredMethod("a", InputStream.class);
        if (readStructureFromStream != null && Modifier.isPrivate(readStructureFromStream.getModifiers())) {
            readStructureFromStream.setAccessible(true);
        }

        loadMethod = DefinedStructure.getMethod("b", GeneratorAccess, BlockPosition, DefinedStructureInfo);
    }

    @Override
    public Structure readStructure(org.bukkit.World bukkitWorld, String name) {
        try {
            Object world = getHandleMethod.invoke(CraftWorld.cast(bukkitWorld));
            Object structureManager = getStructureManagerMethod.invoke(world);
            Object key = minecraftKeyConstructor.newInstance(name);
            Object structure = readStructureMethod.invoke(structureManager, key);
            return new StructureNms(name, structure, BlockPosition);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Unable to read structure", e);
        }
        return null;
    }

    public Structure readStructure(org.bukkit.World bukkitWorld, String name, InputStream nbtStream) {
        try {
            Object world = getHandleMethod.invoke(CraftWorld.cast(bukkitWorld));
            Object structureManager = getStructureManagerMethod.invoke(world);
            Object structure = readStructureFromStream.invoke(structureManager, nbtStream);
            return new StructureNms(name, structure, BlockPosition);
        } catch (IllegalAccessException | InvocationTargetException e) {
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
            Object structure = createStructureMethod.invoke(structureManager, key);
            setPosMethod.invoke(structure, world, startPos, sizePos, includeEntities, structureVoidBlock);
            setAuthorMethod.invoke(structure, author);
            return new StructureNms(name, structure, BlockPosition);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Unable to copy structure", e);
        }
        return null;
    }

    @Override
    public boolean saveStructure(org.bukkit.World bukkitWorld, Structure structure) {
        try {
            Object world = getHandleMethod.invoke(CraftWorld.cast(bukkitWorld));
            Object structureManager = getStructureManagerMethod.invoke(world);
            Object key = minecraftKeyConstructor.newInstance(structure.getName());
            return (boolean) saveMethod.invoke(structureManager, key);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Unable to save structure", e);
        }
        return false;
    }
}
