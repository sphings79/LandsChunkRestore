package de.sphings.landschunkrestore;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class LandsChunkRestore extends JavaPlugin {
   public static LandsChunkRestore plugin;

   public void onEnable() {
      plugin = this;
      this.getServer().getPluginManager().registerEvents(new Listener(), this);
      File baseFolder = new File(plugin.getDataFolder() + "/");
      if (!baseFolder.exists()) {
         baseFolder.mkdir();
      }

      File schemFolder = new File(plugin.getDataFolder() + "/regionfiles/");
      if (!schemFolder.exists()) {
         schemFolder.mkdir();
      }

      System.out.println("§bThis plugin is an extension for the Lands plugin.");
      System.out.println("§bIt saves the chunk on /lands claim/create to a schem file.");
      System.out.println("§bAfter /lands unclaim/delete the chunk will be restored from the schem file.");
      System.out.println("§bThank you for using my plugin.");
   }
}