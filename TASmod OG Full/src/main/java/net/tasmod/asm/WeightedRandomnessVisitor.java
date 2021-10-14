package net.tasmod.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WeightedRandomnessVisitor {

	public static ClassVisitor classVisitor(final String classname, final ClassWriter writer) {
		return new ClassVisitor(Opcodes.ASM9, writer) {
			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
				return randomMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions));
			}
		};
	}

	public static MethodVisitor randomMethodVisitor(final MethodVisitor methodVisitor) {
		return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
			@Override
			public void visitTypeInsn(final int opcode, String type) {
				if (opcode == Opcodes.NEW && type.equalsIgnoreCase("java/util/Random"))
					type = "net/tasmod/random/WeightedRandomMod";
				super.visitTypeInsn(opcode, type);
			}
			@Override
			public void visitMethodInsn(final int opcode, String owner, final String name, final String descriptor, final boolean isInterface) {
				if (name.equalsIgnoreCase("<init>") && owner.equalsIgnoreCase("java/util/Random") && opcode == Opcodes.INVOKESPECIAL)
					owner = "net/tasmod/random/WeightedRandomMod";
				super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
			}
		};
	}

}


