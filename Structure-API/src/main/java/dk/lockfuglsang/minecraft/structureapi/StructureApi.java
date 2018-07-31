package dk.lockfuglsang.minecraft.structureapi;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.InputStream;

/**
 * The StructureAPI enabling plugins to save and paste/paste structures using the Minecraft internal Structure API.
 */
public interface StructureApi {
    /**
     * Saves a structure
     *
     * @param start           the start location, i.e. lower corner of the structure
     * @param size            the size, number of blocks from lower corner in 3 directions
     * @param name            the name
     * @param author          the author
     * @param includeEntities if entities should be included in the structure
     * @return if is was successful
     */
    Structure copy(Location start, Vector size, String name, String author, boolean includeEntities);

    /**
     * Saves a structure
     *
     * @param center          the center of the structure.
     * @param radius          The number of blocks from the center to copy into the structure object.
     * @param name
     * @param author          the author
     * @param includeEntities if entities should be included in the structure
     * @return if is was successful
     */
    Structure copy(Location center, int radius, String name, String author, boolean includeEntities);

    /**
     * Loads a structure
     *
     * @param center          the origin location
     * @param structure       a previously read Structure to be pasted into the world at center.
     * @param mirror          how the structure should be mirrored (FRONT_BACK, LEFT_RIGHT or NONE)
     * @param rotate          how the structure should be rotated (CLOCKWISE_90, CLOCKWISE_180,
     *                        COUNTERCLOCKWISE_90 or NONE)
     * @param includeEntities if entities should be included
     * @return if is was successful
     */
    boolean paste(Location center, Structure structure, String mirror, String rotate, boolean includeEntities);

    /**
     * Reads a structure from an nbt stream.
     *
     * @param name
     * @param nbtStream An input-stream containing a structure in NBT format.
     * @return A structure object.
     * @throws IOException
     */
    Structure readStructure(World world, String name, InputStream nbtStream);

    /**
     * Reads a structure from an nbt stream.
     * @param name the name of a predefined structure.
     * @return A structure object.
     * @throws IOException
     */
    Structure readStructure(World world, String name);

    boolean saveStructure(World bukkitWorld, Structure structure);
}
