package net.tasmod.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RandomnessVisitor {

	public static ClassVisitor classVisitor(final String classname, final ClassWriter writer) {
		return new ClassVisitor(Opcodes.ASM9, writer) {
			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
				if ("net/minecraft/src/World".equalsIgnoreCase(classname) && !name.equalsIgnoreCase("<init>"))
					return super.visitMethod(access, name, descriptor, signature, exceptions);
				return randomMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions));
			}
		};
	}

	public static MethodVisitor randomMethodVisitor(final MethodVisitor methodVisitor) {
		return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
			@Override
			public void visitTypeInsn(final int opcode, String type) {
				if (opcode == Opcodes.NEW && type.equalsIgnoreCase("java/util/Random"))
					type = "net/tasmod/random/SimpleRandomMod";
				super.visitTypeInsn(opcode, type);
			}
			int i = 0;
			@Override
			public void visitMethodInsn(final int opcode, String owner, String name, String descriptor, final boolean isInterface) {
				if (name.equalsIgnoreCase("<init>") && owner.equalsIgnoreCase("java/util/Random") && opcode == Opcodes.INVOKESPECIAL) {
					owner = "net/tasmod/random/SimpleRandomMod";
					super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
					return;
				}
				if (!name.contains("next") || !owner.equalsIgnoreCase("java/util/Random")) {
					super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
					return;
				}
				super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
				if (descriptor.endsWith("J") || descriptor.endsWith("D"))
					super.visitInsn(Opcodes.POP2);
				else
					super.visitInsn(Opcodes.POP);
				if (descriptor.equals("(I)I")) {
					name += "0";
					descriptor = "()I";
				}
				i++;
				if (i == 0)
					super.visitInsn(Opcodes.ICONST_0);
				else if (i == 1)
					super.visitInsn(Opcodes.ICONST_1);
				else if (i == 2)
					super.visitInsn(Opcodes.ICONST_2);
				else if (i == 3)
					super.visitInsn(Opcodes.ICONST_3);
				else if (i == 4)
					super.visitInsn(Opcodes.ICONST_4);
				else if (i == 5)
					super.visitInsn(Opcodes.ICONST_5);
				else 
					super.visitIntInsn(Opcodes.BIPUSH, i);
				super.visitFieldInsn(Opcodes.PUTSTATIC, "net/tasmod/random/SimpleRandomMod", "INSN", "I");
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/tasmod/random/SimpleRandomMod", "_" + name, descriptor, false);
				i++;
			}
		};
	}

}


