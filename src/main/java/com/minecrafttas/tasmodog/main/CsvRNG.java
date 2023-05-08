package com.minecrafttas.tasmodog.main;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CsvRNG extends ClassVisitor {
	
	protected CsvRNG() {
		super(Opcodes.ASM9);
	}
	
	protected CsvRNG(ClassVisitor writer) {
		super(Opcodes.ASM9, writer);
	}

	/**
	 * Launch CSV RNG generator
	 * @param args Launch parameters
	 * @throws Exception Filesystem Exception
	 */
	public static void main(String[] args) throws Exception {
		List<File> classFiles = new ArrayList<>();
		classFiles.addAll(Arrays.asList(new File("bin/main/net/minecraft/client").listFiles()));
		classFiles.addAll(Arrays.asList(new File("bin/main/net/minecraft/src").listFiles()));
		
		CsvRNG csv = new CsvRNG();
		for (File classFile : classFiles) {
			new ClassReader(Files.readAllBytes(classFile.toPath())).accept(csv, 0);
		}
		
	}

	@Override
	public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
		return new MethodVisitor(Opcodes.ASM9) {
			
			@Override
			public void visitMethodInsn(int opcode, String targetOwner, String targetName, String targetDescriptor, boolean isInterface) {
				if (opcode == Opcodes.INVOKEVIRTUAL && "java/util/Random".equalsIgnoreCase(targetOwner)) {
					System.out.println(methodName + ": Random#" + targetName);
				} else if (opcode == Opcodes.INVOKESTATIC && "java/lang/Math".equalsIgnoreCase(targetOwner) && "random".equals(targetName)) {
					System.out.println(methodName + ": Math#random");
				}
				super.visitMethodInsn(opcode, targetOwner, targetName, targetDescriptor, isInterface);
			}
			
		};
	}
	
	
}
