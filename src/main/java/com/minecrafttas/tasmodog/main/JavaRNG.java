package com.minecrafttas.tasmodog.main;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class JavaRNG {

	/**
	 * Launch Java RNG generator
	 * @param args Launch parameters
	 * @throws Exception Filesystem Exception
	 */
	public static void main(String[] args) throws Exception {		
		// prepare arrays
		String nextInt = "\tpublic static final String[][] nextIntDesc = {\n";
		String nextFloat = "\tpublic static final String[][] nextFloatDesc = {\n";
		String nextGaussian = "\tpublic static final String[][] nextGaussianDesc = {\n";
		String nextDouble = "\tpublic static final String[][] nextDoubleDesc = {\n";
		String nextLong = "\tpublic static final String[][] nextLongDesc = {\n";
		String nextBoolean = "\tpublic static final String[][] nextBooleanDesc = {\n";
		String random = "\tpublic static final String[][] randomDesc = {\n";
		
		// go through every rng instance
		for (String line : Files.readAllLines(new File("rng.csv").toPath())) {
			// ignore first line
			if (line.startsWith("Class,Method,Type"))
				continue;
			
			// ignore disabled entries
			if (line.contains(",TRUE,"))
				continue;
			
			// trim fields
			String[] frags = line.split(",");
			frags[4] = frags[4].trim();
			frags[5] = frags[5].trim();
			
			// throw error on invalid name
			if (frags[4].isEmpty())
				throw new RuntimeException("Invalid name: " + frags[4]);

			// throw error on invalid description
			if (frags[5].isEmpty())
				throw new RuntimeException("Invalid description: " + frags[4]);
			
			// sort rng into arrays
			switch (frags[2]) {
				case "nextInt":
					nextInt += "\t\t{ \"" + frags[4] + "\", \"" + frags[5] + "\" },\n";
					break;
				case "nextFloat":
					nextFloat += "\t\t{ \"" + frags[4] + "\", \"" + frags[5] + "\" },\n";
					break;
				case "nextGaussian":
					nextGaussian += "\t\t{ \"" + frags[4] + "\", \"" + frags[5] + "\" },\n";
					break;
				case "nextDouble":
					nextDouble += "\t\t{ \"" + frags[4] + "\", \"" + frags[5] + "\" },\n";
					break;
				case "nextLong":
					nextLong += "\t\t{ \"" + frags[4] + "\", \"" + frags[5] + "\" },\n";
					break;
				case "nextBoolean":
					nextBoolean += "\t\t{ \"" + frags[4] + "\", \"" + frags[5] + "\" },\n";
					break;
				case "random":
					random += "\t\t{ \"" + frags[4] + "\", \"" + frags[5] + "\" },\n";
					break;
			}
		}
		
		// close arrays
		nextInt += "\t};";
		nextFloat += "\t};";
		nextGaussian += "\t};";
		nextDouble += "\t};";
		nextLong += "\t};";
		nextBoolean += "\t};";
		random += "\t};";
		
		// create class file
		FileWriter writer = new FileWriter("src/main/java/com/minecrafttas/tasmodog/tools/KillTheRngOccurences.java");
		
		writer.write("package com.minecrafttas.tasmodog.tools;\n\n");
		writer.write("public class KillTheRngOccurences {\n\n");
		
		writer.write("\tpublic static final int[] nextInt = new int[" + (nextInt.split("\n").length - 2) + "];\n");
		writer.write("\tpublic static final float[] nextFloat = new float[" + (nextFloat.split("\n").length - 2) + "];\n");
		writer.write("\tpublic static final double[] nextGaussian = new double[" + (nextGaussian.split("\n").length - 2) + "];\n");
		writer.write("\tpublic static final double[] nextDouble = new double[" + (nextDouble.split("\n").length - 2) + "];\n");
		writer.write("\tpublic static final long[] nextLong = new long[" + (nextLong.split("\n").length - 2) + "];\n");
		writer.write("\tpublic static final boolean[] nextBoolean = new boolean[" + (nextBoolean.split("\n").length - 2) + "];\n");
		writer.write("\tpublic static final double[] random = new double[" + (random.split("\n").length - 2) + "];\n\n");
		
		writer.write(nextInt + "\n\n");
		writer.write(nextFloat + "\n\n");
		writer.write(nextGaussian + "\n\n");
		writer.write(nextDouble + "\n\n");
		writer.write(nextLong + "\n\n");
		writer.write(nextBoolean + "\n\n");
		writer.write(random + "\n\n");
		
		writer.write("}\n");
		
		writer.close();
	}
	
}
