package net.tasmod.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class VirtualInputVisitor {

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
			public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
				if ((
"getEventKey".equalsIgnoreCase(name)
|| "getEventKeyState".equalsIgnoreCase(name) 
|| "getEventCharacter".equalsIgnoreCase(name) 
|| "next".equalsIgnoreCase(name)
|| "isKeyDown".equalsIgnoreCase(name)) && owner.equalsIgnoreCase("org/lwjgl/input/Keyboard") && opcode == Opcodes.INVOKESTATIC) {
					owner = "net/tasmod/virtual/VirtualKeyboard";
				} else if ((
"getEventDWheel".equalsIgnoreCase(name) 
|| "getEventButton".equalsIgnoreCase(name) 
|| "getEventButtonState".equalsIgnoreCase(name) 
|| "next".equalsIgnoreCase(name) 
|| "setGrabbed".equalsIgnoreCase(name) 
|| "setCursorPosition".equalsIgnoreCase(name)
|| "getEventY".equalsIgnoreCase(name) 
|| "getEventX".equalsIgnoreCase(name) 
|| "getDY".equalsIgnoreCase(name) 
|| "getDX".equalsIgnoreCase(name) 
|| "getY".equalsIgnoreCase(name) 
|| "getX".equalsIgnoreCase(name) 
|| "isButtonDown".equalsIgnoreCase(name)) && owner.equalsIgnoreCase("org/lwjgl/input/Mouse") && opcode == Opcodes.INVOKESTATIC) {
					owner = "net/tasmod/virtual/VirtualMouse";
				}
				super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
			}
		};
	}
	
}


