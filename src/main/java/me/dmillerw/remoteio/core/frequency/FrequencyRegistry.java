package me.dmillerw.remoteio.core.frequency;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.*;
import java.util.Map;

/**
 * Created by dmillerw
 */
public class FrequencyRegistry {

    private static Map<Integer, String> savedFrequencies = Maps.newHashMap();

    public static void saveFrequency(int frequency, String name) {
        savedFrequencies.put(frequency, name);
    }

    public static void deleteFrequency(int frequency) {
        savedFrequencies.remove(frequency);
    }

    public static Map<Integer, String> getSavedFrequencies() {
        ImmutableMap.Builder<Integer, String> builder = ImmutableMap.builder();
        builder.putAll(savedFrequencies);
        return builder.build();
    }

    public static void load(File file) {
        if (!file.exists())
            return;

        savedFrequencies.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(";");
                int frequency;
                try {
                     frequency = Integer.parseInt(split[0]);
                } catch (NumberFormatException ex) {
                    continue;
                }
                savedFrequencies.put(frequency, split[1]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void save(File file) {
        if (file.exists())
            file.delete();

        savedFrequencies.put(1, "Test");
        savedFrequencies.put(2, "Banana");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for(Map.Entry<Integer, String> entry : savedFrequencies.entrySet()) {
                writer.write(Integer.toString(entry.getKey()));
                writer.write(";");
                writer.write(entry.getValue());
                writer.write("\n");
            }

            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
