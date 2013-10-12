JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	ALU.java \
	ALUIllegalArgumentException.java \
	CPU.java \
	Run.java \
	Parser.java \
	Memory.java \
	RegisterIllegalArgumentException.java \
	RegisterSet.java \
	MemIndexOutOfBoundsException.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
