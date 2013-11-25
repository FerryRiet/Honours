package org.MyPackage;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.io.*;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.rascalmpl.interpreter.IEvaluatorContext;
import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.rascalmpl.values.ValueFactoryFactory;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;


public class ClassParser3 {
	
	public ClassParser3(IValueFactory vf) {
		// TODO Auto-generated constructor stub
	}
	
//	public static void main(String[] args){
//		
//	}
	

	public  IList findMethods(ISourceLocation a, IEvaluatorContext b) throws FileNotFoundException,IOException{
        InputStream in=new FileInputStream(a.getPath());
        
        ClassReader cr=new ClassReader(in);
        ClassNode classNode=new ClassNode();
        
        //Generate Factories
        IValueFactory values = ValueFactoryFactory.getValueFactory();
        TypeFactory TF = TypeFactory.getInstance();
        
        //Generate String 
        Type stringType = TF.stringType();
        IList imList = values.list(stringType);
        
        //ClassNode is a ClassVisitor
        cr.accept(classNode, 0);
        List <MethodNode> ls = classNode.methods;
        //Let's move through all the methods

        for(MethodNode methodNode : ls){
            System.out.println(methodNode.name);
        	IString temp = values.string(methodNode.name);
            imList = imList.append(temp);
        }
        return imList;

    }
}
