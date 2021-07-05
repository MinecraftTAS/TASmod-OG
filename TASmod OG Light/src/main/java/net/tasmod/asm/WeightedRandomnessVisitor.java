package net.tasmod.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WeightedRandomnessVisitor {

	public static ClassVisitor classVisitor(String classname, ClassWriter writer) {
		return new ClassVisitor(Opcodes.ASM9, writer) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
				return randomMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions));
			}
		};
	}

	public static MethodVisitor randomMethodVisitor(MethodVisitor methodVisitor) {
		return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
			@Override
			public void visitTypeInsn(int opcode, String type) {
				if (opcode == Opcodes.NEW && type.equalsIgnoreCase("java/util/Random")) {
					type = "net/tasmod/random/WeightedRandomMod";
				}
				super.visitTypeInsn(opcode, type);
			}
			@Override
			public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
				if (name.equalsIgnoreCase("<init>") && owner.equalsIgnoreCase("java/util/Random") && opcode == Opcodes.INVOKESPECIAL) {
					owner = "net/tasmod/random/WeightedRandomMod";
				}
				super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
			}
		};
	}
	
}


