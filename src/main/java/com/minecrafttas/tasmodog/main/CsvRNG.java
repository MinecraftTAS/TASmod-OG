package com.minecrafttas.tasmodog.main;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CsvRNG extends ClassVisitor {
	
	/**
	 * Launch CSV RNG generator
	 * @param args Launch parameters
	 * @throws Exception Filesystem Exception
	 */
	public static void main(String[] args) throws Exception {
		// find classes
		List<File> classFiles = new ArrayList<>();
		classFiles.addAll(Arrays.asList(new File("bin/main/net/minecraft/client").listFiles()));
		classFiles.addAll(Arrays.asList(new File("bin/main/net/minecraft/src").listFiles()));
		
		// find rng in classes
		CsvRNG csv = new CsvRNG();
		for (File classFile : classFiles) {
			csv.setCurrentClass(classFile.getName().replace(".class", ""));
			new ClassReader(Files.readAllBytes(classFile.toPath())).accept(csv, 0);
		}
		
		// count rng
		int total = 0;
		for (Entry<RandomOccurance, Integer> e : csv.getRandom().entrySet()) {
			System.out.println(e.getKey() + " (" + e.getValue() + ")");
			total += e.getValue();
		}
		System.out.println("Total " + total + " occurences of rng found");
		
		// create csv
		PrintWriter writer = new PrintWriter(new File("rng.csv"));
		for (Entry<RandomOccurance, Integer> e : csv.getRandom().entrySet()) {
			RandomOccurance o = e.getKey();
			for (int i = 0; i < e.getValue(); i++) {
				writer.println(String.format("%s,%s,%s,%s,,FALSE,", o.className, o.methodName, o.randomType, i));
			}
		}
		writer.close();
	}

	private Map<RandomOccurance, Integer> random = new HashMap<>();
	private String clazz;

	/**
	 * Set current class
	 * @param clazz Current class
	 */
	public void setCurrentClass(String clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * Get all found rng occurences
	 * @return Rng occurences
	 */
	public Map<RandomOccurance, Integer> getRandom() {
		return this.random;
	}
	
	/**
	 * Visit method and search for rng
	 */
	@Override
	public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
		return new MethodVisitor(Opcodes.ASM9) {
			
			@Override
			public void visitMethodInsn(int opcode, String targetOwner, String targetName, String targetDescriptor, boolean isInterface) {
				RandomOccurance identifier = new RandomOccurance(clazz, methodName, targetName);
				if (opcode == Opcodes.INVOKEVIRTUAL && "java/util/Random".equalsIgnoreCase(targetOwner))
					random.put(identifier, random.getOrDefault(identifier, 0) + 1);
				else if (opcode == Opcodes.INVOKESTATIC && "java/lang/Math".equalsIgnoreCase(targetOwner) && "random".equals(targetName))
					random.put(identifier, random.getOrDefault(identifier, 0) + 1);
				
				super.visitMethodInsn(opcode, targetOwner, targetName, targetDescriptor, isInterface);
			}
			
		};
	}
	
	/**
	 * Random occurance found in code
	 */
	public class RandomOccurance {
		
		private String className;
		private String methodName;
		private String randomType;
		
		/**
		 * Initialize Random Occurance
		 * @param className Class
		 * @param methodName Method
		 * @param randomType next-Type
		 */
		public RandomOccurance(String className, String methodName, String randomType) {
			this.className = className;
			this.methodName = methodName;
			this.randomType = randomType;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.className.hashCode();
			result = prime * result + this.methodName.hashCode();
			result = prime * result + this.randomType.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			RandomOccurance other = (RandomOccurance) obj;
			return this.className.equals(other.className) && this.methodName.equals(other.methodName) && this.randomType.equals(other.randomType);
		}

		@Override
		public String toString() {
			return "net.minecraft.src." + this.className + "#" + this.methodName + " -> " + this.randomType;
		}
		
	}
	
	protected CsvRNG() {
		super(Opcodes.ASM9);
	}
	
	protected CsvRNG(ClassVisitor writer) {
		super(Opcodes.ASM9, writer);
	}
	
}
