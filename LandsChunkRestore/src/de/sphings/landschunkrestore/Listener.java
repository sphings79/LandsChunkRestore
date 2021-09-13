package de.sphings.landschunkrestore;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import me.angeschossen.lands.api.events.ChunkDeleteEvent;
import me.angeschossen.lands.api.events.ChunkPreClaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

public class Listener implements org.bukkit.event.Listener {
   @EventHandler
   public void onAreaClaim(ChunkPreClaimEvent e) {
      World world = e.getWorld().getWorld();
      int chunkX = e.getX();
      int chunkZ = e.getZ();
      File areaSchematic = new File(LandsChunkRestore.plugin.getDataFolder().getAbsolutePath() + "/regionfiles/" + chunkX + "-" + chunkZ + ".schem");
      CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(world), BlockVector3.at(chunkX * 16, 0, chunkZ * 16), BlockVector3.at(chunkX * 16 + 15, world.getMaxHeight(), chunkZ * 16 + 15));
      BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

      try {
         @SuppressWarnings("deprecation")
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1);

         try {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            forwardExtentCopy.setCopyingEntities(false);
            forwardExtentCopy.setCopyingBiomes(true);
            Operations.complete(forwardExtentCopy);
         } catch (Throwable var15) {
            if (editSession != null) {
               try {
                  editSession.close();
               } catch (Throwable var12) {
                  var15.addSuppressed(var12);
               }
            }

            throw var15;
         }

         if (editSession != null) {
            editSession.close();
         }
      } catch (WorldEditException var16) {
         var16.printStackTrace();
      }

      try {
         ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(areaSchematic));

         try {
            writer.write(clipboard);
         } catch (Throwable var13) {
            if (writer != null) {
               try {
                  writer.close();
               } catch (Throwable var11) {
                  var13.addSuppressed(var11);
               }
            }

            throw var13;
         }

         if (writer != null) {
            writer.close();
         }
      } catch (IOException var14) {
         var14.printStackTrace();
      }

   }

   @EventHandler
   public void onAreaUnclaim(ChunkDeleteEvent e) {
      World world = e.getWorld();
      int chunkX = e.getX();
      int chunkZ = e.getZ();
      File areaSchematic = new File(LandsChunkRestore.plugin.getDataFolder().getAbsolutePath() + "/regionfiles/" + chunkX + "-" + chunkZ + ".schem");
      Bukkit.getScheduler().runTask(LandsChunkRestore.plugin, () -> {
         Clipboard clipboard = null;
         ClipboardFormat format = ClipboardFormats.findByFile(areaSchematic);

         try {
            ClipboardReader reader = format.getReader(new FileInputStream(areaSchematic));

            try {
               clipboard = reader.read();
            } catch (Throwable var13) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var10) {
                     var13.addSuppressed(var10);
                  }
               }

               throw var13;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (IOException var14) {
            var14.printStackTrace();
         }

         try {
            @SuppressWarnings("deprecation")
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1);

            try {
               Operation operation = (new ClipboardHolder(clipboard)).createPaste(editSession).to(BlockVector3.at(chunkX * 16, 0, chunkZ * 16)).build();
               Operations.complete(operation);
            } catch (Throwable var11) {
               if (editSession != null) {
                  try {
                     editSession.close();
                  } catch (Throwable var9) {
                     var11.addSuppressed(var9);
                  }
               }

               throw var11;
            }

            if (editSession != null) {
               editSession.close();
            }
         } catch (WorldEditException var12) {
            var12.printStackTrace();
         }

      });
   }
}