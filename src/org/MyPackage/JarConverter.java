package org.MyPackage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.rascalmpl.interpreter.IEvaluatorContext;

public class JarConverter extends M3Converter {
	private String jarFile;
	private String ClassFile;

	JarConverter(TypeStore typeStore) {
		super(typeStore);
	}

	private String extractJarName(ISourceLocation jarLoc) {
		String tmp = jarLoc.getPath().substring(0, jarLoc.getPath().indexOf("!"));
		return tmp.substring(tmp.lastIndexOf("/") + 1);
	}

	private String extractClassName(ISourceLocation jarLoc) {
		return jarLoc.getPath().substring(jarLoc.getPath().indexOf("!") + 1);
	}


	public void convert(ISourceLocation jarLoc, IEvaluatorContext ctx) {

		this.jarFile = extractJarName(jarLoc);
		this.ClassFile = extractClassName(jarLoc);

		try {
			ClassReader cr = new ClassReader(ctx.getResolverRegistry().getInputStream(jarLoc.getURI()));
			ClassNode cn = new ClassNode();

			cr.accept(cn, ClassReader.SKIP_DEBUG);

			this.insert(this.declarations, values.sourceLocation("java+class", jarFile, "/" + cn.name), values.sourceLocation(jarFile));
			
			this.insert(this.extendsRelations, values.sourceLocation("java+class", jarFile, "/" + cn.name), values.sourceLocation("java+class" , "" , cn.superName));

			//  @implements={<|java+class:///m3startv2/viaInterface|,|java+interface:///m3startv2/m3Interface|>},
			for (int i = 0; i < cn.interfaces.size(); ++i) {
				String iface = (String) cn.interfaces.get(i) ;
				this.insert(this.implementsRelations, values.sourceLocation("java+class", jarFile, "/" + cn.name), values.sourceLocation("java+interface", jarFile, "/" + iface));
			}
			
			emitMethods(cn.methods);
			emitFields(cn.fields) ;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Should not happen", e);
		}
	}

	private void emitMethods(List<MethodNode> methods) {
		try {
			for (int i = 0; i < methods.size(); ++i) {
				MethodNode method = methods.get(i);
				JarConverter.this.insert(JarConverter.this.declarations, values.sourceLocation("java+method", jarFile, "/" + method.name), values.sourceLocation(jarFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    //   <|java+field:///m3startv2/Main/intField|,|project://m3startv2/src/m3startv2/Main.java|(54,13,<5,12>,<5,25>)>,
	private void emitFields(List<FieldNode> fields) {
		try {
			for (int i = 0; i < fields.size(); ++i) {
				FieldNode field = fields.get(i);
				JarConverter.this.insert(JarConverter.this.declarations, values.sourceLocation("java+field", jarFile, "/" + field.name), values.sourceLocation(jarFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
