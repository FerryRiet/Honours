module Honours


import util::FileSystem;
import Prelude;
import lang::java::m3::Core;
import demo::common::Crawl;

public M3 Do(){
	loc gauva = |jar:///C:/Users/Chiel/Documents/Evolution/Honours/guava-15.0.jar!|;
	loc log = |file:///C:/Users/Chiel/Documents/Evolution/Honours/log.txt|;
		
	M3 m3Project = createM3FromJar2(gauva);
	iprintToFile(log, m3Project);
	return m3Project;
}

public M3 createM3FromJar2(loc jarFile) {
    str jarName = substring(jarFile.path, 0, findFirst(jarFile.path, "!"));
    jarName = substring(jarName, findLast(jarName, "/")+1);
    loc jarLoc = |jar:///|;
    jarLoc.authority = jarName;
    return composeJavaM3(jarLoc , { createM3FromJarClasss(jarClass) | loc jarClass <- crawl(jarFile, "class") });
}

@javaClass{org.MyPackage.ClassParser3}
@reflect
public java list[str] findMethods(loc Location);

@javaClass{org.MyPackage.EclipseJavaCompiler}
@reflect
public java M3 createM3FromJarClasss(loc location);