package ndea.core.simplend.util.chunk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

public class MCCEAChunk extends LinkedList<MCCEAConfiguration> implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -3900646700438147999L;

  private int chunkID;

  public MCCEAChunk(final int chunkID) {
    this.chunkID = chunkID;
  }

  public MCCEAChunk(final int chunkID, final Collection<MCCEAConfiguration> configurations) {
    this.chunkID = chunkID;
    this.addAll(configurations);
  }

  public static MCCEAChunk parseFrom(final File chunkFile) throws FileNotFoundException, IOException {
    MCCEAChunk chunk = null;

    try (BufferedReader br = new BufferedReader(new FileReader(chunkFile))) {
      String line;
      boolean first = true;
      while ((line = br.readLine()) != null) {
        if (first) {
          String[] lineSplit = line.split("=");
          if (lineSplit[0].trim().equals("chunkID")) {
            int chunkID = Integer.parseInt(lineSplit[1]);
            chunk = new MCCEAChunk(chunkID);
          } else {
            throw new IllegalArgumentException("Malformed chunk file. Does not start with chunkID");
          }
          first = false;
        } else {
          MCCEAConfiguration config = MCCEAConfiguration.parseFrom(line);
          config.setChunk(chunk);
          chunk.add(config);
        }
      }
    }
    return chunk;
  }

  public File getOutputFolder() {
    File outputFolder = new File("chunklogs" + File.separator + this.chunkID);
    outputFolder.mkdirs();
    return outputFolder;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("chunkID=" + this.chunkID + "\n");

    for (MCCEAConfiguration config : this) {
      sb.append(config);
      sb.append("\n");
    }

    return sb.toString();
  }

  public MCCEAConfiguration getForTaskID(final int taskID) {
    for (MCCEAConfiguration config : this) {
      if (config.getTaskID() == taskID) {
        return config;
      }
    }
    return null;
  }

  public int getChunkID() {
    return this.chunkID;
  }

}
